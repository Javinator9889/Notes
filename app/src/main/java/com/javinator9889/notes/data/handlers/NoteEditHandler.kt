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

import android.graphics.Paint
import android.os.Bundle
import android.text.format.DateFormat
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.LifecycleOwner
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.WhichButton
import com.afollestad.materialdialogs.actions.getActionButton
import com.afollestad.materialdialogs.actions.setActionButtonEnabled
import com.afollestad.materialdialogs.datetime.dateTimePicker
import com.javinator9889.notes.R
import com.javinator9889.notes.data.handlers.base.BaseFragmentHandler
import com.javinator9889.notes.data.room.Note
import com.javinator9889.notes.utils.calendar.userLocaleDateTime
import com.javinator9889.notes.utils.calendar.userLocaleShortDate
import com.javinator9889.notes.views.activites.base.BaseMainFragment
import kotlinx.android.synthetic.main.note_edit.*
import kotlinx.android.synthetic.main.notes.*
import timber.log.Timber
import java.util.*

internal const val ARG_TITLE = "args:intent:noteedit:title"
internal const val ARG_LAST_MODIFICATION = "args:intent:noteedit:modification_date"
internal const val ARG_SCHEDULED_ALARM = "args:intent:noteedit:alarm"
internal const val ARG_NOTE_CONTENT = "args:intent:noteedit:content"
internal const val ARG_ID = "args:intent:noteedit:id"
internal const val ARG_IS_EDITING = "args:intent:noteedit:is_editing"

class NoteEditHandler(fragment: BaseMainFragment, lifecycleOwner: LifecycleOwner) :
    BaseFragmentHandler(fragment, lifecycleOwner),
    View.OnClickListener {
    var lastModificationDate: Date = Date(0L)
    private var reminderAlarm: Long = -1L
    private var modificationDate: Date = Date(0L)
    private var noteId: Int = 0

    fun onViewCreated(savedInstanceState: Bundle?) {
        fragment.noteReminder.setOnClickListener(this)
        val args = fragment.arguments ?: savedInstanceState
        args?.let { it ->
            fragment.activity.supportActionBar?.let { bar ->
                bar.setDisplayHomeAsUpEnabled(true)
                bar.title = if (it.getBoolean(ARG_IS_EDITING, false))
                    getText(R.string.editing)
                else getText(R.string.creating)
            }
            fragment.noteTitle.editText?.setText(it.getString(ARG_TITLE))
            fragment.noteContent.editText?.setText(it.getString(ARG_NOTE_CONTENT))
            reminderAlarm = it.getLong(ARG_SCHEDULED_ALARM, -1L).also {
                if (it != -1L) {
                    fragment.noteReminder.text = Date(it).userLocaleDateTime
                    fragment.noteReminder.visibility = View.VISIBLE
                    fragment.noteReminder.paintFlags =
                        if (Calendar.getInstance().time.after(Date(it))) Paint.STRIKE_THRU_TEXT_FLAG
                        else fragment.noteReminder.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                    if (Calendar.getInstance().time.after(Date(it)))
                        fragment.noteReminder.setIconResource(R.drawable.ic_notifications_off)
                    else
                        fragment.noteReminder.setIconResource(R.drawable.ic_alert)
                }
            }
            fragment.noteReminder.setOnClickListener(this)
            fragment.noteTitle.editText?.addTextChangedListener {
                updateModificationDate()
            }
            fragment.noteContent.editText?.addTextChangedListener {
                updateModificationDate()
            }
            it.getLong(ARG_LAST_MODIFICATION, -1L).also {
                modificationDate = if (it == -1L) Calendar.getInstance().time else Date(it)
                lastModificationDate = modificationDate
            }
            noteId = it.getInt(ARG_ID, 0)
        }
    }

    fun saveInputData(): Note = Note(
        title = fragment.noteTitle.editText?.text.toString(),
        content = fragment.noteContent.editText?.text.toString(),
        creationDate = modificationDate,
        scheduledDate = if (reminderAlarm != -1L) Date(reminderAlarm) else null,
        id = noteId
    )

    fun onSaveInstanceState(outState: Bundle) {
        try {
            outState.putString(ARG_TITLE, fragment.noteTitle.editText?.text.toString())
            outState.putString(ARG_NOTE_CONTENT, fragment.noteContent.editText?.text.toString())
            outState.putLong(ARG_SCHEDULED_ALARM, reminderAlarm)
            outState.putLong(ARG_LAST_MODIFICATION, modificationDate.time)
            outState.putInt(ARG_ID, noteId)
        } catch (e: NullPointerException) {
            Timber.w(e, "Fragment pointer is lost in this instance")
        }
    }

    private fun updateModificationDate() {
        modificationDate = Calendar.getInstance().time
        fragment.activity.lastModified.text =
            getString(R.string.last_modification, modificationDate.userLocaleShortDate)
    }

    override fun onClick(v: View) {
        if (v.id != R.id.reminderButton && v.id != R.id.noteReminder)
            return
        MaterialDialog(v.context).show {
            dateTimePicker(
                requireFutureDateTime = true,
                show24HoursView = DateFormat.is24HourFormat(v.context),
                currentDateTime = Calendar.getInstance(),
                autoFlipToTime = true
            ) { _, dateTime ->
                dateTime[Calendar.SECOND] = 0
                reminderAlarm = dateTime.timeInMillis
                with(fragment.noteReminder) {
                    text = dateTime.time.userLocaleDateTime
                    visibility = View.VISIBLE
                    paintFlags = paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                }
                updateModificationDate()
            }
            setActionButtonEnabled(WhichButton.NEUTRAL, true)
            with(getActionButton(WhichButton.NEUTRAL)) {
                setOnClickListener {
                    reminderAlarm = -1L
                    fragment.noteReminder.visibility = View.GONE
                    updateModificationDate()
                    this@show.dismiss()
                }
                text = getText(R.string.remove)
            }
        }
    }
}