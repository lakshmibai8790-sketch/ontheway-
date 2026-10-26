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
import com.example.data.model.FranchiseLocation
import com.example.data.model.FranchiseProfile
import com.example.data.model.LocationStatus
import com.example.ui.theme.*

@Composable
fun LocationsProfileScreen(
    profile: FranchiseProfile,
    locations: List<FranchiseLocation>,
    onUpdateProfile: (FranchiseProfile) -> Unit,
    onAddLocation: (FranchiseLocation) -> Unit,
    onUpdateLocation: (FranchiseLocation) -> Unit,
    onDeleteLocation: (String) -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0 = Locations, 1 = Franchise Profile
    var showAddLocationDialog by remember { mutableStateOf(false) }
    var locationToEdit by remember { mutableStateOf<FranchiseLocation?>(null) }
    var locationToDelete by remember { mutableStateOf<FranchiseLocation?>(null) }

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
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("Hub Locations (${locations.size})", fontWeight = FontWeight.Bold) },
                icon = { Icon(Icons.Default.Store, contentDescription = null) }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("Franchise Profile", fontWeight = FontWeight.Bold) },
                icon = { Icon(Icons.Default.Business, contentDescription = null) }
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (selectedTab == 0) {
            // LOCATIONS MANAGEMENT TAB
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Operational Sorting Hubs",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = CorporateNavy
                )

                Button(
                    onClick = { showAddLocationDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = BrightOrange),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("add_location_button")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Location")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(locations, key = { it.id }) { loc ->
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
                                    Text(loc.name, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = CorporateNavy)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Surface(
                                        color = BrightOrange.copy(alpha = 0.12f),
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text(loc.hubCode, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = BrightOrange, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                    }
                                }

                                Surface(
                                    color = when (loc.status) {
                                        LocationStatus.ACTIVE -> StatusDelivered.copy(alpha = 0.15f)
                                        LocationStatus.PENDING -> AmberAccent.copy(alpha = 0.2f)
                                        LocationStatus.INACTIVE -> SlateBorder
                                    },
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = loc.status.name,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = when (loc.status) {
                                            LocationStatus.ACTIVE -> StatusDelivered
                                            LocationStatus.PENDING -> DarkOrange
                                            LocationStatus.INACTIVE -> SlateMedium
                                        },
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = "Address: ${loc.address}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Text(
                                text = "Manager: ${loc.manager} • Phone: ${loc.phone} • Capacity: ${loc.capacity} parcels/day",
                                fontSize = 11.sp,
                                color = SlateMedium,
                                modifier = Modifier.padding(top = 2.dp)
                            )

                            Spacer(modifier = Modifier.height(10.dp))
                            Divider(color = SlateBorder)
                            Spacer(modifier = Modifier.height(6.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedButton(
                                    onClick = { locationToEdit = loc },
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Edit", fontSize = 11.sp)
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                OutlinedButton(
                                    onClick = { locationToDelete = loc },
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = StatusFailed),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Remove", fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // FRANCHISE PROFILE TAB
            var name by remember { mutableStateOf(profile.name) }
            var contactPerson by remember { mutableStateOf(profile.contactPerson) }
            var phone by remember { mutableStateOf(profile.phone) }
            var email by remember { mutableStateOf(profile.email) }
            var state by remember { mutableStateOf(profile.state) }
            var district by remember { mutableStateOf(profile.district) }
            var city by remember { mutableStateOf(profile.city) }
            var pinCode by remember { mutableStateOf(profile.pinCode) }
            var officeAddress by remember { mutableStateOf(profile.officeAddress) }
            var remarks by remember { mutableStateOf(profile.remarks) }
            var gstin by remember { mutableStateOf(profile.gstin) }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Franchise Identification", fontWeight = FontWeight.Bold, color = CorporateNavy)

                        // Read-only Franchise ID
                        OutlinedTextField(
                            value = profile.franchiseId,
                            onValueChange = {},
                            label = { Text("Franchise Identifier (Protected)") },
                            readOnly = true,
                            enabled = false,
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text("Franchise Operating Name *") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = contactPerson,
                            onValueChange = { contactPerson = it },
                            label = { Text("Contact Person / Manager *") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = phone,
                                onValueChange = { phone = it },
                                label = { Text("Phone") },
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = email,
                                onValueChange = { email = it },
                                label = { Text("Email") },
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = city,
                                onValueChange = { city = it },
                                label = { Text("City") },
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = district,
                                onValueChange = { district = it },
                                label = { Text("District") },
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = state,
                                onValueChange = { state = it },
                                label = { Text("State") },
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = pinCode,
                                onValueChange = { pinCode = it },
                                label = { Text("PIN Code") },
                                modifier = Modifier.weight(1f)
                            )
                        }

                        OutlinedTextField(
                            value = officeAddress,
                            onValueChange = { officeAddress = it },
                            label = { Text("Complete Office / Hub Address") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = gstin,
                            onValueChange = { gstin = it },
                            label = { Text("GSTIN Tax Identifier") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = remarks,
                            onValueChange = { remarks = it },
                            label = { Text("Operational Remarks") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = {
                                val updated = profile.copy(
                                    name = name,
                                    contactPerson = contactPerson,
                                    phone = phone,
                                    email = email,
                                    state = state,
                                    district = district,
                                    city = city,
                                    pinCode = pinCode,
                                    officeAddress = officeAddress,
                                    remarks = remarks,
                                    gstin = gstin
                                )
                                onUpdateProfile(updated)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = BrightOrange),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("save_franchise_profile_button")
                        ) {
                            Icon(Icons.Default.Save, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Save Franchise Profile", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    // Dialog: Add Location
    if (showAddLocationDialog) {
        var name by remember { mutableStateOf("") }
        var hubCode by remember { mutableStateOf("OTW-B0${(7..99).random()}") }
        var manager by remember { mutableStateOf("") }
        var phone by remember { mutableStateOf("+91 ") }
        var address by remember { mutableStateOf("") }
        var capacity by remember { mutableStateOf("2500") }

        AlertDialog(
            onDismissRequest = { showAddLocationDialog = false },
            title = { Text("Register New Hub Location", fontWeight = FontWeight.Bold) },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Location Name *") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = hubCode, onValueChange = { hubCode = it }, label = { Text("Hub Code *") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = manager, onValueChange = { manager = it }, label = { Text("Manager Name") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Phone") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = address, onValueChange = { address = it }, label = { Text("Full Address") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = capacity, onValueChange = { capacity = it }, label = { Text("Parcel Capacity") }, modifier = Modifier.fillMaxWidth())
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (name.isNotBlank()) {
                            onAddLocation(
                                FranchiseLocation(
                                    id = "LOC-${(10..99).random()}",
                                    name = name,
                                    hubCode = hubCode,
                                    manager = manager.ifBlank { "Hub Operations Lead" },
                                    phone = phone,
                                    address = address.ifBlank { "Industrial Area, Mumbai" },
                                    capacity = capacity.toIntOrNull() ?: 2000
                                )
                            )
                            showAddLocationDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BrightOrange)
                ) {
                    Text("Add Location")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showAddLocationDialog = false }) { Text("Cancel") }
            }
        )
    }

    // Dialog: Edit Location
    locationToEdit?.let { loc ->
        var name by remember { mutableStateOf(loc.name) }
        var manager by remember { mutableStateOf(loc.manager) }
        var phone by remember { mutableStateOf(loc.phone) }
        var address by remember { mutableStateOf(loc.address) }
        var capacity by remember { mutableStateOf("${loc.capacity}") }
        var status by remember { mutableStateOf(loc.status) }

        AlertDialog(
            onDismissRequest = { locationToEdit = null },
            title = { Text("Edit Location: ${loc.hubCode}", fontWeight = FontWeight.Bold) },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = manager, onValueChange = { manager = it }, label = { Text("Manager") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Phone") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = address, onValueChange = { address = it }, label = { Text("Address") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = capacity, onValueChange = { capacity = it }, label = { Text("Capacity") }, modifier = Modifier.fillMaxWidth())

                    Text("Status:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        LocationStatus.values().forEach { st ->
                            FilterChip(
                                selected = status == st,
                                onClick = { status = st },
                                label = { Text(st.name, fontSize = 11.sp) }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onUpdateLocation(
                            loc.copy(
                                name = name,
                                manager = manager,
                                phone = phone,
                                address = address,
                                capacity = capacity.toIntOrNull() ?: loc.capacity,
                                status = status
                            )
                        )
                        locationToEdit = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BrightOrange)
                ) {
                    Text("Save Changes")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { locationToEdit = null }) { Text("Cancel") }
            }
        )
    }

    // Dialog: Delete Location Confirmation
    locationToDelete?.let { loc ->
        AlertDialog(
            onDismissRequest = { locationToDelete = null },
            title = { Text("Remove Location: ${loc.name}?", fontWeight = FontWeight.Bold, color = StatusFailed) },
            text = { Text("Are you sure you want to remove hub ${loc.hubCode}? All active orders must be reassigned.") },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteLocation(loc.id)
                        locationToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = StatusFailed)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { locationToDelete = null }) { Text("Cancel") }
            }
        )
    }
}
