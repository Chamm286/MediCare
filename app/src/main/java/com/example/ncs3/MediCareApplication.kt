// MediCareApplication.kt
package com.example.ncs3

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import com.example.ncs3.service.AppointmentScheduler
import com.example.ncs3.utils.SharedPrefs

@HiltAndroidApp
class MediCareApplication : Application() {

    private lateinit var scheduler: AppointmentScheduler

    override fun onCreate() {
        super.onCreate()
        SharedPrefs.init(this)

//        // Khởi tạo và chạy scheduler
//        scheduler = AppointmentScheduler(this)
//        scheduler.startScheduler()
    }

    override fun onTerminate() {
        super.onTerminate()
        scheduler.stopScheduler()
    }
}