package com.example.employeeleaveapp

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequestLeaveScreen(
    onNavigateBack: () -> Unit,
    onLeaveSubmitted: (Int) -> Unit // أضفنا هذا الحدث لإرسال عدد الأيام المخصومة
) {
    val leaveTypes = listOf("إدارية", "مرضية", "طارئة", "وفاة", "دراسة")
    var selectedType by remember { mutableStateOf(leaveTypes[0]) }
    var expanded by remember { mutableStateOf(false) }

    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }
    var reason by remember { mutableStateOf("") }
    var attachmentName by remember { mutableStateOf("لم يتم اختيار ملف") }

    var errorMessage by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "تقديم طلب إجازة جديد",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        if (isError) {
            Text(
                text = errorMessage,
                color = Color(0xFFEF4444),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        Text(text = "نوع الإجازة:", fontSize = 16.sp, modifier = Modifier.padding(bottom = 8.dp))
        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = selectedType,
                onValueChange = {},
                readOnly = true,
                trailingIcon = { Text("▼", modifier = Modifier.padding(end = 8.dp)) },
                modifier = Modifier.fillMaxWidth().clickable { expanded = true }
            )
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.fillMaxWidth(0.9f)
            ) {
                leaveTypes.forEach { type ->
                    DropdownMenuItem(
                        text = { Text(type) },
                        onClick = {
                            selectedType = type
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            OutlinedTextField(
                value = startDate,
                onValueChange = {
                    startDate = it
                    isError = false
                },
                label = { Text("من تاريخ") },
                isError = isError && startDate.isEmpty(),
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = endDate,
                onValueChange = {
                    endDate = it
                    isError = false
                },
                label = { Text("إلى تاريخ") },
                isError = isError && endDate.isEmpty(),
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = reason,
            onValueChange = { reason = it },
            label = { Text("السبب أو الملاحظات (اختياري)") },
            modifier = Modifier.fillMaxWidth().height(100.dp),
            maxLines = 3
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(text = "المرفقات والإثباتات الرسمية:", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = { attachmentName = "medical_report.pdf" },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
            ) {
                Text("إرفاق ملف/صورة")
            }
            Text(text = attachmentName, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        Spacer(modifier = Modifier.weight(1f))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedButton(
                onClick = { onNavigateBack() },
                modifier = Modifier.weight(1f).height(50.dp)
            ) {
                Text("إلغاء")
            }
            Button(
                onClick = {
                    if (startDate.isEmpty() || endDate.isEmpty()) {
                        isError = true
                        errorMessage = "⚠️ عذراً، يرجى ملء حقول التواريخ المطلوبة أولاً!"
                    } else {
                        isError = false
                        // محاكاة: نفترض برمجياً أن الموظف طلب إجازة مدتها "يومين" (2) ليتم خصمها عند الضغط
                        if (selectedType == "إدارية") {
                            onLeaveSubmitted(2)
                        } else {
                            onNavigateBack() // الإجازات الأخرى "وفق الإثبات" لا نخصم منها عدداً ثابتاً حالياً
                        }
                    }
                },
                modifier = Modifier.weight(1f).height(50.dp)
            ) {
                Text("إرسال الطلب")
            }
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
fun RequestLeaveScreenPreview() {
    com.example.employeeleaveapp.ui.theme.EmployeeLeaveAppTheme {
        androidx.compose.runtime.CompositionLocalProvider(
            androidx.compose.ui.platform.LocalLayoutDirection provides androidx.compose.ui.unit.LayoutDirection.Rtl
        ) {
            Surface(color = MaterialTheme.colorScheme.background) {
                RequestLeaveScreen(onNavigateBack = {}, onLeaveSubmitted = {})
            }
        }
    }
}