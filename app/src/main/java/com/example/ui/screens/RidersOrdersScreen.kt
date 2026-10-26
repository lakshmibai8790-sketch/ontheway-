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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.OrderRecord
import com.example.data.model.PayoutSettings
import com.example.data.model.Rider
import com.example.data.model.RiderStatus
import com.example.ui.theme.*
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun RidersOrdersScreen(
    orders: List<OrderRecord>,
    riders: List<Rider>,
    payoutSettings: PayoutSettings,
    onAddOrder: (OrderRecord) -> Unit,
    onAddRider: (Rider) -> Unit
) {
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("en", "IN"))
    var selectedTab by remember { mutableIntStateOf(0) } // 0 = Orders & Payouts, 1 = Riders Directory
    var showAddOrderDialog by remember { mutableStateOf(false) }
    var showAddRiderDialog by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .padding(16.dp)
    ) {
        // Tab selector
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = BrightOrange
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("Daily Orders & Payouts", fontWeight = FontWeight.Bold) },
                icon = { Icon(Icons.Default.ReceiptLong, contentDescription = null) }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("Riders Directory (${riders.size})", fontWeight = FontWeight.Bold) },
                icon = { Icon(Icons.Default.TwoWheeler, contentDescription = null) }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (selectedTab == 0) {
            // ORDERS & PAYOUTS TAB
            val filteredOrders = remember(orders, searchQuery) {
                if (searchQuery.isBlank()) orders else {
                    orders.filter {
                        it.riderName.contains(searchQuery, ignoreCase = true) ||
                                it.riderId.contains(searchQuery, ignoreCase = true) ||
                                it.orderDate.contains(searchQuery)
                    }
                }
            }

            // Action row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search rider or date...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = BrightOrange) },
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.width(10.dp))

                Button(
                    onClick = { showAddOrderDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = BrightOrange),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("add_order_entry_button")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Log Orders")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Configured Rate Banner
            Surface(
                color = CorporateNavy,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Configured Payout Rates:",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "Primary: ₹${payoutSettings.primaryRate} • Club: ₹${payoutSettings.clubRate} • Large: ₹${payoutSettings.largeRate}",
                        color = BrightOrange,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Orders Table / List
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredOrders.take(50), key = { it.id }) { order ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Person, contentDescription = null, tint = BrightOrange, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(order.riderName, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = CorporateNavy)
                                    Text(" (${order.riderId})", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }

                                Surface(
                                    color = BrightOrange.copy(alpha = 0.12f),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        order.orderDate,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = BrightOrange,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Prim: ${order.primaryOrders} (₹${order.primaryAmount.toInt()}) • Club: ${order.clubOrders} (₹${order.clubAmount.toInt()}) • Lrg: ${order.largeOrders} (₹${order.largeAmount.toInt()})",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Text(
                                    text = "Total: ${order.totalOrders} pkgs",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = CorporateNavy
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))
                            Divider(color = SlateBorder)
                            Spacer(modifier = Modifier.height(6.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "SDD Incentive: ₹${order.sddPayout.toInt()}",
                                    fontSize = 11.sp,
                                    color = SlateMedium
                                )

                                Text(
                                    text = "Total Payout: ${currencyFormat.format(order.totalPayout)}",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Black,
                                    color = StatusDelivered
                                )
                            }
                        }
                    }
                }
            }
        } else {
            // RIDERS DIRECTORY TAB
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Active Logistics Fleet",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = CorporateNavy
                )

                Button(
                    onClick = { showAddRiderDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = BrightOrange),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("add_rider_button")
                ) {
                    Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Rider")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(riders, key = { it.id }) { rider ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
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
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(CorporateNavy),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = rider.name.take(1),
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(rider.name, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = CorporateNavy)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        color = BrightOrange.copy(alpha = 0.12f),
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text(rider.id, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = BrightOrange, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                                    }
                                }

                                Text(
                                    text = "${rider.vehicleType} • ${rider.vehicleNumber}",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Text(
                                    text = "Phone: ${rider.phone} • Rating: ${rider.rating} ★",
                                    fontSize = 11.sp,
                                    color = SlateMedium
                                )
                            }

                            Surface(
                                color = when (rider.status) {
                                    RiderStatus.ACTIVE -> StatusDelivered.copy(alpha = 0.15f)
                                    RiderStatus.ON_DELIVERY -> StatusInTransit.copy(alpha = 0.15f)
                                    RiderStatus.OFF_DUTY -> SlateBorder
                                },
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = when (rider.status) {
                                        RiderStatus.ACTIVE -> "Active"
                                        RiderStatus.ON_DELIVERY -> "On Delivery"
                                        RiderStatus.OFF_DUTY -> "Off Duty"
                                    },
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = when (rider.status) {
                                        RiderStatus.ACTIVE -> StatusDelivered
                                        RiderStatus.ON_DELIVERY -> StatusInTransit
                                        RiderStatus.OFF_DUTY -> SlateMedium
                                    },
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Dialog: Add Order Record
    if (showAddOrderDialog) {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        var selectedRider by remember { mutableStateOf(riders.firstOrNull()) }
        var orderDate by remember { mutableStateOf(sdf.format(Date())) }
        var primaryCount by remember { mutableStateOf("15") }
        var clubCount by remember { mutableStateOf("6") }
        var largeCount by remember { mutableStateOf("2") }
        var sddPayout by remember { mutableStateOf("50") }

        AlertDialog(
            onDismissRequest = { showAddOrderDialog = false },
            title = { Text("Log Daily Orders & Payout", fontWeight = FontWeight.Bold) },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text("Select Rider:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    riders.forEach { r ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedRider = r }
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(selected = selectedRider?.id == r.id, onClick = { selectedRider = r })
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("${r.name} (${r.id})", fontSize = 13.sp)
                        }
                    }

                    OutlinedTextField(
                        value = orderDate,
                        onValueChange = { orderDate = it },
                        label = { Text("Order Date (YYYY-MM-DD)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = primaryCount,
                        onValueChange = { primaryCount = it },
                        label = { Text("Primary Orders (₹${payoutSettings.primaryRate})") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = clubCount,
                        onValueChange = { clubCount = it },
                        label = { Text("Club Orders (₹${payoutSettings.clubRate})") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = largeCount,
                        onValueChange = { largeCount = it },
                        label = { Text("Large Orders (₹${payoutSettings.largeRate})") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = sddPayout,
                        onValueChange = { sddPayout = it },
                        label = { Text("SDD Incentive (₹)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        selectedRider?.let { r ->
                            val ord = OrderRecord(
                                id = "ORD-${orderDate.replace("-", "")}-${r.id}-${System.currentTimeMillis() % 1000}",
                                riderId = r.id,
                                riderName = r.name,
                                orderDate = orderDate,
                                primaryOrders = primaryCount.toIntOrNull() ?: 0,
                                clubOrders = clubCount.toIntOrNull() ?: 0,
                                largeOrders = largeCount.toIntOrNull() ?: 0,
                                primaryRate = payoutSettings.primaryRate,
                                clubRate = payoutSettings.clubRate,
                                largeRate = payoutSettings.largeRate,
                                sddPayout = sddPayout.toDoubleOrNull() ?: 50.0
                            )
                            onAddOrder(ord)
                            showAddOrderDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BrightOrange)
                ) {
                    Text("Save Record")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showAddOrderDialog = false }) { Text("Cancel") }
            }
        )
    }

    // Dialog: Add Rider
    if (showAddRiderDialog) {
        var name by remember { mutableStateOf("") }
        var phone by remember { mutableStateOf("") }
        var vehicleType by remember { mutableStateOf("Motorbike") }
        var vehicleNumber by remember { mutableStateOf("MH-02-") }

        AlertDialog(
            onDismissRequest = { showAddRiderDialog = false },
            title = { Text("Register New Delivery Rider", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Full Name *") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Phone Number *") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = vehicleType,
                        onValueChange = { vehicleType = it },
                        label = { Text("Vehicle Type") },
                        placeholder = { Text("e.g. EV Scooter, Motorbike") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = vehicleNumber,
                        onValueChange = { vehicleNumber = it },
                        label = { Text("Vehicle Registration No.") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (name.isNotBlank() && phone.isNotBlank()) {
                            val newRider = Rider(
                                id = "RD-${(107..999).random()}",
                                name = name,
                                phone = phone,
                                vehicleType = vehicleType.ifBlank { "Motorbike" },
                                vehicleNumber = vehicleNumber,
                                status = RiderStatus.ACTIVE
                            )
                            onAddRider(newRider)
                            showAddRiderDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BrightOrange)
                ) {
                    Text("Add Rider")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showAddRiderDialog = false }) { Text("Cancel") }
            }
        )
    }
}
