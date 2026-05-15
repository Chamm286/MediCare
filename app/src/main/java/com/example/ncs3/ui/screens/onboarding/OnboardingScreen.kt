package com.example.ncs3.ui.screens.onboarding

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ncs3.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScreen(onGetStarted: () -> Unit) {
    val pagerState = rememberPagerState(initialPage = 0) { 3 }
    val coroutineScope = rememberCoroutineScope()

    // Tự động chuyển trang
    LaunchedEffect(Unit) {
        while (true) {
            delay(5000)
            val nextPage = (pagerState.currentPage + 1) % 3
            pagerState.animateScrollToPage(nextPage)
        }
    }

    val pages = listOf(
        OnboardingPageData(
            imageRes = R.drawable.anh2,
            title = "Đặt lịch khám thông minh",
            description = "Chọn bác sĩ, chọn giờ khám phù hợp - chỉ với 3 phút, không chờ đợi",
            color = Color(0xFF0D47A1),
            bgGradient = listOf(Color(0xFF0D47A1), Color(0xFF1565C0), Color(0xFF2196F3)),
            accentColor = Color(0xFF64B5F6)
        ),
        OnboardingPageData(
            imageRes = R.drawable.anh3,
            title = "Bác sĩ chuyên khoa hàng đầu",
            description = "Đội ngũ giáo sư, tiến sĩ, bác sĩ giàu kinh nghiệm từ các bệnh viện lớn",
            color = Color(0xFF00BCD4),
            bgGradient = listOf(Color(0xFF00BCD4), Color(0xFF0097A7), Color(0xFF4DD0E1)),
            accentColor = Color(0xFF80DEEA)
        ),
        OnboardingPageData(
            imageRes = R.drawable.anh8,
            title = "Hồ sơ sức khỏe toàn diện",
            description = "Theo dõi lịch sử khám bệnh, đơn thuốc, kết quả xét nghiệm - mọi lúc mọi nơi",
            color = Color(0xFF0097A7),
            bgGradient = listOf(Color(0xFF0097A7), Color(0xFF006064), Color(0xFF4DB6AC)),
            accentColor = Color(0xFF80CBC4)
        )
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.White
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Skip button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Surface(
                    modifier = Modifier
                        .shadow(4.dp, RoundedCornerShape(30.dp)),
                    shape = RoundedCornerShape(30.dp),
                    color = Color.White,
                    tonalElevation = 0.dp
                ) {
                    TextButton(
                        onClick = onGetStarted,
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = pages[pagerState.currentPage].color
                        )
                    ) {
                        Text(
                            "Bỏ qua",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            // HorizontalPager
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f),
                key = { it }
            ) { page ->
                val pageData = pages[page]
                val isCurrentPage = pagerState.currentPage == page

                AnimatedPageContentEnhanced(
                    pageData = pageData,
                    isVisible = isCurrentPage,
                    page = page,
                    currentPage = pagerState.currentPage
                )
            }

            // Bottom section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp, start = 24.dp, end = 24.dp)
            ) {
                // Indicator dots
                Row(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(pagerState.pageCount) { index ->
                        val isSelected = pagerState.currentPage == index
                        val dotWidth by animateDpAsState(
                            targetValue = if (isSelected) 28.dp else 8.dp,
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioLowBouncy,
                                stiffness = Spring.StiffnessLow
                            )
                        )

                        Box(
                            modifier = Modifier
                                .width(dotWidth)
                                .height(8.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isSelected) pages[index].color
                                    else Color.LightGray.copy(alpha = 0.5f)
                                )
                        )
                    }
                }

                // Next/Get Started button
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter),
                    horizontalArrangement = Arrangement.Center
                ) {
                    if (pagerState.currentPage == 2) {
                        // ========== NÚT BẮT ĐẦU ĐÃ CHỈNH CÂN ĐỐI ==========
                        var isHovered by remember { mutableStateOf(false) }
                        val buttonScale by animateFloatAsState(
                            targetValue = if (isHovered) 1.02f else 1f,
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioLowBouncy,
                                stiffness = Spring.StiffnessLow
                            )
                        )

                        Button(
                            onClick = onGetStarted,
                            modifier = Modifier
                                .fillMaxWidth(0.8f)
                                .height(56.dp)
                                .scale(buttonScale),
                            shape = RoundedCornerShape(28.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = pages[2].color
                            ),
                            elevation = ButtonDefaults.buttonElevation(
                                defaultElevation = 6.dp,
                                pressedElevation = 12.dp
                            )
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center,
                                modifier = Modifier.padding(horizontal = 8.dp)
                            ) {
                                // Icon trái tim
                                Text(
                                    "❤️",
                                    fontSize = 20.sp,
                                    modifier = Modifier.alpha(0.9f)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                // Text chính
                                Text(
                                    "BẮT ĐẦU",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 2.sp
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                // Icon mũi tên
                                Text(
                                    "→",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    } else {
                        Button(
                            onClick = {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth(0.5f)
                                .height(52.dp),
                            shape = RoundedCornerShape(26.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = pages[pagerState.currentPage].color
                            ),
                            elevation = ButtonDefaults.buttonElevation(
                                defaultElevation = 6.dp,
                                pressedElevation = 10.dp
                            )
                        ) {
                            Text(
                                "TIẾP THEO",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                letterSpacing = 1.5.sp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("→", fontSize = 18.sp)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AnimatedPageContentEnhanced(
    pageData: OnboardingPageData,
    isVisible: Boolean,
    page: Int,
    currentPage: Int
) {
    val imageScale by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0.85f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "imageScale"
    )

    val imageAlpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0.5f,
        animationSpec = tween(300, easing = FastOutSlowInEasing),
        label = "imageAlpha"
    )

    val titleOffset by animateDpAsState(
        targetValue = if (isVisible) 0.dp else 20.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "titleOffset"
    )

    val titleAlpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(400, easing = FastOutSlowInEasing),
        label = "titleAlpha"
    )

    val descAlpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(500, easing = FastOutSlowInEasing),
        label = "descAlpha"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = pageData.bgGradient.map { it.copy(alpha = 0.08f) }
                )
            )
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Image
        Box(
            modifier = Modifier
                .size(280.dp)
                .shadow(
                    elevation = 20.dp,
                    shape = RoundedCornerShape(36.dp),
                    clip = false,
                    ambientColor = pageData.color,
                    spotColor = pageData.color.copy(alpha = 0.4f)
                )
                .scale(imageScale)
                .alpha(imageAlpha)
        ) {
            Image(
                painter = painterResource(id = pageData.imageRes),
                contentDescription = pageData.title,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(36.dp)),
                contentScale = ContentScale.Crop
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(36.dp))
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.1f))
                        )
                    )
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Title
        Text(
            text = pageData.title,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = pageData.color,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .offset(y = titleOffset)
                .alpha(titleAlpha)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Description
        Surface(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .alpha(descAlpha),
            shape = RoundedCornerShape(24.dp),
            color = pageData.color.copy(alpha = 0.08f)
        ) {
            Text(
                text = pageData.description,
                fontSize = 15.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)
            )
        }

        // Feature tags
        if (page == 0) {
            Spacer(modifier = Modifier.height(20.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.alpha(descAlpha)
            ) {
                FeatureTag("🏥 100+ Bệnh viện", pageData.accentColor)
                FeatureTag("⭐ 4.9 Đánh giá", pageData.accentColor)
            }
        } else if (page == 1) {
            Spacer(modifier = Modifier.height(20.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.alpha(descAlpha)
            ) {
                FeatureTag("👨‍⚕️ 50+ Chuyên khoa", pageData.accentColor)
                FeatureTag("🎓 200+ Bác sĩ", pageData.accentColor)
            }
        } else {
            Spacer(modifier = Modifier.height(20.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.alpha(descAlpha)
            ) {
                FeatureTag("📋 Lịch sử khám", pageData.accentColor)
                FeatureTag("💊 Đơn thuốc điện tử", pageData.accentColor)
            }
        }
    }
}

@Composable
fun FeatureTag(text: String, color: Color) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = color.copy(alpha = 0.12f),
        modifier = Modifier.height(32.dp)
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 14.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = color
            )
        }
    }
}

@Composable
fun animateDpAsState(
    targetValue: androidx.compose.ui.unit.Dp,
    animationSpec: AnimationSpec<androidx.compose.ui.unit.Dp> = spring(),
    label: String = "Dp"
): State<androidx.compose.ui.unit.Dp> {
    return androidx.compose.animation.core.animateDpAsState(
        targetValue = targetValue,
        animationSpec = animationSpec,
        label = label
    )
}

data class OnboardingPageData(
    val imageRes: Int,
    val title: String,
    val description: String,
    val color: Color,
    val bgGradient: List<Color>,
    val accentColor: Color
)