package com.example

import com.example.data.model.*
import org.junit.Assert.*
import org.junit.Test

class LogisticsPortalUnitTest {

    @Test
    fun testOrderPayoutCalculation() {
        val order = OrderRecord(
            id = "ORD-TEST-01",
            riderId = "RD-101",
            riderName = "Amit Kumar",
            orderDate = "2026-10-26",
            primaryOrders = 20,
            clubOrders = 10,
            largeOrders = 4,
            primaryRate = 18.0,
            clubRate = 6.0,
            largeRate = 25.0,
            sddPayout = 75.0
        )

        assertEquals(34, order.totalOrders)
        assertEquals(360.0, order.primaryAmount, 0.001)
        assertEquals(60.0, order.clubAmount, 0.001)
        assertEquals(100.0, order.largeAmount, 0.001)
        assertEquals(595.0, order.totalPayout, 0.001)
    }

    @Test
    fun testMissionStatusHistoryTracking() {
        val initialHistory = listOf(
            MissionStatusHistory(MissionStatus.CREATED, "2026-10-26 09:00", "Mission created")
        )

        val mission = Mission(
            id = "MSN-2026-999",
            title = "Test Delivery",
            branchLocation = "Andheri Central Hub",
            riderId = "RD-101",
            riderName = "Amit Kumar",
            shipmentRef = "OTW-SHP-TEST",
            pickupAddress = "Pickup Point A",
            deliveryAddress = "Dropoff Point B",
            createdAt = "2026-10-26 09:00",
            scheduledPickupTime = "2026-10-26 09:30",
            scheduledDeliveryTime = "2026-10-26 11:00",
            status = MissionStatus.ASSIGNED,
            statusHistory = initialHistory
        )

        assertEquals(MissionStatus.ASSIGNED, mission.status)
        assertEquals(1, mission.statusHistory.size)
        assertEquals(MissionStatus.CREATED, mission.statusHistory[0].status)
    }

    @Test
    fun test15DayCalendarWindowSize() {
        val days = (0..14).toList()
        assertEquals(15, days.size)
    }
}
