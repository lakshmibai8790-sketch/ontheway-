package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.data.model.*
import com.example.ui.components.FranchiseAvatar
import com.example.ui.components.SelfieCameraModal
import com.example.ui.theme.*
import com.example.ui.viewmodel.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainPortalScreen(
    viewModel: LogisticsViewModel
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val currentUser by viewModel.currentUser.collectAsState()
    val profile by viewModel.profile.collectAsState()
    val locations by viewModel.locations.collectAsState()
    val riders by viewModel.riders.collectAsState()
    val orders by viewModel.orders.collectAsState()
    val missions by viewModel.missions.collectAsState()
    val notifications by viewModel.notifications.collectAsState()
    val payoutSettings by viewModel.payoutSettings.collectAsState()
    val currentScreen by viewModel.currentScreen.collectAsState()
    val isSelfieModalOpen by viewModel.isSelfieModalOpen.collectAsState()
    val toastMessage by viewModel.toastMessage.collectAsState()

    val orderStats by viewModel.fifteenDayOrderStats.collectAsState()
    val missionStats by viewModel.fifteenDayMissionStats.collectAsState()
    val reportEndDate by viewModel.reportEndDate.collectAsState()
    val missionSearch by viewModel.missionSearchQuery.collectAsState()
    val missionStatusFilter by viewModel.missionStatusFilter.collectAsState()
    val missionPriorityFilter by viewModel.missionPriorityFilter.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(toastMessage) {
        toastMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearToast()
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = MaterialTheme.colorScheme.surface,
                modifier = Modifier.width(300.dp)
            ) {
                // Drawer Header with Corporate Navy Background & SIDEBAR AVATAR
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(CorporateNavy)
                        .padding(20.dp)
                ) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            // SIDEBAR AVATAR (Requirement 3: Circular F or selfie, clicks to open camera)
                            FranchiseAvatar(
                                selfiePath = currentUser?.selfieImagePath,
                                size = 60.dp,
                                showCameraBadge = true,
                                testTag = "sidebar_avatar",
                                onClick = {
                                    scope.launch { drawerState.close() }
                                    viewModel.openSelfieModal()
                                }
                            )

                            Surface(
                                color = BrightOrange,
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = profile.franchiseId,
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = profile.name,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )

                        Text(
                            text = "Tap avatar above to update selfie photo",
                            color = BrightOrange,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Navigation Items
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Dashboard, contentDescription = null) },
                    label = { Text("Dashboard") },
                    selected = currentScreen == PortalScreen.DASHBOARD,
                    onClick = {
                        viewModel.setScreen(PortalScreen.DASHBOARD)
                        scope.launch { drawerState.close() }
                    },
                    modifier = Modifier.padding(horizontal = 12.dp)
                )

                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.LocalShipping, contentDescription = null) },
                    label = { Text("Missions (${missions.size})") },
                    selected = currentScreen == PortalScreen.MISSIONS,
                    onClick = {
                        viewModel.setScreen(PortalScreen.MISSIONS)
                        scope.launch { drawerState.close() }
                    },
                    modifier = Modifier.padding(horizontal = 12.dp)
                )

                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Assessment, contentDescription = null) },
                    label = { Text("15-Day Performance") },
                    selected = currentScreen == PortalScreen.FIFTEEN_DAY_REPORT,
                    onClick = {
                        viewModel.setScreen(PortalScreen.FIFTEEN_DAY_REPORT)
                        scope.launch { drawerState.close() }
                    },
                    modifier = Modifier.padding(horizontal = 12.dp)
                )

                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.DateRange, contentDescription = null) },
                    label = { Text("Weekly Report") },
                    selected = currentScreen == PortalScreen.WEEKLY_REPORT,
                    onClick = {
                        viewModel.setScreen(PortalScreen.WEEKLY_REPORT)
                        scope.launch { drawerState.close() }
                    },
                    modifier = Modifier.padding(horizontal = 12.dp)
                )

                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.ReceiptLong, contentDescription = null) },
                    label = { Text("Riders & Orders") },
                    selected = currentScreen == PortalScreen.RIDERS_ORDERS,
                    onClick = {
                        viewModel.setScreen(PortalScreen.RIDERS_ORDERS)
                        scope.launch { drawerState.close() }
                    },
                    modifier = Modifier.padding(horizontal = 12.dp)
                )

                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Store, contentDescription = null) },
                    label = { Text("Locations & Profile") },
                    selected = currentScreen == PortalScreen.LOCATIONS_PROFILE,
                    onClick = {
                        viewModel.setScreen(PortalScreen.LOCATIONS_PROFILE)
                        scope.launch { drawerState.close() }
                    },
                    modifier = Modifier.padding(horizontal = 12.dp)
                )

                val unreadNotifs = notifications.count { !it.isRead }
                NavigationDrawerItem(
                    icon = {
                        BadgedBox(badge = {
                            if (unreadNotifs > 0) {
                                Badge { Text("$unreadNotifs") }
                            }
                        }) {
                            Icon(Icons.Default.Notifications, contentDescription = null)
                        }
                    },
                    label = { Text("Notifications & Settings") },
                    selected = currentScreen == PortalScreen.NOTIFICATIONS_SETTINGS,
                    onClick = {
                        viewModel.setScreen(PortalScreen.NOTIFICATIONS_SETTINGS)
                        scope.launch { drawerState.close() }
                    },
                    modifier = Modifier.padding(horizontal = 12.dp)
                )

                Spacer(modifier = Modifier.weight(1f))

                Divider(color = SlateBorder)

                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Logout, contentDescription = null, tint = StatusFailed) },
                    label = { Text("Log Out", color = StatusFailed, fontWeight = FontWeight.Bold) },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        viewModel.logout()
                    },
                    modifier = Modifier.padding(12.dp)
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(BrightOrange),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocalShipping,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "ON THE WAY",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White
                                )
                                Text(
                                    text = currentScreen.title,
                                    fontSize = 11.sp,
                                    color = BrightOrange,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Open Navigation Menu", tint = Color.White)
                        }
                    },
                    actions = {
                        // Notifications action with unread badge
                        val unreadCount = notifications.count { !it.isRead }
                        IconButton(onClick = { viewModel.setScreen(PortalScreen.NOTIFICATIONS_SETTINGS) }) {
                            BadgedBox(badge = {
                                if (unreadCount > 0) {
                                    Badge { Text("$unreadCount") }
                                }
                            }) {
                                Icon(Icons.Default.Notifications, contentDescription = "Notifications", tint = Color.White)
                            }
                        }

                        Spacer(modifier = Modifier.width(6.dp))

                        // TOP-RIGHT HEADER AVATAR (Requirement 3: Circular F or selfie, clicks to open camera)
                        FranchiseAvatar(
                            selfiePath = currentUser?.selfieImagePath,
                            size = 38.dp,
                            showCameraBadge = true,
                            testTag = "header_avatar",
                            onClick = {
                                viewModel.openSelfieModal()
                            }
                        )

                        Spacer(modifier = Modifier.width(12.dp))
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = CorporateNavy,
                        titleContentColor = Color.White,
                        navigationIconContentColor = Color.White,
                        actionIconContentColor = Color.White
                    )
                )
            },
            bottomBar = {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.navigationBarsPadding()
                ) {
                    NavigationBarItem(
                        selected = currentScreen == PortalScreen.DASHBOARD,
                        onClick = { viewModel.setScreen(PortalScreen.DASHBOARD) },
                        icon = { Icon(Icons.Default.Dashboard, contentDescription = null) },
                        label = { Text("Overview") }
                    )
                    NavigationBarItem(
                        selected = currentScreen == PortalScreen.MISSIONS,
                        onClick = { viewModel.setScreen(PortalScreen.MISSIONS) },
                        icon = { Icon(Icons.Default.LocalShipping, contentDescription = null) },
                        label = { Text("Missions") }
                    )
                    NavigationBarItem(
                        selected = currentScreen == PortalScreen.FIFTEEN_DAY_REPORT,
                        onClick = { viewModel.setScreen(PortalScreen.FIFTEEN_DAY_REPORT) },
                        icon = { Icon(Icons.Default.Assessment, contentDescription = null) },
                        label = { Text("15-Day") }
                    )
                    NavigationBarItem(
                        selected = currentScreen == PortalScreen.RIDERS_ORDERS,
                        onClick = { viewModel.setScreen(PortalScreen.RIDERS_ORDERS) },
                        icon = { Icon(Icons.Default.ReceiptLong, contentDescription = null) },
                        label = { Text("Orders") }
                    )
                    NavigationBarItem(
                        selected = currentScreen in listOf(PortalScreen.LOCATIONS_PROFILE, PortalScreen.WEEKLY_REPORT, PortalScreen.NOTIFICATIONS_SETTINGS),
                        onClick = { scope.launch { drawerState.open() } },
                        icon = { Icon(Icons.Default.MoreHoriz, contentDescription = null) },
                        label = { Text("More") }
                    )
                }
            },
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (currentScreen) {
                    PortalScreen.DASHBOARD -> DashboardScreen(
                        profile = profile,
                        locations = locations,
                        riders = riders,
                        orders = orders,
                        missions = missions,
                        orderStats = orderStats,
                        missionStats = missionStats,
                        onNavigate = { viewModel.setScreen(it) },
                        onOpenCreateMission = { viewModel.setScreen(PortalScreen.MISSIONS) },
                        onOpenAddOrder = { viewModel.setScreen(PortalScreen.RIDERS_ORDERS) },
                        onOpenAddLocation = { viewModel.setScreen(PortalScreen.LOCATIONS_PROFILE) }
                    )
                    PortalScreen.MISSIONS -> MissionsScreen(
                        missions = missions,
                        riders = riders,
                        locations = locations,
                        searchQuery = missionSearch,
                        statusFilter = missionStatusFilter,
                        priorityFilter = missionPriorityFilter,
                        onSearchChange = { viewModel.setMissionSearch(it) },
                        onStatusFilterChange = { viewModel.setMissionStatusFilter(it) },
                        onPriorityFilterChange = { viewModel.setMissionPriorityFilter(it) },
                        onCreateMission = { viewModel.addMission(it) },
                        onUpdateStatus = { id, st, n -> viewModel.updateMissionStatus(id, st, n) },
                        onAssignRider = { id, rid, rn -> viewModel.assignRider(id, rid, rn) },
                        onCancelMission = { id, r -> viewModel.cancelMission(id, r) }
                    )
                    PortalScreen.FIFTEEN_DAY_REPORT -> FifteenDayReportScreen(
                        orderStats = orderStats,
                        missionStats = missionStats,
                        reportEndDate = reportEndDate,
                        onDateChange = { viewModel.setReportEndDate(it) }
                    )
                    PortalScreen.WEEKLY_REPORT -> WeeklyReportScreen(
                        orders = orders
                    )
                    PortalScreen.RIDERS_ORDERS -> RidersOrdersScreen(
                        orders = orders,
                        riders = riders,
                        payoutSettings = payoutSettings,
                        onAddOrder = { viewModel.addOrder(it) },
                        onAddRider = { viewModel.addRider(it) }
                    )
                    PortalScreen.LOCATIONS_PROFILE -> LocationsProfileScreen(
                        profile = profile,
                        locations = locations,
                        onUpdateProfile = { viewModel.updateProfile(it) },
                        onAddLocation = { viewModel.addLocation(it) },
                        onUpdateLocation = { viewModel.updateLocation(it) },
                        onDeleteLocation = { viewModel.deleteLocation(it) }
                    )
                    PortalScreen.NOTIFICATIONS_SETTINGS -> NotificationsSettingsScreen(
                        user = currentUser,
                        notifications = notifications,
                        payoutSettings = payoutSettings,
                        onMarkNotificationRead = { viewModel.markNotificationAsRead(it) },
                        onMarkAllRead = { viewModel.markAllNotificationsAsRead() },
                        onUpdatePayoutRates = { p, c, l -> viewModel.updatePayoutRates(p, c, l) },
                        onResetDemoData = { viewModel.resetToDemoData() },
                        onLogout = { viewModel.logout() }
                    )
                }
            }
        }
    }

    // Modal Camera Dialog for Selfie Capture / Replacement
    if (isSelfieModalOpen) {
        SelfieCameraModal(
            title = "Update Franchise Selfie",
            subtitle = "Capture a front-facing selfie to update your sidebar & header avatars",
            isMandatoryLogin = false,
            onSelfieConfirmed = { bitmap ->
                viewModel.saveSelfieImage(bitmap, fromLoginFlow = false)
            },
            onDismiss = { viewModel.closeSelfieModal() }
        )
    }
}
