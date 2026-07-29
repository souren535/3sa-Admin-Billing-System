package com.threesa.billing.presentation.pettycash

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.threesa.billing.presentation.pettycash.components.OutflowItem
import com.threesa.billing.ui.theme.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoricalPettyCashScreen(
    onBackClick: () -> Unit,
    viewModel: HistoricalPettyCashViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Historical Outflows", fontSize = 18.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceWhite),
                windowInsets = WindowInsets(0, 0, 0, 0)
            )
        },
        containerColor = BackgroundCream
    ) { padding ->

        PullToRefreshBox(
            isRefreshing = uiState.isLoading,
            onRefresh = { viewModel.loadHistoricalExpenses() },
            indicator = {},
            modifier = Modifier.fillMaxSize().padding(padding)
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = PrimaryOrange)
            } else if (uiState.errorMessage != null) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(uiState.errorMessage!!, color = Color.Red, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                    Spacer(Modifier.height(16.dp))
                    Button(onClick = { viewModel.loadHistoricalExpenses() }, colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange)) {
                        Text("Retry")
                    }
                }
            } else {
                Column(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp)) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .background(ChipGray, MaterialTheme.shapes.extraLarge)
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text("${uiState.expenses.size} Transactions", fontSize = 12.sp, color = TextSecondary)
                            }
                        }
                        IconButton(onClick = { showDatePicker = true }, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Filled.CalendarMonth, contentDescription = "Date Picker", tint = PrimaryOrange)
                        }
                    }
                    
                    Spacer(Modifier.height(16.dp))

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .border(1.dp, BorderLight, MaterialTheme.shapes.medium)
                            .clip(MaterialTheme.shapes.medium)
                            .background(SurfaceWhite)
                    ) {
                        if (uiState.expenses.isEmpty()) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text("No expenses found", color = TextMuted)
                            }
                        } else {
                            LazyColumn(modifier = Modifier.fillMaxSize()) {
                                itemsIndexed(uiState.expenses) { index, tx ->
                                    OutflowItem(tx)
                                    if (index != uiState.expenses.lastIndex) HorizontalDivider(color = BorderLight)
                                }
                            }
                        }
                    }
                    Spacer(Modifier.height(20.dp))
                }
            }
        }

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
    }
}
