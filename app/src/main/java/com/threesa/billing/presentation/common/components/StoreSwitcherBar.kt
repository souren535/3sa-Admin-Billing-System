package com.threesa.billing.presentation.common.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.threesa.billing.data.remote.dto.StoreDto
import com.threesa.billing.ui.theme.*

@Composable
fun StoreSwitcherBar(
    storeName: String,
    stores: List<StoreDto> = emptyList(),
    onStoreSelected: (StoreDto) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.medium)
                .border(1.dp, BorderLight, MaterialTheme.shapes.medium)
                .background(SurfaceWhite)
                .clickable { expanded = true }
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.Storefront,
                    contentDescription = null,
                    tint = TextSecondary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(
                        text = "SELECTED STORE",
                        fontSize = 10.sp,
                        color = TextSecondary,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = if (storeName.isEmpty()) "Select Store" else storeName,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
            }
            OutlinedButton(
                onClick = { expanded = true },
                shape = MaterialTheme.shapes.small,
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text("Switch Store", fontSize = 13.sp, color = PrimaryOrange)
                Spacer(Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Filled.KeyboardArrowDown,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = PrimaryOrange
                )
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth(0.9f)
        ) {
            if (stores.isEmpty()) {
                DropdownMenuItem(
                    text = { Text("No stores available", color = TextSecondary) },
                    onClick = { expanded = false }
                )
            } else {
                stores.forEach { store ->
                    DropdownMenuItem(
                        text = { Text(store.name ?: "Unknown Store") },
                        onClick = {
                            expanded = false
                            onStoreSelected(store)
                        }
                    )
                }
            }
        }
    }
}
