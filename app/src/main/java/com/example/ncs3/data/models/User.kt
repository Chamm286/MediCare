package com.example.ncs3.data.models

data class User(
    val uid: String = "",
    val email: String = "",
    val fullName: String = "",
    val phone: String = "",
    val dob: String = "",
    val gender: String = "",
    val avatar: String = "",
    val createdAt: Long = 0
)

data class Doctor(
    val id: String = "",
    val name: String = "",
    val specialtyId: String = "",
    val specialty: String = "",
    val hospitalId: String = "",
    val hospital: String = "",
    val avatar: String = "",
    val rating: Float = 0f,
    val experience: Int = 0,
    val price: Int = 0,
    val degree: String = "",
    val description: String = "",
    val schedule: List<String> = emptyList(),
    val timeSlots: List<String> = emptyList(),
    val isOnline: Boolean = false,
    val phone: String = ""
)

data class Appointment(
    val id: String = "",
    val patientId: String = "",
    val doctorId: String = "",
    val doctorName: String = "",
    val specialtyId: String = "",
    val hospitalId: String = "",
    val date: String = "",
    val timeSlot: String = "",
    val symptoms: String = "",
    val status: String = "",
    val type: String = "",
    val createdAt: Long = 0
)

data class Medicine(
    val id: String = "",
    val name: String = "",
    val genericName: String = "",
    val category: String = "",
    val manufacturer: String = "",
    val dosageForm: String = "",
    val strength: String = "",
    val price: Int = 0,
    val prescriptionRequired: Boolean = false,
    val indications: String = "",
    val dosage: String = "",
    val sideEffects: String = ""
)

data class Specialty(
    val id: String = "",
    val name: String = "",
    val icon: String = "",
    val color: String = "",
    val description: String = ""
)

data class Hospital(
    val id: String = "",
    val name: String = "",
    val address: String = "",
    val phone: String = "",
    val rating: Double = 0.0
)
