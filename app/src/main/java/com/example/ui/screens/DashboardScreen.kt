package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.FifteenDayMissionStats
import com.example.ui.viewmodel.FifteenDayOrderStats
import com.example.ui.viewmodel.PortalScreen
import java.text.NumberFormat
import java.util.Locale

@Composable
fun DashboardScreen(
    profile: FranchiseProfile,
    locations: List<FranchiseLocation>,
    riders: List<Rider>,
    orders: List<OrderRecord>,
    missions: List<Mission>,
    orderStats: FifteenDayOrderStats,
    missionStats: FifteenDayMissionStats,
    onNavigate: (PortalScreen) -> Unit,
    onOpenCreateMission: () -> Unit,
    onOpenAddOrder: () -> Unit,
    onOpenAddLocation: () -> Unit
) {
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("en", "IN"))

    val activeLocationsCount = locations.count { it.status == LocationStatus.ACTIVE }
    val onDutyRidersCount = riders.count { it.status != RiderStatus.OFF_DUTY }
    val inProgressMissions = missions.count {
        it.status in listOf(
            MissionStatus.ASSIGNED,
            MissionStatus.ACCEPTED,
            MissionStatus.PICKUP_IN_PROGRESS,
            MissionStatus.PICKED_UP,
            MissionStatus.IN_TRANSIT,
            MissionStatus.OUT_FOR_DELIVERY
        )
    }
    val completedMissions = missions.count { it.status == MissionStatus.DELIVERED }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp)
    ) {
        // Welcome Banner Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CorporateNavy),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = BrightOrange,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = profile.franchiseId,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }

                        Surface(
                            color = Color.White.copy(alpha = 0.15f),
                            shape = CircleShape
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(StatusDelivered)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Operational Hub", color = Color.White, fontSize = 11.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = profile.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Text(
                        text = "${profile.city}, ${profile.state} • Manager: ${profile.contactPerson}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.8f),
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }
        }

        // Quick Actions Row
        item {
            Text(
                text = "Quick Actions",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = CorporateNavy
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                QuickActionButton(
                    icon = Icons.Default.AddLocation,
                    label = "New Mission",
                    modifier = Modifier.weight(1f),
                    testTag = "quick_new_mission_button",
                    onClick = onOpenCreateMission
                )
                QuickActionButton(
                    icon = Icons.Default.ReceiptLong,
                    label = "Log Orders",
                    modifier = Modifier.weight(1f),
                    testTag = "quick_log_orders_button",
                    onClick = onOpenAddOrder
                )
                QuickActionButton(
                    icon = Icons.Default.Store,
                    label = "Add Hub",
                    modifier = Modifier.weight(1f),
                    testTag = "quick_add_hub_button",
                    onClick = onOpenAddLocation
                )
                QuickActionButton(
                    icon = Icons.Default.Assessment,
                    label = "15-Day Report",
                    modifier = Modifier.weight(1f),
                    testTag = "quick_15day_report_button",
                    onClick = { onNavigate(PortalScreen.FIFTEEN_DAY_REPORT) }
                )
            }
        }

        // Key Performance Metrics Grid
        item {
            Text(
                text = "Operations Overview",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = CorporateNavy
            )

            Spacer(modifier = Modifier.height(8.dp))

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MetricCard(
                        title = "Locations",
                        primaryValue = "${locations.size}",
                        secondaryValue = "$activeLocationsCount Active",
                        icon = Icons.Default.Apartment,
                        iconTint = BrightOrange,
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigate(PortalScreen.LOCATIONS_PROFILE) }
                    )

                    MetricCard(
                        title = "Riders",
                        primaryValue = "${riders.size}",
                        secondaryValue = "$onDutyRidersCount On Duty",
                        icon = Icons.Default.TwoWheeler,
                        iconTint = Color(0xFF0284C7),
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigate(PortalScreen.RIDERS_ORDERS) }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MetricCard(
                        title = "Missions",
                        primaryValue = "${missions.size}",
                        secondaryValue = "$inProgressMissions In Transit • $completedMissions Done",
                        icon = Icons.Default.LocalShipping,
                        iconTint = StatusDelivered,
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigate(PortalScreen.MISSIONS) }
                    )

                    MetricCard(
                        title = "15-Day Orders",
                        primaryValue = "${orderStats.totalOrders}",
                        secondaryValue = "Avg ${String.format(Locale.US, "%.1f", orderStats.averageDailyOrders)}/day",
                        icon = Icons.Default.TrendingUp,
                        iconTint = BrightOrange,
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigate(PortalScreen.FIFTEEN_DAY_REPORT) }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MetricCard(
                        title = "Mission On-Time Rate",
                        primaryValue = "${String.format(Locale.US, "%.1f", missionStats.onTimeRate)}%",
                        secondaryValue = "${String.format(Locale.US, "%.1f", missionStats.completionRate)}% Completion",
                        icon = Icons.Default.Timer,
                        iconTint = Color(0xFF8B5CF6),
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigate(PortalScreen.FIFTEEN_DAY_REPORT) }
                    )

                    MetricCard(
                        title = "15-Day Payout",
                        primaryValue = currencyFormat.format(orderStats.totalPayout),
                        secondaryValue = "Rates: ₹18/₹6/₹25",
                        icon = Icons.Default.CurrencyRupee,
                        iconTint = StatusDelivered,
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigate(PortalScreen.RIDERS_ORDERS) }
                    )
                }
            }
        }

        // 15-Day Trend Snapshot
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "15-Day Order Volume Trend",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleMedium,
                                color = CorporateNavy
                            )
                            Text(
                                text = "${orderStats.startDate} to ${orderStats.endDate}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        TextButton(
                            onClick = { onNavigate(PortalScreen.FIFTEEN_DAY_REPORT) }
                        ) {
                            Text("Full Report", color = BrightOrange, fontSize = 12.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // 15 Bars sparkline
                    val maxOrders = orderStats.dailyBreakdown.maxOfOrNull { it.totalOrders } ?: 1
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        orderStats.dailyBreakdown.forEach { day ->
                            val heightFraction = if (maxOrders > 0) (day.totalOrders.toFloat() / maxOrders).coerceIn(0.1f, 1f) else 0.1f
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .width(14.dp)
                                        .fillMaxHeight(heightFraction)
                                        .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                        .background(if (day.totalOrders > 0) BrightOrange else SlateBorder)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = day.date.takeLast(2),
                                    fontSize = 9.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }

        // Recent Delivery Missions Feed
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Active Delivery Missions",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = CorporateNavy
                )

                TextButton(onClick = { onNavigate(PortalScreen.MISSIONS) }) {
                    Text("View All (${missions.size})", color = BrightOrange, fontSize = 12.sp)
                }
            }
        }

        items(missions.take(4)) { mission ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigate(PortalScreen.MISSIONS) },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(
                                when (mission.status) {
                                    MissionStatus.DELIVERED -> StatusDelivered.copy(alpha = 0.15f)
                                    MissionStatus.IN_TRANSIT, MissionStatus.OUT_FOR_DELIVERY -> StatusInTransit.copy(alpha = 0.15f)
                                    MissionStatus.FAILED_DELIVERY, MissionStatus.CANCELLED -> StatusFailed.copy(alpha = 0.15f)
                                    else -> BrightOrange.copy(alpha = 0.15f)
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = when (mission.status) {
                                MissionStatus.DELIVERED -> Icons.Default.CheckCircle
                                MissionStatus.IN_TRANSIT, MissionStatus.OUT_FOR_DELIVERY -> Icons.Default.LocalShipping
                                else -> Icons.Default.Schedule
                            },
                            contentDescription = null,
                            tint = when (mission.status) {
                                MissionStatus.DELIVERED -> StatusDelivered
                                MissionStatus.IN_TRANSIT, MissionStatus.OUT_FOR_DELIVERY -> StatusInTransit
                                MissionStatus.FAILED_DELIVERY, MissionStatus.CANCELLED -> StatusFailed
                                else -> BrightOrange
                            },
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = mission.id,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = CorporateNavy
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = when (mission.priority) {
                                    MissionPriority.URGENT -> StatusFailed.copy(alpha = 0.15f)
                                    MissionPriority.HIGH -> BrightOrange.copy(alpha = 0.15f)
                                    else -> Color(0xFFF1F5F9)
                                }
                            ) {
                                Text(
                                    text = mission.priority.name,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = when (mission.priority) {
                                        MissionPriority.URGENT -> StatusFailed
                                        MissionPriority.HIGH -> BrightOrange
                                        else -> SlateMedium
                                    },
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Text(
                            text = mission.title,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            maxLines = 1,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Text(
                            text = "Rider: ${mission.riderName ?: "Unassigned"} • ${mission.deliveryAddress.take(30)}...",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = when (mission.status) {
                            MissionStatus.DELIVERED -> StatusDelivered.copy(alpha = 0.15f)
                            MissionStatus.IN_TRANSIT, MissionStatus.OUT_FOR_DELIVERY -> StatusInTransit.copy(alpha = 0.15f)
                            MissionStatus.FAILED_DELIVERY -> StatusFailed.copy(alpha = 0.15f)
                            else -> AmberAccent.copy(alpha = 0.2f)
                        }
                    ) {
                        Text(
                            text = mission.status.label,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = when (mission.status) {
                                MissionStatus.DELIVERED -> StatusDelivered
                                MissionStatus.IN_TRANSIT, MissionStatus.OUT_FOR_DELIVERY -> StatusInTransit
                                MissionStatus.FAILED_DELIVERY -> StatusFailed
                                else -> DarkOrange
                            },
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun QuickActionButton(
    icon: ImageVector,
    label: String,
    modifier: Modifier = Modifier,
    testTag: String,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .testTag(testTag)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(BrightOrange.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = BrightOrange,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = CorporateNavy,
                maxLines = 1
            )
        }
    }
}

@Composable
fun MetricCard(
    title: String,
    primaryValue: String,
    secondaryValue: String,
    icon: ImageVector,
    iconTint: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(iconTint.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = primaryValue,
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                color = CorporateNavy
            )

            Text(
                text = secondaryValue,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}
