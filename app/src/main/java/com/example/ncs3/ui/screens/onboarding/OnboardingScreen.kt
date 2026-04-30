package com.example.ncs3.ui.screens.onboarding

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(onGetStarted: () -> Unit) {
    val pagerState = rememberPagerState(initialPage = 0) { 3 }
    val coroutineScope = rememberCoroutineScope()

    val pages = listOf(
        OnboardingPage("🏥", "Đặt lịch khám dễ dàng", "Đặt lịch hẹn với bác sĩ chuyên khoa chỉ với vài thao tác", Color(0xFF0D47A1)),
        OnboardingPage("👨‍⚕️", "Bác sĩ hàng đầu", "Đội ngũ bác sĩ giàu kinh nghiệm, tận tâm với bệnh nhân", Color(0xFF00BCD4)),
        OnboardingPage("📱", "Theo dõi sức khỏe", "Quản lý lịch hẹn, đơn thuốc và hồ sơ sức khỏe", Color(0xFF0097A7))
    )

    Column(modifier = Modifier.fillMaxSize().background(Color.White)) {
        HorizontalPager(state = pagerState, modifier = Modifier.weight(1f)) { page ->
            Column(
                modifier = Modifier.fillMaxSize().padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(pages[page].icon, fontSize = 100.sp)
                Text(pages[page].title, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = pages[page].color)
                Text(pages[page].description, fontSize = 14.sp, color = Color.Gray, modifier = Modifier.padding(horizontal = 16.dp))
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(Modifier.weight(1f), horizontalArrangement = Arrangement.Center) {
                repeat(pagerState.pageCount) { index ->
                    Box(
                        modifier = Modifier.size(8.dp).padding(horizontal = 4.dp).clip(CircleShape).background(if (pagerState.currentPage == index) Color(0xFF0D47A1) else Color.LightGray)
                    )
                }
            }
            
            if (pagerState.currentPage == 2) {
                Button(onClick = onGetStarted, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D47A1)), shape = RoundedCornerShape(24.dp)) {
                    Text("Bắt đầu", color = Color.White, modifier = Modifier.padding(horizontal = 24.dp))
                }
            } else {
                TextButton(onClick = { coroutineScope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) } }) {
                    Text("Tiếp theo", color = Color(0xFF0D47A1))
                }
            }
        }
    }
}

data class OnboardingPage(val icon: String, val title: String, val description: String, val color: Color)