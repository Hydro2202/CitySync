package com.example.citysync.ui.screens.auth

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.citysync.data.OnboardingPage
import com.example.citysync.ui.components.CitySyncLogoHeader
import com.example.citysync.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
    onFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    val pages = listOf(
        OnboardingPage(
            title = "Report City Issues",
            description = "Easily report potholes, broken streetlights, and other city problems with your phone.",
            icon = Icons.Outlined.PhotoCamera,
            iconBackgroundColor = BlueTint,
            iconColor = DeepNavy
        ),
        OnboardingPage(
            title = "Track Your Reports",
            description = "Monitor the progress of your submissions from review to resolution in real-time.",
            icon = Icons.Outlined.Place,
            iconBackgroundColor = GreenTint,
            iconColor = GreenDark
        ),
        OnboardingPage(
            title = "Stay Informed",
            description = "Receive updates on government announcements, community alerts, and emergency notifications.",
            icon = Icons.Outlined.Notifications,
            iconBackgroundColor = OrangeTint,
            iconColor = OrangeDark
        )
    )

    val pagerState = rememberPagerState(pageCount = { pages.size })
    val scope = rememberCoroutineScope()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(
                    horizontal = DesignTokens.OnboardingHeaderPaddingH,
                    vertical = DesignTokens.OnboardingHeaderPaddingV / 2 // Adjusting vertical padding since statusBarsPadding adds some
                ),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            CitySyncLogoHeader()
            Text(
                text = "Skip",
                color = TextMuted,
                fontSize = DesignTokens.OnboardingSkipSize,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .clickable { onFinished() }
                    .padding(8.dp)
            )
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f)
        ) { position ->
            val page = pages[position]
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = DesignTokens.OnboardingContentPaddingH),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(DesignTokens.OnboardingFeatureIconBox)
                        .background(page.iconBackgroundColor, RoundedCornerShape(DesignTokens.OnboardingFeatureIconCorner)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = page.icon,
                        contentDescription = null,
                        tint = page.iconColor,
                        modifier = Modifier.size(DesignTokens.OnboardingFeatureIconSize)
                    )
                }

                Spacer(modifier = Modifier.height(DesignTokens.OnboardingTitleTopGap))

                Text(
                    text = page.title,
                    fontSize = DesignTokens.OnboardingTitleSize,
                    fontWeight = FontWeight.Bold,
                    color = TextDark,
                    textAlign = TextAlign.Center,
                    lineHeight = 30.sp
                )

                Spacer(modifier = Modifier.height(DesignTokens.OnboardingTitleDescGap))

                Text(
                    text = page.description,
                    fontSize = DesignTokens.OnboardingDescSize,
                    color = TextMuted,
                    textAlign = TextAlign.Center,
                    lineHeight = DesignTokens.OnboardingDescLineHeight,
                    fontWeight = FontWeight.Normal
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = DesignTokens.OnboardingFooterPaddingH)
                .padding(bottom = DesignTokens.OnboardingFooterPaddingBottom),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(DesignTokens.OnboardingDotGap),
                modifier = Modifier.padding(bottom = DesignTokens.OnboardingDotButtonGap)
            ) {
                repeat(pages.size) { index ->
                    val isActive = pagerState.currentPage == index
                    Box(
                        modifier = Modifier
                            .height(DesignTokens.OnboardingDotHeight)
                            .width(
                                if (isActive) DesignTokens.OnboardingDotActiveWidth
                                else DesignTokens.OnboardingDotInactiveWidth
                            )
                            .background(
                                color = if (isActive) DeepNavy else Color(0xFFD1D5DB),
                                shape = RoundedCornerShape(50)
                            )
                    )
                }
            }

            Button(
                onClick = {
                    if (pagerState.currentPage < pages.size - 1) {
                        scope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    } else {
                        onFinished()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(DesignTokens.OnboardingButtonHeight),
                colors = ButtonDefaults.buttonColors(containerColor = DeepNavy),
                shape = RoundedCornerShape(DesignTokens.OnboardingButtonCorner),
                contentPadding = PaddingValues(0.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = if (pagerState.currentPage == pages.size - 1) "Get Started" else "Next",
                        fontSize = DesignTokens.OnboardingButtonTextSize,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(DesignTokens.OnboardingButtonArrowGap))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(DesignTokens.OnboardingButtonArrowSize)
                    )
                }
            }
        }
    }
}
