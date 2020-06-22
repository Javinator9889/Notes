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

import android.app.AlarmManager
import android.app.AlarmManager.RTC_WAKEUP
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.app.AlarmManagerCompat
import timber.log.Timber

class AlarmHandler(private val context: Context) {
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun scheduleAlarm(alarm: Alarm) {
        Timber.d("Scheduling alarm $alarm")
        createPendingIntentForAlarm(alarm)?.let {
            AlarmManagerCompat.setExactAndAllowWhileIdle(alarmManager, RTC_WAKEUP, alarm.time, it)
        }
    }

    fun cancelAlarm(alarm: Alarm) {
        Timber.d("Cancelling alarm $alarm")
        createPendingIntentForAlarm(alarm, flags = PendingIntent.FLAG_NO_CREATE)?.let {
            alarmManager.cancel(it)
        }
    }

    fun updateAlarm(alarm: Alarm) {
        cancelAlarm(alarm)
        scheduleAlarm(alarm)
    }

    private fun createPendingIntentForAlarm(alarm: Alarm, flags: Int = 0): PendingIntent? =
        with(Intent(context, AlarmReceiver::class.java)) {
            action = ACTION_ALARM
            putExtra(ARG_BUNDLE, Bundle(1).apply { putParcelable(ARG_ALARM, alarm) })
//            putExtra(ARG_ALARM, alarm)
//            putExtra(ARG_ID, alarm.id)
            PendingIntent.getBroadcast(context, alarm.id, this, flags)
        }
}