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
package com.javinator9889.notes.notifications

import android.app.NotificationChannel
import android.app.NotificationChannelGroup
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import androidx.annotation.DrawableRes
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.javinator9889.notes.views.activites.MainActivity


internal const val PENDING_INTENT_CODE = 201


class NotificationsHandler(
    private val context: Context,
    private val channelId: String,
    private val channelName: String = "",
    private val channelDesc: String = "",
    notificationGroup: String? = null
) {
    private val vibrationPattern = longArrayOf(300L, 300L, 300L, 300L)

    init {
        if (!isNotificationChannelCreated())
            createNotificationChannel()
        notificationGroup?.let { createNotificationGroup(it) }
    }

    fun createNotification(
        @DrawableRes iconDrawable: Int,
        largeIcon: Bitmap?,
        title: CharSequence?,
        content: CharSequence?,
        priority: Int = NotificationCompat.PRIORITY_DEFAULT,
        longContent: CharSequence? = null,
        extras: Bundle? = null,
        group: String? = null,
        titleInBold: Boolean = false
    ) {
        val actionIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            extras?.let { putExtras(it) }
        }
        val notifyPendingIntent = PendingIntent.getActivity(
            context, PENDING_INTENT_CODE, actionIntent, PendingIntent.FLAG_UPDATE_CURRENT
        )
        with(NotificationCompat.Builder(context, channelId)) {
            setSmallIcon(iconDrawable)
            setLargeIcon(largeIcon)
            setContentTitle(title)
            setContentText(content)
            setPriority(priority)
            setVibrate(vibrationPattern)
            setContentIntent(notifyPendingIntent)
            setAutoCancel(true)
            longContent?.let {
                setStyle(NotificationCompat.BigTextStyle().bigText(it))
            }
            group?.let { setGroup(group) }
            build()
        }.run {
            NotificationManagerCompat.from(context)
                .notify(NotificationsId.id, this)
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val that = this
            val channel =
                NotificationChannel(channelId, channelName, importance)
                    .apply {
                        description = channelDesc
                        vibrationPattern = that.vibrationPattern
                        enableVibration(true)
                    }
            NotificationManagerCompat.from(context).createNotificationChannel(channel)
        }
    }

    private fun isNotificationChannelCreated(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = context
                .getSystemService(Context.NOTIFICATION_SERVICE) as
                    NotificationManager
            val channel = manager.getNotificationChannel(channelId)
            channel?.let {
                return it.importance != NotificationManager.IMPORTANCE_NONE
            } ?: return false
        } else {
            return NotificationManagerCompat.from(context)
                .areNotificationsEnabled()
        }
    }

    private fun createNotificationGroup(group: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannelGroup(channelId, group).also {
                NotificationManagerCompat.from(context).createNotificationChannelGroup(it)
            }
        }
    }
}