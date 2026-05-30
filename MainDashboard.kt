package com.example.employeeleaveapp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class LeaveType(val name: String, val balance: String, val color: Color)

@Composable
fun MainDashboard(
    currentBalance: Int, // استقبلنا الرقم المتغير هنا
    onNavigateToRequest: () -> Unit
) {
    // بناء القائمة مع دمج الرقم البرمجي المتغير داخل الإجازة الإدارية
    val leaveTypes = listOf(
        LeaveType("إدارية", "$currentBalance يوم / سنة", Color(0xFF38BDF8)),
        LeaveType("مرضية", "وفق الإثبات", Color(0xFFF87171)),
        LeaveType("طارئة", "وفق الإثبات", Color(0xFFFBBF24)),
        LeaveType("وفاة", "وفق الإثبات", Color(0xFF10B981)),
        LeaveType("دراسة", "وفق الإثبات", Color(0xFFA78BFA))
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "أهلاً بك، أحمد",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = "رصيد ومراقبة إجازاتك الحالية:",
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(leaveTypes) { leave ->
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    modifier = Modifier.fillMaxWidth().height(120.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(12.dp),
                        verticalArrangement = Arrangement.SpaceBetween,
                        horizontalAlignment = Alignment.Start
                    ) {
                        Box(modifier = Modifier.size(24.dp, 4.dp).background(leave.color, RoundedCornerShape(2.dp)))
                        Text(text = leave.name, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Text(text = leave.balance, fontSize = 14.sp, color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }

        Button(
            onClick = { onNavigateToRequest() },
            modifier = Modifier.fillMaxWidth().height(55.dp).padding(top = 8.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(text = "تقديم طلب إجازة جديد", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
fun MainDashboardPreview() {
    com.example.employeeleaveapp.ui.theme.EmployeeLeaveAppTheme {
        androidx.compose.runtime.CompositionLocalProvider(
            androidx.compose.ui.platform.LocalLayoutDirection provides androidx.compose.ui.unit.LayoutDirection.Rtl
        ) {
            Surface(color = MaterialTheme.colorScheme.background) {
                MainDashboard(currentBalance = 15, onNavigateToRequest = {})
            }
        }
    }
}