package com.threesa.billing.presentation.common.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.threesa.billing.ui.theme.*

enum class BadgeStyle { SUCCESS, WARNING, ERROR }

@Composable
fun StatusBadge(
    text: String,
    style: BadgeStyle,
    modifier: Modifier = Modifier
) {
    val (bg, fg) = when (style) {
        BadgeStyle.SUCCESS -> SuccessGreenBg to SuccessGreen
        BadgeStyle.WARNING -> WarningAmberBg to WarningAmber
        BadgeStyle.ERROR -> ErrorRedBg to ErrorRed
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .background(bg, RoundedCornerShape(50))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(text, color = fg, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
    }
}
