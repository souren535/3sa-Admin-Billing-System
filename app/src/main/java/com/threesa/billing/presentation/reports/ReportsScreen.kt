package com.threesa.billing.presentation.reports

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.threesa.billing.domain.model.Invoice
import com.threesa.billing.domain.model.InvoiceStatus
import com.threesa.billing.presentation.common.components.AppHeader
import com.threesa.billing.presentation.common.components.StoreSwitcherBar
import com.threesa.billing.presentation.common.components.StatusBadge
import com.threesa.billing.presentation.common.components.BadgeStyle
import com.threesa.billing.presentation.reports.components.InvoiceRow
import com.threesa.billing.presentation.reports.components.ReportStatCard
import com.threesa.billing.presentation.reports.components.ReportsTabs
import com.threesa.billing.ui.theme.*
import java.text.NumberFormat
import java.util.Locale
import java.text.SimpleDateFormat
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreen(
    onNavigateToProfile: () -> Unit = {},
    viewModel: ReportsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val listState = rememberLazyListState()
    var showTopSection by remember { mutableStateOf(true) }
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()
    
    // Format selected date
    val selectedDate = remember(datePickerState.selectedDateMillis) {
        val millis = datePickerState.selectedDateMillis ?: System.currentTimeMillis()
        val formatter = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        formatter.format(Date(millis))
    }

    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                if (available.y < -10f) {
                    showTopSection = false
                } else if (available.y > 10f) {
                    showTopSection = true
                }
                return Offset.Zero
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundCream)
            .padding(bottom = 90.dp) // Fixed height before bottom navigation bar
    ) {
        AppHeader(onAvatarClick = onNavigateToProfile)

        Box(modifier = Modifier.fillMaxSize().weight(1f)) {
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                return@Box
            }

            val data = uiState.data ?: return@Box
            val invoices = viewModel.filteredInvoices()

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp)
            ) {
                AnimatedVisibility(
                    visible = showTopSection,
                    enter = expandVertically(),
                    exit = shrinkVertically()
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Spacer(Modifier.height(16.dp))
                        StoreSwitcherBar(storeName = data.storeName, onSwitchStoreClick = {})
                        Spacer(Modifier.height(16.dp))

                        // Action Row: Date Picker & Export PDF
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedButton(
                                onClick = { showDatePicker = true },
                                shape = MaterialTheme.shapes.small,
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Icon(Icons.Filled.CalendarMonth, contentDescription = null, modifier = Modifier.size(16.dp), tint = TextPrimary)
                                Spacer(Modifier.width(8.dp))
                                Text(selectedDate, color = TextPrimary)
                                Spacer(Modifier.width(4.dp))
                                Icon(Icons.Filled.KeyboardArrowDown, contentDescription = null, tint = TextPrimary)
                            }

                            OutlinedButton(
                                onClick = { /* TODO */ },
                                shape = MaterialTheme.shapes.small,
                                border = androidx.compose.foundation.BorderStroke(1.dp, PeachBg),
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                            ) {
                                Icon(Icons.Filled.PictureAsPdf, contentDescription = null, tint = PrimaryOrange, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(8.dp))
                                Text("Export PDF", color = PrimaryOrange, fontWeight = FontWeight.Medium)
                            }
                        }
                        Spacer(Modifier.height(20.dp))

                        // Stat Cards Grid
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                            ReportStatCard(
                                label = "Total Invoices", value = "${data.totalInvoices}",
                                icon = Icons.Filled.Description, bgColor = PeachBg, iconColor = PrimaryOrange,
                                modifier = Modifier.weight(1f)
                            )
                            ReportStatCard(
                                label = "Total Sales", value = rupee.format(data.totalSales),
                                icon = Icons.Filled.CurrencyRupee, bgColor = MintBg, iconColor = MintIcon,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Spacer(Modifier.height(12.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                            ReportStatCard(
                                label = "Paid", value = "${data.paidCount}",
                                icon = Icons.Filled.CheckCircle, bgColor = MintBg, iconColor = MintIcon,
                                modifier = Modifier.weight(1f)
                            )
                            ReportStatCard(
                                label = "Unpaid", value = "${data.unpaidCount}",
                                icon = Icons.Filled.Error, bgColor = SoftRedBg, iconColor = SoftRedIcon,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                Spacer(Modifier.height(24.dp))

                // Tabs and Sort
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    ReportsTabs(
                        selectedTab = uiState.selectedTab,
                        onTabSelect = viewModel::onTabSelect,
                        paidCount = data.paidCount,
                        unpaidCount = data.unpaidCount,
                        totalCount = data.totalInvoices
                    )
                    IconButton(
                        onClick = { /* TODO */ },
                        modifier = Modifier
                            .background(ChipGray, MaterialTheme.shapes.small)
                            .size(36.dp)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Sort, contentDescription = "Sort", tint = TextPrimary)
                    }
                }
                HorizontalDivider(color = BorderLight)
                Spacer(Modifier.height(16.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    // Table Header
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(SurfaceMuted)
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        Text("Invoice No.", fontSize = 12.sp, color = TextSecondary, fontWeight = FontWeight.Medium, modifier = Modifier.weight(1.8f))
                        Text("Customer Details", fontSize = 12.sp, color = TextSecondary, fontWeight = FontWeight.Medium, modifier = Modifier.weight(2.2f))
                        Text("Total (₹)", fontSize = 12.sp, color = TextSecondary, fontWeight = FontWeight.Medium, modifier = Modifier.weight(1.2f), textAlign = TextAlign.End)
                        Text("Status", fontSize = 12.sp, color = TextSecondary, fontWeight = FontWeight.Medium, modifier = Modifier.weight(1.3f), textAlign = TextAlign.End)
                    }
                    HorizontalDivider(color = BorderLight)
                    
                    // Invoice List
                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .fillMaxSize()
                            .nestedScroll(nestedScrollConnection)
                    ) {
                        itemsIndexed(invoices, key = { _, inv -> inv.id }) { index, invoice ->
                            Box(modifier = Modifier.background(SurfaceWhite)) {
                                InvoiceRow(invoice)
                            }
                            HorizontalDivider(color = BorderLight)
                        }
                    }
                }
                Spacer(Modifier.height(16.dp))
            }
        }
        
        if (showDatePicker) {
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(onClick = { showDatePicker = false }) {
                        Text("OK")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePicker = false }) {
                        Text("Cancel")
                    }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }
    }
}

private val rupee = NumberFormat.getCurrencyInstance(Locale("en", "IN"))
