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
package com.javinator9889.notes.views.activites.base

import android.os.Bundle
import android.view.View
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_HALF_EXPANDED
import com.javinator9889.notes.R

abstract class TopBarSheetFragmentActivity : AppCompatActivity() {
    @LayoutRes open var layoutId: Int = R.layout.notes
    abstract val bottomSheetId: Int
    protected lateinit var sheetBehavior: BottomSheetBehavior<View>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutId)
        supportActionBar?.elevation = 0F

        sheetBehavior = BottomSheetBehavior.from(findViewById<View>(bottomSheetId))
        sheetBehavior.isFitToContents = false
        sheetBehavior.isHideable = false
        sheetBehavior.state = STATE_EXPANDED
        sheetBehavior.halfExpandedRatio = .7F
    }

    @BottomSheetBehavior.State
    protected fun toggleBottomSheet(): Int {
        sheetBehavior.state = when (sheetBehavior.state) {
            STATE_EXPANDED -> STATE_HALF_EXPANDED
            STATE_HALF_EXPANDED -> STATE_EXPANDED
            else -> STATE_EXPANDED
        }
        return sheetBehavior.state
    }
}