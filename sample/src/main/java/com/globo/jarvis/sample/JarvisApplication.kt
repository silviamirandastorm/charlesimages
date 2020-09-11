package com.globo.jarvis.sample

import android.app.Application
import androidx.multidex.MultiDexApplication
import com.globo.jarvis.Device
import com.globo.jarvis.Environment
import com.globo.jarvis.JarvisClient
import com.globo.jarvis.sample.BuildConfig.DEBUG
import com.globo.jarvis.sample.BuildConfig.VERSION_NAME

class JarvisApplication : MultiDexApplication(), JarvisClient.Settings {
    companion object {
        const val TENANT = "globo-play-beta"
        const val GLB_ID =
            "13a58351c50ca5324b6fc1725b0d44b7d66516a7270373459314a7541384c76574d4f31514f6a627774706c6b6b375f3536585372345651765a76657a7a6e39322d62644b3770346962314666463435744b4244506630445f706347424154666277375f7279413d3d3a303a667265697461735f6272756e6e61667265"

        const val USER_ID =
            "19b1b56e09ccf61bca0745e2cef49b69d6872545f6b743357726f6f6657645979337a71426348"
    }

    override fun onCreate() {
        super.onCreate()
        JarvisClient.initialize(this)
    }

    override fun glbId(): String? = GLB_ID

    override fun userId(): String? = USER_ID

    override fun tenant(): String = TENANT

    override fun device(): Device = Device.MOBILE

    override fun version(): String = VERSION_NAME

    override fun application(): Application = this

    override fun enableLog(): Boolean = DEBUG

    override fun environment(): String = Environment.BETA.value

}