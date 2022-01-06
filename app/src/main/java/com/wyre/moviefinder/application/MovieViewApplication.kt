package com.wyre.moviefinder.application

import android.app.Application
import com.facebook.drawee.backends.pipeline.Fresco

class MovieViewApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Fresco.initialize(this)
    }
}