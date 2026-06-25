package com.example.citysync.ui.screens.auth

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.example.citysync.data.AuthManager
import com.example.citysync.ui.components.CitySyncLogoSignIn
import com.example.citysync.ui.components.CitySyncLogoSignUp
import com.example.citysync.ui.theme.*
import io.github.jan.supabase.exceptions.RestException
import io.ktor.client.plugins.HttpRequestTimeoutException
import kotlinx.coroutines.launch
import java.net.ConnectException

@Composable
fun SignInScreen(
    onSignInSuccess: () -> Unit,
    onNavigateToSignUp: () -> Unit,
    onNavigateToForgotPassword: () -> Unit
) {
    val authManager = remember { AuthManager() }
    val scope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    var emailOrPhone by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    var rememberMe by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)
            .padding(horizontal = DesignTokens.ScreenPaddingHorizontal),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 64.dp) // Generous top padding
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CitySyncLogoSignIn()
            Text(
                "Welcome back",
                fontSize = DesignTokens.SignInTitleSize,
                fontWeight = FontWeight.Bold,
                color = TextDark,
                lineHeight = 32.sp
            )
            Text(
                "Sign in to your account",
                fontSize = DesignTokens.SignInSubtitleSize,
                color = TextMuted,
                fontWeight = FontWeight.Normal
            )

            Spacer(modifier = Modifier.height(DesignTokens.SignInFormTopGap))

            if (errorMessage != null) {
                Text(errorMessage!!, color = Color.Red, fontSize = 12.sp, modifier = Modifier.padding(bottom = 8.dp))
            }

            AuthLabel("Email Address")
            AuthOutlinedField(
                value = emailOrPhone,
                onValueChange = { emailOrPhone = it },
                placeholder = "Enter your email",
                leadingIcon = { Icon(Icons.Outlined.Email, null, tint = TextMuted, modifier = Modifier.size(DesignTokens.AuthFieldIconSize)) }
            )

            Spacer(modifier = Modifier.height(DesignTokens.AuthFieldGap))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                AuthLabel("Password", modifier = Modifier)
                Text(
                    "Forgot Password?",
                    color = DeepNavy,
                    fontSize = DesignTokens.AuthLinkSize,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable { onNavigateToForgotPassword() }
                )
            }
            AuthOutlinedField(
                value = password,
                onValueChange = { password = it },
                placeholder = "Enter your password",
                leadingIcon = { Icon(Icons.Outlined.Lock, null, tint = TextMuted, modifier = Modifier.size(DesignTokens.AuthFieldIconSize)) },
                trailingIcon = {
                    val icon = if (passwordVisible) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(icon, contentDescription = null, tint = TextMuted, modifier = Modifier.size(DesignTokens.AuthFieldIconSize))
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation()
            )

            Spacer(modifier = Modifier.height(DesignTokens.AuthFieldGap))

            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = rememberMe,
                    onCheckedChange = { rememberMe = it },
                    colors = CheckboxDefaults.colors(checkedColor = DeepNavy),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Remember me", color = TextDark, fontSize = DesignTokens.AuthLabelSize, fontWeight = FontWeight.Normal)
            }

            Spacer(modifier = Modifier.height(24.dp))

            val isSignInEnabled = emailOrPhone.isNotBlank() && password.isNotBlank() && !isLoading

            Button(
                onClick = {
                    isLoading = true
                    errorMessage = null
                    scope.launch {
                        try {
                            authManager.signIn(emailOrPhone, password)
                            onSignInSuccess()
                        } catch (e: Exception) {
                            val msg = e.message ?: ""
                            errorMessage = when {
                                msg.contains("Invalid login credentials", true) -> "Invalid email or password."
                                msg.contains("Email not confirmed", true) -> "Please confirm your email address."
                                else -> "Login failed. Please check your connection."
                            }
                        } finally {
                            isLoading = false
                        }
                    }
                },
                enabled = isSignInEnabled,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(DesignTokens.AuthButtonHeight),
                colors = ButtonDefaults.buttonColors(
                    containerColor = DeepNavy,
                    disabledContainerColor = DeepNavy.copy(alpha = 0.5f)
                ),
                shape = RoundedCornerShape(DesignTokens.AuthButtonCorner),
                contentPadding = PaddingValues(0.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                } else {
                    Text("Sign In", fontSize = DesignTokens.AuthButtonTextSize, fontWeight = FontWeight.SemiBold, color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFFE0E0E0), thickness = 1.dp)
                Text(
                    "Or continue with",
                    color = TextMuted,
                    fontSize = DesignTokens.AuthDividerTextSize,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFFE0E0E0), thickness = 1.dp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(DesignTokens.AuthSocialGap)
            ) {
                OutlinedButton(
                    onClick = {},
                    modifier = Modifier
                        .weight(1f)
                        .height(DesignTokens.AuthSocialButtonHeight),
                    shape = RoundedCornerShape(DesignTokens.AuthFieldCorner),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextDark),
                    border = ButtonDefaults.outlinedButtonBorder.copy(width = 1.dp)
                ) {
                    Text("Google", color = TextDark, fontSize = DesignTokens.AuthLabelSize, fontWeight = FontWeight.Medium)
                }
                OutlinedButton(
                    onClick = {},
                    modifier = Modifier
                        .weight(1f)
                        .height(DesignTokens.AuthSocialButtonHeight),
                    shape = RoundedCornerShape(DesignTokens.AuthFieldCorner),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextDark),
                    border = ButtonDefaults.outlinedButtonBorder.copy(width = 1.dp)
                ) {
                    Text("Facebook", color = TextDark, fontSize = DesignTokens.AuthLabelSize, fontWeight = FontWeight.Medium)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
            Row {
                Text("Don't have an account? ", color = TextMuted, fontSize = DesignTokens.AuthLabelSize)
                Text(
                    "Sign Up",
                    color = DeepNavy,
                    fontWeight = FontWeight.Bold,
                    fontSize = DesignTokens.AuthLabelSize,
                    modifier = Modifier.clickable { onNavigateToSignUp() }
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun ForgotPasswordScreen(onBack: () -> Unit) {
    BackHandler(onBack = onBack)
    var email by rememberSaveable { mutableStateOf("") }
    var isLinkSent by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)
            .statusBarsPadding()
            .padding(horizontal = DesignTokens.ScreenPaddingHorizontal)
    ) {
        Row(
            modifier = Modifier
                .offset(x = (-12).dp) // Align icon with the left edge
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = ripple(bounded = false, radius = 24.dp),
                    onClick = onBack
                )
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                contentDescription = "Back",
                tint = TextMuted,
                modifier = Modifier.size(DesignTokens.ForgotBackIconSize)
            )
            Text(
                "Back",
                color = TextMuted,
                fontSize = DesignTokens.ForgotBackTextSize,
                fontWeight = FontWeight.Medium
            )
        }

        if (!isLinkSent) {
            Spacer(modifier = Modifier.height(DesignTokens.ForgotTitleTopGap))
            Text(
                "Forgot Password?",
                fontSize = DesignTokens.ForgotTitleSize,
                fontWeight = FontWeight.Bold,
                color = TextDark,
                lineHeight = 32.sp
            )
            Spacer(modifier = Modifier.height(DesignTokens.ForgotDescTopGap))
            Text(
                "Enter your registered email and we'll send you a link to reset your password.",
                color = TextMuted,
                fontSize = DesignTokens.ForgotDescSize,
                lineHeight = DesignTokens.ForgotDescLineHeight,
                fontWeight = FontWeight.Normal
            )

            Spacer(modifier = Modifier.height(DesignTokens.ForgotFormTopGap))
            AuthLabel("Email Address")
            AuthOutlinedField(
                value = email,
                onValueChange = { email = it },
                placeholder = "Enter your email",
                leadingIcon = {
                    Icon(
                        Icons.Outlined.Email,
                        null,
                        tint = TextMuted,
                        modifier = Modifier.size(DesignTokens.AuthFieldIconSize)
                    )
                }
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = { isLinkSent = true },
                enabled = email.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(DesignTokens.AuthButtonHeight),
                colors = ButtonDefaults.buttonColors(
                    containerColor = DeepNavy,
                    disabledContainerColor = DeepNavy.copy(alpha = 0.5f)
                ),
                shape = RoundedCornerShape(DesignTokens.AuthButtonCorner),
                contentPadding = PaddingValues(0.dp)
            ) {
                Text(
                    "Send Reset Link",
                    fontSize = DesignTokens.AuthButtonTextSize,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            }
        } else {
            Spacer(modifier = Modifier.height(DesignTokens.ForgotTitleTopGap))
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(GreenTint, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = GreenDark,
                    modifier = Modifier.size(32.dp)
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                "Reset Link Sent!",
                fontSize = DesignTokens.ForgotTitleSize,
                fontWeight = FontWeight.Bold,
                color = TextDark
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                "We've sent a password reset link to $email. Please check your inbox and follow the instructions to reset your password.",
                color = TextMuted,
                fontSize = DesignTokens.ForgotDescSize,
                lineHeight = DesignTokens.ForgotDescLineHeight,
                fontWeight = FontWeight.Normal
            )
            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = onBack,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(DesignTokens.AuthButtonHeight),
                colors = ButtonDefaults.buttonColors(containerColor = DeepNavy),
                shape = RoundedCornerShape(DesignTokens.AuthButtonCorner)
            ) {
                Text("Back to Sign In", color = Color.White, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
fun SignUpWizardScreen(onSignUpComplete: () -> Unit, onSignInRedirect: () -> Unit) {
    val authManager = remember { AuthManager() }
    val scope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    var currentStep by rememberSaveable { mutableStateOf(1) }

    var firstName by rememberSaveable { mutableStateOf("") }
    var lastName by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var phone by rememberSaveable { mutableStateOf("") }
    var address by rememberSaveable { mutableStateOf("") }

    var createPassword by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }
    var createPasswordVisible by rememberSaveable { mutableStateOf(false) }
    var confirmPasswordVisible by rememberSaveable { mutableStateOf(false) }
    var agreeToTerms by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)
            .padding(DesignTokens.ScreenPaddingHorizontal)
            .verticalScroll(rememberScrollState())
    ) {
        if (currentStep == 2) {
            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                contentDescription = "Back",
                tint = TextMuted,
                modifier = Modifier
                    .size(DesignTokens.ForgotBackIconSize)
                    .clickable { currentStep = 1 }
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            CitySyncLogoSignUp(modifier = Modifier.padding(bottom = DesignTokens.SignUpHeaderLogoBottomGap))
        }

        Text("Create Account", fontSize = DesignTokens.SignUpTitleSize, fontWeight = FontWeight.Bold, color = TextDark, lineHeight = 32.sp)
        Text(
            text = if (currentStep == 1) "Step 1 of 2 — Personal Information" else "Step 2 of 2 — Security",
            color = TextMuted,
            fontSize = DesignTokens.SignUpSubtitleSize,
            modifier = Modifier.padding(top = 4.dp),
            fontWeight = FontWeight.Normal
        )

        LinearProgressIndicator(
            progress = { if (currentStep == 1) 0.5f else 1f },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = DesignTokens.SignUpProgressVerticalPadding)
                .height(DesignTokens.SignUpProgressHeight),
            color = DeepNavy,
            trackColor = Color(0xFFE0E0E0),
            strokeCap = StrokeCap.Round
        )

        if (errorMessage != null) {
            Text(errorMessage!!, color = Color.Red, fontSize = 12.sp, modifier = Modifier.padding(bottom = 8.dp))
        }

        if (currentStep == 1) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(DesignTokens.SignUpNameRowGap)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    AuthLabel("First Name")
                    AuthOutlinedField(
                        value = firstName,
                        onValueChange = { firstName = it },
                        placeholder = "Raiden",
                        leadingIcon = { Icon(Icons.Outlined.Person, null, tint = TextMuted, modifier = Modifier.size(DesignTokens.AuthFieldIconSize)) }
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    AuthLabel("Last Name")
                    AuthOutlinedField(
                        value = lastName,
                        onValueChange = { lastName = it },
                        placeholder = "Villapando"
                    )
                }
            }
            Spacer(modifier = Modifier.height(DesignTokens.SignUpFieldGap))
            AuthLabel("Email Address")
            AuthOutlinedField(
                value = email,
                onValueChange = { email = it },
                placeholder = "raiden@example.com",
                leadingIcon = { Icon(Icons.Outlined.Email, null, tint = TextMuted, modifier = Modifier.size(DesignTokens.AuthFieldIconSize)) }
            )

            Spacer(modifier = Modifier.height(DesignTokens.SignUpFieldGap))
            AuthLabel("Phone Number")
            AuthOutlinedField(
                value = phone,
                onValueChange = { phone = it },
                placeholder = "+63 912 345 6789",
                leadingIcon = { Icon(Icons.Outlined.Phone, null, tint = TextMuted, modifier = Modifier.size(DesignTokens.AuthFieldIconSize)) }
            )

            Spacer(modifier = Modifier.height(DesignTokens.SignUpFieldGap))
            AuthLabel("Complete Address")
            AuthOutlinedField(
                value = address,
                onValueChange = { address = it },
                placeholder = "Street, Barangay, Municipality",
                leadingIcon = { Icon(Icons.Outlined.Place, null, tint = TextMuted, modifier = Modifier.size(DesignTokens.AuthFieldIconSize)) },
                minLines = 3
            )

            Spacer(modifier = Modifier.height(32.dp))
            
            val isStep1Valid = firstName.isNotBlank() && lastName.isNotBlank() && 
                               email.isNotBlank() && phone.isNotBlank() && address.isNotBlank()

            Button(
                onClick = { currentStep = 2 },
                enabled = isStep1Valid,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(DesignTokens.AuthButtonHeight),
                colors = ButtonDefaults.buttonColors(
                    containerColor = DeepNavy,
                    disabledContainerColor = DeepNavy.copy(alpha = 0.5f)
                ),
                shape = RoundedCornerShape(DesignTokens.AuthButtonCorner),
                contentPadding = PaddingValues(0.dp)
            ) {
                Text("Continue", color = Color.White, fontSize = DesignTokens.AuthButtonTextSize, fontWeight = FontWeight.SemiBold)
            }
        } else {
            AuthLabel("Create Password")
            AuthOutlinedField(
                value = createPassword,
                onValueChange = { createPassword = it },
                placeholder = "Minimum 8 characters",
                leadingIcon = { Icon(Icons.Outlined.Lock, null, tint = TextMuted, modifier = Modifier.size(DesignTokens.AuthFieldIconSize)) },
                trailingIcon = {
                    val icon = if (createPasswordVisible) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff
                    IconButton(onClick = { createPasswordVisible = !createPasswordVisible }) {
                        Icon(icon, contentDescription = null, tint = TextMuted, modifier = Modifier.size(DesignTokens.AuthFieldIconSize))
                    }
                },
                visualTransformation = if (createPasswordVisible) VisualTransformation.None else PasswordVisualTransformation()
            )

            Spacer(modifier = Modifier.height(DesignTokens.SignUpFieldGap))
            AuthLabel("Confirm Password")
            AuthOutlinedField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                placeholder = "Re-enter your password",
                leadingIcon = { Icon(Icons.Outlined.Lock, null, tint = TextMuted, modifier = Modifier.size(DesignTokens.AuthFieldIconSize)) },
                trailingIcon = {
                    val icon = if (confirmPasswordVisible) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff
                    IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                        Icon(icon, contentDescription = null, tint = TextMuted, modifier = Modifier.size(DesignTokens.AuthFieldIconSize))
                    }
                },
                visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation()
            )

            Spacer(modifier = Modifier.height(DesignTokens.SignUpFieldGap))
            ElevatedCard(
                colors = CardDefaults.elevatedCardColors(containerColor = OffWhite),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(DesignTokens.AuthFieldCorner),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Password requirements:", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextDark)
                    Text("• At least 8 characters", fontSize = 13.sp, color = TextMuted, lineHeight = 20.sp)
                    Text("• One uppercase letter", fontSize = 13.sp, color = TextMuted, lineHeight = 20.sp)
                    Text("• One number", fontSize = 13.sp, color = TextMuted, lineHeight = 20.sp)
                }
            }
            Spacer(modifier = Modifier.height(DesignTokens.SignUpFieldGap))
            Row(verticalAlignment = Alignment.Top) {
                Checkbox(
                    checked = agreeToTerms,
                    onCheckedChange = { agreeToTerms = it },
                    colors = CheckboxDefaults.colors(checkedColor = DeepNavy),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = buildAnnotatedString {
                        append("I agree to the ")
                        withStyle(SpanStyle(color = DeepNavy, fontWeight = FontWeight.SemiBold)) {
                            append("Terms of Service")
                        }
                        append(" and ")
                        withStyle(SpanStyle(color = DeepNavy, fontWeight = FontWeight.SemiBold)) {
                            append("Privacy Policy")
                        }
                    },
                    fontSize = 13.sp,
                    color = TextDark,
                    lineHeight = 18.sp
                )
            }
            Spacer(modifier = Modifier.height(32.dp))
            
            val isStep2Valid = createPassword.isNotBlank() && 
                               confirmPassword.isNotBlank() && 
                               createPassword == confirmPassword && 
                               agreeToTerms && !isLoading

            Button(
                onClick = {
                    isLoading = true
                    errorMessage = null
                    scope.launch {
                        try {
                            val fullName = "$firstName $lastName".trim()
                            authManager.signUp(email, createPassword, fullName, phone, address)
                            onSignUpComplete()
                        } catch (e: Exception) {
                            android.util.Log.e("RegistrationError", "Sign-up pipeline conflict detected", e)
                            val detail = buildString {
                                if (e is RestException) {
                                    append(e.error)
                                    e.description?.let { append(" — ").append(it) }
                                } else {
                                    append(e.localizedMessage ?: e.message ?: e.toString())
                                }
                            }

                            errorMessage = when {
                                detail.contains("already registered", true) -> "This email is already in use."
                                detail.contains("duplicate key", true) ||
                                    detail.contains("23505", true) ||
                                    detail.contains("users_pkey", true) ->
                                    "Server profile sync conflict. Re-run supabase/handle_new_user.sql in the SQL Editor, then try again."
                                detail.contains("database error", true) ||
                                    detail.contains("Database error saving new user", true) ->
                                    "Database error during signup. Check Supabase Logs, then re-run handle_new_user.sql."
                                else -> "Failed to create account. Please verify input formats."
                            }
                            e.printStackTrace()
                        } finally {
                            isLoading = false
                        }
                    }
                },
                enabled = isStep2Valid,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(DesignTokens.AuthButtonHeight),
                colors = ButtonDefaults.buttonColors(
                    containerColor = DeepNavy,
                    disabledContainerColor = DeepNavy.copy(alpha = 0.5f)
                ),
                shape = RoundedCornerShape(DesignTokens.AuthButtonCorner),
                contentPadding = PaddingValues(0.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                } else {
                    Text("Create Account", color = Color.White, fontSize = DesignTokens.AuthButtonTextSize, fontWeight = FontWeight.SemiBold)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            Text("Already have an account? ", color = TextMuted, fontSize = DesignTokens.AuthLabelSize)
            Text(
                "Sign In",
                color = DeepNavy,
                fontWeight = FontWeight.Bold,
                fontSize = DesignTokens.AuthLabelSize,
                modifier = Modifier.clickable { onSignInRedirect() }
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun AuthLabel(text: String, modifier: Modifier = Modifier.fillMaxWidth()) {
    Text(
        text = text,
        modifier = modifier,
        fontWeight = FontWeight.Medium,
        fontSize = DesignTokens.AuthLabelSize,
        color = TextDark
    )
}

@Composable
private fun AuthOutlinedField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    minLines: Int = 1
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholder, color = TextMuted, fontSize = DesignTokens.AuthLabelSize) },
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        visualTransformation = visualTransformation,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = DesignTokens.AuthLabelFieldGap)
            .defaultMinSize(minHeight = DesignTokens.AuthFieldHeight),
        shape = RoundedCornerShape(DesignTokens.AuthFieldCorner),
        colors = authTextFieldColors(),
        minLines = minLines,
        singleLine = minLines == 1
    )
}

@Composable
private fun authTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = DeepNavy,
    unfocusedBorderColor = Color(0xFFE0E0E0),
    cursorColor = DeepNavy
)
