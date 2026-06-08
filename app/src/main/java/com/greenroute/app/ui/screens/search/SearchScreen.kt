package com.greenroute.app.ui.screens.search

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import androidx.core.content.ContextCompat
import com.greenroute.app.ui.components.TransportOptionItem
import com.greenroute.app.ui.components.getTransportColor
import com.greenroute.app.ui.components.getTransportIcon
import com.greenroute.app.ui.components.getTransportName
import com.greenroute.app.ui.theme.*
import com.greenroute.app.util.getLastKnownLocation
import com.greenroute.app.viewmodel.SearchNavigationEvent
import com.greenroute.app.viewmodel.SearchViewModel

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
    val density = LocalDensity.current

    // ── Navigation events ─────────────────────────────────────────────────────
    LaunchedEffect(Unit) {
        viewModel.navigationEvents.collect { event ->
            when (event) {
                is SearchNavigationEvent.NavigateToRouteDetail -> onNavigateToDetail(event.routeId)
            }
        }
    }

    // ── Location permission + GPS ─────────────────────────────────────────────
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            getLastKnownLocation(context)?.let { (lat, lng) ->
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
            getLastKnownLocation(context)?.let { (lat, lng) ->
                viewModel.setCurrentLocation(lat, lng)
            }
        } else {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    // Local search text — syncs with viewModel when cleared
    var searchText by remember { mutableStateOf("") }
    LaunchedEffect(uiState.searchQuery) {
        if (uiState.searchQuery.isEmpty()) searchText = ""
    }

    // Measure text field width so the dropdown matches it exactly
    var textFieldWidthPx by remember { mutableIntStateOf(0) }

    val hasDestination = uiState.destination.isNotEmpty()

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
                        .statusBarsPadding()
                        .padding(top = 12.dp, bottom = 24.dp)
                        .padding(horizontal = 16.dp)
                ) {
                    // Back + title
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onBackClick) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Voltar", tint = TextOnGreen)
                        }
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "Pesquisar destino",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = TextOnGreen
                        )
                    }

                    Spacer(Modifier.height(12.dp))

                    // Origin indicator
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                    ) {
                        Icon(
                            Icons.Default.MyLocation,
                            contentDescription = null,
                            tint = TextOnGreen.copy(alpha = 0.8f),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = if (uiState.currentLocationString != null)
                                "A partir da tua localização atual"
                            else
                                "A partir de Lisboa, Portugal",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextOnGreen.copy(alpha = 0.8f)
                        )
                    }

                    Spacer(Modifier.height(10.dp))

                    // ── Search field + autocomplete dropdown ──────────────────
                    // Use Box + DropdownMenu(focusable = false) instead of
                    // ExposedDropdownMenuBox to avoid focus being stolen from the
                    // TextField when the suggestion list appears.
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = searchText,
                            onValueChange = { v ->
                                searchText = v
                                viewModel.updateSearchQuery(v)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .onGloballyPositioned { coords ->
                                    textFieldWidthPx = coords.size.width
                                },
                            placeholder = {
                                Text("Para onde queres ir?", color = TextSecondary)
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Search, contentDescription = null, tint = TextSecondary)
                            },
                            trailingIcon = {
                                if (searchText.isNotEmpty()) {
                                    IconButton(onClick = {
                                        searchText = ""
                                        viewModel.updateSearchQuery("")
                                    }) {
                                        Icon(Icons.Default.Clear, contentDescription = "Limpar", tint = TextSecondary)
                                    }
                                }
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = CardBackground,
                                unfocusedContainerColor = CardBackground,
                                focusedBorderColor = GreenLight,
                                unfocusedBorderColor = CardBackground,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            singleLine = true
                        )

                        // Autocomplete dropdown — focusable=false keeps typing focus in TextField
                        DropdownMenu(
                            expanded = uiState.locationPredictions.isNotEmpty(),
                            onDismissRequest = { viewModel.clearPredictions() },
                            properties = PopupProperties(focusable = false),
                            modifier = Modifier
                                .width(with(density) { textFieldWidthPx.toDp() })
                        ) {
                            uiState.locationPredictions.forEach { prediction ->
                                DropdownMenuItem(
                                    text = {
                                        Column {
                                            Text(
                                                text = prediction.getPrimaryText(null).toString(),
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontWeight = FontWeight.Medium,
                                                color = TextPrimary,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            val secondary = prediction.getSecondaryText(null).toString()
                                            if (secondary.isNotEmpty()) {
                                                Text(
                                                    text = secondary,
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = TextSecondary,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                            }
                                        }
                                    },
                                    leadingIcon = {
                                        Icon(Icons.Default.LocationOn, null, tint = GreenPrimary)
                                    },
                                    onClick = { viewModel.selectPrediction(prediction) }
                                )
                            }
                        }
                    }
                }
            }

            // ── Content area: empty state OR filter chips + results ────────────
            if (!hasDestination) {
                // ── Empty state — shown before any destination is selected
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = GreenLight,
                            modifier = Modifier.size(72.dp)
                        )
                        Spacer(Modifier.height(16.dp))
                        Text(
                            text = "Para onde queres ir?",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = "Escreve o destino acima para ver as opções de transporte e as emissões de CO₂.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            } else {
                // ── Transport filter chips
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, bottom = 8.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val transportTypes = listOf("car", "bus", "train", "metro", "walk", "bike")
                    items(transportTypes) { type ->
                        val isSelected = uiState.selectedTransport == type
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.selectTransport(if (isSelected) "" else type) },
                            label = { Text(getTransportName(type)) },
                            leadingIcon = {
                                Icon(
                                    getTransportIcon(type),
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
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

                // ── Results header
                Text(
                    text = "Opções para ${uiState.searchQuery}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = TextSecondary,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                // ── Loading or transport list
                if (uiState.isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
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
                    val filteredOptions = if (uiState.selectedTransport.isNullOrEmpty())
                        uiState.transportOptions
                    else
                        uiState.transportOptions.filter { it.type == uiState.selectedTransport }

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
        }

        // ── Full-screen loading overlay while route is being saved ────────────
        if (uiState.isStartingRoute) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.45f)),
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
                        Text("A calcular rota…", fontWeight = FontWeight.Medium, color = TextPrimary)
                    }
                }
            }
        }
    }
}
