package com.greenroute.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.greenroute.app.ui.theme.*

/**
 * Simulated map placeholder component.
 * In a real app, this would integrate with Google Maps or similar.
 */
@Composable
fun MapPlaceholder(
    modifier: Modifier = Modifier,
    onLocationClick: () -> Unit = {}
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        GreenLight.copy(alpha = 0.3f),
                        GreenSurface
                    )
                )
            )
    ) {
        // Grid pattern to simulate map
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            repeat(4) {
                Divider(
                    color = GreenLight.copy(alpha = 0.2f),
                    thickness = 1.dp
                )
            }
        }
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            repeat(4) {
                VerticalDivider(
                    color = GreenLight.copy(alpha = 0.2f),
                    thickness = 1.dp
                )
            }
        }

        // Map icon in center
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Map,
                contentDescription = "Mapa",
                tint = GreenPrimary.copy(alpha = 0.5f),
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Mapa interativo",
                style = MaterialTheme.typography.bodyMedium,
                color = GreenPrimary.copy(alpha = 0.7f),
                fontWeight = FontWeight.Medium
            )
        }

        // Location marker
        Icon(
            imageVector = Icons.Default.LocationOn,
            contentDescription = "Localização",
            tint = GreenPrimary,
            modifier = Modifier
                .align(Alignment.Center)
                .offset(x = 30.dp, y = (-20).dp)
                .size(32.dp)
        )

        // My location button
        FloatingActionButton(
            onClick = onLocationClick,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(12.dp)
                .size(40.dp),
            containerColor = CardBackground,
            contentColor = GreenPrimary,
            elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 2.dp)
        ) {
            Icon(
                imageVector = Icons.Default.MyLocation,
                contentDescription = "Minha localização",
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun VerticalDivider(
    color: androidx.compose.ui.graphics.Color,
    thickness: androidx.compose.ui.unit.Dp
) {
    Box(
        modifier = Modifier
            .fillMaxHeight()
            .width(thickness)
            .background(color)
    )
}
