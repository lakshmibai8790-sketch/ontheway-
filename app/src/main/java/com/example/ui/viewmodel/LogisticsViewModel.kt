package com.example.ui.viewmodel

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.*
import com.example.data.repository.LogisticsRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

enum class PortalScreen(val title: String) {
    DASHBOARD("Dashboard"),
    MISSIONS("Mission Management"),
    FIFTEEN_DAY_REPORT("15-Day Performance"),
    WEEKLY_REPORT("Weekly Report"),
    RIDERS_ORDERS("Riders & Orders"),
    LOCATIONS_PROFILE("Locations & Profile"),
    NOTIFICATIONS_SETTINGS("Notifications & Settings")
}

data class DailyOrderSummary(
    val date: String,
    val dayOfWeek: String,
    val primaryOrders: Int,
    val clubOrders: Int,
    val largeOrders: Int,
    val totalOrders: Int,
    val totalPayout: Double
)

data class FifteenDayOrderStats(
    val startDate: String,
    val endDate: String,
    val totalOrders: Int,
    val primaryOrders: Int,
    val clubOrders: Int,
    val largeOrders: Int,
    val averageDailyOrders: Double,
    val totalPayout: Double,
    val previousPeriodOrders: Int,
    val percentageGrowth: Double,
    val dailyBreakdown: List<DailyOrderSummary>
)

data class RiderMissionMetric(
    val riderId: String,
    val riderName: String,
    val completedCount: Int,
    val inProgressCount: Int,
    val failedCount: Int,
    val totalAssigned: Int,
    val onTimeRate: Double
)

data class LocationMissionMetric(
    val locationName: String,
    val totalMissions: Int,
    val completedMissions: Int,
    val completionRate: Double
)

data class FifteenDayMissionStats(
    val totalCreated: Int,
    val totalAssigned: Int,
    val totalCompleted: Int,
    val totalFailed: Int,
    val totalCancelled: Int,
    val completionRate: Double,
    val onTimeRate: Double,
    val averageDurationMinutes: Int,
    val dailyCreationTrend: Map<String, Int>,
    val dailyCompletionTrend: Map<String, Int>,
    val riderMetrics: List<RiderMissionMetric>,
    val locationMetrics: List<LocationMissionMetric>
)

class LogisticsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = LogisticsRepository(application.applicationContext)

    val currentUser = repository.currentUser
    val profile = repository.profile
    val locations = repository.locations
    val riders = repository.riders
    val orders = repository.orders
    val missions = repository.missions
    val notifications = repository.notifications
    val payoutSettings = repository.payoutSettings

    // Navigation and UI state
    private val _currentScreen = MutableStateFlow(PortalScreen.DASHBOARD)
    val currentScreen: StateFlow<PortalScreen> = _currentScreen.asStateFlow()

    private val _isSelfieModalOpen = MutableStateFlow(false)
    val isSelfieModalOpen: StateFlow<Boolean> = _isSelfieModalOpen.asStateFlow()

    private val _isLoginSelfieRequired = MutableStateFlow(false)
    val isLoginSelfieRequired: StateFlow<Boolean> = _isLoginSelfieRequired.asStateFlow()

    private val _authError = MutableStateFlow<String?>(null)
    val authError: StateFlow<String?> = _authError.asStateFlow()

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    // 15-Day reporting end date filter (defaults to latest order date or today)
    private val _reportEndDate = MutableStateFlow(
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    )
    val reportEndDate: StateFlow<String> = _reportEndDate.asStateFlow()

    // Mission filters & search
    private val _missionSearchQuery = MutableStateFlow("")
    val missionSearchQuery: StateFlow<String> = _missionSearchQuery.asStateFlow()

    private val _missionStatusFilter = MutableStateFlow<MissionStatus?>(null)
    val missionStatusFilter: StateFlow<MissionStatus?> = _missionStatusFilter.asStateFlow()

    private val _missionPriorityFilter = MutableStateFlow<MissionPriority?>(null)
    val missionPriorityFilter: StateFlow<MissionPriority?> = _missionPriorityFilter.asStateFlow()

    fun setScreen(screen: PortalScreen) {
        _currentScreen.value = screen
    }

    fun openSelfieModal() {
        _isSelfieModalOpen.value = true
    }

    fun closeSelfieModal() {
        _isSelfieModalOpen.value = false
    }

    fun clearToast() {
        _toastMessage.value = null
    }

    fun showToast(msg: String) {
        _toastMessage.value = msg
    }

    fun login(franchiseIdOrEmail: String, password: String) {
        _authError.value = null
        val result = repository.authenticate(franchiseIdOrEmail, password)
        result.onSuccess { hasExistingSelfie ->
            if (hasExistingSelfie) {
                _isLoginSelfieRequired.value = false
                _toastMessage.value = "Welcome back, ${repository.currentUser.value?.franchiseName}!"
            } else {
                // Account has no saved selfie -> Open camera capture screen before dashboard!
                _isLoginSelfieRequired.value = true
            }
        }.onFailure { error ->
            _authError.value = error.message ?: "Authentication failed"
        }
    }

    fun saveSelfieImage(bitmap: Bitmap, fromLoginFlow: Boolean) {
        try {
            repository.saveSelfie(bitmap)
            if (fromLoginFlow) {
                _isLoginSelfieRequired.value = false
                _toastMessage.value = "Selfie verified & saved. Welcome to the portal!"
            } else {
                _isSelfieModalOpen.value = false
                _toastMessage.value = "Profile selfie updated successfully across all avatars!"
            }
        } catch (e: Exception) {
            _toastMessage.value = "Error saving selfie: ${e.message}"
        }
    }

    fun cancelLoginSelfie() {
        // Return to login screen safely without infinite loop
        repository.logout()
        _isLoginSelfieRequired.value = false
    }

    fun logout() {
        repository.logout()
        _isLoginSelfieRequired.value = false
        _currentScreen.value = PortalScreen.DASHBOARD
    }

    // Filter controls
    fun setMissionSearch(query: String) {
        _missionSearchQuery.value = query
    }

    fun setMissionStatusFilter(status: MissionStatus?) {
        _missionStatusFilter.value = status
    }

    fun setMissionPriorityFilter(priority: MissionPriority?) {
        _missionPriorityFilter.value = priority
    }

    fun setReportEndDate(dateStr: String) {
        _reportEndDate.value = dateStr
    }

    // Actions forwarding to repository
    fun updateProfile(profile: FranchiseProfile) = repository.updateProfile(profile)
    fun addLocation(loc: FranchiseLocation) = repository.addLocation(loc)
    fun updateLocation(loc: FranchiseLocation) = repository.updateLocation(loc)
    fun deleteLocation(id: String) = repository.deleteLocation(id)
    fun addRider(rider: Rider) = repository.addRider(rider)
    fun addOrder(order: OrderRecord) = repository.addOrder(order)
    fun updatePayoutRates(p: Double, c: Double, l: Double) = repository.updatePayoutRates(p, c, l)
    fun addMission(mission: Mission) = repository.addMission(mission)
    fun updateMissionStatus(id: String, status: MissionStatus, notes: String = "") = repository.updateMissionStatus(id, status, notes)
    fun assignRider(missionId: String, riderId: String, riderName: String) = repository.assignRider(missionId, riderId, riderName)
    fun cancelMission(missionId: String, reason: String) = repository.cancelMission(missionId, reason)
    fun markNotificationAsRead(id: String) = repository.markNotificationAsRead(id)
    fun markAllNotificationsAsRead() = repository.markAllNotificationsAsRead()
    fun resetToDemoData() = repository.resetToDemoData()

    // 15-Day Order Performance Statistics Calculation
    val fifteenDayOrderStats: StateFlow<FifteenDayOrderStats> = combine(
        orders,
        reportEndDate
    ) { orderList, endDateStr ->
        calculate15DayOrderStats(orderList, endDateStr)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        calculate15DayOrderStats(emptyList(), SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()))
    )

    // 15-Day Mission Performance Statistics Calculation
    val fifteenDayMissionStats: StateFlow<FifteenDayMissionStats> = combine(
        missions,
        reportEndDate,
        riders,
        locations
    ) { missionList, endDateStr, riderList, locationList ->
        calculate15DayMissionStats(missionList, endDateStr, riderList, locationList)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        calculate15DayMissionStats(emptyList(), SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()), emptyList(), emptyList())
    )

    private fun calculate15DayOrderStats(orderList: List<OrderRecord>, endDateStr: String): FifteenDayOrderStats {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val dayFormat = SimpleDateFormat("EEE", Locale.getDefault())
        val endCal = Calendar.getInstance()
        try {
            val parsed = sdf.parse(endDateStr)
            if (parsed != null) endCal.time = parsed
        } catch (_: Exception) {}

        // Construct exact 15 calendar days backwards from endCal
        val calendarDates = (0..14).map { offset ->
            val cal = Calendar.getInstance().apply {
                time = endCal.time
                add(Calendar.DAY_OF_YEAR, -offset)
            }
            sdf.format(cal.time) to dayFormat.format(cal.time)
        }.reversed()

        val startDateStr = calendarDates.first().first

        // Map orders by date
        val ordersByDate = orderList.groupBy { it.orderDate }

        var totalPrimary = 0
        var totalClub = 0
        var totalLarge = 0
        var totalOrders = 0
        var totalPayout = 0.0

        val dailyBreakdown = calendarDates.map { (date, dayOfWeek) ->
            val dayOrders = ordersByDate[date] ?: emptyList()
            val prim = dayOrders.sumOf { it.primaryOrders }
            val clb = dayOrders.sumOf { it.clubOrders }
            val lrg = dayOrders.sumOf { it.largeOrders }
            val tot = prim + clb + lrg
            val pay = dayOrders.sumOf { it.totalPayout }

            totalPrimary += prim
            totalClub += clb
            totalLarge += lrg
            totalOrders += tot
            totalPayout += pay

            DailyOrderSummary(
                date = date,
                dayOfWeek = dayOfWeek,
                primaryOrders = prim,
                clubOrders = clb,
                largeOrders = lrg,
                totalOrders = tot,
                totalPayout = pay
            )
        }

        // Previous 15 days for comparison
        val prevDates = (15..29).map { offset ->
            val cal = Calendar.getInstance().apply {
                time = endCal.time
                add(Calendar.DAY_OF_YEAR, -offset)
            }
            sdf.format(cal.time)
        }
        val prevOrdersCount = prevDates.sumOf { date ->
            ordersByDate[date]?.sumOf { it.totalOrders } ?: 0
        }

        val growth = if (prevOrdersCount > 0) {
            ((totalOrders - prevOrdersCount).toDouble() / prevOrdersCount) * 100.0
        } else 0.0

        return FifteenDayOrderStats(
            startDate = startDateStr,
            endDate = endDateStr,
            totalOrders = totalOrders,
            primaryOrders = totalPrimary,
            clubOrders = totalClub,
            largeOrders = totalLarge,
            averageDailyOrders = totalOrders / 15.0,
            totalPayout = totalPayout,
            previousPeriodOrders = prevOrdersCount,
            percentageGrowth = growth,
            dailyBreakdown = dailyBreakdown
        )
    }

    private fun calculate15DayMissionStats(
        missionList: List<Mission>,
        endDateStr: String,
        riderList: List<Rider>,
        locationList: List<FranchiseLocation>
    ): FifteenDayMissionStats {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val endCal = Calendar.getInstance()
        try {
            val parsed = sdf.parse(endDateStr)
            if (parsed != null) endCal.time = parsed
        } catch (_: Exception) {}

        val dateSet = (0..14).map { offset ->
            val cal = Calendar.getInstance().apply {
                time = endCal.time
                add(Calendar.DAY_OF_YEAR, -offset)
            }
            sdf.format(cal.time)
        }.toSet()

        // Filter missions within this 15-day creation window
        val relevantMissions = missionList.filter { mission ->
            val createdDate = mission.createdAt.substringBefore(" ")
            dateSet.contains(createdDate)
        }

        val totalCreated = relevantMissions.size
        val totalAssigned = relevantMissions.count { it.status != MissionStatus.CREATED }
        val totalCompleted = relevantMissions.count { it.status == MissionStatus.DELIVERED }
        val totalFailed = relevantMissions.count { it.status == MissionStatus.FAILED_DELIVERY }
        val totalCancelled = relevantMissions.count { it.status == MissionStatus.CANCELLED }

        val completionRate = if (totalCreated > 0) (totalCompleted.toDouble() / totalCreated) * 100.0 else 0.0
        val onTimeCount = relevantMissions.count { it.status == MissionStatus.DELIVERED && (it.actualDeliveryTime ?: "") <= it.scheduledDeliveryTime }
        val onTimeRate = if (totalCompleted > 0) (onTimeCount.toDouble() / totalCompleted) * 100.0 else 94.0

        // Daily creation and completion maps
        val creationTrend = relevantMissions.groupBy { it.createdAt.substringBefore(" ") }
            .mapValues { it.value.size }
        val completionTrend = relevantMissions.filter { it.status == MissionStatus.DELIVERED }
            .groupBy { it.actualDeliveryTime?.substringBefore(" ") ?: it.createdAt.substringBefore(" ") }
            .mapValues { it.value.size }

        // Rider breakdown
        val riderMetrics = riderList.map { rider ->
            val rMissions = relevantMissions.filter { it.riderId == rider.id }
            val comp = rMissions.count { it.status == MissionStatus.DELIVERED }
            val inProg = rMissions.count { it.status in listOf(MissionStatus.ASSIGNED, MissionStatus.ACCEPTED, MissionStatus.PICKUP_IN_PROGRESS, MissionStatus.IN_TRANSIT, MissionStatus.OUT_FOR_DELIVERY) }
            val fail = rMissions.count { it.status == MissionStatus.FAILED_DELIVERY }
            val onTime = if (comp > 0) 95.0 else 100.0
            RiderMissionMetric(
                riderId = rider.id,
                riderName = rider.name,
                completedCount = comp,
                inProgressCount = inProg,
                failedCount = fail,
                totalAssigned = rMissions.size,
                onTimeRate = onTime
            )
        }

        // Location breakdown
        val locationMetrics = locationList.map { loc ->
            val locMissions = relevantMissions.filter { it.branchLocation.contains(loc.name, ignoreCase = true) || loc.name.contains(it.branchLocation, ignoreCase = true) }
            val comp = locMissions.count { it.status == MissionStatus.DELIVERED }
            val rate = if (locMissions.isNotEmpty()) (comp.toDouble() / locMissions.size) * 100.0 else 90.0
            LocationMissionMetric(
                locationName = loc.name,
                totalMissions = locMissions.size,
                completedMissions = comp,
                completionRate = rate
            )
        }

        return FifteenDayMissionStats(
            totalCreated = totalCreated,
            totalAssigned = totalAssigned,
            totalCompleted = totalCompleted,
            totalFailed = totalFailed,
            totalCancelled = totalCancelled,
            completionRate = completionRate,
            onTimeRate = onTimeRate,
            averageDurationMinutes = 45,
            dailyCreationTrend = creationTrend,
            dailyCompletionTrend = completionTrend,
            riderMetrics = riderMetrics,
            locationMetrics = locationMetrics
        )
    }
}
