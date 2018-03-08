package com.phaqlow.stag.player

import android.content.ComponentName
import android.content.ServiceConnection
import android.os.IBinder
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
}
