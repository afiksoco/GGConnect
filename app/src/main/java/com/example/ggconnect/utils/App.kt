package com.example.ggconnect.utils
import android.app.Application


class App: Application() {
    override fun onCreate() {
        super.onCreate()
        ImageLoader.init(this)
    }
}