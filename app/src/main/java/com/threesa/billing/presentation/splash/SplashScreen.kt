package com.threesa.billing.presentation.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.threesa.billing.R
import com.threesa.billing.ui.theme.BackgroundCream
import com.threesa.billing.ui.theme.PrimaryOrange
import com.threesa.billing.ui.theme.TextPrimary
import com.threesa.billing.ui.theme.TextSecondary
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    isLoggedIn: Boolean?,
    onSplashComplete: (Boolean) -> Unit
) {
    val scale = remember { Animatable(0.7f) }
    val alpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        scale.animateTo(
            targetValue = 1.0f,
            animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing)
        )
    }

    LaunchedEffect(Unit) {
        alpha.animateTo(
            targetValue = 1.0f,
            animationSpec = tween(durationMillis = 600)
        )
    }

    LaunchedEffect(isLoggedIn) {
        delay(1800)
        onSplashComplete(isLoggedIn == true)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFFFFFFF),
                        BackgroundCream,
                        Color(0xFFFFF6F0)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .scale(scale.value)
                .alpha(alpha.value),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Ultra-Crisp Logo Image Container
            Box(
                modifier = Modifier
                    .size(140.dp)
                    .clip(RoundedCornerShape(32.dp))
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_launcher),
                    contentDescription = "Billing System Logo",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp)
                )
            }

            Spacer(Modifier.height(28.dp))

            Text(
                text = "Billing System",
                fontSize = 26.sp,
                fontWeight = FontWeight.ExtraBold,
                color = TextPrimary
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = "Smart POS & Inventory Solution",
                fontSize = 14.sp,
                color = PrimaryOrange,
                fontWeight = FontWeight.SemiBold
            )
        }

        // Modern Bottom Loading Indicator
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 60.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(32.dp),
                color = PrimaryOrange,
                strokeWidth = 3.dp
            )
            Spacer(Modifier.height(14.dp))
            Text(
                text = "Loading application...",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = TextSecondary
            )
        }
    }
}
