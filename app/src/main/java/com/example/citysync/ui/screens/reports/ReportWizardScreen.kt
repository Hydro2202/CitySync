package com.example.citysync.ui.screens.reports

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import com.example.citysync.R
import com.example.citysync.data.AuthManager
import com.example.citysync.data.ReportPriority
import com.example.citysync.data.model.Report
import com.example.citysync.data.repository.ReportRepository
import com.example.citysync.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun ReportWizardScreen(onBack: () -> Unit, onComplete: () -> Unit) {
    val authManager = remember { AuthManager() }
    val repository = remember { ReportRepository() }
    val scope = rememberCoroutineScope()
    var isSubmitting by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    var currentStep by remember { mutableStateOf(1) }
    var selectedImageRes by remember { mutableStateOf<Int?>(null) }
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    var houseNo by remember { mutableStateOf("") }
    var zipCode by remember { mutableStateOf("") }
    var streetName by remember { mutableStateOf("") }
    var barangay by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var province by remember { mutableStateOf("") }
    var landmark by remember { mutableStateOf("") }

    val isStep1Valid = selectedImageRes != null
    val isStep2Valid = title.isNotBlank() && description.isNotBlank()
    val isStep3Valid = selectedCategory != null
    val isStep4Valid = streetName.isNotBlank() && barangay.isNotBlank() && city.isNotBlank()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(OffWhite)
            .navigationBarsPadding()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            if (currentStep <= 5) {
                // Fixed Header
                WizardHeader(
                    currentStep = currentStep,
                    onBack = {
                        if (currentStep > 1) currentStep-- else onBack()
                    }
                )
            }

            Box(modifier = Modifier.weight(1f)) {
                when (currentStep) {
                    1 -> Step1AddPhotos(
                        photoAdded = selectedImageRes != null,
                        onPhotoAdded = { if (it) selectedImageRes = R.drawable.brokenlight else selectedImageRes = null },
                        onNext = { if (isStep1Valid) currentStep = 2 },
                        isValid = isStep1Valid,
                        selectedImageRes = selectedImageRes
                    )
                    2 -> Step2IssueDetails(
                        title = title,
                        onTitleChange = { title = it },
                        description = description,
                        onDescriptionChange = { description = it },
                        onBack = { currentStep = 1 },
                        onNext = { if (isStep2Valid) currentStep = 3 },
                        isValid = isStep2Valid
                    )
                    3 -> Step3CategorySelector(
                        selectedCategory = selectedCategory,
                        onCategorySelected = { selectedCategory = it },
                        onBack = { currentStep = 2 },
                        onNext = { if (isStep3Valid) currentStep = 4 },
                        isValid = isStep3Valid
                    )
                    4 -> Step4LocationForm(
                        houseNo = houseNo, onHouseNoChange = { houseNo = it },
                        zipCode = zipCode, onZipCodeChange = { zipCode = it },
                        streetName = streetName, onStreetNameChange = { streetName = it },
                        barangay = barangay, onBarangayChange = { barangay = it },
                        city = city, onCityChange = { city = it },
                        province = province, onProvinceChange = { province = it },
                        landmark = landmark, onLandmarkChange = { landmark = it },
                        onBack = { currentStep = 3 },
                        onNext = { if (isStep4Valid) currentStep = 5 },
                        isValid = isStep4Valid
                    )
                    5 -> Step5Review(
                        title = title,
                        category = selectedCategory ?: "",
                        description = description,
                        location = "$streetName, $barangay, $city",
                        landmark = landmark,
                        isSubmitting = isSubmitting,
                        errorMessage = errorMessage,
                        selectedImageRes = selectedImageRes,
                        onBack = { currentStep = 4 },
                        onSubmit = {
                            isSubmitting = true
                            errorMessage = null
                            scope.launch {
                                try {
                                    val user = authManager.getCurrentUser()
                                    if (user == null) throw Exception("No authenticated user found")
                                    
                                    val reference = "REP-2026-${System.currentTimeMillis().toString().takeLast(6)}"
                                    val priority = ReportPriority.forCategory(selectedCategory ?: "")
                                    val newReport = Report(
                                        reportedBy = user.id,
                                        title = title,
                                        tags = "$selectedCategory, $priority",
                                        priority = priority,
                                        status = "Assigned",
                                        location = "$streetName, $barangay, $city",
                                        description = description,
                                        reference = reference,
                                        imageUrl = "" // Placeholder
                                    )
                                    repository.createReport(newReport)
                                    currentStep = 6
                                } catch (e: Exception) {
                                    errorMessage = "Submission failed: ${e.message}"
                                    println("Report Submission Failure: ${e.message}")
                                } finally {
                                    isSubmitting = false
                                }
                            }
                        }
                    )
                    6 -> Step6Success(onComplete = onComplete)
                }
            }
        }
    }
}

@Composable
fun WizardHeader(currentStep: Int, onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(DeepNavy)
            .statusBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
            }
            Text(
                "Report an Issue",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        // Progress Indicator
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(vertical = 12.dp, horizontal = 24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                for (i in 1..5) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(4.dp)
                            .background(
                                if (i <= currentStep) DeepNavy else Color(0xFFE2E8F0),
                                RoundedCornerShape(2.dp)
                            )
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Step $currentStep of 5 — ${getStepLabel(currentStep)}",
                fontSize = 12.sp,
                color = TextMuted
            )
        }
    }
}

fun getStepLabel(step: Int): String = when (step) {
    1 -> "Add Photos"
    2 -> "Issue Details"
    3 -> "Select Category"
    4 -> "Issue Location"
    5 -> "Review & Submit"
    else -> ""
}

@Composable
fun Step1AddPhotos(
    photoAdded: Boolean,
    onPhotoAdded: (Boolean) -> Unit,
    onNext: () -> Unit,
    isValid: Boolean,
    selectedImageRes: Int? = null
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text("Add Photos", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = TextDark)
        Text("At least 1 photo is required. Add up to 4.", fontSize = 14.sp, color = TextMuted)
        
        Spacer(modifier = Modifier.height(32.dp))

        if (!photoAdded) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                PhotoActionCard(
                    label = "Take Photo",
                    icon = Icons.Outlined.PhotoCamera,
                    iconBg = BlueTint,
                    iconColor = DeepNavy,
                    modifier = Modifier.weight(1f),
                    onClick = { onPhotoAdded(true) }
                )
                PhotoActionCard(
                    label = "Upload Image",
                    icon = Icons.Outlined.Image,
                    iconBg = Color(0xFFF3E5F5),
                    iconColor = Color(0xFF8E24AA),
                    modifier = Modifier.weight(1f),
                    onClick = { onPhotoAdded(true) }
                )
            }
        } else {
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.LightGray)
            ) {
                // Display the selected local resource image
                if (selectedImageRes != null) {
                    Image(
                        painter = painterResource(id = selectedImageRes),
                        contentDescription = "Selected Photo",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    // Fallback placeholder
                    Box(modifier = Modifier.fillMaxSize().background(Color(0xFFE2E8F0)), contentAlignment = Alignment.Center) {
                        Icon(Icons.Outlined.Image, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(48.dp))
                    }
                }
                
                // Delete button
                IconButton(
                    onClick = { onPhotoAdded(false) },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .size(24.dp)
                        .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Delete", tint = Color.White, modifier = Modifier.size(16.dp))
                }

                // Label
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(8.dp)
                        .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text("Photo 1", color = Color.White, fontSize = 10.sp)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFF137333), modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("1 photo(s) added", color = Color(0xFF137333), fontSize = 14.sp)
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onNext,
            enabled = isValid,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isValid) DeepNavy else Color(0xFF94A3B8),
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Continue", fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
fun PhotoActionCard(label: String, icon: ImageVector, iconBg: Color, iconColor: Color, modifier: Modifier, onClick: () -> Unit) {
    Surface(
        modifier = modifier
            .height(120.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFFE2E8F0)),
        color = Color.White
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(iconBg, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = iconColor)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(label, fontSize = 14.sp, color = TextDark, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun Step2IssueDetails(
    title: String, onTitleChange: (String) -> Unit,
    description: String, onDescriptionChange: (String) -> Unit,
    onBack: () -> Unit, onNext: () -> Unit, isValid: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text("Issue Details", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = TextDark)
        Text("Provide a clear title and description of the problem.", fontSize = 14.sp, color = TextMuted)
        
        Spacer(modifier = Modifier.height(32.dp))

        AuthLabel("Title", required = true)
        OutlinedTextField(
            value = title,
            onValueChange = onTitleChange,
            placeholder = { Text("E.g., Broken streetlight on Main Street", color = TextMuted) },
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = DeepNavy, unfocusedBorderColor = Color(0xFFE2E8F0))
        )

        Spacer(modifier = Modifier.height(24.dp))

        AuthLabel("Description", required = true)
        OutlinedTextField(
            value = description,
            onValueChange = { if (it.length <= 500) onDescriptionChange(it) },
            placeholder = { Text("Describe the issue in detail...", color = TextMuted) },
            modifier = Modifier.fillMaxWidth().height(160.dp).padding(top = 8.dp),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = DeepNavy, unfocusedBorderColor = Color(0xFFE2E8F0))
        )
        Text(
            "${description.length}/500",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.End,
            fontSize = 12.sp,
            color = TextMuted
        )

        Spacer(modifier = Modifier.weight(1f))

        Row(modifier = Modifier.fillMaxWidth().padding(top = 32.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            OutlinedButton(
                onClick = onBack,
                modifier = Modifier.weight(1f).height(54.dp),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
            ) {
                Text("Back", color = TextDark)
            }
            Button(
                onClick = onNext,
                enabled = isValid,
                modifier = Modifier.weight(1f).height(54.dp),
                colors = ButtonDefaults.buttonColors(containerColor = if (isValid) DeepNavy else Color(0xFF94A3B8)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Continue")
            }
        }
    }
}

@Composable
fun Step3CategorySelector(selectedCategory: String?, onCategorySelected: (String) -> Unit, onBack: () -> Unit, onNext: () -> Unit, isValid: Boolean) {
    val categories = listOf(
        "Roads & Infrastructure" to Icons.Outlined.Map,
        "Lighting" to Icons.Outlined.Lightbulb,
        "Waste Management" to Icons.Outlined.Delete,
        "Water & Drainage" to Icons.Outlined.WaterDrop,
        "Public Safety" to Icons.Outlined.Shield,
        "Parks & Recreation" to Icons.Outlined.Park,
        "Traffic" to Icons.Outlined.Traffic,
        "Other" to Icons.Outlined.Article
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text("Select Category", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = TextDark)
        Text("Choose the category that best describes the issue.", fontSize = 14.sp, color = TextMuted)
        
        Spacer(modifier = Modifier.height(24.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(categories) { (label, icon) ->
                CategoryCard(
                    label = label,
                    icon = icon,
                    isSelected = selectedCategory == label,
                    onClick = { onCategorySelected(label) }
                )
            }
        }

        Row(modifier = Modifier.fillMaxWidth().padding(top = 24.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            OutlinedButton(
                onClick = onBack,
                modifier = Modifier.weight(1f).height(54.dp),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
            ) {
                Text("Back", color = TextDark)
            }
            Button(
                onClick = onNext,
                enabled = isValid,
                modifier = Modifier.weight(1f).height(54.dp),
                colors = ButtonDefaults.buttonColors(containerColor = if (isValid) DeepNavy else Color(0xFF94A3B8)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Continue")
            }
        }
    }
}

@Composable
fun CategoryCard(label: String, icon: ImageVector, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected) DeepNavy else Color(0xFFE2E8F0)
        ),
        color = if (isSelected) BlueTint else Color.White
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Icon(icon, contentDescription = null, tint = if (isSelected) DeepNavy else TextMuted)
            Spacer(modifier = Modifier.height(8.dp))
            Text(label, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = TextDark, lineHeight = 16.sp)
        }
    }
}

@Composable
fun Step4LocationForm(
    houseNo: String, onHouseNoChange: (String) -> Unit,
    zipCode: String, onZipCodeChange: (String) -> Unit,
    streetName: String, onStreetNameChange: (String) -> Unit,
    barangay: String, onBarangayChange: (String) -> Unit,
    city: String, onCityChange: (String) -> Unit,
    province: String, onProvinceChange: (String) -> Unit,
    landmark: String, onLandmarkChange: (String) -> Unit,
    onBack: () -> Unit, onNext: () -> Unit, isValid: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text("Issue Location", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = TextDark)
        Text("Enter the exact address where the issue is located.", fontSize = 14.sp, color = TextMuted)
        
        Spacer(modifier = Modifier.height(24.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Column(modifier = Modifier.weight(1f)) {
                AuthLabel("House / Unit No.")
                OutlinedTextField(houseNo, onHouseNoChange, placeholder = { Text("123", color = TextMuted) }, modifier = Modifier.padding(top = 4.dp), shape = RoundedCornerShape(12.dp))
            }
            Column(modifier = Modifier.weight(1f)) {
                AuthLabel("ZIP Code")
                OutlinedTextField(zipCode, onZipCodeChange, placeholder = { Text("1000", color = TextMuted) }, modifier = Modifier.padding(top = 4.dp), shape = RoundedCornerShape(12.dp))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        AuthLabel("Street Name", required = true)
        OutlinedTextField(streetName, onStreetNameChange, placeholder = { Text("Main Street", color = TextMuted) }, modifier = Modifier.fillMaxWidth().padding(top = 4.dp), shape = RoundedCornerShape(12.dp))

        Spacer(modifier = Modifier.height(16.dp))
        AuthLabel("Barangay", required = true)
        OutlinedTextField(barangay, onBarangayChange, placeholder = { Text("Barangay 5", color = TextMuted) }, modifier = Modifier.fillMaxWidth().padding(top = 4.dp), shape = RoundedCornerShape(12.dp))

        Spacer(modifier = Modifier.height(16.dp))
        AuthLabel("City / Municipality", required = true)
        OutlinedTextField(city, onCityChange, placeholder = { Text("Manila", color = TextMuted) }, modifier = Modifier.fillMaxWidth().padding(top = 4.dp), shape = RoundedCornerShape(12.dp))

        Spacer(modifier = Modifier.height(16.dp))
        AuthLabel("Province")
        OutlinedTextField(province, onProvinceChange, placeholder = { Text("Metro Manila", color = TextMuted) }, modifier = Modifier.fillMaxWidth().padding(top = 4.dp), shape = RoundedCornerShape(12.dp))

        Spacer(modifier = Modifier.height(16.dp))
        AuthLabel("Landmark (optional)")
        OutlinedTextField(landmark, onLandmarkChange, placeholder = { Text("Beside the school...", color = TextMuted) }, modifier = Modifier.fillMaxWidth().padding(top = 4.dp).height(80.dp), shape = RoundedCornerShape(12.dp))

        Spacer(modifier = Modifier.height(32.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            OutlinedButton(
                onClick = onBack,
                modifier = Modifier.weight(1f).height(54.dp),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
            ) {
                Text("Back", color = TextDark)
            }
            Button(
                onClick = onNext,
                enabled = isValid,
                modifier = Modifier.weight(1f).height(54.dp),
                colors = ButtonDefaults.buttonColors(containerColor = if (isValid) DeepNavy else Color(0xFF94A3B8)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Continue")
            }
        }
    }
}

@Composable
fun Step5Review(
    title: String, category: String, description: String, 
    location: String, landmark: String, isSubmitting: Boolean,
    errorMessage: String? = null,
    selectedImageRes: Int? = null,
    onBack: () -> Unit, onSubmit: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text("Review Your Report", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = TextDark)
        Text("Please review all information before submitting.", fontSize = 14.sp, color = TextMuted)
        
        Spacer(modifier = Modifier.height(24.dp))

        // Media Preview
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            if (selectedImageRes != null) {
                Image(
                    painter = painterResource(id = selectedImageRes),
                    contentDescription = null,
                    modifier = Modifier.size(80.dp).clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(modifier = Modifier.size(80.dp).background(Color(0xFFE2E8F0), RoundedCornerShape(8.dp)), contentAlignment = Alignment.Center) {
                    Icon(Icons.Outlined.Image, contentDescription = null, tint = Color.Gray)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        ReviewCard("TITLE", title)
        ReviewCard("CATEGORY", category)
        ReviewCard("DESCRIPTION", description)
        ReviewCard("LOCATION", location)
        if (landmark.isNotBlank()) ReviewCard("LANDMARK", landmark)

        Spacer(modifier = Modifier.height(24.dp))

        Surface(
            color = Color(0xFFE6F4EA),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                "By submitting this report, you confirm that the information provided is accurate. False reports may result in penalties under city ordinances.",
                color = Color(0xFF137333),
                fontSize = 13.sp,
                modifier = Modifier.padding(16.dp),
                lineHeight = 18.sp
            )
        }

        if (errorMessage != null) {
            Surface(
                color = Color(0xFFFCE8E6),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
            ) {
                Text(
                    text = errorMessage,
                    color = Color(0xFFC5221F),
                    fontSize = 13.sp,
                    modifier = Modifier.padding(16.dp),
                    lineHeight = 18.sp
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Row(modifier = Modifier.fillMaxWidth().padding(top = 32.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            OutlinedButton(
                onClick = onBack,
                enabled = !isSubmitting,
                modifier = Modifier.weight(1f).height(54.dp),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
            ) {
                Text("Back", color = TextDark)
            }
            Button(
                onClick = onSubmit,
                enabled = !isSubmitting,
                modifier = Modifier.weight(1f).height(54.dp),
                colors = ButtonDefaults.buttonColors(containerColor = DeepNavy),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                } else {
                    Text("Submit Report")
                }
            }
        }
    }
}

@Composable
fun ReviewCard(label: String, value: String) {
    Surface(
        color = Color.White,
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF1F5F9)),
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(label, fontSize = 11.sp, color = TextMuted, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, fontSize = 15.sp, color = TextDark)
        }
    }
}

@Composable
fun Step6Success(onComplete: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(OffWhite)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .background(Color(0xFFE6F4EA), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFF137333), modifier = Modifier.size(40.dp))
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text("Report Submitted!", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = TextDark)
        Text("Your report has been submitted successfully.", fontSize = 14.sp, color = Color(0xFF718096), textAlign = TextAlign.Center)
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Reference: ", color = Color(0xFF718096), fontSize = 14.sp)
            Text("REP-SUCCESS", color = TextDark, fontWeight = FontWeight.Medium, fontSize = 14.sp)
        }
        
        Spacer(modifier = Modifier.height(48.dp))
        
        Button(
            onClick = onComplete,
            colors = ButtonDefaults.buttonColors(containerColor = DeepNavy),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Text("Go to Reports")
        }
    }
}

@Composable
private fun AuthLabel(text: String, required: Boolean = false) {
    Row {
        Text(text, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = TextDark)
        if (required) {
            Text(" *", color = Color.Red, fontSize = 14.sp)
        }
    }
}
