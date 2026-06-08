package com.greenroute.app.ui.screens.detail

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalConfiguration
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.*
import com.greenroute.app.data.local.entities.Route
import com.greenroute.app.data.remote.DirectionsStep
import com.greenroute.app.ui.components.CO2Badge
import com.greenroute.app.ui.components.getTransportColor
import com.greenroute.app.ui.components.getTransportIcon
import com.greenroute.app.ui.components.getTransportName
import com.greenroute.app.ui.theme.*
import com.greenroute.app.viewmodel.RouteDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RouteDetailScreen(
    viewModel: RouteDetailViewModel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    if (uiState.isLoading) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = GreenPrimary)
        }
        return
    }

    if (uiState.error != null || uiState.route == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(uiState.error ?: "Erro desconhecido", color = StatusError)
        }
        return
    }

    val route = uiState.route ?: return

    // Adaptive map/detail split: taller phones give more space to the map
    val configuration = LocalConfiguration.current
    val mapWeight = if (configuration.screenHeightDp >= 700) 0.52f else 0.45f
    val detailWeight = 1f - mapWeight

    Scaffold(
        modifier = modifier,
        // Inner Scaffold owns system bar insets (outer Scaffold disabled its own top)
        contentWindowInsets = WindowInsets.systemBars,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "${route.startLocation ?: "Origem"} → ${route.endLocation ?: "Destino"}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = getTransportName(route.transportType ?: "car"),
                            style = MaterialTheme.typography.bodySmall,
                            color = getTransportColor(route.transportType ?: "car"),
                            maxLines = 1
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.toggleSaved() }) {
                        Icon(
                            imageVector = if (route.isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = if (route.isSaved) "Remover dos guardados" else "Guardar",
                            tint = if (route.isSaved) GreenPrimary else TextSecondary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BackgroundLight,
                    titleContentColor = TextPrimary
                )
            )
        },
        containerColor = BackgroundLight
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            RouteMapView(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(mapWeight),
                polylinePoints = uiState.polylinePoints,
                transportType = route.transportType ?: "car",
                originLat = route.originLat,
                originLng = route.originLng,
                destLat = route.destLat,
                destLng = route.destLng,
                isLoadingDirections = uiState.isLoadingDirections
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(detailWeight),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    RouteSummaryCard(
                        route = route,
                        onNavigate = {
                            val lat = route.destLat ?: return@RouteSummaryCard
                            val lng = route.destLng ?: return@RouteSummaryCard
                            val navMode = when (route.transportType) {
                                "walk" -> "w"
                                "bike" -> "b"
                                "transit", "bus", "train", "metro" -> "r"
                                else -> "d"
                            }
                            val uri = Uri.parse("google.navigation:q=$lat,$lng&mode=$navMode")
                            val intent = Intent(Intent.ACTION_VIEW, uri).apply {
                                setPackage("com.google.android.apps.maps")
                            }
                            if (intent.resolveActivity(context.packageManager) != null) {
                                context.startActivity(intent)
                            } else {
                                // Fallback: open Google Maps in browser
                                val webUri = Uri.parse("https://www.google.com/maps/dir/?api=1&destination=$lat,$lng&travelmode=${route.transportType ?: "driving"}")
                                context.startActivity(Intent(Intent.ACTION_VIEW, webUri))
                            }
                        }
                    )
                }

                if (uiState.steps.isNotEmpty()) {
                    item {
                        Text(
                            text = "Indicações",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }
                    items(uiState.steps) { step ->
                        StepItem(step = step)
                    }
                }
            }
        }
    }
}

@Composable
private fun RouteMapView(
    modifier: Modifier = Modifier,
    polylinePoints: List<LatLng>,
    transportType: String,
    originLat: Double?,
    originLng: Double?,
    destLat: Double?,
    destLng: Double?,
    isLoadingDirections: Boolean = false
) {
    val defaultLatLng = LatLng(38.7223, -9.1393) // Lisbon
    val initialPosition = if (originLat != null && originLng != null) {
        LatLng(originLat, originLng)
    } else defaultLatLng

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(initialPosition, 13f)
    }

    LaunchedEffect(polylinePoints, originLat, destLat) {
        try {
            val builder = LatLngBounds.Builder()
            var hasPoints = false
            if (polylinePoints.size >= 2) {
                polylinePoints.forEach { builder.include(it) }
                hasPoints = true
            } else {
                if (originLat != null && originLng != null) {
                    builder.include(LatLng(originLat, originLng))
                    hasPoints = true
                }
                if (destLat != null && destLng != null) {
                    builder.include(LatLng(destLat, destLng))
                    hasPoints = true
                }
            }
            if (hasPoints) {
                cameraPositionState.animate(
                    CameraUpdateFactory.newLatLngBounds(builder.build(), 120)
                )
            }
        } catch (_: Exception) {}
    }

    Box(
        modifier = modifier.clip(RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp))
    ) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(isMyLocationEnabled = false),
            uiSettings = MapUiSettings(
                zoomControlsEnabled = true,
                myLocationButtonEnabled = false
            )
        ) {
            if (polylinePoints.size >= 2) {
                Polyline(
                    points = polylinePoints,
                    color = getTransportColor(transportType),
                    width = 12f
                )
            }

            if (originLat != null && originLng != null) {
                Marker(
                    state = MarkerState(position = LatLng(originLat, originLng)),
                    title = "Partida",
                    icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)
                )
            }

            if (destLat != null && destLng != null) {
                Marker(
                    state = MarkerState(position = LatLng(destLat, destLng)),
                    title = "Chegada",
                    icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
                )
            }
        }

        // Overlay spinner while fetching fresh directions
        if (isLoadingDirections) {
            Surface(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(12.dp),
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
                tonalElevation = 4.dp
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = GreenPrimary,
                        strokeWidth = 2.dp
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "A calcular rota…",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextPrimary
                    )
                }
            }
        }
    }
}

@Composable
private fun RouteSummaryCard(route: Route, onNavigate: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = getTransportColor(route.transportType ?: "car").copy(alpha = 0.15f),
                        modifier = Modifier.size(48.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = getTransportIcon(route.transportType ?: "car"),
                                contentDescription = null,
                                tint = getTransportColor(route.transportType ?: "car"),
                                modifier = Modifier.size(26.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = getTransportName(route.transportType ?: "car"),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "${route.duration} min · ${"%.1f".format(route.distance)} km",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                }
                CO2Badge(emission = route.co2Emission ?: 0.0)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onNavigate,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Navigation, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Iniciar Navegação", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun StepItem(step: DirectionsStep) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = Icons.Default.TurnRight,
            contentDescription = null,
            tint = GreenPrimary,
            modifier = Modifier
                .size(20.dp)
                .padding(top = 2.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = step.instruction,
                style = MaterialTheme.typography.bodyMedium,
                color = TextPrimary
            )
            Text(
                text = "${step.distanceMeters}m · ${step.durationSeconds / 60}min",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
        }
    }
    HorizontalDivider(
        modifier = Modifier.padding(vertical = 8.dp),
        color = BackgroundLight
    )
}
