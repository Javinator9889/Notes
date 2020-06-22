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
 * Created by Javinator9889 on 19/06/20 - Notes.
 */
package com.javinator9889.notes.application

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.javinator9889.notes.BuildConfig
import com.javinator9889.notes.data.repositories.NoteRepository
import com.javinator9889.notes.data.room.NoteDatabase
import com.javinator9889.notes.jobs.alarms.Alarm
import com.javinator9889.notes.jobs.alarms.AlarmHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber


class Notes : Application() {
    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        if (BuildConfig.DEBUG)
            Timber.plant(Timber.DebugTree())
        GlobalScope.launch { loadPendingNotifications() }
    }

    private suspend fun loadPendingNotifications() {
        val repository = NoteDatabase.getDatabase(this).noteDao().let {
            NoteRepository(it)
        }
        val alarmHandler = AlarmHandler(this)
        val pendingAlarms = withContext(Dispatchers.IO) { repository.getPendingAlarms() }
        Timber.d("Scheduling alarms: $pendingAlarms")
        for (pendingNote in pendingAlarms) {
            alarmHandler.updateAlarm(Alarm(pendingNote.id, pendingNote.scheduledDate!!.time))
        }
    }
}