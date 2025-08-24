package com.louishildebrand.memo

import android.app.Application
import com.louishildebrand.memo.data.SettingsRepository

class MemoApplication: Application() {
    lateinit var settingsRepository: SettingsRepository
        private set

    override fun onCreate() {
        super.onCreate()
        this.settingsRepository = SettingsRepository(applicationContext)
    }
}