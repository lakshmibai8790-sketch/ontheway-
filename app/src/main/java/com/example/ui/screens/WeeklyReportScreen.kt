package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.OrderRecord
import com.example.ui.theme.*
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun WeeklyReportScreen(
    orders: List<OrderRecord>
) {
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("en", "IN"))
    var weekOffset by remember { mutableIntStateOf(0) } // 0 = Current week, 1 = Last week, etc.

    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val dayFormat = SimpleDateFormat("EEE", Locale.getDefault())

    // Calculate Monday-Sunday dates for selected week
    val weekDays = remember(weekOffset) {
        val cal = Calendar.getInstance()
        cal.firstDayOfWeek = Calendar.MONDAY
        cal.add(Calendar.WEEK_OF_YEAR, -weekOffset)
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)

        (0..6).map {
            val d = cal.time
            val dateStr = sdf.format(d)
            val dayName = dayFormat.format(d)
            cal.add(Calendar.DAY_OF_YEAR, 1)
            dateStr to dayName
        }
    }

    val startDateStr = weekDays.first().first
    val endDateStr = weekDays.last().first

    // Group orders
    val ordersByDate = remember(orders) { orders.groupBy { it.orderDate } }

    var weekPrimary = 0
    var weekClub = 0
    var weekLarge = 0
    var weekTotalOrders = 0
    var weekTotalPayout = 0.0

    val dailyData = weekDays.map { (date, dayName) ->
        val dayOrders = ordersByDate[date] ?: emptyList()
        val prim = dayOrders.sumOf { it.primaryOrders }
        val clb = dayOrders.sumOf { it.clubOrders }
        val lrg = dayOrders.sumOf { it.largeOrders }
        val tot = prim + clb + lrg
        val pay = dayOrders.sumOf { it.totalPayout }

        weekPrimary += prim
        weekClub += clb
        weekLarge += lrg
        weekTotalOrders += tot
        weekTotalPayout += pay

        DayOrderData(date, dayName, prim, clb, lrg, tot, pay)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        // Week Selector Header
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
                        Text(
                            text = "Weekly Performance Audit",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        Surface(
                            color = BrightOrange,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = if (weekOffset == 0) "Current Week" else "$weekOffset Wk Ago",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Period: $startDateStr (Mon) to $endDateStr (Sun)",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.85f)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        OutlinedButton(
                            onClick = { weekOffset++ },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                        ) {
                            Icon(Icons.Default.ArrowBack, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Previous Week")
                        }

                        if (weekOffset > 0) {
                            Button(
                                onClick = { weekOffset-- },
                                colors = ButtonDefaults.buttonColors(containerColor = BrightOrange)
                            ) {
                                Text("Next Week")
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }
        }

        // Summary KPI Row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ReportKPICard(
                    title = "Weekly Orders",
                    value = "$weekTotalOrders",
                    subtitle = "Avg ${weekTotalOrders / 7}/day",
                    tint = BrightOrange,
                    modifier = Modifier.weight(1f)
                )

                ReportKPICard(
                    title = "Weekly Payout",
                    value = currencyFormat.format(weekTotalPayout),
                    subtitle = "₹18 Prim / ₹6 Club / ₹25 Lrg",
                    tint = StatusDelivered,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Weekly Category Breakdown Cards
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ReportKPICard(
                    title = "Primary Orders",
                    value = "$weekPrimary",
                    subtitle = currencyFormat.format(weekPrimary * 18.0),
                    tint = CorporateNavy,
                    modifier = Modifier.weight(1f)
                )

                ReportKPICard(
                    title = "Club Orders",
                    value = "$weekClub",
                    subtitle = currencyFormat.format(weekClub * 6.0),
                    tint = AmberAccent,
                    modifier = Modifier.weight(1f)
                )

                ReportKPICard(
                    title = "Large Orders",
                    value = "$weekLarge",
                    subtitle = currencyFormat.format(weekLarge * 25.0),
                    tint = Color(0xFF8B5CF6),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // 7-Day Day-by-Day Bar Chart
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Day-of-Week Comparison (Mon - Sun)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = CorporateNavy
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    val maxDaily = dailyData.maxOfOrNull { it.total } ?: 1

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        dailyData.forEach { day ->
                            val heightFraction = if (maxDaily > 0) (day.total.toFloat() / maxDaily).coerceIn(0.08f, 1f) else 0.08f
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = "${day.total}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CorporateNavy
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Box(
                                    modifier = Modifier
                                        .width(22.dp)
                                        .fillMaxHeight(heightFraction)
                                        .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                                        .background(if (day.total > 0) BrightOrange else SlateBorder)
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = day.dayName,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CorporateNavy
                                )
                                Text(
                                    text = day.date.takeLast(2),
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }

        // Weekly Breakdown Table
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Weekly Audit Summary Table",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = CorporateNavy
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Table Header
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(CorporateNavy, RoundedCornerShape(6.dp))
                            .padding(vertical = 8.dp, horizontal = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Day", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp, modifier = Modifier.weight(1f))
                        Text("Date", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp, modifier = Modifier.weight(1.2f))
                        Text("Prim", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp, textAlign = TextAlign.Center, modifier = Modifier.weight(0.9f))
                        Text("Club", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp, textAlign = TextAlign.Center, modifier = Modifier.weight(0.9f))
                        Text("Large", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp, textAlign = TextAlign.Center, modifier = Modifier.weight(0.9f))
                        Text("Total", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp, textAlign = TextAlign.Center, modifier = Modifier.weight(1f))
                        Text("Payout", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp, textAlign = TextAlign.End, modifier = Modifier.weight(1.3f))
                    }

                    dailyData.forEachIndexed { idx, d ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(if (idx % 2 == 0) Color.Transparent else SurfaceElevated)
                                .padding(vertical = 8.dp, horizontal = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(d.dayName, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = CorporateNavy, modifier = Modifier.weight(1f))
                            Text(d.date.substring(5), fontSize = 11.sp, modifier = Modifier.weight(1.2f))
                            Text("${d.primary}", fontSize = 11.sp, textAlign = TextAlign.Center, modifier = Modifier.weight(0.9f))
                            Text("${d.club}", fontSize = 11.sp, textAlign = TextAlign.Center, modifier = Modifier.weight(0.9f))
                            Text("${d.large}", fontSize = 11.sp, textAlign = TextAlign.Center, modifier = Modifier.weight(0.9f))
                            Text("${d.total}", fontWeight = FontWeight.Bold, color = BrightOrange, fontSize = 12.sp, textAlign = TextAlign.Center, modifier = Modifier.weight(1f))
                            Text(currencyFormat.format(d.payout), fontSize = 11.sp, fontWeight = FontWeight.SemiBold, textAlign = TextAlign.End, modifier = Modifier.weight(1.3f))
                        }
                    }

                    Divider(color = SlateBorder)

                    // Total
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(BrightOrange.copy(alpha = 0.1f))
                            .padding(vertical = 10.dp, horizontal = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("TOTAL", fontWeight = FontWeight.Black, fontSize = 11.sp, color = CorporateNavy, modifier = Modifier.weight(2.2f))
                        Text("$weekPrimary", fontWeight = FontWeight.Bold, fontSize = 11.sp, textAlign = TextAlign.Center, modifier = Modifier.weight(0.9f))
                        Text("$weekClub", fontWeight = FontWeight.Bold, fontSize = 11.sp, textAlign = TextAlign.Center, modifier = Modifier.weight(0.9f))
                        Text("$weekLarge", fontWeight = FontWeight.Bold, fontSize = 11.sp, textAlign = TextAlign.Center, modifier = Modifier.weight(0.9f))
                        Text("$weekTotalOrders", fontWeight = FontWeight.Black, color = BrightOrange, fontSize = 12.sp, textAlign = TextAlign.Center, modifier = Modifier.weight(1f))
                        Text(currencyFormat.format(weekTotalPayout), fontWeight = FontWeight.Black, color = StatusDelivered, fontSize = 11.sp, textAlign = TextAlign.End, modifier = Modifier.weight(1.3f))
                    }
                }
            }
        }
    }
}

private data class DayOrderData(
    val date: String,
    val dayName: String,
    val primary: Int,
    val club: Int,
    val large: Int,
    val total: Int,
    val payout: Double
)
