package com.example.data.model

data class UserAccount(
    val franchiseId: String,
    val email: String,
    val franchiseName: String,
    val selfieImagePath: String? = null,
    val passwordHash: String = "franchise123"
)

data class FranchiseProfile(
    val franchiseId: String = "FR-000001",
    val name: String = "On The Way Logistics - West Hub",
    val contactPerson: String = "Vikramaditya Singhania",
    val phone: String = "+91 98201 44552",
    val email: String = "mumbai.franchise@onthewaylogistics.in",
    val state: String = "Maharashtra",
    val district: String = "Mumbai Suburban",
    val city: String = "Mumbai",
    val pinCode: String = "400072",
    val officeAddress: String = "Suite 401, Apex Logistics Park, Sakinaka Junction, Andheri East",
    val remarks: String = "Tier-1 Express Sorting & Delivery Hub (Serving Zone 4)",
    val gstin: String = "27AAFCO8821B1Z8"
)

enum class LocationStatus {
    ACTIVE,
    PENDING,
    INACTIVE
}

data class FranchiseLocation(
    val id: String,
    val name: String,
    val hubCode: String,
    val manager: String,
    val phone: String,
    val address: String,
    val status: LocationStatus = LocationStatus.ACTIVE,
    val capacity: Int = 1200
)

enum class RiderStatus {
    ACTIVE,
    ON_DELIVERY,
    OFF_DUTY
}

data class Rider(
    val id: String,
    val name: String,
    val phone: String,
    val vehicleType: String = "Motorbike",
    val vehicleNumber: String = "MH-02-EE-4521",
    val status: RiderStatus = RiderStatus.ACTIVE,
    val rating: Double = 4.8,
    val joinedDate: String = "2024-03-15"
)

data class OrderRecord(
    val id: String,
    val riderId: String,
    val riderName: String,
    val orderDate: String, // YYYY-MM-DD
    val primaryOrders: Int,
    val clubOrders: Int,
    val largeOrders: Int,
    val primaryRate: Double = 18.0,
    val clubRate: Double = 6.0,
    val largeRate: Double = 25.0,
    val sddPayout: Double = 50.0
) {
    val totalOrders: Int get() = primaryOrders + clubOrders + largeOrders
    val primaryAmount: Double get() = primaryOrders * primaryRate
    val clubAmount: Double get() = clubOrders * clubRate
    val largeAmount: Double get() = largeOrders * largeRate
    val totalPayout: Double get() = primaryAmount + clubAmount + largeAmount + sddPayout
}

enum class MissionType {
    DELIVERY,
    PICKUP,
    COLLECTION,
    RETURN,
    ROUTE_EXPRESS
}

enum class MissionPriority {
    LOW,
    MEDIUM,
    HIGH,
    URGENT
}

enum class MissionStatus(val label: String) {
    CREATED("Created"),
    ASSIGNED("Assigned"),
    ACCEPTED("Accepted"),
    PICKUP_IN_PROGRESS("Pickup in Progress"),
    PICKED_UP("Picked Up"),
    IN_TRANSIT("In Transit"),
    OUT_FOR_DELIVERY("Out for Delivery"),
    DELIVERED("Delivered"),
    FAILED_DELIVERY("Failed Delivery"),
    CANCELLED("Cancelled"),
    RETURNED("Returned")
}

data class MissionStatusHistory(
    val status: MissionStatus,
    val timestamp: String,
    val notes: String = ""
)

data class Mission(
    val id: String, // e.g. MSN-2026-001
    val title: String,
    val type: MissionType = MissionType.DELIVERY,
    val franchiseId: String = "FR-000001",
    val branchLocation: String,
    val riderId: String?,
    val riderName: String?,
    val shipmentRef: String,
    val pickupAddress: String,
    val deliveryAddress: String,
    val createdAt: String, // YYYY-MM-DD HH:mm
    val scheduledPickupTime: String,
    val scheduledDeliveryTime: String,
    val actualPickupTime: String? = null,
    val actualDeliveryTime: String? = null,
    val priority: MissionPriority = MissionPriority.MEDIUM,
    val status: MissionStatus = MissionStatus.ASSIGNED,
    val packageCount: Int = 1,
    val distanceKm: Double = 5.2,
    val deliveryNotes: String = "",
    val podReference: String? = null,
    val statusHistory: List<MissionStatusHistory> = emptyList()
)

data class NotificationItem(
    val id: String,
    val title: String,
    val message: String,
    val timestamp: String,
    val category: String, // MISSION, LOCATION, ORDER, SYSTEM
    val isRead: Boolean = false
)

data class PayoutSettings(
    val primaryRate: Double = 18.0,
    val clubRate: Double = 6.0,
    val largeRate: Double = 25.0
)
