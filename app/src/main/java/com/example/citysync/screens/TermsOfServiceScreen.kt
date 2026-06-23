package com.example.citysync.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.citysync.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TermsOfServiceScreen(
    onBack: () -> Unit = {}
) {
    val terms = listOf(
        Pair("1. Acceptance of Terms", "By accessing or using CitySync, you agree to be bound by these Terms of Service. If you do not agree to these terms, please do not use the application."),
        Pair("2. Use of the Application", "CitySync is a government-affiliated service platform for citizens to report infrastructure issues and access city services. You agree to use this application only for lawful purposes and in a manner that does not infringe the rights of others."),
        Pair("3. User Accounts", "You are responsible for maintaining the confidentiality of your account credentials. You agree to notify us immediately of any unauthorized use of your account. The city government reserves the right to terminate accounts that violate these terms."),
        Pair("4. Report Submissions", "All reports submitted must be accurate and truthful. False or misleading reports may result in account suspension and are subject to penalties under applicable law. The city reserves the right to verify and investigate all submissions."),
        Pair("5. Privacy", "Your use of CitySync is also governed by our Privacy Policy. By using the application, you consent to the collection, use, and sharing of your information as described in the Privacy Policy."),
        Pair("6. Intellectual Property", "All content, features, and functionality of CitySync, including but not limited to text, graphics, logos, and software, are owned by the city government and are protected by applicable intellectual property laws."),
        Pair("7. Limitation of Liability", "The city government shall not be liable for any indirect, incidental, or consequential damages arising from your use of CitySync. We do not guarantee the accuracy or completeness of any information provided through the platform."),
        Pair("8. Changes to Terms", "We reserve the right to modify these Terms of Service at any time. Changes will be posted within the application and take effect immediately upon posting. Continued use of CitySync constitutes acceptance of the revised terms.")
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Terms of Service",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFF0D4E89)
                )
            )
        },
        containerColor = Color(0xFFF4F6F9)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Metadata Card
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "Last updated: June 10, 2026 · Effective immediately upon account registration.",
                    modifier = Modifier.padding(16.dp),
                    fontSize = 13.sp,
                    color = Color(0xFF5A6B7C),
                    textAlign = TextAlign.Center
                )
            }

            // Terms Cards
            terms.forEach { (title, body) ->
                LegalCard(title, body)
            }

            // Footer Help Card
            Surface(
                color = Color(0xFFF1F5F9).copy(alpha = 0.5f),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "For questions about these terms, contact us at legal@citysync.gov.ph",
                    modifier = Modifier.padding(20.dp),
                    fontSize = 12.sp,
                    color = Color(0xFF718096),
                    textAlign = TextAlign.Center
                )
            }
            
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun LegalCard(title: String, body: String) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A202C)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                body,
                fontSize = 14.sp,
                color = Color(0xFF5A6B7C),
                lineHeight = 22.sp
            )
        }
    }
}

@Preview
@Composable
fun TermsOfServiceScreenPreview() {
    CitySyncTheme {
        TermsOfServiceScreen()
    }
}
