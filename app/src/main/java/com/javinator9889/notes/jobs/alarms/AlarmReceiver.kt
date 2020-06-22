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
package com.javinator9889.notes.jobs.alarms

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.javinator9889.notes.jobs.workers.NoteAlarmWorker
import com.javinator9889.notes.utils.goAsync
import timber.log.Timber

internal const val ARG_BUNDLE = "args:intent:bundle"
internal const val ARG_ALARM = "args:alarm:alarm"
internal const val ACTION_ALARM = "com.javinator9889.notes.alarm:action:alarm"


class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Timber.d("Received alarm!")
        if (intent.action == ACTION_ALARM) {
//        if (intent.hasExtra(ARG_ALARM)) {
            Timber.d("Extra has alarm")
            val bundle = intent.getBundleExtra(ARG_BUNDLE) ?: return
            val alarm = bundle.getParcelable<Alarm>(ARG_ALARM) ?: return
//            val alarm = intent.getParcelableExtra<Alarm>(ARG_ALARM)!!
            Timber.d("Creating NoteAlarmWorker")
            val worker = NoteAlarmWorker(context, alarm)
            goAsync {
                Timber.d("Broadcast going async")
                worker.doWork()
            }
//        }
        }
    }
}