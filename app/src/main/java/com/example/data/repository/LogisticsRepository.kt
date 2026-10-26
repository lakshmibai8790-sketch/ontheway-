package com.example.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.example.data.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class LogisticsRepository(private val context: Context) {

    private val prefs = context.getSharedPreferences("otw_logistics_prefs", Context.MODE_PRIVATE)

    // Current logged-in user
    private val _currentUser = MutableStateFlow<UserAccount?>(null)
    val currentUser: StateFlow<UserAccount?> = _currentUser.asStateFlow()

    // Franchise Profile
    private val _profile = MutableStateFlow(FranchiseProfile())
    val profile: StateFlow<FranchiseProfile> = _profile.asStateFlow()

    // Locations
    private val _locations = MutableStateFlow<List<FranchiseLocation>>(emptyList())
    val locations: StateFlow<List<FranchiseLocation>> = _locations.asStateFlow()

    // Riders
    private val _riders = MutableStateFlow<List<Rider>>(emptyList())
    val riders: StateFlow<List<Rider>> = _riders.asStateFlow()

    // Orders
    private val _orders = MutableStateFlow<List<OrderRecord>>(emptyList())
    val orders: StateFlow<List<OrderRecord>> = _orders.asStateFlow()

    // Missions
    private val _missions = MutableStateFlow<List<Mission>>(emptyList())
    val missions: StateFlow<List<Mission>> = _missions.asStateFlow()

    // Notifications
    private val _notifications = MutableStateFlow<List<NotificationItem>>(emptyList())
    val notifications: StateFlow<List<NotificationItem>> = _notifications.asStateFlow()

    // Payout Settings
    private val _payoutSettings = MutableStateFlow(
        PayoutSettings(
            primaryRate = prefs.getFloat("rate_primary", 18.0f).toDouble(),
            clubRate = prefs.getFloat("rate_club", 6.0f).toDouble(),
            largeRate = prefs.getFloat("rate_large", 25.0f).toDouble()
        )
    )
    val payoutSettings: StateFlow<PayoutSettings> = _payoutSettings.asStateFlow()

    init {
        seedInitialData()
        checkExistingSession()
    }

    private fun checkExistingSession() {
        val savedFranchiseId = prefs.getString("active_franchise_id", null)
        if (savedFranchiseId != null) {
            val selfiePath = prefs.getString("selfie_path_$savedFranchiseId", null)
            val email = prefs.getString("email_$savedFranchiseId", "franchise@onthewaylogistics.in") ?: "franchise@onthewaylogistics.in"
            val name = prefs.getString("name_$savedFranchiseId", "On The Way Logistics - West Hub") ?: "On The Way Logistics - West Hub"
            _currentUser.value = UserAccount(
                franchiseId = savedFranchiseId,
                email = email,
                franchiseName = name,
                selfieImagePath = selfiePath
            )
        }
    }

    fun authenticate(franchiseIdOrEmail: String, password: String):Result<Boolean> {
        val trimmed = franchiseIdOrEmail.trim()
        val defaultId = "FR-000001"
        val defaultEmail = "franchise@onthewaylogistics.in"

        // Allow demo user or matching custom registered users
        val isValid = (trimmed.equals(defaultId, ignoreCase = true) || trimmed.equals(defaultEmail, ignoreCase = true) || trimmed.startsWith("FR-")) &&
                (password == "franchise123" || password.length >= 6)

        if (!isValid) {
            return Result.failure(Exception("Invalid Franchise ID or Password. Try FR-000001 / franchise123"))
        }

        val fid = if (trimmed.startsWith("FR-", ignoreCase = true)) trimmed.uppercase() else defaultId
        val selfiePath = prefs.getString("selfie_path_$fid", null)

        val user = UserAccount(
            franchiseId = fid,
            email = if (trimmed.contains("@")) trimmed else defaultEmail,
            franchiseName = "On The Way Logistics - $fid",
            selfieImagePath = selfiePath
        )

        _currentUser.value = user
        prefs.edit().putString("active_franchise_id", fid).apply()

        // Returns true if selfie already exists, false if selfie required
        return Result.success(!selfiePath.isNullOrEmpty() && File(selfiePath).exists())
    }

    fun saveSelfie(bitmap: Bitmap): String {
        val user = _currentUser.value ?: throw IllegalStateException("No active user session")
        val dir = File(context.filesDir, "selfies").apply { mkdirs() }
        val file = File(dir, "selfie_${user.franchiseId}_${System.currentTimeMillis()}.jpg")

        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
        }

        val path = file.absolutePath
        prefs.edit().putString("selfie_path_${user.franchiseId}", path).apply()

        val updatedUser = user.copy(selfieImagePath = path)
        _currentUser.value = updatedUser

        // Add a notification about selfie update
        addNotification(
            title = "Profile Selfie Updated",
            message = "Your franchise security selfie has been synchronized across all avatars.",
            category = "SYSTEM"
        )

        return path
    }

    fun logout() {
        prefs.edit().remove("active_franchise_id").apply()
        _currentUser.value = null
    }

    // Profile updates
    fun updateProfile(newProfile: FranchiseProfile) {
        _profile.value = newProfile
        addNotification("Profile Updated", "Franchise details for ${newProfile.name} were successfully saved.", "SYSTEM")
    }

    // Location management
    fun addLocation(location: FranchiseLocation) {
        _locations.value = listOf(location) + _locations.value
        addNotification("New Location Added", "Hub ${location.name} (${location.hubCode}) is now registered.", "LOCATION")
    }

    fun updateLocation(location: FranchiseLocation) {
        _locations.value = _locations.value.map { if (it.id == location.id) location else it }
    }

    fun deleteLocation(locationId: String) {
        _locations.value = _locations.value.filter { it.id != locationId }
    }

    // Rider management
    fun addRider(rider: Rider) {
        _riders.value = listOf(rider) + _riders.value
    }

    // Order management
    fun addOrder(order: OrderRecord) {
        _orders.value = listOf(order) + _orders.value
        addNotification("Order Recorded", "${order.totalOrders} deliveries logged for ${order.riderName}.", "ORDER")
    }

    fun updatePayoutRates(primary: Double, club: Double, large: Double) {
        _payoutSettings.value = PayoutSettings(primary, club, large)
        prefs.edit()
            .putFloat("rate_primary", primary.toFloat())
            .putFloat("rate_club", club.toFloat())
            .putFloat("rate_large", large.toFloat())
            .apply()
    }

    // Mission management
    fun addMission(mission: Mission) {
        _missions.value = listOf(mission) + _missions.value
        addNotification(
            title = "Mission Created",
            message = "${mission.id}: Assigned to ${mission.riderName ?: "Unassigned"} (${mission.priority})",
            category = "MISSION"
        )
    }

    fun updateMissionStatus(missionId: String, newStatus: MissionStatus, notes: String = "") {
        val now = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())
        _missions.value = _missions.value.map { mission ->
            if (mission.id == missionId) {
                val updatedHistory = mission.statusHistory + MissionStatusHistory(newStatus, now, notes)
                val actualPickup = if (newStatus == MissionStatus.PICKED_UP || newStatus == MissionStatus.IN_TRANSIT) {
                    mission.actualPickupTime ?: now
                } else mission.actualPickupTime

                val actualDelivery = if (newStatus == MissionStatus.DELIVERED) {
                    now
                } else mission.actualDeliveryTime

                mission.copy(
                    status = newStatus,
                    statusHistory = updatedHistory,
                    actualPickupTime = actualPickup,
                    actualDeliveryTime = actualDelivery
                )
            } else mission
        }

        addNotification(
            title = "Mission $missionId Status: ${newStatus.label}",
            message = if (notes.isNotEmpty()) notes else "Status updated to ${newStatus.label}",
            category = "MISSION"
        )
    }

    fun assignRider(missionId: String, riderId: String, riderName: String) {
        val now = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())
        _missions.value = _missions.value.map { mission ->
            if (mission.id == missionId) {
                val newHistory = mission.statusHistory + MissionStatusHistory(
                    MissionStatus.ASSIGNED,
                    now,
                    "Assigned to $riderName ($riderId)"
                )
                mission.copy(
                    riderId = riderId,
                    riderName = riderName,
                    status = if (mission.status == MissionStatus.CREATED) MissionStatus.ASSIGNED else mission.status,
                    statusHistory = newHistory
                )
            } else mission
        }
    }

    fun cancelMission(missionId: String, reason: String) {
        updateMissionStatus(missionId, MissionStatus.CANCELLED, "Cancellation reason: $reason")
    }

    // Notifications
    fun addNotification(title: String, message: String, category: String) {
        val now = SimpleDateFormat("dd MMM, HH:mm", Locale.getDefault()).format(Date())
        val item = NotificationItem(
            id = UUID.randomUUID().toString(),
            title = title,
            message = message,
            timestamp = now,
            category = category,
            isRead = false
        )
        _notifications.value = listOf(item) + _notifications.value
    }

    fun markNotificationAsRead(id: String) {
        _notifications.value = _notifications.value.map {
            if (it.id == id) it.copy(isRead = true) else it
        }
    }

    fun markAllNotificationsAsRead() {
        _notifications.value = _notifications.value.map { it.copy(isRead = true) }
    }

    fun resetToDemoData() {
        seedInitialData()
        addNotification("System Reset", "Demo data has been refreshed to initial template state.", "SYSTEM")
    }

    private fun seedInitialData() {
        // Sample Locations
        _locations.value = listOf(
            FranchiseLocation("LOC-01", "Andheri East Central Sorting Hub", "OTW-B01", "Rajesh Varma", "+91 98200 11223", "Plot 14, MIDC Central Road, Andheri East, Mumbai", LocationStatus.ACTIVE, 3500),
            FranchiseLocation("LOC-02", "Bandra Kurla Complex (BKC) Express Depot", "OTW-B02", "Ananya Deshmukh", "+91 98200 44556", "Unit 3B, Trade Link Building, BKC, Mumbai", LocationStatus.ACTIVE, 2800),
            FranchiseLocation("LOC-03", "Powai Tech Park Delivery Centre", "OTW-B03", "Farhan Akhtar", "+91 98200 77889", "Hiranandani Business Park, Powai, Mumbai", LocationStatus.ACTIVE, 2200),
            FranchiseLocation("LOC-04", "Thane West Secondary Distribution Hub", "OTW-B04", "Sunil Pawar", "+91 98200 99001", "Ghodbunder Road, Majiwada, Thane", LocationStatus.ACTIVE, 1900),
            FranchiseLocation("LOC-05", "Navi Mumbai Vashi Logistics Center", "OTW-B05", "Kavita Patil", "+91 98200 33445", "Sector 19, Vashi, Navi Mumbai", LocationStatus.ACTIVE, 2400),
            FranchiseLocation("LOC-06", "Kandivali Industrial Area Extension", "OTW-B06", "Dinesh Mehta", "+91 98200 55667", "Charkop Naka, Kandivali West, Mumbai", LocationStatus.PENDING, 1500)
        )

        // Sample Riders
        _riders.value = listOf(
            Rider("RD-101", "Amit Kumar", "+91 91234 56789", "Motorbike (Bajaj Pulsar)", "MH-02-AB-1234", RiderStatus.ACTIVE, 4.9, "2024-01-10"),
            Rider("RD-102", "Rahul Sharma", "+91 92345 67890", "EV Scooter (Ather 450X)", "MH-02-CD-5678", RiderStatus.ON_DELIVERY, 4.8, "2024-02-14"),
            Rider("RD-103", "Sachin Patil", "+91 93456 78901", "Motorbike (Hero Splendor)", "MH-03-EF-9012", RiderStatus.ACTIVE, 4.7, "2024-03-01"),
            Rider("RD-104", "Mohammed Zaid", "+91 94567 89012", "EV Scooter (Ola S1)", "MH-02-GH-3456", RiderStatus.ON_DELIVERY, 4.9, "2024-04-20"),
            Rider("RD-105", "Vikas Shinde", "+91 95678 90123", "Motorbike (Honda Shine)", "MH-04-JK-7890", RiderStatus.ACTIVE, 4.6, "2024-05-11"),
            Rider("RD-106", "Deepak Verma", "+91 96789 01234", "Cargo Three-Wheeler", "MH-02-LM-2345", RiderStatus.OFF_DUTY, 4.8, "2024-06-05")
        )

        // Seed 30 days of realistic orders for 15-day and weekly reports
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val cal = Calendar.getInstance()
        val orderList = mutableListOf<OrderRecord>()
        val riderList = _riders.value

        for (dayOffset in 0..29) {
            val dateCal = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -dayOffset) }
            val dateStr = sdf.format(dateCal.time)

            // Random daily orders per rider
            riderList.forEachIndexed { index, rider ->
                val seed = (dayOffset * 7 + index * 13) % 20
                val primary = 12 + (seed % 14)
                val club = 5 + (seed % 8)
                val large = 2 + (seed % 5)
                orderList.add(
                    OrderRecord(
                        id = "ORD-${dateStr.replace("-", "")}-${rider.id}",
                        riderId = rider.id,
                        riderName = rider.name,
                        orderDate = dateStr,
                        primaryOrders = primary,
                        clubOrders = club,
                        largeOrders = large,
                        primaryRate = 18.0,
                        clubRate = 6.0,
                        largeRate = 25.0,
                        sddPayout = if (primary > 18) 75.0 else 50.0
                    )
                )
            }
        }
        _orders.value = orderList

        // Seed Sample Missions
        val todayStr = sdf.format(Date())
        _missions.value = listOf(
            Mission(
                id = "MSN-2026-081",
                title = "High-Value Electronic Consignment Delivery",
                type = MissionType.DELIVERY,
                branchLocation = "Andheri East Central Sorting Hub",
                riderId = "RD-101",
                riderName = "Amit Kumar",
                shipmentRef = "OTW-SHP-90218",
                pickupAddress = "Warehouse 4A, MIDC Industrial Area, Andheri East",
                deliveryAddress = "Tower B, Raheja Mindspace, Malad West, Mumbai",
                createdAt = "$todayStr 08:30",
                scheduledPickupTime = "$todayStr 09:15",
                scheduledDeliveryTime = "$todayStr 11:30",
                actualPickupTime = "$todayStr 09:10",
                priority = MissionPriority.URGENT,
                status = MissionStatus.IN_TRANSIT,
                packageCount = 4,
                distanceKm = 11.2,
                deliveryNotes = "Call customer 10 minutes prior to gate security entry."
            ),
            Mission(
                id = "MSN-2026-082",
                title = "Pharma Cold-Chain Batch Pickup",
                type = MissionType.PICKUP,
                branchLocation = "Powai Tech Park Delivery Centre",
                riderId = "RD-102",
                riderName = "Rahul Sharma",
                shipmentRef = "OTW-SHP-90219",
                pickupAddress = "Lupin Research Lab, Hiranandani, Powai",
                deliveryAddress = "Apollo Central Dispensary, Chembur West",
                createdAt = "$todayStr 09:00",
                scheduledPickupTime = "$todayStr 09:45",
                scheduledDeliveryTime = "$todayStr 12:00",
                priority = MissionPriority.HIGH,
                status = MissionStatus.PICKUP_IN_PROGRESS,
                packageCount = 2,
                distanceKm = 14.5,
                deliveryNotes = "Maintain insulated thermal box upright."
            ),
            Mission(
                id = "MSN-2026-083",
                title = "E-Commerce Return Consignment Bulk",
                type = MissionType.RETURN,
                branchLocation = "Bandra Kurla Complex (BKC) Express Depot",
                riderId = "RD-104",
                riderName = "Mohammed Zaid",
                shipmentRef = "OTW-SHP-90220",
                pickupAddress = "One BKC, G Block, Bandra Kurla Complex",
                deliveryAddress = "Hub Return Staging Yard, Sakinaka, Andheri",
                createdAt = "$todayStr 09:30",
                scheduledPickupTime = "$todayStr 10:15",
                scheduledDeliveryTime = "$todayStr 13:00",
                actualPickupTime = "$todayStr 10:20",
                priority = MissionPriority.MEDIUM,
                status = MissionStatus.OUT_FOR_DELIVERY,
                packageCount = 8,
                distanceKm = 9.8,
                deliveryNotes = "Verify customer digital return barcode on package."
            ),
            Mission(
                id = "MSN-2026-084",
                title = "Retail Cash Collection & Invoices",
                type = MissionType.COLLECTION,
                branchLocation = "Thane West Secondary Distribution Hub",
                riderId = "RD-103",
                riderName = "Sachin Patil",
                shipmentRef = "OTW-SHP-90221",
                pickupAddress = "Reliance Retail Supermart, Ghodbunder Rd, Thane",
                deliveryAddress = "OTW Finance Vault, Andheri Central Hub",
                createdAt = "$todayStr 10:00",
                scheduledPickupTime = "$todayStr 11:00",
                scheduledDeliveryTime = "$todayStr 14:30",
                priority = MissionPriority.HIGH,
                status = MissionStatus.ACCEPTED,
                packageCount = 1,
                distanceKm = 22.4,
                deliveryNotes = "Sealed tamper-evident envelope with supervisor signature."
            ),
            Mission(
                id = "MSN-2026-080",
                title = "Morning Express Documents Dispatch",
                type = MissionType.ROUTE_EXPRESS,
                branchLocation = "Andheri East Central Sorting Hub",
                riderId = "RD-105",
                riderName = "Vikas Shinde",
                shipmentRef = "OTW-SHP-90215",
                pickupAddress = "Corporate Office 702, Sakinaka Junction",
                deliveryAddress = "Bombay High Court Chambers, Fort, Mumbai",
                createdAt = "$todayStr 07:15",
                scheduledPickupTime = "$todayStr 07:45",
                scheduledDeliveryTime = "$todayStr 09:30",
                actualPickupTime = "$todayStr 07:40",
                actualDeliveryTime = "$todayStr 09:18",
                priority = MissionPriority.URGENT,
                status = MissionStatus.DELIVERED,
                packageCount = 2,
                distanceKm = 26.0,
                deliveryNotes = "Handover to clerk Mr. Kadam.",
                podReference = "POD-BHC-99214-RECVD"
            ),
            Mission(
                id = "MSN-2026-079",
                title = "Residential Fragile Gift Parcel",
                type = MissionType.DELIVERY,
                branchLocation = "Powai Tech Park Delivery Centre",
                riderId = "RD-102",
                riderName = "Rahul Sharma",
                shipmentRef = "OTW-SHP-90210",
                pickupAddress = "Cake Studio, Galleria Powai",
                deliveryAddress = "Flat 1204, Nahar Amrit Shakti, Chandivali",
                createdAt = "$todayStr 06:45",
                scheduledPickupTime = "$todayStr 07:30",
                scheduledDeliveryTime = "$todayStr 08:30",
                actualPickupTime = "$todayStr 07:25",
                actualDeliveryTime = "$todayStr 08:22",
                priority = MissionPriority.LOW,
                status = MissionStatus.DELIVERED,
                packageCount = 1,
                distanceKm = 3.8,
                deliveryNotes = "Do not ring bell before 8am.",
                podReference = "POD-POW-4412-OK"
            )
        )

        // Seed Notifications
        _notifications.value = listOf(
            NotificationItem("NOTIF-1", "Mission Delivered", "Mission MSN-2026-080 was successfully delivered by Vikas Shinde.", "Today, 09:20", "MISSION", false),
            NotificationItem("NOTIF-2", "Payout Rates Active", "Primary ₹18.0, Club ₹6.0, Large ₹25.0 rate card loaded.", "Today, 08:00", "ORDER", false),
            NotificationItem("NOTIF-3", "Location Audit Complete", "Andheri East Central Sorting Hub verified for 3,500 parcel capacity.", "Yesterday", "LOCATION", true),
            NotificationItem("NOTIF-4", "Selfie Authentication Mandatory", "Franchise security policy: Selfie camera capture required on active portal.", "24 Oct", "SYSTEM", true)
        )
    }
}
