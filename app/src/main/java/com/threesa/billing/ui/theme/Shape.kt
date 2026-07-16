package com.threesa.billing.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val BillingSystemShapes = Shapes(
    extraSmall = RoundedCornerShape(6.dp),
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(14.dp),   // cards (dashboard, petty cash, inventory)
    large = RoundedCornerShape(18.dp),
    extraLarge = RoundedCornerShape(24.dp)
)