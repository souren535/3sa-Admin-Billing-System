package com.threesa.billing.presentation.pettycash

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
    onNavigateToHistorical: () -> Unit = {},
    viewModel: PettyCashViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val rupee = remember { NumberFormat.getCurrencyInstance(Locale("en", "IN")) }
    
    var showDatePicker by remember { mutableStateOf(false) }
    var showStoreDialog by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    // Find the name of the store that is currently selected in the UI
    val selectedStoreName = uiState.stores.find { it.id.toString() == uiState.selectedStoreId }?.name 
        ?: uiState.data?.storeName ?: "Select Store"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundCream)
    ) {
        AppHeader(onAvatarClick = onNavigateToProfile)
        
        Box(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxSize()) {
                
                // 1. Store Switcher Section
                if (uiState.stores.isNotEmpty() || uiState.data != null) {
                    Box(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
                        StoreSwitcherBar(
                            storeName = selectedStoreName,
                            stores = uiState.stores,
                            onStoreSelected = { store ->
                                viewModel.onStoreSelected(store.id?.toString() ?: "")
                            }
                        )
                    }
                }

                // 2. Main Content (Balance & Outflows)
                if (uiState.data != null) {
                    val data = uiState.data!!
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 16.dp)
                    ) {
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
                                if (data.outflows.isEmpty()) {
                                    Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                                        Text("No outflows recorded today", color = TextMuted)
                                    }
                                } else {
                                    data.outflows.forEachIndexed { index, tx ->
                                        OutflowItem(tx)
                                        if (index != data.outflows.lastIndex) HorizontalDivider(color = BorderLight)
                                    }
                                }
                                
                                HorizontalDivider(color = BorderLight)
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(SoftRedBg)
                                        .clickable { onNavigateToHistorical() }
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
                } 
                // 3. Error State
                else if (!uiState.isLoading && uiState.errorMessage != null) {
                    Box(modifier = Modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(uiState.errorMessage!!, color = Color.Red, fontSize = 14.sp, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                            Spacer(Modifier.height(16.dp))
                            Button(onClick = { viewModel.refresh() }, colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange)) {
                                Text("Retry")
                            }
                        }
                    }
                } 
                // 4. Empty State
                else if (!uiState.isLoading && uiState.stores.isEmpty()) {
                     Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No stores available", color = TextSecondary)
                    }
                }
            }

            // 5. Loading Overlay
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.05f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = PrimaryOrange)
                }
            }
        }

        // --- Dialogs ---

        if (showDatePicker) {
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(onClick = { showDatePicker = false }) { Text("OK") }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }

        if (showStoreDialog) {
            AlertDialog(
                onDismissRequest = { showStoreDialog = false },
                title = { Text("Select Store") },
                text = {
                    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                        uiState.stores.forEach { store ->
                            ListItem(
                                headlineContent = { Text(store.name ?: "Unnamed Store") },
                                modifier = Modifier.fillMaxWidth().clickable {
                                    viewModel.onStoreSelected(store.id?.toString() ?: "")
                                    showStoreDialog = false
                                }
                            )
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showStoreDialog = false }) { Text("Close") }
                }
            )
        }
    }
}
