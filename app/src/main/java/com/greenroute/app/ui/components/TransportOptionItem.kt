package com.greenroute.app.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.greenroute.app.ui.theme.*

/**
 * Data class representing a transport option in search results.
 */
data class TransportOption(
    val type: String,
    val duration: Int,      // in minutes
    val co2Emission: Double, // in grams
    val distance: Double = 0.0
)

/**
 * Transport option item for the search screen.
 */
@Composable
fun TransportOptionItem(
    option: TransportOption,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onSelect() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = CardBackground
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left side: Transport icon and name
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = getTransportColor(option.type).copy(alpha = 0.15f),
                    modifier = Modifier.size(44.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = getTransportIcon(option.type),
                            contentDescription = option.type,
                            tint = getTransportColor(option.type),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Column {
                    Text(
                        text = getTransportName(option.type),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "${option.duration} min",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
            }

            // Right side: CO2 and play button
            Row(verticalAlignment = Alignment.CenterVertically) {
                CO2Badge(emission = option.co2Emission)
                
                Spacer(modifier = Modifier.width(12.dp))
                
                FilledIconButton(
                    onClick = onSelect,
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = GreenPrimary,
                        contentColor = TextOnGreen
                    ),
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Iniciar",
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}
