package com.phaqlow.stag.ui.playlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.phaqlow.stag.R
import com.phaqlow.stag.util.ui.LifecycleFragment


class PlaylistFragment : LifecycleFragment() {
    companion object {
        const val TAG = "Playlist Fragment"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View
            = inflater.inflate(R.layout.fragment_playlist, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
        setRxBindings()
        loadData()
    }

    private fun initViews() {
    }

    private fun loadData() {
    }

    private fun setRxBindings() {
    }
}
