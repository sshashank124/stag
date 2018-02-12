package com.phaqlow.stag.ui.playlist

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.phaqlow.stag.R
import com.phaqlow.stag.app.App
import io.reactivex.disposables.CompositeDisposable


class PlaylistFragment : Fragment() {
    private val lifecycleDisposables = CompositeDisposable()

    companion object {
        const val TAG = "Playlist Fragment"
        fun newInstance() = PlaylistFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View
            = inflater.inflate(R.layout.fragment_playlist, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity!!.application as App).appComponent.inject(this)

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

    override fun onDestroyView() {
        super.onDestroyView()
        lifecycleDisposables.clear()
    }
}
