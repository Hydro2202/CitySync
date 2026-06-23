package com.example.citysync.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.citysync.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactSupportScreen(
    onBack: () -> Unit = {},
    onMessageSent: () -> Unit = {}
) {
    var name by remember { mutableStateOf("Raiden Villapando") }
    var email by remember { mutableStateOf("raiden.villapando@email.com") }
    var category by remember { mutableStateOf("General Inquiry") }
    var message by remember { mutableStateOf("") }
    var isDropdownExpanded by remember { mutableStateOf(false) }
    
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val categories = listOf(
        "General Inquiry",
        "Technical Issue",
        "Report Problem",
        "Account Help",
        "Feedback",
        "Other"
    )

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Contact Support",
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
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ContactFormField("Your Name", name) { name = it }
            ContactFormField("Email Address", email) { email = it }
            
            // Category Dropdown
            Column {
                Text(
                    "Category",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF5A6B7C),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Box {
                    OutlinedTextField(
                        value = category,
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { isDropdownExpanded = true },
                        shape = RoundedCornerShape(12.dp),
                        trailingIcon = {
                            Icon(
                                Icons.Default.KeyboardArrowDown,
                                contentDescription = null,
                                tint = Color(0xFF0D4E89)
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF0D4E89),
                            unfocusedBorderColor = Color(0xFFE2E8F0),
                            unfocusedContainerColor = Color.White,
                            focusedContainerColor = Color.White
                        )
                    )
                    // Transparent layer to capture clicks since text field is readOnly
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .clickable { isDropdownExpanded = true }
                    )
                    
                    DropdownMenu(
                        expanded = isDropdownExpanded,
                        onDismissRequest = { isDropdownExpanded = false },
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .background(Color.White)
                    ) {
                        categories.forEach { item ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        item,
                                        color = if (item == category) Color(0xFF0D4E89) else Color.Black,
                                        fontWeight = if (item == category) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                onClick = {
                                    category = item
                                    isDropdownExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            // Message field
            Column {
                Text(
                    "Message",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF5A6B7C),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                OutlinedTextField(
                    value = message,
                    onValueChange = { message = it },
                    placeholder = { Text("Describe your issue or question in detail...", color = Color(0xFFA0AEC0)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF0D4E89),
                        unfocusedBorderColor = Color(0xFFE2E8F0),
                        unfocusedContainerColor = Color.White,
                        focusedContainerColor = Color.White
                    )
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { 
                    if (message.isNotBlank()) {
                        onMessageSent()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                enabled = message.isNotBlank(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF0D4E89),
                    disabledContainerColor = Color(0xFF0D4E89).copy(alpha = 0.5f)
                )
            ) {
                Text("Send Message", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}

@Composable
fun ContactFormField(label: String, value: String, onValueChange: (String) -> Unit) {
    Column {
        Text(
            label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF5A6B7C),
            modifier = Modifier.padding(bottom = 8.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF0D4E89),
                unfocusedBorderColor = Color(0xFFE2E8F0),
                unfocusedContainerColor = Color.White,
                focusedContainerColor = Color.White
            )
        )
    }
}

@Preview
@Composable
fun ContactSupportScreenPreview() {
    CitySyncTheme {
        ContactSupportScreen()
    }
}
