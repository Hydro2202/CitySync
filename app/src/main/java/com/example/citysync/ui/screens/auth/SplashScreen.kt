package com.example.citysync.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.citysync.ui.components.CitySyncLogoSplash
import com.example.citysync.ui.theme.DeepNavy
import com.example.citysync.ui.theme.DesignTokens

@Composable
fun SplashScreen(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DeepNavy)
    ) {
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CitySyncLogoSplash()

//            Spacer(modifier = Modifier.height(DesignTokens.SplashTaglineTopGap))

            Text(
                text = "Citizen Services Platform",
                color = Color.White,
                fontSize = DesignTokens.SplashTaglineSize,
                fontWeight = FontWeight.Medium,
                lineHeight = 24.sp
            )
        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = DesignTokens.SplashDotsBottomPadding),
            horizontalArrangement = Arrangement.spacedBy(DesignTokens.SplashDotsGap)
        ) {
            repeat(3) { index ->
                Box(
                    modifier = Modifier
                        .size(DesignTokens.SplashDotsSize)
                        .background(
                            color = if (index == 0) Color.White else Color.White.copy(alpha = 0.4f),
                            shape = CircleShape
                        )
                )
            }
        }
    }
}
