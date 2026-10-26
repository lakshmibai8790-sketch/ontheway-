package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.NotificationItem
import com.example.data.model.PayoutSettings
import com.example.data.model.UserAccount
import com.example.ui.theme.*

@Composable
fun NotificationsSettingsScreen(
    user: UserAccount?,
    notifications: List<NotificationItem>,
    payoutSettings: PayoutSettings,
    onMarkNotificationRead: (String) -> Unit,
    onMarkAllRead: () -> Unit,
    onUpdatePayoutRates: (Double, Double, Double) -> Unit,
    onResetDemoData: () -> Unit,
    onLogout: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0 = Notifications, 1 = Settings

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .padding(16.dp)
    ) {
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = BrightOrange
        ) {
            val unreadCount = notifications.count { !it.isRead }
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("Notifications ${if (unreadCount > 0) "($unreadCount)" else ""}", fontWeight = FontWeight.Bold) },
                icon = { Icon(Icons.Default.Notifications, contentDescription = null) }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("Portal Settings", fontWeight = FontWeight.Bold) },
                icon = { Icon(Icons.Default.Settings, contentDescription = null) }
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (selectedTab == 0) {
            // NOTIFICATIONS TAB
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Operational Activity Feed",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = CorporateNavy
                )

                if (notifications.any { !it.isRead }) {
                    TextButton(onClick = onMarkAllRead) {
                        Text("Mark all as read", color = BrightOrange, fontSize = 12.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (notifications.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No notifications at this time.", color = SlateMedium)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(notifications, key = { it.id }) { notif ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onMarkNotificationRead(notif.id) },
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (!notif.isRead) MaterialTheme.colorScheme.surface else SurfaceElevated
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = if (!notif.isRead) 2.dp else 0.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(
                                            when (notif.category) {
                                                "MISSION" -> BrightOrange.copy(alpha = 0.15f)
                                                "ORDER" -> StatusDelivered.copy(alpha = 0.15f)
                                                "LOCATION" -> Color(0xFF0284C7).copy(alpha = 0.15f)
                                                else -> SlateBorder
                                            }
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = when (notif.category) {
                                            "MISSION" -> Icons.Default.LocalShipping
                                            "ORDER" -> Icons.Default.Receipt
                                            "LOCATION" -> Icons.Default.Store
                                            else -> Icons.Default.Info
                                        },
                                        contentDescription = null,
                                        tint = when (notif.category) {
                                            "MISSION" -> BrightOrange
                                            "ORDER" -> StatusDelivered
                                            "LOCATION" -> Color(0xFF0284C7)
                                            else -> CorporateNavy
                                        },
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = notif.title,
                                            fontWeight = if (!notif.isRead) FontWeight.Bold else FontWeight.Medium,
                                            fontSize = 13.sp,
                                            color = CorporateNavy
                                        )
                                        Text(
                                            text = notif.timestamp,
                                            fontSize = 10.sp,
                                            color = SlateMedium
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(2.dp))

                                    Text(
                                        text = notif.message,
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                if (!notif.isRead) {
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(BrightOrange)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // SETTINGS TAB
            var primaryRate by remember { mutableStateOf("${payoutSettings.primaryRate}") }
            var clubRate by remember { mutableStateOf("${payoutSettings.clubRate}") }
            var largeRate by remember { mutableStateOf("${payoutSettings.largeRate}") }
            var showResetDialog by remember { mutableStateOf(false) }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Payout Configuration Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(
                            text = "Rider Payout Rate Card (₹)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = CorporateNavy
                        )
                        Text(
                            text = "Configure rates used across daily orders, 15-day reports, and weekly audits. Default rates: ₹18 Primary, ₹6 Club, ₹25 Large.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        OutlinedTextField(
                            value = primaryRate,
                            onValueChange = { primaryRate = it },
                            label = { Text("Primary Delivery Rate (₹)") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = clubRate,
                            onValueChange = { clubRate = it },
                            label = { Text("Club Order Rate (₹)") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = largeRate,
                            onValueChange = { largeRate = it },
                            label = { Text("Large / Heavy Order Rate (₹)") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Button(
                            onClick = {
                                val p = primaryRate.toDoubleOrNull() ?: 18.0
                                val c = clubRate.toDoubleOrNull() ?: 6.0
                                val l = largeRate.toDoubleOrNull() ?: 25.0
                                onUpdatePayoutRates(p, c, l)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = BrightOrange),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("save_payout_rates_button")
                        ) {
                            Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Save Rates")
                        }
                    }
                }

                // Account Security Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = "Franchise Account Security",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = CorporateNavy
                        )

                        Text("Active Session: ${user?.franchiseId ?: "FR-000001"}")
                        Text("Selfie Verification: ${if (user?.selfieImagePath != null) "Verified & Active" else "Pending Capture"}", color = if (user?.selfieImagePath != null) StatusDelivered else DarkOrange, fontWeight = FontWeight.Bold)

                        Spacer(modifier = Modifier.height(4.dp))

                        OutlinedButton(
                            onClick = onLogout,
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = StatusFailed),
                            modifier = Modifier.testTag("portal_logout_button")
                        ) {
                            Icon(Icons.Default.Logout, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Log Out of Franchise Portal")
                        }
                    }
                }

                // Data Maintenance Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Portal Maintenance",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = CorporateNavy
                        )
                        Text(
                            text = "Reset all demonstration missions, 30 days of sample order data, and locations to original seed state.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        OutlinedButton(
                            onClick = { showResetDialog = true },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = DarkOrange),
                            modifier = Modifier.testTag("reset_demo_data_button")
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Reset Sample Data")
                        }
                    }
                }
            }

            if (showResetDialog) {
                AlertDialog(
                    onDismissRequest = { showResetDialog = false },
                    title = { Text("Reset to Initial Demo State?", fontWeight = FontWeight.Bold) },
                    text = { Text("This will refresh all operational orders, missions, and hubs to the standard On The Way Logistics template.") },
                    confirmButton = {
                        Button(
                            onClick = {
                                onResetDemoData()
                                showResetDialog = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = BrightOrange)
                        ) {
                            Text("Reset")
                        }
                    },
                    dismissButton = {
                        OutlinedButton(onClick = { showResetDialog = false }) { Text("Cancel") }
                    }
                )
            }
        }
    }
}
