package com.example.employeeleaveapp

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(onLoginSuccess: () -> Unit) {
    // متغيرات لحفظ ما يكتبه الموظف داخل الخانات
    var employeeId by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isArabic by remember { mutableStateOf(true) } // لتغيير اللغة لاحقاً

    // ترتيب العناصر عمودياً فوق بعضها
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // عنوان الشاشة الرئيسي
        Text(
            text = if (isArabic) "تسجيل الدخول" else "Login",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(40.dp))

        // خانة إدخال الرقم الوظيفي
        OutlinedTextField(
            value = employeeId,
            onValueChange = { employeeId = it },
            label = { Text(if (isArabic) "الرقم الوظيفي" else "Employee ID") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // خانة إدخال كلمة المرور
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text(if (isArabic) "كلمة المرور" else "Password") },
            visualTransformation = PasswordVisualTransformation(), // لإخفاء كلمة المرور بنقاط
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(32.dp))

        // زر تسجيل الدخول
        Button(
            onClick = {
                // مؤقتاً: عند الضغط على الزر سينقله للشاشة الرئيسية مباشرة
                if (employeeId.isNotEmpty() && password.isNotEmpty()) {
                    onLoginSuccess()
                }
            },
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Text(
                text = if (isArabic) "دخول" else "Login",
                fontSize = 18.sp
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // زر جانبي صغير لتبديل اللغة (جمالية ومنطقية بنفس الوقت)
        TextButton(onClick = { isArabic = !isArabic }) {
            Text(text = if (isArabic) "Change to English" else "التحويل للعربية")
        }
    }
}