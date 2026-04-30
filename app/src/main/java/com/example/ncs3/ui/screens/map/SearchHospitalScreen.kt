package com.example.ncs3.ui.screens.map

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.ncs3.data.models.Hospital
import com.example.ncs3.data.repository.MedicareRepository
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchHospitalScreen(
    navController: NavController
) {
    val scope = rememberCoroutineScope()
    val repository = remember { MedicareRepository() }

    var searchQuery by remember { mutableStateOf("") }
    var hospitals by remember { mutableStateOf<List<Hospital>>(emptyList()) }
    var filteredHospitals by remember { mutableStateOf<List<Hospital>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var selectedDistrict by remember { mutableStateOf("Tất cả") }
    var showDistrictFilter by remember { mutableStateOf(false) }

    val districts = listOf("Tất cả", "Quận 1", "Quận 2", "Quận 3", "Quận 4", "Quận 5", "Quận 7", "Quận 10", "Bình Thạnh", "Tân Bình", "Gò Vấp")

    LaunchedEffect(Unit) {
        scope.launch {
            hospitals = repository.getHospitals()
            filteredHospitals = hospitals
            isLoading = false
        }
    }

    fun filterHospitals() {
        filteredHospitals = hospitals.filter { hospital ->
            val matchSearch = searchQuery.isEmpty() ||
                    hospital.name.contains(searchQuery, ignoreCase = true) ||
                    hospital.address.contains(searchQuery, ignoreCase = true)
            val matchDistrict = selectedDistrict == "Tất cả" || hospital.address.contains(selectedDistrict, ignoreCase = true)
            matchSearch && matchDistrict
        }
    }

    LaunchedEffect(searchQuery, selectedDistrict) {
        filterHospitals()
    }

    if (showDistrictFilter) {
        AlertDialog(
            onDismissRequest = { showDistrictFilter = false },
            title = { Text("Chọn quận/huyện", fontWeight = FontWeight.Bold) },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 400.dp)
                        .verticalScroll(androidx.compose.foundation.rememberScrollState())
                ) {
                    districts.forEach { district ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedDistrict = district
                                    showDistrictFilter = false
                                }
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedDistrict == district,
                                onClick = {
                                    selectedDistrict = district
                                    showDistrictFilter = false
                                },
                                colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF0D47A1))
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(district, fontSize = 14.sp)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showDistrictFilter = false }) {
                    Text("Đóng", color = Color(0xFF0D47A1))
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Tìm bệnh viện", fontWeight = FontWeight.Bold, color = Color.White)
                        Text("${filteredHospitals.size} bệnh viện/phòng khám", fontSize = 11.sp, color = Color.White.copy(alpha = 0.8f))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0D47A1)),
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, null, tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = {
                        if (searchQuery.isNotEmpty()) {
                            searchQuery = ""
                        } else {
                            showDistrictFilter = true
                        }
                    }) {
                        Icon(
                            if (selectedDistrict != "Tất cả") Icons.Filled.FilterAlt else Icons.Outlined.FilterAlt,
                            null,
                            tint = Color.White
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF5F7FA))
        ) {
            // Search bar
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(56.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Outlined.Search, null, tint = Color(0xFF9AA0A6), modifier = Modifier.size(22.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    BasicTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier.weight(1f),
                        textStyle = LocalTextStyle.current.copy(fontSize = 14.sp),
                        decorationBox = { innerTextField ->
                            if (searchQuery.isEmpty()) {
                                Text("Tìm theo tên bệnh viện, địa chỉ...", color = Color(0xFF9AA0A6), fontSize = 14.sp)
                            }
                            innerTextField()
                        }
                    )
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Outlined.Close, null, modifier = Modifier.size(18.dp), tint = Color(0xFF9AA0A6))
                        }
                    }
                }
            }

            // Filter chip
            if (selectedDistrict != "Tất cả") {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = true,
                        onClick = { selectedDistrict = "Tất cả" },
                        label = { Text("$selectedDistrict ✕", fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF0D47A1),
                            selectedLabelColor = Color.White
                        )
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Hospital list
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF0D47A1))
                }
            } else if (filteredHospitals.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Outlined.LocationOff, null, modifier = Modifier.size(80.dp), tint = Color(0xFFBDBDBD))
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Không tìm thấy bệnh viện", fontSize = 16.sp, fontWeight = FontWeight.Medium, color = Color.Gray)
                        Text("Thử tìm kiếm với từ khóa khác", fontSize = 13.sp, color = Color.Gray)
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredHospitals) { hospital ->
                        HospitalSearchCard(
                            hospital = hospital,
                            onNavigate = {
                                val query = hospital.address.replace(" ", "+")
                                val uri = "https://www.google.com/maps/search/?api=1&query=$query"
                                val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(uri))
                                navController.context.startActivity(intent)
                            },
                            onViewDetail = {
                                navController.navigate("hospital_detail/${hospital.id}")
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun HospitalSearchCard(
    hospital: Hospital,
    onNavigate: () -> Unit,
    onViewDetail: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onViewDetail() }
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                // Avatar
                Box(
                    modifier = Modifier
                        .size(55.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE3F2FD)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🏥", fontSize = 28.sp)
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Hospital info
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        hospital.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Outlined.LocationOn, null, modifier = Modifier.size(12.dp), tint = Color.Gray)
                        Text(
                            hospital.address,
                            fontSize = 12.sp,
                            color = Color.Gray,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Star, null, modifier = Modifier.size(12.dp), tint = Color(0xFFFFB800))
                            Text(" ${hospital.rating}", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        }
                        Text("•", fontSize = 10.sp, color = Color.Gray)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Outlined.Phone, null, modifier = Modifier.size(12.dp), tint = Color.Gray)
                            Text(" ${hospital.phone}", fontSize = 11.sp, color = Color.Gray)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onViewDetail,
                    modifier = Modifier.weight(1f).height(40.dp),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Icon(Icons.Outlined.Info, null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Chi tiết", fontSize = 12.sp)
                }

                Button(
                    onClick = onNavigate,
                    modifier = Modifier.weight(1f).height(40.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D47A1)),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Icon(Icons.Outlined.Directions, null, modifier = Modifier.size(16.dp), tint = Color.White)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Chỉ đường", fontSize = 12.sp, color = Color.White)
                }
            }
        }
    }
}