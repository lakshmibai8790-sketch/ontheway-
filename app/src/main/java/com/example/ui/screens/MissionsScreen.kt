package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun MissionsScreen(
    missions: List<Mission>,
    riders: List<Rider>,
    locations: List<FranchiseLocation>,
    searchQuery: String,
    statusFilter: MissionStatus?,
    priorityFilter: MissionPriority?,
    onSearchChange: (String) -> Unit,
    onStatusFilterChange: (MissionStatus?) -> Unit,
    onPriorityFilterChange: (MissionPriority?) -> Unit,
    onCreateMission: (Mission) -> Unit,
    onUpdateStatus: (missionId: String, newStatus: MissionStatus, notes: String) -> Unit,
    onAssignRider: (missionId: String, riderId: String, riderName: String) -> Unit,
    onCancelMission: (missionId: String, reason: String) -> Unit
) {
    var showCreateDialog by remember { mutableStateOf(false) }
    var selectedMissionForStatus by remember { mutableStateOf<Mission?>(null) }
    var selectedMissionForAssign by remember { mutableStateOf<Mission?>(null) }
    var selectedMissionForDetails by remember { mutableStateOf<Mission?>(null) }
    var selectedMissionForCancel by remember { mutableStateOf<Mission?>(null) }

    val filteredMissions = remember(missions, searchQuery, statusFilter, priorityFilter) {
        missions.filter { m ->
            val matchesSearch = searchQuery.isBlank() ||
                    m.id.contains(searchQuery, ignoreCase = true) ||
                    m.title.contains(searchQuery, ignoreCase = true) ||
                    (m.riderName ?: "").contains(searchQuery, ignoreCase = true) ||
                    m.shipmentRef.contains(searchQuery, ignoreCase = true)

            val matchesStatus = statusFilter == null || m.status == statusFilter
            val matchesPriority = priorityFilter == null || m.priority == priorityFilter

            matchesSearch && matchesStatus && matchesPriority
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .padding(16.dp)
    ) {
        // Header with Title and Create Action
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Delivery Missions",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = CorporateNavy
                )
                Text(
                    text = "${filteredMissions.size} of ${missions.size} missions displayed",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Button(
                onClick = { showCreateDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = BrightOrange),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("create_mission_button")
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Create Mission", fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchChange,
            placeholder = { Text("Search by Mission ID, Rider, or Shipment Ref...") },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = null, tint = BrightOrange)
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onSearchChange("") }) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear")
                    }
                }
            },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("search_mission_input"),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface
            )
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Status Filter Chips
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            item {
                FilterChip(
                    selected = statusFilter == null,
                    onClick = { onStatusFilterChange(null) },
                    label = { Text("All Statuses") }
                )
            }
            items(MissionStatus.values()) { status ->
                FilterChip(
                    selected = statusFilter == status,
                    onClick = { onStatusFilterChange(if (statusFilter == status) null else status) },
                    label = { Text(status.label) }
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Priority Filter Chips
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            item {
                FilterChip(
                    selected = priorityFilter == null,
                    onClick = { onPriorityFilterChange(null) },
                    label = { Text("All Priorities") }
                )
            }
            items(MissionPriority.values()) { priority ->
                FilterChip(
                    selected = priorityFilter == priority,
                    onClick = { onPriorityFilterChange(if (priorityFilter == priority) null else priority) },
                    label = { Text(priority.name) }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Missions List or Empty State
        if (filteredMissions.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Inbox,
                        contentDescription = null,
                        tint = SlateLight,
                        modifier = Modifier.size(56.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "No missions match current filters",
                        fontWeight = FontWeight.Bold,
                        color = CorporateNavy
                    )
                    Text(
                        text = "Try clearing search or filters to see all assigned missions.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredMissions, key = { it.id }) { mission ->
                    MissionCard(
                        mission = mission,
                        onUpdateStatusClick = { selectedMissionForStatus = mission },
                        onAssignRiderClick = { selectedMissionForAssign = mission },
                        onDetailsClick = { selectedMissionForDetails = mission },
                        onCancelClick = { selectedMissionForCancel = mission }
                    )
                }
            }
        }
    }

    // Dialog: Create Mission
    if (showCreateDialog) {
        CreateMissionDialog(
            riders = riders,
            locations = locations,
            onDismiss = { showCreateDialog = false },
            onConfirm = { newMission ->
                onCreateMission(newMission)
                showCreateDialog = false
            }
        )
    }

    // Dialog: Update Status
    selectedMissionForStatus?.let { mission ->
        UpdateStatusDialog(
            mission = mission,
            onDismiss = { selectedMissionForStatus = null },
            onConfirm = { status, notes ->
                onUpdateStatus(mission.id, status, notes)
                selectedMissionForStatus = null
            }
        )
    }

    // Dialog: Assign Rider
    selectedMissionForAssign?.let { mission ->
        AssignRiderDialog(
            mission = mission,
            riders = riders,
            onDismiss = { selectedMissionForAssign = null },
            onConfirm = { riderId, riderName ->
                onAssignRider(mission.id, riderId, riderName)
                selectedMissionForAssign = null
            }
        )
    }

    // Dialog: Mission Details
    selectedMissionForDetails?.let { mission ->
        MissionDetailsDialog(
            mission = mission,
            onDismiss = { selectedMissionForDetails = null }
        )
    }

    // Dialog: Cancel Mission
    selectedMissionForCancel?.let { mission ->
        CancelMissionDialog(
            mission = mission,
            onDismiss = { selectedMissionForCancel = null },
            onConfirm = { reason ->
                onCancelMission(mission.id, reason)
                selectedMissionForCancel = null
            }
        )
    }
}

@Composable
fun MissionCard(
    mission: Mission,
    onUpdateStatusClick: () -> Unit,
    onAssignRiderClick: () -> Unit,
    onDetailsClick: () -> Unit,
    onCancelClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("mission_card_${mission.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row: Mission ID, Type, Priority, Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = mission.id,
                        fontWeight = FontWeight.Black,
                        fontSize = 15.sp,
                        color = CorporateNavy
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = BrightOrange.copy(alpha = 0.12f)
                    ) {
                        Text(
                            text = mission.type.name,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = BrightOrange,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = when (mission.priority) {
                            MissionPriority.URGENT -> StatusFailed.copy(alpha = 0.15f)
                            MissionPriority.HIGH -> AmberAccent.copy(alpha = 0.25f)
                            else -> SlateBorder
                        }
                    ) {
                        Text(
                            text = mission.priority.name,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = when (mission.priority) {
                                MissionPriority.URGENT -> StatusFailed
                                MissionPriority.HIGH -> DarkOrange
                                else -> SlateMedium
                            },
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = when (mission.status) {
                        MissionStatus.DELIVERED -> StatusDelivered.copy(alpha = 0.15f)
                        MissionStatus.IN_TRANSIT, MissionStatus.OUT_FOR_DELIVERY -> StatusInTransit.copy(alpha = 0.15f)
                        MissionStatus.FAILED_DELIVERY, MissionStatus.CANCELLED -> StatusFailed.copy(alpha = 0.15f)
                        else -> AmberAccent.copy(alpha = 0.2f)
                    }
                ) {
                    Text(
                        text = mission.status.label,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = when (mission.status) {
                            MissionStatus.DELIVERED -> StatusDelivered
                            MissionStatus.IN_TRANSIT, MissionStatus.OUT_FOR_DELIVERY -> StatusInTransit
                            MissionStatus.FAILED_DELIVERY, MissionStatus.CANCELLED -> StatusFailed
                            else -> DarkOrange
                        },
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = mission.title,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = CorporateNavy
            )

            Text(
                text = "Ref: ${mission.shipmentRef} • Location: ${mission.branchLocation}",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Addresses
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
                Icon(
                    imageVector = Icons.Default.RadioButtonChecked,
                    contentDescription = null,
                    tint = StatusDelivered,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Pickup: ${mission.pickupAddress}",
                    fontSize = 11.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = BrightOrange,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Deliver: ${mission.deliveryAddress}",
                    fontSize = 11.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Rider and Package summary
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = BrightOrange,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (mission.riderName != null) "${mission.riderName} (${mission.riderId})" else "Not Assigned",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = CorporateNavy
                    )
                }

                Text(
                    text = "${mission.packageCount} pkgs • ${mission.distanceKm} km",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            Divider(color = SlateBorder)
            Spacer(modifier = Modifier.height(10.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onUpdateStatusClick,
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(vertical = 6.dp)
                ) {
                    Text("Status", fontSize = 11.sp)
                }

                OutlinedButton(
                    onClick = onAssignRiderClick,
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(vertical = 6.dp)
                ) {
                    Text("Assign", fontSize = 11.sp)
                }

                Button(
                    onClick = onDetailsClick,
                    colors = ButtonDefaults.buttonColors(containerColor = CorporateNavy),
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(vertical = 6.dp)
                ) {
                    Text("Details", fontSize = 11.sp)
                }

                if (mission.status !in listOf(MissionStatus.DELIVERED, MissionStatus.CANCELLED)) {
                    IconButton(
                        onClick = onCancelClick,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Cancel,
                            contentDescription = "Cancel Mission",
                            tint = StatusFailed
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CreateMissionDialog(
    riders: List<Rider>,
    locations: List<FranchiseLocation>,
    onDismiss: () -> Unit,
    onConfirm: (Mission) -> Unit
) {
    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    val now = sdf.format(Date())

    var title by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(MissionType.DELIVERY) }
    var selectedLocation by remember { mutableStateOf(locations.firstOrNull()?.name ?: "Central Sorting Hub") }
    var selectedRider by remember { mutableStateOf(riders.firstOrNull()) }
    var shipmentRef by remember { mutableStateOf("OTW-SHP-${(10000..99999).random()}") }
    var pickupAddress by remember { mutableStateOf("") }
    var deliveryAddress by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf(MissionPriority.MEDIUM) }
    var packageCount by remember { mutableStateOf("1") }
    var distanceKm by remember { mutableStateOf("6.5") }
    var deliveryNotes by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create New Logistics Mission", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Mission Title *") },
                    placeholder = { Text("e.g. Express E-Commerce Delivery") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = shipmentRef,
                    onValueChange = { shipmentRef = it },
                    label = { Text("Shipment / Consignment Ref *") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Type selector
                Text("Mission Type:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(MissionType.values()) { t ->
                        FilterChip(
                            selected = selectedType == t,
                            onClick = { selectedType = t },
                            label = { Text(t.name, fontSize = 11.sp) }
                        )
                    }
                }

                // Priority selector
                Text("Priority Level:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(MissionPriority.values()) { p ->
                        FilterChip(
                            selected = priority == p,
                            onClick = { priority = p },
                            label = { Text(p.name, fontSize = 11.sp) }
                        )
                    }
                }

                OutlinedTextField(
                    value = pickupAddress,
                    onValueChange = { pickupAddress = it },
                    label = { Text("Pickup Address *") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = deliveryAddress,
                    onValueChange = { deliveryAddress = it },
                    label = { Text("Delivery Address *") },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = packageCount,
                        onValueChange = { packageCount = it },
                        label = { Text("Packages") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = distanceKm,
                        onValueChange = { distanceKm = it },
                        label = { Text("Distance (km)") },
                        modifier = Modifier.weight(1f)
                    )
                }

                OutlinedTextField(
                    value = deliveryNotes,
                    onValueChange = { deliveryNotes = it },
                    label = { Text("Delivery Notes") },
                    placeholder = { Text("Special instructions for rider...") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val missionId = "MSN-2026-${(100..999).random()}"
                    val mission = Mission(
                        id = missionId,
                        title = title.ifBlank { "Delivery Task $missionId" },
                        type = selectedType,
                        branchLocation = selectedLocation,
                        riderId = selectedRider?.id,
                        riderName = selectedRider?.name,
                        shipmentRef = shipmentRef,
                        pickupAddress = pickupAddress.ifBlank { "Central Warehouse, Andheri" },
                        deliveryAddress = deliveryAddress.ifBlank { "Destination Customer Gate, Powai" },
                        createdAt = now,
                        scheduledPickupTime = now,
                        scheduledDeliveryTime = now,
                        priority = priority,
                        status = if (selectedRider != null) MissionStatus.ASSIGNED else MissionStatus.CREATED,
                        packageCount = packageCount.toIntOrNull() ?: 1,
                        distanceKm = distanceKm.toDoubleOrNull() ?: 5.0,
                        deliveryNotes = deliveryNotes,
                        statusHistory = listOf(
                            MissionStatusHistory(
                                status = if (selectedRider != null) MissionStatus.ASSIGNED else MissionStatus.CREATED,
                                timestamp = now,
                                notes = "Mission created in portal"
                            )
                        )
                    )
                    onConfirm(mission)
                },
                colors = ButtonDefaults.buttonColors(containerColor = BrightOrange)
            ) {
                Text("Create Mission")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun UpdateStatusDialog(
    mission: Mission,
    onDismiss: () -> Unit,
    onConfirm: (MissionStatus, String) -> Unit
) {
    var selectedStatus by remember { mutableStateOf(mission.status) }
    var notes by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Update Mission Status: ${mission.id}", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Current Status: ${mission.status.label}", fontWeight = FontWeight.Medium)

                Text("Select New Status:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                LazyColumn(modifier = Modifier.height(200.dp)) {
                    items(MissionStatus.values()) { status ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedStatus = status }
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedStatus == status,
                                onClick = { selectedStatus = status }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(status.label, fontSize = 14.sp)
                        }
                    }
                }

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Status Change Notes") },
                    placeholder = { Text("e.g. Package collected from consignor") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(selectedStatus, notes) },
                colors = ButtonDefaults.buttonColors(containerColor = BrightOrange)
            ) {
                Text("Update Status")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun AssignRiderDialog(
    mission: Mission,
    riders: List<Rider>,
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit
) {
    var selectedRider by remember { mutableStateOf(riders.firstOrNull()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Assign Rider to ${mission.id}", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Choose available delivery rider:")
                LazyColumn(modifier = Modifier.height(200.dp)) {
                    items(riders) { rider ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedRider = rider }
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedRider?.id == rider.id,
                                onClick = { selectedRider = rider }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text("${rider.name} (${rider.id})", fontWeight = FontWeight.Bold)
                                Text("${rider.vehicleType} • Rating: ${rider.rating} ★", fontSize = 11.sp, color = SlateMedium)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    selectedRider?.let { onConfirm(it.id, it.name) }
                },
                enabled = selectedRider != null,
                colors = ButtonDefaults.buttonColors(containerColor = BrightOrange)
            ) {
                Text("Assign")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun MissionDetailsDialog(
    mission: Mission,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Mission Details: ${mission.id}", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Title: ${mission.title}", fontWeight = FontWeight.Bold)
                Text("Status: ${mission.status.label} • Priority: ${mission.priority.name}")
                Text("Shipment Ref: ${mission.shipmentRef}")
                Text("Location: ${mission.branchLocation}")
                Text("Assigned Rider: ${mission.riderName ?: "None"} (${mission.riderId ?: "-"})")
                Text("Pickup: ${mission.pickupAddress}")
                Text("Delivery: ${mission.deliveryAddress}")
                Text("Created: ${mission.createdAt}")
                if (mission.actualDeliveryTime != null) {
                    Text("Delivered At: ${mission.actualDeliveryTime}", color = StatusDelivered, fontWeight = FontWeight.Bold)
                }
                if (mission.podReference != null) {
                    Text("POD Reference: ${mission.podReference}", fontWeight = FontWeight.Bold)
                }
                if (mission.deliveryNotes.isNotBlank()) {
                    Text("Notes: ${mission.deliveryNotes}")
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text("Status History Trail:", fontWeight = FontWeight.Bold)
                if (mission.statusHistory.isEmpty()) {
                    Text("No status transition history recorded.", fontSize = 12.sp, color = SlateMedium)
                } else {
                    mission.statusHistory.forEach { h ->
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(h.status.label, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    Text(h.timestamp, fontSize = 10.sp, color = SlateMedium)
                                }
                                if (h.notes.isNotBlank()) {
                                    Text(h.notes, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) { Text("Close") }
        }
    )
}

@Composable
fun CancelMissionDialog(
    mission: Mission,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var reason by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Cancel Mission: ${mission.id}?", fontWeight = FontWeight.Bold, color = StatusFailed) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Are you sure you want to cancel this delivery mission? This status change will be recorded in the audit trail.")
                OutlinedTextField(
                    value = reason,
                    onValueChange = { reason = it },
                    label = { Text("Cancellation Reason") },
                    placeholder = { Text("e.g. Consignor cancelled booking") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(reason.ifBlank { "Cancelled by franchise manager" }) },
                colors = ButtonDefaults.buttonColors(containerColor = StatusFailed)
            ) {
                Text("Confirm Cancellation")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) { Text("Keep Mission") }
        }
    )
}
