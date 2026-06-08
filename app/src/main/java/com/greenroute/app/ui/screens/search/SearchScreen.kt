package com.greenroute.app.ui.screens.search

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.greenroute.app.ui.components.TransportOptionItem
import com.greenroute.app.ui.components.getTransportColor
import com.greenroute.app.ui.components.getTransportIcon
import com.greenroute.app.ui.components.getTransportName
import com.greenroute.app.ui.theme.*
import com.greenroute.app.viewmodel.SearchNavigationEvent
import com.greenroute.app.viewmodel.SearchViewModel

@SuppressLint("MissingPermission")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    viewModel: SearchViewModel,
    onBackClick: () -> Unit,
    onNavigateToRecent: () -> Unit,
    onNavigateToDetail: (Int) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    // ── Navigation events ────────────────────────────────────────────────────
    LaunchedEffect(Unit) {
        viewModel.navigationEvents.collect { event ->
            when (event) {
                is SearchNavigationEvent.NavigateToRouteDetail -> onNavigateToDetail(event.routeId)
            }
        }
    }

    // ── Location permission + GPS ─────────────────────────────────────────────
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            getLastLocation(context)?.let { (lat, lng) ->
                viewModel.setCurrentLocation(lat, lng)
            }
        } else {
            viewModel.setLocationPermissionDenied()
        }
    }

    LaunchedEffect(Unit) {
        val granted = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (granted) {
            getLastLocation(context)?.let { (lat, lng) ->
                viewModel.setCurrentLocation(lat, lng)
            }
        } else {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    // Keep text field in sync
    var searchText by remember(uiState.searchQuery) { mutableStateOf(uiState.searchQuery) }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundLight)
        ) {
            // ── Green header ──────────────────────────────────────────────────
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = GreenPrimary,
                shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 48.dp, bottom = 24.dp)
                        .padding(horizontal = 16.dp)
                ) {
                    // Back + title
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Voltar",
                                tint = TextOnGreen
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Pesquisar destino",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = TextOnGreen
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Origin indicator
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MyLocation,
                            contentDescription = null,
                            tint = TextOnGreen.copy(alpha = 0.8f),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (uiState.currentLocationString != null)
                                "A partir da tua localização atual"
                            else
                                "A partir de Lisboa, Portugal",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextOnGreen.copy(alpha = 0.85f)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Destination search field with integrated autocomplete dropdown
                    ExposedDropdownMenuBox(
                        expanded = uiState.locationPredictions.isNotEmpty(),
                        onExpandedChange = { expanded ->
                            if (!expanded) viewModel.clearPredictions()
                        }
                    ) {
                        OutlinedTextField(
                            value = searchText,
                            onValueChange = {
                                searchText = it
                                viewModel.updateSearchQuery(it)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            placeholder = { Text("Para onde queres ir?") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = null,
                                    tint = TextSecondary
                                )
                            },
                            trailingIcon = {
                                if (searchText.isNotEmpty()) {
                                    IconButton(onClick = {
                                        searchText = ""
                                        viewModel.updateSearchQuery("")
                                    }) {
                                        Icon(
                                            Icons.Default.Clear,
                                            contentDescription = "Limpar",
                                            tint = TextSecondary
                                        )
                                    }
                                }
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = CardBackground,
                                unfocusedContainerColor = CardBackground,
                                focusedBorderColor = GreenLight,
                                unfocusedBorderColor = CardBackground
                            ),
                            singleLine = true
                        )

                        // Autocomplete dropdown — appears as popup below the text field
                        ExposedDropdownMenu(
                            expanded = uiState.locationPredictions.isNotEmpty(),
                            onDismissRequest = { viewModel.clearPredictions() },
                            modifier = Modifier.exposedDropdownSize(matchTextFieldWidth = true)
                        ) {
                            uiState.locationPredictions.forEach { prediction ->
                                DropdownMenuItem(
                                    text = {
                                        Column {
                                            Text(
                                                text = prediction.getPrimaryText(null).toString(),
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontWeight = FontWeight.Medium,
                                                color = TextPrimary
                                            )
                                            val secondary = prediction.getSecondaryText(null).toString()
                                            if (secondary.isNotEmpty()) {
                                                Text(
                                                    text = secondary,
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = TextSecondary
                                                )
                                            }
                                        }
                                    },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.LocationOn,
                                            contentDescription = null,
                                            tint = GreenPrimary
                                        )
                                    },
                                    onClick = { viewModel.selectPrediction(prediction) }
                                )
                            }
                        }
                    }
                }
            }

            // ── Transport filter chips ────────────────────────────────────────
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val transportTypes = listOf("car", "bus", "train", "metro", "walk", "bike")
                items(transportTypes) { type ->
                    val isSelected = uiState.selectedTransport == type
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            viewModel.selectTransport(if (isSelected) "" else type)
                        },
                        label = { Text(getTransportName(type)) },
                        leadingIcon = {
                            Icon(
                                imageVector = getTransportIcon(type),
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = getTransportColor(type).copy(alpha = 0.2f),
                            selectedLabelColor = getTransportColor(type),
                            selectedLeadingIconColor = getTransportColor(type)
                        )
                    )
                }
            }

            // ── Results header ────────────────────────────────────────────────
            Text(
                text = if (uiState.destination.isNotEmpty()) "Opções para ${uiState.searchQuery}"
                       else "Opções de transporte",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                maxLines = 1,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
            )

            // ── Transport options ─────────────────────────────────────────────
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = GreenPrimary)
                        Spacer(Modifier.height(12.dp))
                        Text(
                            "A calcular rotas…",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                }
            } else {
                val filteredOptions = if (uiState.selectedTransport.isNullOrEmpty()) {
                    uiState.transportOptions
                } else {
                    uiState.transportOptions.filter { it.type == uiState.selectedTransport }
                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredOptions) { option ->
                        TransportOptionItem(
                            option = option,
                            onSelect = { viewModel.startRoute(option) }
                        )
                    }
                }
            }
        }

        // ── Full-screen loading overlay when saving route ─────────────────────
        if (uiState.isStartingRoute) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.4f)),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CardBackground)
                ) {
                    Column(
                        modifier = Modifier.padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(color = GreenPrimary)
                        Spacer(Modifier.height(12.dp))
                        Text(
                            "A calcular rota…",
                            fontWeight = FontWeight.Medium,
                            color = TextPrimary
                        )
                    }
                }
            }
        }
    }
}

// ── Location helper ───────────────────────────────────────────────────────────

@SuppressLint("MissingPermission")
private fun getLastLocation(context: Context): Pair<Double, Double>? {
    return try {
        val lm = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val location =
            lm.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                ?: lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                ?: lm.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER)
        location?.let { Pair(it.latitude, it.longitude) }
    } catch (_: Exception) {
        null
    }
}
