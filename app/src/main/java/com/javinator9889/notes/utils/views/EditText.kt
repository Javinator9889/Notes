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
package com.javinator9889.notes.utils.views

import android.app.Activity
import android.content.Context
import android.os.Build
import android.text.InputType
import android.view.inputmethod.InputMethodManager
import android.widget.EditText


fun EditText.disableSoftInput() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        showSoftInputOnFocus = false
    else {
        setRawInputType(InputType.TYPE_CLASS_TEXT)
        setTextIsSelectable(true)
    }
}

fun EditText.clearFocusCloseKeyboard(context: Context) {
    with(context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager) {
        hideSoftInputFromWindow(windowToken, 0)
    }
}