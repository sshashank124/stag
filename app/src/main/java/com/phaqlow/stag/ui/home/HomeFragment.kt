package com.phaqlow.stag.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jakewharton.rxbinding2.support.design.widget.selections
import com.phaqlow.stag.R
import com.phaqlow.stag.ui.songs.SongsListFragment
import com.phaqlow.stag.ui.tags.TagsListFragment
import com.phaqlow.stag.util.C
import com.phaqlow.stag.util.ui.LifecycleFragment
import kotlinx.android.synthetic.main.fragment_home.*


class HomeFragment : LifecycleFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View
            = inflater.inflate(R.layout.fragment_home, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
    }

    private fun initViews() {
        tabs.selections().register { setTab(it.position) }
        tabs.getTabAt(C.TAB_TAGS)?.select()
    }

    private fun setTab(tab: Int) {
        val fragment = when(tab) {
            C.TAB_TAGS  -> TagsListFragment()
            C.TAB_SONGS -> SongsListFragment()
            else -> TagsListFragment()
        }
        childFragmentManager.beginTransaction().replace(R.id.container, fragment).commit()
    }
}
