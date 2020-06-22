/*
 * Copyright © 2020 - present | Notes by Javinator9889
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see https://www.gnu.org/licenses/.
 *
 * Created by Javinator9889 on 20/06/20 - Notes.
 */
package com.javinator9889.notes.data.handlers

import android.os.Bundle
import android.text.Editable
import android.text.Spannable
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.SparseArray
import android.view.MenuItem
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.core.util.set
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.javinator9889.notes.R
import com.javinator9889.notes.data.handlers.base.BaseFragmentHandler
import com.javinator9889.notes.data.room.Note
import com.javinator9889.notes.data.viewmodels.NoteViewModel
import com.javinator9889.notes.utils.animator.LoopAnimationListener
import com.javinator9889.notes.views.activites.base.BaseMainFragment
import com.javinator9889.notes.views.items.NoteItem
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.ISelectionListener
import com.mikepenz.fastadapter.adapters.FastItemAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter
import com.mikepenz.fastadapter.expandable.ExpandableExtension
import com.mikepenz.fastadapter.expandable.getExpandableExtension
import com.mikepenz.fastadapter.helpers.ActionModeHelper
import com.mikepenz.fastadapter.helpers.RangeSelectorHelper
import com.mikepenz.fastadapter.helpers.UndoHelper
import com.mikepenz.fastadapter.select.SelectExtension
import com.mikepenz.fastadapter.select.getSelectExtension
import com.mikepenz.fastadapter.swipe.SimpleSwipeCallback
import kotlinx.android.synthetic.main.notes.*
import kotlinx.android.synthetic.main.notes_list_view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.*


class ItemListHandler(
    fragment: BaseMainFragment,
    private val noteViewModel: NoteViewModel,
    private val fab: FloatingActionButton,
    lifecycleOwner: LifecycleOwner
) : BaseFragmentHandler(fragment, lifecycleOwner), SwipeRefreshLayout.OnRefreshListener,
    SimpleSwipeCallback.ItemSwipeCallback,
    ISelectionListener<NoteItem> {
    val itemAdapter = ItemAdapter<NoteItem>()
    val recyclerViewAdapter = RecyclerViewAdapter()
    private val displayedNotes = SparseArray<Date>()
    private val selectedNotesSet = mutableSetOf<NoteItem>()

    fun setupViews() = lifecycleOwner.lifecycleScope.launchWhenCreated {
        val activityLauncher = object : ClickableSpan() {
            override fun onClick(widget: View) {
                fab.callOnClick()
            }
        }
        val spannableText = SpannableString(getText(R.string.click_to_create)).apply {
            setSpan(activityLauncher, 0, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        withContext(Dispatchers.Main) {
            fragment.noContentText.text = getText(R.string.no_items)
            fragment.clickableText.text = spannableText
            fragment.clickableText.movementMethod = LinkMovementMethod.getInstance()
            fragment.lottieAnimation.addAnimatorListener(
                LoopAnimationListener(
                    fragment.lottieAnimation,
                    minFrame = 151,
                    maxFrame = 270
                )
            )
            fragment.lottieAnimation.setMaxFrame(270)
        }
    }

    fun onNoteChanged(noteList: List<Note>?) = lifecycleOwner.lifecycleScope.launchWhenCreated {
        if (noteList.isNullOrEmpty()) fragment.noContentLayout.visibility = View.VISIBLE
        else {
            fragment.noContentLayout.visibility = View.INVISIBLE
            noteList.forEach { note ->
                val item = NoteItem(note)
                val position = itemAdapter.getAdapterPosition(item)
                if (position != -1) {
                    if (displayedNotes[item.noteId] != item.data.creationDate) {
                        itemAdapter[position] = item
                        itemAdapter.move(position, 0)
                    }
                } else {
                    itemAdapter.add(0, item)
                }
                displayedNotes[item.noteId] = item.data.creationDate
            }
        }
        withContext(Dispatchers.Main) { updateItemCount() }
    }

    override fun onRefresh() {
        lifecycleOwner.lifecycleScope.launchWhenCreated {
            with(fragment.activity.swipeRefresh) {
                withContext(Dispatchers.Main) { isRefreshing = true }
                itemAdapter.clear()
                displayedNotes.clear()
                noteViewModel.allNotes.value?.asReversed()?.forEach { note ->
                    itemAdapter.add(NoteItem(note))
                    displayedNotes[note.id] = note.creationDate
                }
                withContext(Dispatchers.Main) { updateItemCount(); isRefreshing = false }
            }
        }
    }

    override fun itemSwiped(position: Int, direction: Int) {
        val item = itemAdapter.getAdapterItem(position)
        if (direction != ItemTouchHelper.LEFT)
            return
        itemAdapter.remove(position)
        noteViewModel.delete(item.noteId)
        Snackbar.make(fragment.recyclerList, R.string.item_removed, Snackbar.LENGTH_SHORT)
            .setAction(R.string.undo) { noteViewModel.insert(item.data) }
            .show()
    }

    override fun onSelectionChanged(item: NoteItem, selected: Boolean) {
        if (selected) selectedNotesSet.add(item)
        else selectedNotesSet.remove(item)
    }

    fun filter(input: Editable?) = lifecycleOwner.lifecycleScope.launchWhenCreated {
        withContext(Dispatchers.Main) { updateItemCount() }
        itemAdapter.filter(input)
        itemAdapter.itemFilter.filterPredicate = { item: NoteItem, constraint: CharSequence? ->
            (item.data.title?.contains(constraint.toString(), ignoreCase = true) ?: false
                    || item.data.content?.contains(
                constraint.toString(),
                ignoreCase = true
            ) ?: false)
        }
    }

    private fun updateItemCount() {
        fragment.activity.itemCount.text =
            fragment.resources.getQuantityString(
                R.plurals.notes,
                itemAdapter.adapterItemCount,
                itemAdapter.adapterItemCount
            )
    }

    open inner class RecyclerViewAdapter internal constructor() : RecyclerView.OnScrollListener() {
        private lateinit var fastItemAdapter: FastItemAdapter<NoteItem>
        private lateinit var expandableExtension: ExpandableExtension<NoteItem>
        private lateinit var selectExtension: SelectExtension<NoteItem>
        private lateinit var rangeSelectorHelper: RangeSelectorHelper<*>
        private lateinit var undoHelper: UndoHelper<NoteItem>
        private var actionModeHelper: ActionModeHelper<NoteItem>? = null

        fun onViewCreated(view: View, savedInstanceState: Bundle?) =
            lifecycleOwner.lifecycleScope.launchWhenCreated {
                fastItemAdapter = FastItemAdapter(itemAdapter)
                val rvManager = LinearLayoutManager(view.context).apply {
                    isSmoothScrollbarEnabled = true
                }
                withContext(Dispatchers.Main) {
                    with(fragment.recyclerList) {
                        layoutManager = rvManager
                        adapter = fastItemAdapter
                        itemAnimator = DefaultItemAnimator()
                        isNestedScrollingEnabled = true
                        addOnScrollListener(this@RecyclerViewAdapter)
                        invalidate()
                    }
                }
                selectExtension = fastItemAdapter.getSelectExtension().apply {
                    isSelectable = true
                    multiSelect = true
                    selectOnLongClick = true
                    allowDeselection = true
                    selectWithItemUpdate = true
                }
                expandableExtension = fastItemAdapter.getExpandableExtension()
                actionModeHelper =
                    ActionModeHelper(fastItemAdapter, R.menu.cab, ActionBarCallback())
                        .withTitleProvider(object : ActionModeHelper.ActionModeTitleProvider {
                            override fun getTitle(selected: Int) =
                                "$selected/${fastItemAdapter.itemAdapter.adapterItemCount}"
                        })
                rangeSelectorHelper = RangeSelectorHelper(fastItemAdapter)
                    .withSavedInstanceState(savedInstanceState)
                    .withActionModeHelper(actionModeHelper)
                fastItemAdapter.onPreClickListener = { _, _, item, _ ->
                    Timber.d("PreClick $item")
                    val res = actionModeHelper?.onClick(fragment.activity, item)
                    if (res != null && !res) true else res ?: false
                }
                fastItemAdapter.onPreLongClickListener = { _, _, _, position ->
                    Timber.d("PreLongClick $position")
                    val actionModeWasActive = actionModeHelper?.isActive ?: false
                    val actionMode = actionModeHelper?.onLongClick(fragment.activity, position)
                    rangeSelectorHelper.onLongClick(position)
                    actionMode != null && !actionModeWasActive
                }
                fastItemAdapter.onClickListener =
                    { v, adapter, item, position ->
                        Timber.d("Clicked $item")
                        // check if the actionMode consumes the click. This returns true, if it does, false if not
                        if (actionModeHelper?.isActive == true) {
                            rangeSelectorHelper.onClick()
                            false
                        } else fragment.activity.onItemClick(v, adapter, item, position)
                    }
                undoHelper =
                    UndoHelper(fastItemAdapter, object : UndoHelper.UndoListener<NoteItem> {
                        override fun commitRemove(
                            positions: Set<Int>,
                            removed: ArrayList<FastAdapter.RelativeInfo<NoteItem>>
                        ) {
                            Timber.e("Positions: $positions removed: ${removed.size}")
                        }
                    })
                val leaveBehindDrawableLeft =
                    ResourcesCompat.getDrawable(
                        view.resources,
                        R.drawable.ic_delete_sweep_white,
                        null
                    )
                val touchCallback = SimpleSwipeCallback(
                    this@ItemListHandler, leaveBehindDrawableLeft, ItemTouchHelper.LEFT
                )
                val touchHelper = ItemTouchHelper(touchCallback)
                touchHelper.attachToRecyclerView(fragment.recyclerList)
                fastItemAdapter.withSavedInstanceState(savedInstanceState)
            }

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (newState == RecyclerView.SCROLL_STATE_IDLE) fab.show()
            else fab.hide()
        }

        fun onSaveInstanceState(outState: Bundle) {
            fastItemAdapter.saveInstanceState(outState)
        }

        internal inner class ActionBarCallback : ActionModeHelper.ActionItemClickedListener {
            override fun onClick(
                mode: androidx.appcompat.view.ActionMode,
                item: MenuItem
            ): Boolean {
                // delete the selected items with the SubItemUtil to correctly handle sub items
                // this will even delete empty headers if you want to
                val selectedItems = selectExtension.selectedItems
                undoHelper.remove(
                    fragment.recyclerList,
                    "Items removed",
                    "Undo",
                    Snackbar.LENGTH_LONG,
                    selectExtension.selections
                ).addCallback(object : BaseTransientBottomBar.BaseCallback<Snackbar>() {
                    override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                        Timber.d("Dismissed snackbar")
                        super.onDismissed(transientBottomBar, event)
                        if (event != DISMISS_EVENT_ACTION) {
                            for (note in selectedItems) {
                                Timber.d("Removed note by id: ${note.noteId}")
                                noteViewModel.delete(note.noteId)
                            }
                        }
                    }
                })
                //as we no longer have a selection so the actionMode can be finished
                mode.finish()
                //we consume the event
                return true
            }
        }
    }
}