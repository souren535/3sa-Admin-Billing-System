package com.threesa.billing.presentation.pettycash.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.threesa.billing.domain.model.OutflowIconType
import com.threesa.billing.domain.model.PettyCashTransaction
import com.threesa.billing.ui.theme.*
import java.text.NumberFormat
import java.util.Locale

@Composable
fun OutflowItem(transaction: PettyCashTransaction) {
    val rupee = remember { NumberFormat.getCurrencyInstance(Locale("en", "IN")) }
    val (icon, bg, tint) = when (transaction.iconType) {
        OutflowIconType.CLEANING -> Triple(Icons.Filled.CleaningServices, PinkIconBg, PinkIconTint)
        OutflowIconType.DELIVERY -> Triple(Icons.Filled.LocalShipping, PinkIconBg, PinkIconTint)
        OutflowIconType.OFFICE -> Triple(Icons.Filled.Inventory, TanIconBg, TanIconTint)
    }

    Row(
        verticalAlignment = Alignment.Top,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(bg, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(20.dp))
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(transaction.title, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                Text(
                    rupee.format(transaction.amount),
                    color = ErrorRed,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }
            Spacer(Modifier.height(2.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Schedule, contentDescription = null, tint = TextMuted, modifier = Modifier.size(12.dp))
                Spacer(Modifier.width(4.dp))
                Text(transaction.time, fontSize = 12.sp, color = TextMuted)
            }
            Spacer(Modifier.height(6.dp))
            Text(
                "\"${transaction.reason}\"",
                fontSize = 13.sp,
                fontStyle = FontStyle.Italic,
                color = TextSecondary,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}

@Composable
private fun remember(calculation: () -> NumberFormat) =
    androidx.compose.runtime.remember { calculation() }