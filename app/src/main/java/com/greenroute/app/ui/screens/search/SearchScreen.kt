package com.greenroute.app.ui.screens.search

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
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
    ) { granted ->
        if (granted) getLastKnownLocation(context)?.let { (lat, lng) ->
            viewModel.setCurrentLocation(lat, lng)
        } else viewModel.setLocationPermissionDenied()
    }
    LaunchedEffect(Unit) {
        val granted = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        if (granted) getLastKnownLocation(context)?.let { (lat, lng) ->
            viewModel.setCurrentLocation(lat, lng)
        } else permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    // Local text — kept in sync with viewModel
    var searchText by remember { mutableStateOf("") }
    LaunchedEffect(uiState.searchQuery) {
        if (uiState.searchQuery.isEmpty()) searchText = ""
    }

    val hasPredictions = uiState.locationPredictions.isNotEmpty()
    val hasDestination = uiState.destination.isNotEmpty()
    // Show "no results" feedback when user typed enough but got nothing back
    val showNoResults = !uiState.isLoadingPredictions
            && uiState.autocompleteError == null
            && searchText.length > 2
            && !hasPredictions
            && !hasDestination

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundLight)
        ) {

            // ─────────────────────────────────────────────────────────────────
            // Green header
            // ─────────────────────────────────────────────────────────────────
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = GreenPrimary,
                shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(top = 12.dp, bottom = 20.dp)
                        .padding(horizontal = 16.dp)
                ) {
                    // Back + title
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onBackClick) {
                            Icon(Icons.Default.ArrowBack, "Voltar", tint = TextOnGreen)
                        }
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "Pesquisar destino",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = TextOnGreen
                        )
                    }

                    Spacer(Modifier.height(10.dp))

                    // Origin indicator
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    ) {
                        Icon(
                            Icons.Default.MyLocation,
                            null,
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

                    // Search field — plain OutlinedTextField, no Dropdown inside Surface
                    OutlinedTextField(
                        value = searchText,
                        onValueChange = { v ->
                            searchText = v
                            viewModel.updateSearchQuery(v)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Para onde queres ir?", color = TextSecondary) },
                        leadingIcon = {
                            if (uiState.isLoadingPredictions) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = GreenPrimary,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Icon(Icons.Default.Search, null, tint = TextSecondary)
                            }
                        },
                        trailingIcon = {
                            if (searchText.isNotEmpty()) {
                                IconButton(onClick = {
                                    searchText = ""
                                    viewModel.updateSearchQuery("")
                                }) {
                                    Icon(Icons.Default.Clear, "Limpar", tint = TextSecondary)
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
                }
            }

            // ─────────────────────────────────────────────────────────────────
            // Content area (below Surface, not clipped by it)
            // ─────────────────────────────────────────────────────────────────

            when {

                // ── 1. Prediction list ────────────────────────────────────────
                hasPredictions -> {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .padding(top = 8.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = CardBackground),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        uiState.locationPredictions.forEachIndexed { idx, result ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { viewModel.selectPrediction(result) }
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.LocationOn,
                                    null,
                                    tint = GreenPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = result.primaryText,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Medium,
                                        color = TextPrimary,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    if (result.secondaryText.isNotEmpty()) {
                                        Text(
                                            text = result.secondaryText,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = TextSecondary,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                            }
                            if (idx < uiState.locationPredictions.lastIndex) {
                                HorizontalDivider(
                                    modifier = Modifier.padding(horizontal = 16.dp),
                                    color = MaterialTheme.colorScheme.outlineVariant
                                )
                            }
                        }
                    }
                    // Fill remaining space so nothing bleeds through
                    Spacer(Modifier.weight(1f))
                }

                // ── 2. API error ─────────────────────────────────────────────
                uiState.autocompleteError != null -> {
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
                                Icons.Default.WifiOff,
                                null,
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(Modifier.height(12.dp))
                            Text(
                                "Erro na pesquisa",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary
                            )
                            Spacer(Modifier.height(6.dp))
                            Text(
                                text = uiState.autocompleteError!!,
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                // ── 3. "No results" after typing ──────────────────────────────
                showNoResults -> {
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
                                Icons.Default.SearchOff,
                                null,
                                tint = GreenLight,
                                modifier = Modifier.size(56.dp)
                            )
                            Spacer(Modifier.height(12.dp))
                            Text(
                                "Sem resultados para \"$searchText\"",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondary,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                // ── 4. Transport options (destination selected) ───────────────
                hasDestination -> {
                    // Filter chips
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp, bottom = 8.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val types = listOf("car", "bus", "train", "metro", "walk", "bike")
                        items(types) { type ->
                            val selected = uiState.selectedTransport == type
                            FilterChip(
                                selected = selected,
                                onClick = { viewModel.selectTransport(if (selected) "" else type) },
                                label = { Text(getTransportName(type)) },
                                leadingIcon = {
                                    Icon(
                                        getTransportIcon(type), null,
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

                    Text(
                        "Opções para ${uiState.searchQuery}",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = TextSecondary,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    if (uiState.isLoading) {
                        Box(
                            modifier = Modifier.fillMaxWidth().weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                CircularProgressIndicator(color = GreenPrimary)
                                Spacer(Modifier.height(12.dp))
                                Text("A calcular rotas…", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                            }
                        }
                    } else {
                        val filtered = if (uiState.selectedTransport.isNullOrEmpty())
                            uiState.transportOptions
                        else
                            uiState.transportOptions.filter { it.type == uiState.selectedTransport }

                        LazyColumn(
                            modifier = Modifier.fillMaxWidth().weight(1f),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(filtered) { option ->
                                TransportOptionItem(option = option, onSelect = { viewModel.startRoute(option) })
                            }
                        }
                    }
                }

                // ── 5. Empty state (nothing typed yet) ───────────────────────
                else -> {
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
                                Icons.Default.Search,
                                null,
                                tint = GreenLight,
                                modifier = Modifier.size(72.dp)
                            )
                            Spacer(Modifier.height(16.dp))
                            Text(
                                "Para onde queres ir?",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                "Escreve o destino acima para ver as opções de transporte e as emissões de CO₂.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondary,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }

        // ── Full-screen overlay while saving route ────────────────────────────
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
