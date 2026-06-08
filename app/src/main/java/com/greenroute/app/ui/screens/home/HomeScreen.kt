package com.greenroute.app.ui.screens.home

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.greenroute.app.ui.components.MapPlaceholder
import com.greenroute.app.ui.components.RouteCard
import com.greenroute.app.ui.theme.*
import com.greenroute.app.util.getLastKnownLocation
import com.greenroute.app.viewmodel.HomeViewModel

/**
 * Home screen with greeting, map and recent routes.
 * Requests location permission on first load so the map can show the blue dot.
 */
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onSearchClick: () -> Unit,
    onProfileClick: () -> Unit = {},
    onRouteClick: (Int) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    // ── Location state ────────────────────────────────────────────────────────
    var userLat by remember { mutableStateOf<Double?>(null) }
    var userLng by remember { mutableStateOf<Double?>(null) }
    var hasLocationPermission by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasLocationPermission = isGranted
        if (isGranted) {
            getLastKnownLocation(context)?.let { (lat, lng) ->
                userLat = lat
                userLng = lng
            }
        }
    }

    LaunchedEffect(Unit) {
        val granted = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        hasLocationPermission = granted
        if (granted) {
            getLastKnownLocation(context)?.let { (lat, lng) ->
                userLat = lat
                userLng = lng
            }
        } else {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundLight),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        // Header
        item {
            HomeHeader(
                userName = uiState.user?.name ?: "Viajante",
                onSearchClick = onSearchClick,
                onProfileClick = onProfileClick
            )
        }

        // Map — passes location so the blue dot and camera centre work
        item {
            MapPlaceholder(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .padding(top = 16.dp),
                userLat = userLat,
                userLng = userLng,
                hasLocationPermission = hasLocationPermission
            )
        }

        // Recent routes section header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(top = 24.dp, bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Viagens recentes",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }
        }

        // Loading state
        if (uiState.isLoading) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = GreenPrimary)
                }
            }
        }

        // Recent routes list
        items(uiState.recentRoutes) { route ->
            RouteCard(
                route = route,
                onCardClick = { onRouteClick(route.id) },
                onSaveClick = { viewModel.toggleSaveRoute(route) },
                onDeleteClick = { viewModel.deleteRoute(route) },
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 12.dp)
            )
        }

        // Empty state
        if (!uiState.isLoading && uiState.recentRoutes.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Sem viagens recentes",
                            style = MaterialTheme.typography.bodyLarge,
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Pesquise um destino para começar!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextHint
                        )
                    }
                }
            }
        }
    }
}

// ── Header ────────────────────────────────────────────────────────────────────

@Composable
private fun HomeHeader(
    userName: String,
    onSearchClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = GreenPrimary,
        shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp)
                .padding(top = 12.dp, bottom = 24.dp)
        ) {
            // Top row: greeting + profile button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
                    Text(
                        text = "Pronto para viajar,",
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextOnGreen.copy(alpha = 0.8f),
                        maxLines = 1
                    )
                    Text(
                        text = "$userName?",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextOnGreen,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Surface(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .clickable { onProfileClick() },
                    color = TextOnGreen.copy(alpha = 0.2f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Perfil",
                            tint = TextOnGreen,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Search bar (tappable — navigates to SearchScreen)
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSearchClick() },
                shape = RoundedCornerShape(12.dp),
                color = TextOnGreen
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Pesquisar",
                        tint = TextSecondary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Para onde queres ir?",
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextHint
                    )
                }
            }
        }
    }
}
