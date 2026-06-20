package com.example.citysync.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.citysync.R
import com.example.citysync.ui.theme.CitySyncTheme
import com.example.citysync.ui.theme.DesignTokens

@Composable
fun CitySyncLogo(
    modifier: Modifier = Modifier,
    contentDescription: String = "CitySync Logo",
    contentScale: ContentScale = ContentScale.Fit
) {
    // LOGO
    Image(
        painter = painterResource(id = R.drawable.city_sync),
        contentDescription = contentDescription,
        modifier = modifier,
        contentScale = contentScale
    )
}

/** Splash screen — brand logo. */
@Composable
fun CitySyncLogoSplash(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(DesignTokens.SplashLogoSize),
        contentAlignment = Alignment.Center
    ) {
        CitySyncLogo(
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Fit
        )
    }
}

/** Onboarding  */
@Composable
fun CitySyncLogoHeader(modifier: Modifier = Modifier) {
    CitySyncLogo(
        modifier = modifier.size(DesignTokens.OnboardingHeaderLogoSize),
        contentScale = ContentScale.Fit
    )
}

/** Sign In — centered logo in white badge. */
@Composable
fun CitySyncLogoSignIn(modifier: Modifier = Modifier) {
    val shape = RoundedCornerShape(DesignTokens.SignInLogoCorner)
    Box(
        modifier = modifier
            .size(DesignTokens.SignInLogoSize)
            .clip(shape)
            .background(Color.White, shape)
            .padding(DesignTokens.SignInLogoInnerPadding),
        contentAlignment = Alignment.Center
    ) {
        CitySyncLogo(modifier = Modifier.fillMaxSize())
    }
}

/** Sign Up header logo. */
@Composable
fun CitySyncLogoSignUp(modifier: Modifier = Modifier) {
    CitySyncLogo(
        modifier = modifier.size(DesignTokens.SignUpHeaderLogoSize)
    )
}

@Composable
fun CitySyncLogoBadge(
    size: Dp = DesignTokens.OnboardingHeaderLogoSize,
    showBorder: Boolean = false,
    backgroundColor: Color = Color.Transparent,
    borderColor: Color = Color(0xFFE0E0E0),
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(8.dp)
    Box(
        modifier = modifier
            .size(size)
            .then(
                if (backgroundColor != Color.Transparent) {
                    Modifier.background(backgroundColor, shape)
                } else {
                    Modifier
                }
            )
            .then(
                if (showBorder) {
                    Modifier.border(1.dp, borderColor, shape)
                } else {
                    Modifier
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        CitySyncLogo(modifier = Modifier.fillMaxSize())
    }
}

@Preview(showBackground = true)
@Composable
fun CitySyncLogoSplashPreview() {
    CitySyncTheme {
        CitySyncLogoSplash()
    }
}
