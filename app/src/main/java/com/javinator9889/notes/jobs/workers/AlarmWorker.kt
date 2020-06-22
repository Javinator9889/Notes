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
import androidx.annotation.ArrayRes
import androidx.annotation.StringRes
import com.javinator9889.notes.jobs.alarms.Alarm
import com.javinator9889.notes.utils.workers.Result
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber

abstract class AlarmWorker(context: Context) {
    protected val context = context.applicationContext
    protected abstract val alarm: Alarm
    internal val coroutineContext = Dispatchers.Default
    internal val job = Job()

    fun doWork() {
        val coroutineScope = CoroutineScope(coroutineContext + job)
        coroutineScope.launch {
            Timber.d("Launching work")
            try {
                val result = work()
                Timber.d("Result: $result")
                if (result == Result.SUCCESS)
                    job.complete()
            } catch (t: Throwable) {
                Timber.e(t, "Error in worker!")
                job.completeExceptionally(t)
            }
        }
    }

    abstract suspend fun work(): Result

    protected fun getString(@StringRes resId: Int): String =
        context.getString(resId)

    protected fun getString(@StringRes resId: Int, vararg args: Any): String =
        context.getString(resId, *args)

    protected fun getText(@StringRes resId: Int): CharSequence =
        context.getText(resId)

    protected fun getStringArray(@ArrayRes resId: Int): Array<out String> =
        context.resources.getStringArray(resId)
}