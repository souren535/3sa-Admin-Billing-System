package com.threesa.billing.presentation.inventory.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.threesa.billing.domain.model.Product
import com.threesa.billing.domain.model.StockStatus
import com.threesa.billing.presentation.common.components.BadgeStyle
import com.threesa.billing.presentation.common.components.StatusBadge
import com.threesa.billing.ui.theme.TextMuted
import com.threesa.billing.ui.theme.TextSecondary

@Composable
fun ProductRow(product: Product) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Column(modifier = Modifier.weight(2.5f)) {
            Text(
                product.name,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Text(
            "${product.stock}",
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(0.8f)
        )
        Row(
            verticalAlignment = Alignment.CenterVertically, 
            horizontalArrangement = Arrangement.End,
            modifier = Modifier.weight(1.2f)
        ) {
            StatusBadge(
                text = if (product.status == StockStatus.IN_STOCK) "In Stock" else "Low Stock",
                style = if (product.status == StockStatus.IN_STOCK) BadgeStyle.SUCCESS else BadgeStyle.WARNING
            )
        }
    }
}