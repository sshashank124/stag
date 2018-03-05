package com.phaqlow.stag.ui.songs

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.support.design.widget.FloatingActionButton
import android.view.View
import com.phaqlow.stag.R
import com.phaqlow.stag.model.dao.Songs
import com.phaqlow.stag.model.dao.TagSongJoins
import com.phaqlow.stag.model.entity.Song
import com.phaqlow.stag.model.entity.Tag
import com.phaqlow.stag.model.entity.TagSongJoin
import com.phaqlow.stag.ui.home.ListFragment
import com.phaqlow.stag.util.productWith
import javax.inject.Inject


class SongsListFragment : ListFragment<Song>() {
    @Inject lateinit var songsDb: Songs
    @Inject lateinit var joins: TagSongJoins

    private lateinit var tagChooserDialog: Dialog

    override fun onAttach(context: Context) {
        super.onAttach(context)
        itemsRecyclerAdapter = SongsRecyclerAdapter(itemsList)
        itemsDb = songsDb
    }

    override fun inflateAdditionalViews(rootView: View): View {
        rootView.findViewById<FloatingActionButton>(R.id.context_fab).setImageResource(R.drawable.ic_tag_add)
        return rootView
    }

    override fun fabActionOnItems(selectedItems: List<Song>) {
        joins.getAllTagsExceptWithSongs(selectedItems)
                .register { showTagChoices(selectedItems, it.sorted()) }
    }

    private fun showTagChoices(selectedSongs: List<Song>, potentialTags: List<Tag>) {
        val checkedItems = BooleanArray(potentialTags.size)
        tagChooserDialog = AlertDialog.Builder(activity)
                .setTitle(R.string.tag_chooser_title)
                .setMultiChoiceItems(potentialTags.map { it.name }.toTypedArray(), checkedItems,
                        { _, selectPos, checked -> checkedItems[selectPos] = checked })
                .setNegativeButton(R.string.cancel, { _, _ -> itemsRecyclerAdapter.clearSelections() })
                .setPositiveButton(R.string.add, { _, _ ->
                    val joinPairs = potentialTags.filterIndexed { index, _ -> checkedItems[index] }
                            .productWith(selectedSongs, { tag, song -> TagSongJoin(tag.id, song.id)})
                    joins.insertTagSongJoins(joinPairs).register()
                    itemsRecyclerAdapter.clearSelections()
                }).create()
        tagChooserDialog.show()
    }

    override val detailActivityClass = SongDetailActivity::class
}
