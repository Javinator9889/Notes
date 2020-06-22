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
package com.javinator9889.notes.jobs

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.javinator9889.notes.data.repositories.NoteRepository
import com.javinator9889.notes.data.room.NoteDatabase
import com.javinator9889.notes.jobs.alarms.Alarm
import com.javinator9889.notes.jobs.alarms.AlarmHandler
import com.javinator9889.notes.utils.goAsync
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class BootCompletedJob : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED
            || intent.action != Intent.ACTION_MY_PACKAGE_REPLACED)
            return
        val alarmHandler = AlarmHandler(context)
        val repository = NoteDatabase.getDatabase(context).noteDao().let { NoteRepository(it) }
        goAsync {
            val pendingAlarms = withContext(Dispatchers.IO) {
                repository.getPendingAlarms()
            }
            for (pendingNote in pendingAlarms) {
                alarmHandler.scheduleAlarm(Alarm(pendingNote.id, pendingNote.scheduledDate!!.time))
            }
        }
    }
}