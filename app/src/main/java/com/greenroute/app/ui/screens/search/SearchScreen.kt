package com.greenroute.app.ui.screens.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.greenroute.app.ui.components.TransportOptionItem
import com.greenroute.app.ui.components.getTransportColor
import com.greenroute.app.ui.components.getTransportIcon
import com.greenroute.app.ui.components.getTransportName
import com.greenroute.app.ui.theme.*
import com.greenroute.app.viewmodel.SearchViewModel

/**
 * Search screen with transport options and filters.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    viewModel: SearchViewModel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    var searchText by remember { mutableStateOf("Metro Entrecampos") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundLight)
    ) {
        // Header with search
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
                // Back button and title
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
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

                Spacer(modifier = Modifier.height(16.dp))

                // Search field
                OutlinedTextField(
                    value = searchText,
                    onValueChange = { 
                        searchText = it
                        viewModel.updateDestination(it)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Para onde queres ir?") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = TextSecondary
                        )
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
            }
        }

        // Transport filter chips
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

        // Results header
        Text(
            text = "Opções de transporte",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        // Transport options list
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = GreenPrimary)
            }
        } else {
            val filteredOptions = if (uiState.selectedTransport.isNullOrEmpty()) {
                uiState.transportOptions
            } else {
                uiState.transportOptions.filter { it.type == uiState.selectedTransport }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
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
