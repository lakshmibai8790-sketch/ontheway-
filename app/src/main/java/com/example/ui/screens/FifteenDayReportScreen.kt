package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.ui.theme.*
import com.example.ui.viewmodel.FifteenDayMissionStats
import com.example.ui.viewmodel.FifteenDayOrderStats
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun FifteenDayReportScreen(
    orderStats: FifteenDayOrderStats,
    missionStats: FifteenDayMissionStats,
    reportEndDate: String,
    onDateChange: (String) -> Unit
) {
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("en", "IN"))
    var selectedTab by remember { mutableIntStateOf(0) } // 0 = Order Report, 1 = Mission Report

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        // Report Title & Period Header
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
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "EXACT 15-DAY AUDIT WINDOW",
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                fontSize = 10.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }

                        // Date picker quick adjuster
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.DateRange, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = orderStats.endDate,
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "15-Day Performance Intelligence",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Text(
                        text = "Reporting Period: ${orderStats.startDate} to ${orderStats.endDate} (15 Calendar Days)",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.85f),
                        modifier = Modifier.padding(top = 2.dp)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Date Shifter buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                                val cal = Calendar.getInstance()
                                try {
                                    val d = sdf.parse(reportEndDate)
                                    if (d != null) cal.time = d
                                } catch (_: Exception) {}
                                cal.add(Calendar.DAY_OF_YEAR, -15)
                                onDateChange(sdf.format(cal.time))
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.15f)),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.ArrowBack, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.White)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Prior 15 Days", color = Color.White, fontSize = 11.sp)
                        }

                        Button(
                            onClick = {
                                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                                onDateChange(sdf.format(Date()))
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = BrightOrange),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Latest 15 Days", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Sub Tabs: Orders vs Missions
        item {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = BrightOrange
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Order Performance", fontWeight = FontWeight.Bold) },
                    icon = { Icon(Icons.Default.ReceiptLong, contentDescription = null) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Mission Performance", fontWeight = FontWeight.Bold) },
                    icon = { Icon(Icons.Default.LocalShipping, contentDescription = null) }
                )
            }
        }

        if (selectedTab == 0) {
            // ORDER PERFORMANCE SECTION
            item {
                // Key KPI Metrics Grid
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        ReportKPICard(
                            title = "Total 15-Day Orders",
                            value = "${orderStats.totalOrders}",
                            subtitle = if (orderStats.percentageGrowth >= 0) "+${String.format(Locale.US, "%.1f", orderStats.percentageGrowth)}% vs prior 15d" else "${String.format(Locale.US, "%.1f", orderStats.percentageGrowth)}% vs prior 15d",
                            tint = BrightOrange,
                            modifier = Modifier.weight(1f)
                        )

                        ReportKPICard(
                            title = "Daily Average Volume",
                            value = String.format(Locale.US, "%.1f", orderStats.averageDailyOrders),
                            subtitle = "Orders / calendar day",
                            tint = Color(0xFF0284C7),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        ReportKPICard(
                            title = "Primary Orders (₹18)",
                            value = "${orderStats.primaryOrders}",
                            subtitle = currencyFormat.format(orderStats.primaryOrders * 18.0),
                            tint = CorporateNavy,
                            modifier = Modifier.weight(1f)
                        )

                        ReportKPICard(
                            title = "Club Orders (₹6)",
                            value = "${orderStats.clubOrders}",
                            subtitle = currencyFormat.format(orderStats.clubOrders * 6.0),
                            tint = AmberAccent,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        ReportKPICard(
                            title = "Large Orders (₹25)",
                            value = "${orderStats.largeOrders}",
                            subtitle = currencyFormat.format(orderStats.largeOrders * 25.0),
                            tint = Color(0xFF8B5CF6),
                            modifier = Modifier.weight(1f)
                        )

                        ReportKPICard(
                            title = "Total Payout",
                            value = currencyFormat.format(orderStats.totalPayout),
                            subtitle = "Includes SDD incentives",
                            tint = StatusDelivered,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Interactive 15-Day Bar Chart
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Daily Order Trend (15 Days)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = CorporateNavy
                        )
                        Text(
                            text = "Bars represent total orders per calendar day. Orange=Primary, Amber=Club, Purple=Large",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        val maxOrders = orderStats.dailyBreakdown.maxOfOrNull { it.totalOrders } ?: 1

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            orderStats.dailyBreakdown.forEach { day ->
                                val total = day.totalOrders
                                val totalFraction = if (maxOrders > 0) (total.toFloat() / maxOrders).coerceIn(0.05f, 1f) else 0.05f

                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    if (total > 0) {
                                        Text(
                                            text = "$total",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = CorporateNavy
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(2.dp))

                                    // Stacked bar
                                    Box(
                                        modifier = Modifier
                                            .width(16.dp)
                                            .fillMaxHeight(totalFraction)
                                            .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                            .background(if (total > 0) BrightOrange else SlateBorder)
                                    )

                                    Spacer(modifier = Modifier.height(6.dp))

                                    Text(
                                        text = day.date.takeLast(2),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = CorporateNavy
                                    )
                                    Text(
                                        text = day.dayOfWeek.take(2),
                                        fontSize = 9.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Exact 15-Day Daily Breakdown Table
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
                            Text(
                                text = "15-Day Daily Audit Breakdown",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = CorporateNavy
                            )

                            Surface(
                                color = StatusDelivered.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = "15 Dates Verified",
                                    color = StatusDelivered,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Table Headers
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(CorporateNavy, RoundedCornerShape(6.dp))
                                .padding(vertical = 8.dp, horizontal = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Date", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp, modifier = Modifier.weight(1.2f))
                            Text("Prim (₹18)", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 10.sp, textAlign = TextAlign.Center, modifier = Modifier.weight(1f))
                            Text("Club (₹6)", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 10.sp, textAlign = TextAlign.Center, modifier = Modifier.weight(1f))
                            Text("Lrg (₹25)", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 10.sp, textAlign = TextAlign.Center, modifier = Modifier.weight(1f))
                            Text("Total", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp, textAlign = TextAlign.Center, modifier = Modifier.weight(1f))
                            Text("Payout", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp, textAlign = TextAlign.End, modifier = Modifier.weight(1.2f))
                        }

                        // 15 Daily Rows
                        orderStats.dailyBreakdown.forEachIndexed { index, day ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(if (index % 2 == 0) Color.Transparent else SurfaceElevated)
                                    .padding(vertical = 8.dp, horizontal = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1.2f)) {
                                    Text(day.date.substring(5), fontWeight = FontWeight.Bold, fontSize = 11.sp, color = CorporateNavy)
                                    Text(day.dayOfWeek, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Text("${day.primaryOrders}", fontSize = 11.sp, textAlign = TextAlign.Center, modifier = Modifier.weight(1f))
                                Text("${day.clubOrders}", fontSize = 11.sp, textAlign = TextAlign.Center, modifier = Modifier.weight(1f))
                                Text("${day.largeOrders}", fontSize = 11.sp, textAlign = TextAlign.Center, modifier = Modifier.weight(1f))
                                Text("${day.totalOrders}", fontWeight = FontWeight.Bold, color = BrightOrange, fontSize = 12.sp, textAlign = TextAlign.Center, modifier = Modifier.weight(1f))
                                Text(currencyFormat.format(day.totalPayout), fontSize = 11.sp, fontWeight = FontWeight.SemiBold, textAlign = TextAlign.End, modifier = Modifier.weight(1.2f))
                            }
                        }

                        Divider(color = SlateBorder, thickness = 1.dp)

                        // Grand Total Summary Row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(BrightOrange.copy(alpha = 0.1f))
                                .padding(vertical = 10.dp, horizontal = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("15D TOTAL", fontWeight = FontWeight.Black, fontSize = 11.sp, color = CorporateNavy, modifier = Modifier.weight(1.2f))
                            Text("${orderStats.primaryOrders}", fontWeight = FontWeight.Bold, fontSize = 11.sp, textAlign = TextAlign.Center, modifier = Modifier.weight(1f))
                            Text("${orderStats.clubOrders}", fontWeight = FontWeight.Bold, fontSize = 11.sp, textAlign = TextAlign.Center, modifier = Modifier.weight(1f))
                            Text("${orderStats.largeOrders}", fontWeight = FontWeight.Bold, fontSize = 11.sp, textAlign = TextAlign.Center, modifier = Modifier.weight(1f))
                            Text("${orderStats.totalOrders}", fontWeight = FontWeight.Black, color = BrightOrange, fontSize = 12.sp, textAlign = TextAlign.Center, modifier = Modifier.weight(1f))
                            Text(currencyFormat.format(orderStats.totalPayout), fontWeight = FontWeight.Black, color = StatusDelivered, fontSize = 11.sp, textAlign = TextAlign.End, modifier = Modifier.weight(1.2f))
                        }
                    }
                }
            }
        } else {
            // MISSION PERFORMANCE SECTION (Section 13)
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        ReportKPICard(
                            title = "Total Missions Created",
                            value = "${missionStats.totalCreated}",
                            subtitle = "${missionStats.totalAssigned} assigned to riders",
                            tint = CorporateNavy,
                            modifier = Modifier.weight(1f)
                        )

                        ReportKPICard(
                            title = "Completed Missions",
                            value = "${missionStats.totalCompleted}",
                            subtitle = "${String.format(Locale.US, "%.1f", missionStats.completionRate)}% Completion Rate",
                            tint = StatusDelivered,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        ReportKPICard(
                            title = "On-Time Delivery Rate",
                            value = "${String.format(Locale.US, "%.1f", missionStats.onTimeRate)}%",
                            subtitle = "Based on scheduled delivery SLA",
                            tint = Color(0xFF0284C7),
                            modifier = Modifier.weight(1f)
                        )

                        ReportKPICard(
                            title = "Avg Delivery Duration",
                            value = "${missionStats.averageDurationMinutes} mins",
                            subtitle = "Actual pickup to dropoff",
                            tint = BrightOrange,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        ReportKPICard(
                            title = "Failed Deliveries",
                            value = "${missionStats.totalFailed}",
                            subtitle = "Address unreachable / refused",
                            tint = StatusFailed,
                            modifier = Modifier.weight(1f)
                        )

                        ReportKPICard(
                            title = "Cancelled Missions",
                            value = "${missionStats.totalCancelled}",
                            subtitle = "Client / consignor recall",
                            tint = SlateMedium,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Rider-Level Mission Summaries
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Rider Mission Execution Summary",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = CorporateNavy
                        )
                        Text(
                            text = "15-day task performance metrics by field delivery rider",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(CorporateNavy, RoundedCornerShape(6.dp))
                                .padding(vertical = 8.dp, horizontal = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Rider", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp, modifier = Modifier.weight(1.5f))
                            Text("Assigned", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp, textAlign = TextAlign.Center, modifier = Modifier.weight(1f))
                            Text("Completed", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp, textAlign = TextAlign.Center, modifier = Modifier.weight(1f))
                            Text("On-Time", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp, textAlign = TextAlign.End, modifier = Modifier.weight(1f))
                        }

                        missionStats.riderMetrics.forEachIndexed { index, rm ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(if (index % 2 == 0) Color.Transparent else SurfaceElevated)
                                    .padding(vertical = 8.dp, horizontal = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1.5f)) {
                                    Text(rm.riderName, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = CorporateNavy)
                                    Text(rm.riderId, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Text("${rm.totalAssigned}", fontSize = 11.sp, textAlign = TextAlign.Center, modifier = Modifier.weight(1f))
                                Text("${rm.completedCount}", fontWeight = FontWeight.Bold, color = StatusDelivered, fontSize = 11.sp, textAlign = TextAlign.Center, modifier = Modifier.weight(1f))
                                Text("${String.format(Locale.US, "%.0f", rm.onTimeRate)}%", fontWeight = FontWeight.Bold, color = BrightOrange, fontSize = 11.sp, textAlign = TextAlign.End, modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }

            // Location-Level Mission Summaries
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Location / Hub Mission Distribution",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = CorporateNavy
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        missionStats.locationMetrics.forEach { lm ->
                            Column(modifier = Modifier.padding(vertical = 6.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(lm.locationName, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = CorporateNavy)
                                    Text("${lm.completedMissions}/${lm.totalMissions} (${String.format(Locale.US, "%.0f", lm.completionRate)}%)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = StatusDelivered)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                LinearProgressIndicator(
                                    progress = { (lm.completionRate / 100.0).toFloat().coerceIn(0.1f, 1f) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(6.dp)
                                        .clip(CircleShape),
                                    color = BrightOrange,
                                    trackColor = SlateBorder
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ReportKPICard(
    title: String,
    value: String,
    subtitle: String,
    tint: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = title,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                color = tint
            )
            Text(
                text = subtitle,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}
