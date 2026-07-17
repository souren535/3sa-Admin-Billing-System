package com.threesa.billing.presentation.reports.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
import com.threesa.billing.domain.model.Invoice
import com.threesa.billing.domain.model.InvoiceStatus
import com.threesa.billing.presentation.common.components.BadgeStyle
import com.threesa.billing.presentation.common.components.StatusBadge
import com.threesa.billing.ui.theme.LavenderIcon
import com.threesa.billing.ui.theme.MintIcon
import com.threesa.billing.ui.theme.PrimaryOrange
import com.threesa.billing.ui.theme.TextPrimary
import com.threesa.billing.ui.theme.TextSecondary
import java.text.NumberFormat
import java.util.Locale

private fun getPaymentMethodColor(method: String): Color {
    return when (method.lowercase(Locale.getDefault())) {
        "online" -> Color(0xFF2563EB) // Blue
        "cash" -> MintIcon           // Green
        "card" -> LavenderIcon       // Purple
        else -> TextSecondary
    }
}

private val decimalFormat = NumberFormat.getNumberInstance(Locale("en", "IN")).apply {
    minimumFractionDigits = 2
    maximumFractionDigits = 2
}

@Composable
fun InvoiceRow(invoice: Invoice) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Column(modifier = Modifier.weight(1.8f)) {
            Text(invoice.id, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Spacer(Modifier.height(4.dp))
            Text(invoice.date, fontSize = 10.sp, color = TextSecondary)
            Text(invoice.time, fontSize = 10.sp, color = TextSecondary)
        }
        Column(modifier = Modifier.weight(2.2f)) {
            Text(invoice.customerName, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
            Spacer(Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(invoice.customerPhone, fontSize = 12.sp, color = TextSecondary)
                Spacer(Modifier.width(4.dp))
                Icon(Icons.Filled.Call, contentDescription = null, tint = PrimaryOrange, modifier = Modifier.size(12.dp))
            }
        }
        Column(
            horizontalAlignment = Alignment.End,
            modifier = Modifier.weight(1.2f)
        ) {
            Text(
                text = decimalFormat.format(invoice.total),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = TextPrimary
            )
            invoice.paymentMethod?.let { method ->
                Spacer(Modifier.height(4.dp))
                Text(
                    text = method,
                    fontSize = 11.sp,
                    color = getPaymentMethodColor(method),
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
        Row(horizontalArrangement = Arrangement.End, modifier = Modifier.weight(1.3f)) {
            StatusBadge(
                text = if (invoice.status == InvoiceStatus.PAID) "Paid" else "Unpaid",
                style = if (invoice.status == InvoiceStatus.PAID) BadgeStyle.SUCCESS else BadgeStyle.ERROR
            )
        }
    }
}
