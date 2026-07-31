package com.threesa.billing.presentation.dashboard.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.threesa.billing.domain.model.Store
import com.threesa.billing.domain.model.StoreStatus
import com.threesa.billing.presentation.common.components.BadgeStyle
import com.threesa.billing.presentation.common.components.StatusBadge
import com.threesa.billing.ui.theme.*
import com.threesa.billing.utils.rsp
import java.text.NumberFormat
import java.util.Locale

@Composable
fun StoreExpandableCard(
    store: Store,
    isExpanded: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val rupeeFormat = remember(store.totalRevenue) {
        NumberFormat.getCurrencyInstance(Locale("en", "IN"))
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() },
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(1.dp, BorderLight),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column {
            // Header Row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .background(PeachBg, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Storefront,
                            contentDescription = null,
                            tint = PrimaryOrange,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(Modifier.width(16.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = store.name,
                            fontSize = 17.rsp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Spacer(Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(
                                    color = if (store.status == StoreStatus.ACTIVE) SuccessGreen else WarningAmber,
                                    shape = CircleShape
                                )
                        )
                    }
                }
                
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(BackgroundCream, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        tint = TextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Expanded Content
            if (isExpanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .padding(bottom = 20.dp)
                ) {
                    HorizontalDivider(color = BorderLight.copy(alpha = 0.5f))
                    Spacer(Modifier.height(16.dp))
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column {
                            Text(
                                "TOTAL REVENUE",
                                fontSize = 11.rsp,
                                color = TextSecondary,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = rupeeFormat.format(store.totalRevenue),
                                fontSize = 20.rsp,
                                fontWeight = FontWeight.ExtraBold,
                                color = SuccessGreen
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                "VOLUME",
                                fontSize = 11.rsp,
                                color = TextSecondary,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = "${store.billsToday} Bills",
                                fontSize = 15.rsp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }
                    }
                }
            }
        }
    }
}