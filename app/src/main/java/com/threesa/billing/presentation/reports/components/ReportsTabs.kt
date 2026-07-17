package com.threesa.billing.presentation.reports.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.threesa.billing.presentation.reports.ReportsTab
import com.threesa.billing.ui.theme.MintIcon
import com.threesa.billing.ui.theme.PrimaryOrange
import com.threesa.billing.ui.theme.SoftRedIcon
import com.threesa.billing.ui.theme.TextSecondary

@Composable
fun ReportsTabs(
    selectedTab: ReportsTab,
    onTabSelect: (ReportsTab) -> Unit,
    paidCount: Int,
    unpaidCount: Int,
    totalCount: Int
) {
    Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
        ReportTabItem(
            text = "All ($totalCount)",
            selected = selectedTab == ReportsTab.ALL,
            onClick = { onTabSelect(ReportsTab.ALL) }
        )
        ReportTabItem(
            text = "Paid ($paidCount)",
            selected = selectedTab == ReportsTab.PAID,
            onClick = { onTabSelect(ReportsTab.PAID) },
            selectedColor = MintIcon
        )
        ReportTabItem(
            text = "Unpaid ($unpaidCount)",
            selected = selectedTab == ReportsTab.UNPAID,
            onClick = { onTabSelect(ReportsTab.UNPAID) },
            selectedColor = SoftRedIcon
        )
    }
}

@Composable
private fun ReportTabItem(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    selectedColor: Color = PrimaryOrange
) {
    val color = if (selected) selectedColor else TextSecondary
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(vertical = 8.dp)
    ) {
        Text(text, color = color, fontWeight = if (selected) FontWeight.Medium else FontWeight.Normal, fontSize = 14.sp)
        Spacer(Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .width(50.dp)
                .height(2.dp)
                .background(if (selected) color else Color.Transparent)
        )
    }
}
