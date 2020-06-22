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
package com.javinator9889.notes.views.fragments

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.activityViewModels
import com.google.android.material.snackbar.Snackbar
import com.javinator9889.notes.R
import com.javinator9889.notes.data.handlers.NoteEditHandler
import com.javinator9889.notes.data.viewmodels.NoteViewModel
import com.javinator9889.notes.jobs.alarms.Alarm
import com.javinator9889.notes.jobs.alarms.AlarmHandler
import com.javinator9889.notes.views.activites.base.BaseMainFragment
import kotlinx.android.synthetic.main.notes.*
import timber.log.Timber
import java.util.*


class NoteEditFragment : BaseMainFragment(), View.OnClickListener {
    override val layoutId: Int = R.layout.note_edit
    internal val noteEditHandler = NoteEditHandler(this, this)
    private val noteViewModel: NoteViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Timber.d("Creating Edit Fragment")
        noteEditHandler.onViewCreated(savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        noteEditHandler.onSaveInstanceState(outState)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean =
        if (item.itemId == android.R.id.home) {
            activity.onBackPressed()
            true
        } else super.onOptionsItemSelected(item)

    override fun onClick(v: View) = noteEditHandler.onClick(v)

    fun saveInputData() {
        val note = noteEditHandler.saveInputData()
        if (note.title.isNullOrEmpty() && note.content.isNullOrEmpty()) {
            Snackbar.make(
                activity.notesLayout,
                R.string.discarding_empty,
                Snackbar.LENGTH_LONG
            ).show()
            return
        }
        if (note.id != 0) {
            if (note.creationDate != noteEditHandler.lastModificationDate)
                noteViewModel.update(note)
        } else {
            noteViewModel.insert(note)
        }
        val alarmHandler = AlarmHandler(requireContext())
        if (note.scheduledDate != null && note.scheduledDate.after(Calendar.getInstance().time))
            alarmHandler.updateAlarm(Alarm(note.id, note.scheduledDate.time))
        else
            alarmHandler.cancelAlarm(Alarm(note.id))
    }
}