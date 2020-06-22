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
 * Created by Javinator9889 on 21/06/20 - Notes.
 */
package com.javinator9889.notes.data.handlers

import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import androidx.core.view.ViewCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.commit
import androidx.transition.ChangeBounds
import androidx.transition.TransitionManager
import com.javinator9889.notes.R
import com.javinator9889.notes.data.handlers.base.BaseHandler
import com.javinator9889.notes.utils.calendar.userLocaleShortDate
import com.javinator9889.notes.utils.toPx
import com.javinator9889.notes.views.activites.MainActivity
import com.javinator9889.notes.views.fragments.ItemListFragment
import com.javinator9889.notes.views.fragments.NoteEditFragment
import com.javinator9889.notes.views.items.NoteItem
import kotlinx.android.synthetic.main.notes.*
import java.util.*

internal const val ARG_FAB_STATUS_IS_SHOWN = "args:mainactivity:fab:status_shown"
internal const val ARG_EDIT_FRAGMENT = "args:mainactivity:editfragment:status"

class MainActivityHandler(private val activity: MainActivity) : BaseHandler(activity),
    View.OnClickListener {
    private lateinit var itemListFragment: ItemListFragment
    private var noteEditFragment: NoteEditFragment? = null

    fun setupViews(savedInstanceState: Bundle?) {
        ViewCompat.setElevation(activity.backdrop, 4F.toPx(activity))
        activity.searchEditText.addTextChangedListener { itemListFragment.onSearchTermInput(it) }
        activity.fab.setOnClickListener(this)
        activity.reminderButton.setOnClickListener(this)
        activity.cancelButton.setOnClickListener(this)
        activity.save?.setOnClickListener(this)
        if (savedInstanceState == null && !activity.intent.hasExtra(ARG_EDIT_FRAGMENT)) {
            activity.supportFragmentManager.commit {
                itemListFragment = ItemListFragment()
                add(R.id.contentView, itemListFragment)
                show(itemListFragment)
            }
        } else {
            itemListFragment = savedInstanceState?.let {
                activity.supportFragmentManager.getFragment(
                    it,
                    R.layout.notes_list_view.toString()
                ) as ItemListFragment
            } ?: ItemListFragment().also {
                activity.supportFragmentManager.commit {
                    add(R.id.contentView, it)
                    show(it)
                }
            }
            if (activity.contentDescription != null)
                activity.supportFragmentManager.commit {
                    replace(R.id.contentView, itemListFragment)
                    show(itemListFragment)
                }
            val extras = savedInstanceState ?: activity.intent.extras!!
            if (extras.getBoolean(ARG_EDIT_FRAGMENT, false)) {
                with(Bundle(5)) {
                    putString(ARG_TITLE, extras.getString(ARG_TITLE))
                    putString(ARG_NOTE_CONTENT, extras.getString(ARG_NOTE_CONTENT))
                    putLong(ARG_SCHEDULED_ALARM, extras.getLong(ARG_SCHEDULED_ALARM))
                    putLong(
                        ARG_LAST_MODIFICATION,
                        extras.getLong(ARG_LAST_MODIFICATION)
                    )
                    putInt(ARG_ID, extras.getInt(ARG_ID))
                    launchEditFragment(this)
                }
            }
            if (extras.getBoolean(ARG_FAB_STATUS_IS_SHOWN, true)) activity.fab.show()
            else activity.fab.hide()
        }
        activity.swipeRefresh.setOnRefreshListener(itemListFragment)
    }

    fun onSaveInstanceState(outState: Bundle) {
        activity.supportFragmentManager.putFragment(
            outState,
            R.layout.notes_list_view.toString(),
            itemListFragment
        )
        noteEditFragment?.let {
            it.onSaveInstanceState(outState)
            outState.putBoolean(ARG_EDIT_FRAGMENT, true)
        } ?: outState.putBoolean(ARG_EDIT_FRAGMENT, false)
        outState.putBoolean(ARG_FAB_STATUS_IS_SHOWN, activity.fab.isOrWillBeShown)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.fab -> launchEditFragment(Bundle().apply { putBoolean(ARG_IS_EDITING, false) })
            R.id.reminderButton -> noteEditFragment?.onClick(v)
            R.id.cancelButton -> loadPreviousFragment(saveData = false)
            R.id.save -> loadPreviousFragment()
        }
    }

    internal fun launchEditFragment(item: NoteItem) = with(Bundle(6)) {
        putString(ARG_TITLE, item.data.title)
        putString(ARG_NOTE_CONTENT, item.data.content)
        item.data.scheduledDate?.let { putLong(ARG_SCHEDULED_ALARM, it.time) }
        putLong(ARG_LAST_MODIFICATION, item.data.creationDate.time)
        putBoolean(ARG_IS_EDITING, true)
        putInt(ARG_ID, item.noteId)
        launchEditFragment(this)
    }

    internal fun launchEditFragment(args: Bundle? = null) {
        val fragment = NoteEditFragment().apply { arguments = args }
        launchEditFragment(fragment, args)
    }

    internal fun launchEditFragment(fragment: NoteEditFragment, args: Bundle? = null) {
        val contentView = if (activity.contentDescription != null) {
            R.id.contentDescription
        } else R.id.contentView
        activity.supportFragmentManager.commit {
            setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
            if (contentView == R.id.contentView) {
                add(contentView, fragment)
                hide(itemListFragment)
                addToBackStack(null)
            } else {
                replace(contentView, fragment)
                noteEditFragment?.saveInputData()
                disallowAddToBackStack()
            }
            show(fragment)
        }
        noteEditFragment = fragment
        loadEditInterface(args)
    }

    internal fun loadEditInterface(args: Bundle?) {
        activity.fab.hide()
        val lastModification = args?.getLong(
            ARG_LAST_MODIFICATION,
            Calendar.getInstance().timeInMillis
        ) ?: Calendar.getInstance().timeInMillis
        activity.lastModified.text =
            getString(R.string.last_modification, Date(lastModification).userLocaleShortDate)
        activity.lastModified.visibility = View.VISIBLE
        activity.reminderButton.visibility = View.VISIBLE
        activity.cancelButton.visibility = View.VISIBLE
        activity.itemCount.visibility = View.INVISIBLE
        activity.filterButton.visibility = View.INVISIBLE
        loadLandscapeView()
    }

    internal fun loadListInterface() {
        activity.fab.show()
        activity.lastModified.visibility = View.GONE
        activity.reminderButton.visibility = View.GONE
        activity.cancelButton.visibility = View.GONE
        activity.supportActionBar?.let {
            it.title = getText(R.string.app_name)
            it.setDisplayHomeAsUpEnabled(false)
        }
        activity.itemCount.visibility = View.VISIBLE
        activity.filterButton.visibility = View.VISIBLE
        hideLandscapeView()
    }

    internal fun loadPreviousFragment(saveData: Boolean = true) {
        if (saveData)
            noteEditFragment?.saveInputData()
        if (activity.contentDescription != null && noteEditFragment != null)
            activity.supportFragmentManager.commit { remove(noteEditFragment!!) }
        noteEditFragment = null
        activity.supportFragmentManager.popBackStack()
        loadListInterface()
    }

    internal fun loadLandscapeView() {
        if (activity.contentDescription != null) {
            val enterRightAnimation = AnimationUtils.loadAnimation(activity, R.anim.enter_right)
            activity.contentDescription!!.apply {
                if (visibility == View.GONE) {
                    visibility = View.VISIBLE
                    clearAnimation()
                    startAnimation(enterRightAnimation)
                    val transition = ChangeBounds().apply {
                        duration = enterRightAnimation.duration
                    }
                    TransitionManager.beginDelayedTransition(activity.contentView, transition)
                }
            }
            activity.divider!!.apply {
                if (visibility == View.GONE) {
                    visibility = View.VISIBLE
                    clearAnimation()
                    startAnimation(enterRightAnimation)
                }
            }
            activity.save!!.visibility = View.VISIBLE
        }
    }

    internal fun hideLandscapeView() {
        if (activity.contentDescription != null) {
            val enterRightAnimation = AnimationUtils.loadAnimation(activity, R.anim.exit_right)
            activity.contentDescription!!.apply {
                if (visibility == View.VISIBLE) {
                    visibility = View.GONE
                    clearAnimation()
                    startAnimation(enterRightAnimation)
                    val transition = ChangeBounds().apply {
                        duration = enterRightAnimation.duration
                    }
                    TransitionManager.beginDelayedTransition(activity.contentView, transition)
                }
            }
            activity.divider!!.apply {
                if (visibility == View.VISIBLE) {
                    visibility = View.GONE
                    clearAnimation()
                    startAnimation(enterRightAnimation)
                }
            }
            activity.save!!.visibility = View.GONE
        }
    }
}