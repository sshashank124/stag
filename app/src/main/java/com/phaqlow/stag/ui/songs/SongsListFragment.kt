package com.phaqlow.stag.ui.songs

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.phaqlow.stag.R


class SongsListFragment : Fragment() {

    companion object {
        fun newInstance() = SongsListFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View
            = inflater.inflate(R.layout.fragment_songs_list, container, false)

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
