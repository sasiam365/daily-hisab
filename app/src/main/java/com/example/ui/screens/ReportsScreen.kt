package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Categories
import com.example.data.TransactionEntity
import com.example.ui.theme.*
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreen(
    transactions: List<TransactionEntity>,
    totalIncome: Double,
    totalExpense: Double,
    balance: Double,
    onBack: () -> Unit
) {
    val currencyFormat = NumberFormat.getNumberInstance(Locale.getDefault())
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Overall, 1: Monthly Summary

    // Month selector state for Monthly Summary
    val calendarInstance = Calendar.getInstance()
    var selectedYear by remember { mutableIntStateOf(calendarInstance.get(Calendar.YEAR)) }
    var selectedMonth by remember { mutableIntStateOf(calendarInstance.get(Calendar.MONTH)) } // 0-11

    val monthNames = arrayOf(
        "জানুয়ারি (January)", "ফেব্রুয়ারি (February)", "মার্চ (March)",
        "এপ্রিল (April)", "মে (May)", "জুন (June)",
        "জুলাই (July)", "আগস্ট (August)", "সেপ্টেম্বর (September)",
        "অক্টোবর (October)", " নভেম্বর (November)", "ডিসেম্বর (December)"
    )

    // Filter transactions for selected month if in monthly tab
    val monthlyTransactions = remember(transactions, selectedYear, selectedMonth) {
        transactions.filter { tx ->
            val cal = Calendar.getInstance().apply { timeInMillis = tx.date }
            cal.get(Calendar.YEAR) == selectedYear && cal.get(Calendar.MONTH) == selectedMonth
        }
    }

    val monthlyIncome = monthlyTransactions.filter { it.type.uppercase() == "INCOME" }.sumOf { it.amount }
    val monthlyExpense = monthlyTransactions.filter { it.type.uppercase() == "EXPENSE" }.sumOf { it.amount }
    val monthlyBalance = monthlyIncome - monthlyExpense

    val activeTransactions = if (selectedTab == 0) transactions else monthlyTransactions
    val activeIncome = if (selectedTab == 0) totalIncome else monthlyIncome
    val activeExpense = if (selectedTab == 0) totalExpense else monthlyExpense
    val activeBalance = if (selectedTab == 0) balance else monthlyBalance

    val expenseTransactions = activeTransactions.filter { it.type.uppercase() == "EXPENSE" }
    val categoryTotals = expenseTransactions.groupBy { it.category }
        .mapValues { entry -> entry.value.sumOf { it.amount } }
        .entries.sortedByDescending { it.value }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "রিপোর্ট ও বিশ্লেষণ / Reports", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Tab Selector: Overall vs Monthly Summary
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surface),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .background(if (selectedTab == 0) EmeraldPrimary else Color.Transparent)
                            .clickable { selectedTab = 0 },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "সার্বিক / Overall",
                            color = if (selectedTab == 0) Color.White else MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .background(if (selectedTab == 1) EmeraldPrimary else Color.Transparent)
                            .clickable { selectedTab = 1 },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "মাসিক সারসংক্ষেপ / Monthly",
                            color = if (selectedTab == 1) Color.White else MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // If Monthly tab is selected, show Month Picker Controls
            if (selectedTab == 1) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(onClick = {
                                if (selectedMonth > 0) {
                                    selectedMonth--
                                } else {
                                    selectedMonth = 11
                                    selectedYear--
                                }
                            }) {
                                Icon(Icons.Default.ChevronLeft, contentDescription = "Previous Month")
                            }

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = monthNames[selectedMonth],
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = EmeraldPrimary
                                )
                                Text(
                                    text = "বছর / Year: $selectedYear",
                                    fontSize = 12.sp,
                                    color = TextMuted
                                )
                            }

                            IconButton(onClick = {
                                if (selectedMonth < 11) {
                                    selectedMonth++
                                } else {
                                    selectedMonth = 0
                                    selectedYear++
                                }
                            }) {
                                Icon(Icons.Default.ChevronRight, contentDescription = "Next Month")
                            }
                        }
                    }
                }
            }

            // Overview Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = if (selectedTab == 0) "সার্বিক সারসংক্ষেপ / Overall Summary" else "${monthNames[selectedMonth]} $selectedYear সারসংক্ষেপ",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Divider()
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(text = "মোট আয় / Income", fontSize = 13.sp, color = TextMuted)
                                Text(text = "৳ ${currencyFormat.format(activeIncome)}", fontWeight = FontWeight.Bold, color = IncomeGreen, fontSize = 16.sp)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(text = "মোট খরচ / Expense", fontSize = 13.sp, color = TextMuted)
                                Text(text = "৳ ${currencyFormat.format(activeExpense)}", fontWeight = FontWeight.Bold, color = ExpenseRed, fontSize = 16.sp)
                            }
                        }
                        Divider()
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "নেট সঞ্চয় / Net Balance", fontWeight = FontWeight.Medium)
                            Text(
                                text = "৳ ${currencyFormat.format(activeBalance)}",
                                fontWeight = FontWeight.Bold,
                                color = if (activeBalance >= 0) IncomeGreen else ExpenseRed,
                                fontSize = 16.sp
                            )
                        }
                    }
                }
            }

            // Category Breakdown Header
            item {
                Text(
                    text = "খাত অনুযায়ী খরচ / Expense by Category",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            if (categoryTotals.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "কোনো খরচের তথ্য নেই",
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                    }
                }
            } else {
                items(categoryTotals) { (categoryName, amount) ->
                    val percentage = if (activeExpense > 0) (amount / activeExpense).toFloat() else 0f
                    val categoryInfo = Categories.list.find { it.name == categoryName }
                    val bengaliName = categoryInfo?.bengaliName ?: categoryName

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "$bengaliName ($categoryName)", fontWeight = FontWeight.Bold)
                                Text(text = "৳ ${currencyFormat.format(amount)}", fontWeight = FontWeight.Bold, color = ExpenseRed)
                            }

                            LinearProgressIndicator(
                                progress = { percentage },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp)),
                                color = EmeraldPrimary,
                                trackColor = EmeraldContainer,
                            )

                            Text(
                                text = "মোট খরচের ${(percentage * 100).toInt()}%",
                                fontSize = 12.sp,
                                color = TextMuted
                            )
                        }
                    }
                }
            }
        }
    }
}
