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
package com.javinator9889.notes.utils

import android.content.Context
import android.util.DisplayMetrics


fun Float.toPx(context: Context) = with(context.resources.displayMetrics) {
    this@toPx * (densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
}

fun Float.toDp(context: Context) = with(context.resources.displayMetrics) {
    this@toDp / (densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
}