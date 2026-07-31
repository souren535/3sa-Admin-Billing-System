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
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.background
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.threesa.billing.domain.model.Product
import com.threesa.billing.domain.model.StockStatus
import com.threesa.billing.ui.theme.TextMuted
import com.threesa.billing.ui.theme.TextSecondary
import com.threesa.billing.ui.theme.TextPrimary
import com.threesa.billing.ui.theme.SuccessGreen
import com.threesa.billing.ui.theme.WarningAmber

@Composable
fun ProductRow(product: Product) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(if (product.status == StockStatus.IN_STOCK) SuccessGreen else WarningAmber)
        )
        Spacer(Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = product.name,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = product.category,
                fontSize = 12.sp,
                color = TextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        
        Spacer(Modifier.width(48.dp))

        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = "Stock: ${product.stock} pcs",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = if (product.status == StockStatus.IN_STOCK) "In Stock" else "Low Stock",
                color = if (product.status == StockStatus.IN_STOCK) SuccessGreen else WarningAmber,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}