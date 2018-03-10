package com.phaqlow.stag.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jakewharton.rxbinding2.support.design.widget.selections
import com.phaqlow.stag.R
import com.phaqlow.stag.ui.songs.SongsListFragment
import com.phaqlow.stag.ui.tags.TagsListFragment
import com.phaqlow.stag.util.TAB_SONGS
import com.phaqlow.stag.util.TAB_TAGS
import com.phaqlow.stag.util.disposables.DisposableFragment
import kotlinx.android.synthetic.main.fragment_home.*


class HomeFragment : DisposableFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View = inflater.inflate(R.layout.fragment_home, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
    }

    private fun initViews() {
        tabs.selections().register { setTab(it.position) }
        tabs.getTabAt(TAB_TAGS)?.select()
    }

    private fun setTab(tabIndex: Int) =
            childFragmentManager.beginTransaction().replace(R.id.container, getFragmentAt(tabIndex)).commit()

    private fun getFragmentAt(tabIndex: Int) = when (tabIndex) {
        TAB_TAGS -> TagsListFragment()
        TAB_SONGS -> SongsListFragment()
        else -> TagsListFragment()
    }
}
