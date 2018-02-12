package com.phaqlow.stag.ui

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jakewharton.rxbinding2.support.design.widget.RxTabLayout
import com.phaqlow.stag.R
import com.phaqlow.stag.ui.songs.SongsListFragment
import com.phaqlow.stag.ui.tags.TagsListFragment
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import kotlinx.android.synthetic.main.fragment_home.*


class HomeFragment : Fragment() {
    private val lifecycleDisposables = CompositeDisposable()

    companion object {
        const val TAG = "Home Fragment"
        fun newInstance() = HomeFragment()
        const val TAB_TAGS = 0
        const val TAB_SONGS = 1
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View
            = inflater.inflate(R.layout.fragment_home, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
        setRxBindings()
    }

    private fun initViews() {
        RxTabLayout.selections(tabs)
                .subscribe { tab -> setTab(tab.position) }
                .addTo(lifecycleDisposables)

        tabs.getTabAt(TAB_TAGS)?.select()
    }

    private fun setRxBindings() {
    }

    private fun setTab(tab: Int) {
        val fragment = when(tab) {
            TAB_TAGS -> TagsListFragment.newInstance()
            TAB_SONGS -> SongsListFragment.newInstance()
            else -> TagsListFragment.newInstance()
        }
        childFragmentManager.beginTransaction().replace(R.id.container, fragment).commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        lifecycleDisposables.clear()
    }
}
