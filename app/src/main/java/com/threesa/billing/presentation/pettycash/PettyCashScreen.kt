package com.threesa.billing.presentation.pettycash

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.threesa.billing.presentation.common.components.AppHeader
import com.threesa.billing.presentation.common.components.StoreSwitcherBar
import com.threesa.billing.presentation.pettycash.components.OutflowItem
import com.threesa.billing.ui.theme.*
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PettyCashScreen(
    onNavigateToProfile: () -> Unit = {},
    viewModel: PettyCashViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val rupee = remember { NumberFormat.getCurrencyInstance(Locale("en", "IN")) }
    
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundCream)
    ) {
        AppHeader(onAvatarClick = onNavigateToProfile)
        Box(modifier = Modifier.fillMaxSize().weight(1f)) {
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                return@Box
            }
            val data = uiState.data ?: return@Box

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp)
            ) {
                item {
                    StoreSwitcherBar(storeName = data.storeName, onSwitchStoreClick = {})
                    Spacer(Modifier.height(16.dp))
                }

                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, BorderLight, MaterialTheme.shapes.medium)
                            .background(SurfaceWhite, MaterialTheme.shapes.medium)
                            .padding(20.dp)
                    ) {
                        Text(
                            "CURRENT CASH (${data.storeName.uppercase()})",
                            fontSize = 11.sp,
                            color = TextSecondary,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            rupee.format(data.currentCash),
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(Modifier.height(24.dp))
                }

                item {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Today's Outflows", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .background(ChipGray, MaterialTheme.shapes.extraLarge)
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text("${data.outflows.size} Transactions", fontSize = 12.sp, color = TextSecondary)
                            }
                            Spacer(Modifier.width(8.dp))
                            IconButton(onClick = { showDatePicker = true }, modifier = Modifier.size(32.dp)) {
                                Icon(Icons.Filled.CalendarMonth, contentDescription = "Date Picker", tint = PrimaryOrange)
                            }
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                }

                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, BorderLight, MaterialTheme.shapes.medium)
                            .clip(MaterialTheme.shapes.medium)
                            .background(SurfaceWhite)
                    ) {
                        data.outflows.forEachIndexed { index, tx ->
                            OutflowItem(tx)
                            if (index != data.outflows.lastIndex) HorizontalDivider(color = BorderLight)
                        }
                        HorizontalDivider(color = BorderLight)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(SoftRedBg)
                                .padding(vertical = 14.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "View All Historical Outflows",
                                color = PrimaryOrange,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }

                item { Spacer(Modifier.height(90.dp)) }
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
}