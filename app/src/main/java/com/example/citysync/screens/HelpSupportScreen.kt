package com.example.citysync.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.citysync.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpSupportScreen(
    onBack: () -> Unit = {},
    onContactSupport: () -> Unit = {}
) {
    val context = LocalContext.current
    
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Help & Support",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Back", tint = Color.White)
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // How can we help header
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0D4E89)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "How can we help?",
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Browse our FAQs or contact our support team directly.",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Text(
                "Frequently Asked Questions",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A202C),
                modifier = Modifier.padding(vertical = 4.dp)
            )

            // FAQ Items
            val faqs = listOf(
                FAQData(
                    "How do I submit a report?",
                    "Tap \"Report Issue\" on the Dashboard or the Reports tab. Follow the step-by-step form: add photos, title, description, category, and your complete address. Review and submit."
                ),
                FAQData(
                    "How long does it take for my report to be resolved?",
                    "Resolution times vary by report type and priority. Most reports are acknowledged within 24–48 hours. You can track progress in real-time through the Report Tracking screen."
                ),
                FAQData(
                    "Can I edit a report after submitting?",
                    "Once submitted, reports cannot be edited to maintain data integrity. However, you can add comments to provide additional information."
                ),
                FAQData(
                    "How do I get emergency assistance?",
                    "Tap the Emergency Assistance banner on the Dashboard or go to the Emergency tab. You can call 911 directly or send an emergency alert with your details."
                ),
                FAQData(
                    "How do I update my profile information?",
                    "Go to Profile → Edit Profile. Update your name, email, phone, and address. Changes will reflect across the entire app."
                ),
                FAQData(
                    "What types of issues can I report?",
                    "You can report infrastructure issues, road damage, flooding, waste management, public lighting, noise, and other city service problems."
                )
            )

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                faqs.forEach { faq ->
                    FAQAccordion(faq)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Contact Support",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A202C)
            )

            // Support Channels
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    SupportChannelRow(
                        icon = Icons.Default.Chat,
                        title = "Send a Message",
                        subtitle = "We reply within 24 hours",
                        onClick = onContactSupport
                    )
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = Color(0xFFF1F5F9))
                    SupportChannelRow(
                        icon = Icons.Default.Email,
                        title = "Email Support",
                        subtitle = "support@citysync.gov.ph",
                        onClick = {
                            val intent = Intent(Intent.ACTION_SENDTO).apply {
                                data = Uri.parse("mailto:support@citysync.gov.ph")
                                putExtra(Intent.EXTRA_SUBJECT, "Support Request - CitySync")
                            }
                            context.startActivity(Intent.createChooser(intent, "Send Email"))
                        }
                    )
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = Color(0xFFF1F5F9))
                    SupportChannelRow(
                        icon = Icons.Default.Phone,
                        title = "Call Hotline",
                        subtitle = "(02) 8888-SYNC",
                        onClick = {
                            val intent = Intent(Intent.ACTION_DIAL).apply {
                                data = Uri.parse("tel:0288887962") // SYNC corresponds to 7962 on keypad
                            }
                            context.startActivity(intent)
                        }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

data class FAQData(val question: String, val answer: String)

@Composable
fun FAQAccordion(faq: FAQData) {
    var expanded by remember { mutableStateOf(false) }
    
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth().clickable { expanded = !expanded }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    faq.question,
                    modifier = Modifier.weight(1f),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1A202C)
                )
                Icon(
                    if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = Color(0xFF718096)
                )
            }
            
            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        faq.answer,
                        fontSize = 14.sp,
                        color = Color(0xFF4A5568),
                        lineHeight = 20.sp
                    )
                }
            }
        }
    }
}

@Composable
fun SupportChannelRow(icon: ImageVector, title: String, subtitle: String, onClick: () -> Unit = {}) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(40.dp).background(Color(0xFFEBF8FF), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = Color(0xFF0D4E89), modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFF1A202C))
            Text(subtitle, fontSize = 13.sp, color = Color(0xFF718096))
        }
        Spacer(modifier = Modifier.weight(1f))
        Icon(Icons.Default.ChevronRight, null, tint = Color(0xFFCBD5E0))
    }
}

@Preview
@Composable
fun HelpSupportScreenPreview() {
    CitySyncTheme {
        HelpSupportScreen()
    }
}
