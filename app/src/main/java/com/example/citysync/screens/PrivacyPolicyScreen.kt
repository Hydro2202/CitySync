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
fun PrivacyPolicyScreen(
    onBack: () -> Unit = {}
) {
    val policies = listOf(
        Pair("Information We Collect", "We collect information you provide when creating an account (name, email, phone number), information from your reports (location addresses, photos, descriptions), and usage data to improve the application."),
        Pair("How We Use Your Information", "Your information is used to process and respond to your reports, send notifications about report status updates, deliver relevant city announcements, and improve CitySync services."),
        Pair("Information Sharing", "Your reports and information may be shared with relevant city departments and agencies to facilitate resolution. We do not sell your personal information to third parties."),
        Pair("Data Security", "We implement industry-standard security measures to protect your personal information. However, no method of transmission over the internet is completely secure."),
        Pair("Your Rights", "You have the right to access, correct, or delete your personal information. You can manage your privacy preferences in Settings → Privacy Settings, or contact support to request data deletion."),
        Pair("Cookies and Analytics", "CitySync may use analytics tools to understand how users interact with the app. This data is aggregated and anonymized. You can opt out in Privacy Settings."),
        Pair("Contact & Updates", "For privacy inquiries, contact privacy@citysync.gov.ph. We may update this policy periodically and will notify you of material changes through the app.")
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Privacy Policy",
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
                    "Last updated: June 10, 2026 · This policy explains how CitySync collects and uses your data.",
                    modifier = Modifier.padding(16.dp),
                    fontSize = 13.sp,
                    color = Color(0xFF5A6B7C),
                    textAlign = TextAlign.Center
                )
            }

            // Policy Cards
            policies.forEach { (title, body) ->
                LegalCard(title, body)
            }

            // Footer Help Card
            Surface(
                color = Color(0xFFF1F5F9).copy(alpha = 0.5f),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "For privacy questions, contact us at privacy@citysync.gov.ph",
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

@Preview
@Composable
fun PrivacyPolicyScreenPreview() {
    CitySyncTheme {
        PrivacyPolicyScreen()
    }
}
