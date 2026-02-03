package com.greenroute.app.ui.screens.saved

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.greenroute.app.data.local.entities.Route
import com.greenroute.app.ui.components.RouteCard
import com.greenroute.app.ui.theme.*
import com.greenroute.app.viewmodel.SavedUiState
import com.greenroute.app.viewmodel.SavedViewModel

/**
 * Saved routes screen showing bookmarked trips.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedScreen(
    viewModel: SavedViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState(initial = SavedUiState())

    SavedScreenContent(
        uiState = uiState,
        onRemoveSaved = { viewModel.removeSavedRoute(it) },
        onDelete = { viewModel.deleteRoute(it) },
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedScreenContent(
    uiState: SavedUiState,
    onRemoveSaved: (Route) -> Unit,
    onDelete: (Route) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundLight)
    ) {
        // Header
        TopAppBar(
            title = {
                Text(
                    text = "Viagens salvas",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = BackgroundLight,
                titleContentColor = TextPrimary
            )
        )

        // Content
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = GreenPrimary)
                }
            }
            uiState.savedRoutes.isEmpty() -> {
                // Empty state
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Bookmark,
                            contentDescription = null,
                            tint = GreenLight,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Sem viagens salvas",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Guarda as tuas viagens favoritas aqui!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextHint
                        )
                    }
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        items = uiState.savedRoutes,
                        key = { it.id }
                    ) { route ->
                        RouteCard(
                            route = route,
                            onCardClick = {},
                            onSaveClick = { onRemoveSaved(route) },
                            onDeleteClick = { onDelete(route) }
                        )
                    }
                }
            }
        }
    }
}
