package com.example.ncs3.ui.screens.onboarding

import androidx.compose.animation.core.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(onGetStarted: () -> Unit) {
    val pagerState = rememberPagerState(initialPage = 0) { 3 }
    val coroutineScope = rememberCoroutineScope()

    // Hệ màu Medical Premium nhất quán
    val brandPrimary = Color(0xFF0284C7)
    val textPrimary = Color(0xFF0F172A)
    val textSecondary = Color(0xFF475569)
    val lightGrayBg = Color(0xFFF1F5F9)

    LaunchedEffect(Unit) {
        while (true) {
            delay(5000)
            if (!pagerState.isScrollInProgress) {
                val nextPage = (pagerState.currentPage + 1) % 3
                pagerState.animateScrollToPage(nextPage)
            }
        }
    }

    val pages = listOf(
        OnboardingPageData(
            imageRes = R.drawable.anh2,
            title = "Đặt Lịch Khám Thông Minh",
            description = "Chủ động lựa chọn bác sĩ và khung giờ phù hợp. Đặt lịch nhanh chóng chỉ với vài thao tác, xóa bỏ hoàn toàn thời gian chờ đợi tại bệnh viện.",
            tags = listOf("Tối ưu thời gian", "Đặt lịch 24/7", "Xác nhận tức thì")
        ),
        OnboardingPageData(
            imageRes = R.drawable.anh3,
            title = "Chuyên Gia Đầu Ngành",
            description = "Kết nối trực tiếp với đội ngũ Giáo sư, Tiến sĩ, Bác sĩ giàu kinh nghiệm đến từ các bệnh viện trung ương tuyến đầu trên cả nước.",
            tags = listOf("Bác sĩ Chuyên khoa", "Hồ sơ minh bạch", "Tư vấn tận tâm")
        ),
        OnboardingPageData(
            imageRes = R.drawable.anh8,
            title = "Quản Lý Sức Khỏe Số",
            description = "Lưu trữ toàn bộ lịch sử khám bệnh, đơn thuốc điện tử và kết quả xét nghiệm an toàn tại một nơi. Tra cứu tiện lợi mọi lúc mọi nơi.",
            tags = listOf("Bảo mật tuyệt đối", "Đơn thuốc số", "Cập nhật liên tục")
        )
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.White
    ) { paddingValues ->
        // Sử dụng Box bao ngoài cùng để cố định cụm Bottom không bị đẩy xiên vẹo
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // 1. TOP BAR: Nút bỏ qua cố định góc trên
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 24.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (pagerState.currentPage < 2) {
                    Text(
                        text = "Bỏ qua",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = textSecondary.copy(alpha = 0.6f),
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onGetStarted() }
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    )
                } else {
                    Spacer(modifier = Modifier.height(36.dp))
                }
            }

            // 2. MAIN CONTENT: Thân máy chứa Pager trượt giải phóng không gian trung tâm
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.8f) // Giới hạn chỉ chiếm tối đa 80% chiều cao thân máy
                    .align(Alignment.TopCenter),
                key = { it }
            ) { page ->
                val pageData = pages[page]
                val isCurrentPage = pagerState.currentPage == page

                AnimatedPageContentEnhanced(
                    pageData = pageData,
                    isVisible = isCurrentPage,
                    brandPrimary = brandPrimary,
                    textPrimary = textPrimary,
                    textSecondary = textSecondary
                )
            }

            // 3. BOTTOM CONTROLS: Neo chặt ở đáy màn hình, cực kỳ cân đối
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding()
                    .padding(start = 24.dp, end = 24.dp, bottom = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Chỉ báo trang (Indicator Dots)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 24.dp)
                ) {
                    repeat(pagerState.pageCount) { index ->
                        val isSelected = pagerState.currentPage == index
                        val dotWidth by animateDpAsState(
                            targetValue = if (isSelected) 24.dp else 6.dp,
                            animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = Spring.StiffnessLow)
                        )

                        Box(
                            modifier = Modifier
                                .width(dotWidth)
                                .height(6.dp)
                                .clip(CircleShape)
                                .background(if (isSelected) brandPrimary else brandPrimary.copy(alpha = 0.2f))
                        )
                    }
                }

                // Nút hành động chính
                val isLastPage = pagerState.currentPage == 2
                Button(
                    onClick = {
                        if (isLastPage) {
                            onGetStarted()
                        } else {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isLastPage) brandPrimary else lightGrayBg
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                ) {
                    Text(
                        text = if (isLastPage) "Bắt đầu trải nghiệm" else "Tiếp tục",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isLastPage) Color.White else brandPrimary
                    )
                }
            }
        }
    }
}

@Composable
fun AnimatedPageContentEnhanced(
    pageData: OnboardingPageData,
    isVisible: Boolean,
    brandPrimary: Color,
    textPrimary: Color,
    textSecondary: Color
) {
    val imageScale by animateFloatAsState(
        targetValue = if (isVisible) 1.0f else 0.95f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = Spring.StiffnessMedium)
    )
    val contentAlpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(durationMillis = 350)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Khung ảnh được thiết kế lại kích thước cố định để cân bằng mắt
        Box(
            modifier = Modifier
                .size(width = 280.dp, height = 240.dp)
                .scale(imageScale)
                .alpha(contentAlpha),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize(0.95f)
                    .shadow(
                        elevation = 20.dp,
                        shape = RoundedCornerShape(24.dp),
                        clip = false,
                        ambientColor = brandPrimary.copy(alpha = 0.2f),
                        spotColor = brandPrimary.copy(alpha = 0.3f)
                    )
            )

            Image(
                painter = painterResource(id = pageData.imageRes),
                contentDescription = pageData.title,
                modifier = Modifier
                    .fillMaxSize(0.95f)
                    .clip(RoundedCornerShape(24.dp)),
                contentScale = ContentScale.Crop
            )
        }

        // Khoảng cách vàng phân tách khối ảnh và chữ
        Spacer(modifier = Modifier.height(36.dp))

        // Khối chữ & Thẻ tính năng
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .alpha(contentAlpha),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = pageData.title,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = textPrimary,
                textAlign = TextAlign.Center,
                lineHeight = 32.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = pageData.description,
                fontSize = 14.sp,
                color = textSecondary,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Dòng Tag tính năng gọn gàng, thanh thoát
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                pageData.tags.forEachIndexed { index, tag ->
                    FeatureTag(text = tag, brandPrimary = brandPrimary)
                    if (index < pageData.tags.size - 1) {
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 8.dp)
                                .size(4.dp)
                                .clip(CircleShape)
                                .background(brandPrimary.copy(alpha = 0.3f))
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FeatureTag(text: String, brandPrimary: Color) {
    Text(
        text = text,
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold,
        color = brandPrimary,
        modifier = Modifier
            .background(brandPrimary.copy(alpha = 0.05f), RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp)
    )
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
    val tags: List<String>
)