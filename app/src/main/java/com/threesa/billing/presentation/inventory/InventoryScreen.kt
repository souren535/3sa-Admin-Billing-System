package com.threesa.billing.presentation.inventory

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.threesa.billing.presentation.common.components.AppHeader
import com.threesa.billing.presentation.common.components.StoreSwitcherBar
import com.threesa.billing.presentation.inventory.components.InventoryTabs
import com.threesa.billing.presentation.inventory.components.LowStockAlertRow
import com.threesa.billing.presentation.inventory.components.ProductRow
import com.threesa.billing.presentation.inventory.components.StatCard
import com.threesa.billing.ui.theme.*

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.material3.pulltorefresh.PullToRefreshBox

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun InventoryScreen(
    onNavigateToProfile: () -> Unit = {},
    viewModel: InventoryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var categoryExpanded by remember { mutableStateOf(false) }

    val listState = rememberLazyListState()
    var showTopSection by remember { mutableStateOf(true) }

    val isImeVisible = WindowInsets.isImeVisible
    LaunchedEffect(isImeVisible) {
        if (isImeVisible) {
            showTopSection = false
        }
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
            .padding(bottom = 90.dp) // Fixed height before reaching floating bottom nav bar
    ) {
        AppHeader(onAvatarClick = onNavigateToProfile)
        
        PullToRefreshBox(
            isRefreshing = uiState.isLoading,
            onRefresh = { viewModel.loadInventory(uiState.selectedStoreId ?: "1") },
            indicator = {},
            modifier = Modifier.fillMaxSize().weight(1f)
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = PrimaryOrange
                )
            }

            if (uiState.errorMessage != null && uiState.data == null) {
                Column(
                    modifier = Modifier.align(Alignment.Center).padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = uiState.errorMessage!!, color = Color.Red, textAlign = TextAlign.Center)
                    Spacer(Modifier.height(8.dp))
                    Button(onClick = { uiState.selectedStoreId?.let { viewModel.loadInventory(it) } }) {
                        Text("Retry")
                    }
                }
            } else if (uiState.data != null) {
                val data = uiState.data!!
                val products = viewModel.filteredProducts()

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
                        val selectedStoreName = uiState.stores.find { it.id.toString() == uiState.selectedStoreId }?.name
                            ?: uiState.data?.storeName ?: "Select Store"

                        StoreSwitcherBar(
                            storeName = selectedStoreName,
                            stores = uiState.stores,
                            onStoreSelected = { store ->
                                viewModel.onStoreSelected(store.id?.toString() ?: "")
                            }
                        )
                        Spacer(Modifier.height(16.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                            StatCard(
                                label = "Total Products", value = "${data.totalProducts}",
                                icon = Icons.Filled.Inventory2, bgColor = LavenderBg, iconColor = LavenderIcon,
                                modifier = Modifier.weight(1f)
                            )
                            StatCard(
                                label = "Total Stock", value = "${data.totalStock}",
                                icon = Icons.Filled.Layers, bgColor = MintBg, iconColor = MintIcon,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Spacer(Modifier.height(12.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                            StatCard(
                                label = "Low Stock", value = "${data.lowStockCount}",
                                icon = Icons.Filled.Warning, bgColor = PeachBg, iconColor = PeachIcon,
                                modifier = Modifier.weight(1f)
                            )
                            StatCard(
                                label = "Out of Stock", value = "${data.outOfStockCount}",
                                icon = Icons.Filled.Close, bgColor = SoftRedBg, iconColor = SoftRedIcon,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
                
                Spacer(Modifier.height(20.dp))

                InventoryTabs(
                    selectedTab = uiState.selectedTab,
                    onTabSelect = viewModel::onTabSelect
                )
                Spacer(Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .pointerInput(Unit) {
                            detectTapGestures {
                                showTopSection = false
                            }
                        }
                ) {
                    OutlinedTextField(
                        value = uiState.searchQuery,
                        onValueChange = viewModel::onSearchChange,
                        placeholder = { Text("Search products...") },
                        leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                        trailingIcon = {
                            if (uiState.searchQuery.isNotEmpty()) {
                                IconButton(onClick = { viewModel.onSearchChange("") }) {
                                    Icon(Icons.Filled.Clear, contentDescription = "Clear search")
                                }
                            }
                        },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .onFocusChanged { focusState ->
                                if (focusState.isFocused) {
                                    showTopSection = false
                                }
                            }
                    )
                }
                Spacer(Modifier.height(12.dp))

                if (uiState.selectedTab == InventoryTab.ALL) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .border(1.dp, BorderLight, MaterialTheme.shapes.medium)
                            .clip(MaterialTheme.shapes.medium)
                            .background(SurfaceWhite)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(SurfaceMuted)
                                .padding(horizontal = 16.dp, vertical = 12.dp)
                        ) {
                            Text("PRODUCT", fontSize = 13.sp, color = TextSecondary, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(2.5f))
                            Text("STOCK", fontSize = 13.sp, color = TextSecondary, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(0.8f))
                            Text(
                                "STATUS",
                                fontSize = 13.sp,
                                color = TextSecondary,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.weight(1.2f),
                                textAlign = TextAlign.End
                            )
                        }
                        HorizontalDivider(color = BorderLight)
                        
                        LazyColumn(
                            state = listState,
                            modifier = Modifier
                                .fillMaxSize()
                                .nestedScroll(nestedScrollConnection)
                        ) {
                            itemsIndexed(products, key = { _, p -> p.id }) { index, product ->
                                ProductRow(product)
                                if (index != products.lastIndex) {
                                    HorizontalDivider(color = BorderLight)
                                }
                            }
                        }
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .border(1.dp, PeachBg, MaterialTheme.shapes.medium)
                            .clip(MaterialTheme.shapes.medium)
                            .background(SurfaceWhite)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(PeachBg)
                                .padding(horizontal = 16.dp, vertical = 12.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.Warning, contentDescription = null, tint = PrimaryOrange, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(8.dp))
                                Text("Low Stock Alerts", color = PrimaryOrange, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("View All", color = Color(0xFF3B82F6), fontSize = 13.sp, fontWeight = FontWeight.Medium)
                                Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = Color(0xFF3B82F6), modifier = Modifier.size(16.dp))
                            }
                        }
                        HorizontalDivider(color = BorderLight)
                        
                        LazyColumn(
                            state = listState,
                            modifier = Modifier
                                .fillMaxSize()
                                .nestedScroll(nestedScrollConnection)
                        ) {
                            itemsIndexed(products, key = { _, p -> p.id }) { index, product ->
                                LowStockAlertRow(product)
                                if (index != products.lastIndex) {
                                    HorizontalDivider(color = BorderLight)
                                }
                            }
                        }
                    }
                }
                Spacer(Modifier.height(16.dp))
            }
        }
    }
}
}
