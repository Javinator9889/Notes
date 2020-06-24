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
 * Created by Javinator9889 on 22/06/20 - Notes.
 */
package com.javinator9889.notes.jobs.workers

import android.content.Context
import android.os.Bundle
import com.javinator9889.notes.R
import com.javinator9889.notes.data.handlers.*
import com.javinator9889.notes.data.repositories.NoteRepository
import com.javinator9889.notes.data.room.NoteDatabase
import com.javinator9889.notes.jobs.alarms.Alarm
import com.javinator9889.notes.notifications.NotificationsHandler
import com.javinator9889.notes.utils.Constants.NOTE_CHANNEL_ID
import com.javinator9889.notes.utils.workers.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import timber.log.Timber


internal const val GROUP_KEY_NOTE_NOTIFICATIONS = "com.javinator9889.notes.NOTE_NOTIFICATIONS"


class NoteAlarmWorker(context: Context, override val alarm: Alarm) : AlarmWorker(context) {
    override suspend fun work(): Result = coroutineScope {
        Timber.d("Working...")
        val noteDao = NoteDatabase.getDatabase(context).noteDao()
        val repository = NoteRepository(noteDao)
        val noteDeferred = async(context = Dispatchers.IO) { repository.get(alarm.id) }
        val notificationsHandler = NotificationsHandler(
            context = context,
            channelId = NOTE_CHANNEL_ID,
            channelName = getString(R.string.note_notifications_name),
            channelDesc = getString(R.string.note_notifications_desc)
        )
        Timber.d("Awaiting for note to be retrieved")
        val note = noteDeferred.await() ?: return@coroutineScope Result.FAILURE
        Timber.d("Obtained note: $note")
        val extras = with(Bundle(5)) {
            putString(ARG_TITLE, note.title)
            putString(ARG_NOTE_CONTENT, note.content)
            putLong(ARG_SCHEDULED_ALARM, note.scheduledDate!!.time)
            putLong(ARG_LAST_MODIFICATION, note.creationDate.time)
            putInt(ARG_ID, note.id)
            putBoolean(ARG_EDIT_FRAGMENT, true)
            this
        }
        withContext(Dispatchers.Main) {
            notificationsHandler.createNotification(
                iconDrawable = R.drawable.ic_event_note,
                largeIcon = null,
                title = note.title,
                content = note.content,
                longContent = note.content,
                group = GROUP_KEY_NOTE_NOTIFICATIONS,
                extras = extras
            )
        }
        Result.SUCCESS
    }
}