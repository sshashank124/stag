package com.phaqlow.stag.player

import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import com.phaqlow.stag.util.SPOTIFY_EXTRA_ACCESS_TOKEN
import com.phaqlow.stag.util.log


object MusicPlayerServiceConnection : ServiceConnection {
    private var musicPlayerService: MusicPlayerService? = null
    val musicPlayer get() = musicPlayerService

    override fun onServiceConnected(className: ComponentName, service: IBinder) {
        musicPlayerService = (service as MusicPlayerService.BinderImpl).getService()
        log("MP service BOUND")
    }

    override fun onServiceDisconnected(className: ComponentName) {
        musicPlayerService = null
        log("MP service DISCONNECTED")
    }

    private var shouldUnbindService = false

    fun bindService(contextActivity: Activity, spotifyAccessToken: String) {
        Intent(contextActivity, MusicPlayerService::class.java).let { serviceIntent ->
            contextActivity.startService(Intent(serviceIntent).putExtra(SPOTIFY_EXTRA_ACCESS_TOKEN, spotifyAccessToken))
            contextActivity.bindService(serviceIntent, this, 0)
            shouldUnbindService = true
        }
    }

    fun unbindService(contextActivity: Activity) {
        if (shouldUnbindService) {
            contextActivity.unbindService(this)
            shouldUnbindService = false
        }
    }
}
