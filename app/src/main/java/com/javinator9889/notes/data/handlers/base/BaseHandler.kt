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
package com.javinator9889.notes.data.handlers.base

import android.content.Context
import androidx.annotation.StringRes


abstract class BaseHandler(internal val context: Context) {
    fun getString(@StringRes id: Int) = context.getString(id)
    fun getString(@StringRes id: Int, vararg args: Any) = context.getString(id, *args)
    fun getText(@StringRes id: Int) = context.getText(id)
}