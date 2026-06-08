package com.greenroute.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.greenroute.app.data.local.entities.Route
import com.greenroute.app.ui.theme.*

/**
 * Reusable card component for displaying route information.
 */
@Composable
fun RouteCard(
    route: Route,
    onCardClick: () -> Unit = {},
    onSaveClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {},
    showMenu: Boolean = true,
    modifier: Modifier = Modifier
) {
    var menuExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onCardClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (route.isSaved) GreenSurface else CardBackground
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // ── Origin → Destination ──────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f).padding(end = 4.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.TripOrigin,
                            contentDescription = "Partida",
                            tint = GreenPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = route.startLocation ?: "Origem",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    // Connector line
                    Box(
                        modifier = Modifier
                            .padding(start = 7.dp, top = 2.dp, bottom = 2.dp)
                            .width(2.dp)
                            .height(12.dp)
                            .background(GreenLight)
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Chegada",
                            tint = GreenPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = route.endLocation ?: "Destino",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // Context menu
                if (showMenu) {
                    Box {
                        IconButton(
                            onClick = { menuExpanded = true },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "Menu",
                                tint = TextSecondary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        DropdownMenu(
                            expanded = menuExpanded,
                            onDismissRequest = { menuExpanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text(if (route.isSaved) "Remover dos salvos" else "Guardar") },
                                onClick = { menuExpanded = false; onSaveClick() },
                                leadingIcon = {
                                    Icon(
                                        imageVector = if (route.isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                        contentDescription = null
                                    )
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Eliminar") },
                                onClick = { menuExpanded = false; onDeleteClick() },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = null,
                                        tint = StatusError
                                    )
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // ── Transport · distance · duration · CO2 ────────────────────────
            // weight(1f) on the transport label ensures it shrinks/truncates
            // before pushing the fixed-width distance or CO2 badge off-screen
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Transport icon + name — flex item, truncates when space is tight
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = getTransportIcon(route.transportType ?: "car"),
                        contentDescription = null,
                        tint = getTransportColor(route.transportType ?: "car"),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = getTransportName(route.transportType ?: "car"),
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Distance · duration — fixed width text
                Text(
                    text = "${"%.1f".format(route.distance)} km · ${route.duration} min",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    maxLines = 1
                )

                // CO2 badge
                CO2Badge(emission = route.co2Emission ?: 0.0)
            }
        }
    }
}

/**
 * Badge showing CO2 emission with colour coding.
 */
@Composable
fun CO2Badge(emission: Double) {
    val color = when {
        emission <= 0.0   -> CO2Low
        emission <= 100.0 -> CO2Low
        emission <= 200.0 -> CO2Medium
        emission <= 300.0 -> CO2High
        else              -> CO2VeryHigh
    }

    Surface(
        shape = RoundedCornerShape(8.dp),
        color = color.copy(alpha = 0.15f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Eco,
                contentDescription = "CO2",
                tint = color,
                modifier = Modifier.size(12.dp)
            )
            Spacer(modifier = Modifier.width(3.dp))
            Text(
                text = "${emission.toInt()}g",
                style = MaterialTheme.typography.labelSmall,
                color = color,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1
            )
        }
    }
}

// ── Helper functions ──────────────────────────────────────────────────────────

fun getTransportIcon(type: String): ImageVector {
    return when (type.lowercase()) {
        "car", "carro"            -> Icons.Default.DirectionsCar
        "bus", "autocarro"        -> Icons.Default.DirectionsBus
        "train", "comboio"        -> Icons.Default.Train
        "metro"                   -> Icons.Default.Subway
        "walk", "a pé"            -> Icons.Default.DirectionsWalk
        "bike", "bicicleta"       -> Icons.Default.DirectionsBike
        "electric_car"            -> Icons.Default.ElectricCar
        "motorcycle", "moto"      -> Icons.Default.TwoWheeler
        else                      -> Icons.Default.DirectionsCar
    }
}

fun getTransportColor(type: String): androidx.compose.ui.graphics.Color {
    return when (type.lowercase()) {
        "car", "carro"            -> TransportCar
        "bus", "autocarro"        -> TransportBus
        "train", "comboio"        -> TransportTrain
        "metro"                   -> TransportMetro
        "walk", "a pé"            -> TransportWalk
        "bike", "bicicleta"       -> TransportBike
        else                      -> TransportCar
    }
}

fun getTransportName(type: String): String {
    return when (type.lowercase()) {
        "car"          -> "Carro"
        "bus"          -> "Autocarro"
        "train"        -> "Comboio"
        "metro"        -> "Metro"
        "walk"         -> "A pé"
        "bike"         -> "Bicicleta"
        "electric_car" -> "Carro elétrico"
        "motorcycle"   -> "Mota"
        else           -> type.replaceFirstChar { it.uppercase() }
    }
}
