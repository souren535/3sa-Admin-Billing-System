package com.threesa.billing.presentation.reports.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Print
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
fun InvoiceRow(
    invoice: Invoice,
    isDownloading: Boolean = false,
    onPrintClick: () -> Unit = {}
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 12.dp)
    ) {
        // 1. Print Icon Button (First Element on Far Left)
        IconButton(
            onClick = onPrintClick,
            enabled = !isDownloading,
            modifier = Modifier.size(32.dp).padding(end = 4.dp)
        ) {
            if (isDownloading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    color = PrimaryOrange,
                    strokeWidth = 2.dp
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Print,
                    contentDescription = "Print/Download Invoice",
                    tint = PrimaryOrange,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        // 2. Invoice No., Date, Time
        Column(modifier = Modifier.weight(1.6f)) {
            Text(invoice.id, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            if (invoice.date.isNotBlank()) Text(invoice.date, fontSize = 10.sp, color = TextSecondary)
            if (invoice.time.isNotBlank()) Text(invoice.time, fontSize = 10.sp, color = TextSecondary)
        }

        // 3. Customer Details
        Column(modifier = Modifier.weight(2.0f)) {
            Text(invoice.customerName, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
            if (invoice.customerPhone.isNotBlank()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(invoice.customerPhone, fontSize = 12.sp, color = TextSecondary)
                    Spacer(Modifier.width(4.dp))
                    Icon(Icons.Filled.Call, contentDescription = null, tint = PrimaryOrange, modifier = Modifier.size(12.dp))
                }
            }
        }

        // 4. Total (₹) & Payment Method (Centered on next line under Total)
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.weight(1.2f)
        ) {
            Text(
                text = decimalFormat.format(invoice.total),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                textAlign = TextAlign.Center
            )
            invoice.paymentMethod?.let { method ->
                Spacer(Modifier.height(2.dp))
                Text(
                    text = method,
                    fontSize = 11.sp,
                    color = getPaymentMethodColor(method),
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center
                )
            }
        }

        // 5. Status Badge (End Box on Far Right)
        Row(
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1.1f)
        ) {
            StatusBadge(
                text = if (invoice.status == InvoiceStatus.PAID) "Paid" else "Unpaid",
                style = if (invoice.status == InvoiceStatus.PAID) BadgeStyle.SUCCESS else BadgeStyle.ERROR
            )
        }
    }
}
