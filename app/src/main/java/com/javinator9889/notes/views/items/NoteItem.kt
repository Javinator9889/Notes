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
 * Created by Javinator9889 on 18/06/20 - Notes.
 */
package com.javinator9889.notes.views.items

import android.graphics.Paint
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import com.google.android.material.card.MaterialCardView
import com.javinator9889.notes.R
import com.javinator9889.notes.data.room.Note
import com.javinator9889.notes.utils.calendar.userLocaleDate
import com.javinator9889.notes.utils.calendar.userLocaleDateTime
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.items.AbstractItem
import java.util.*


open class NoteItem(val data: Note) : AbstractItem<NoteItem.ViewHolder>() {
    @LayoutRes
    override val layoutRes: Int = R.layout.note_view

    @IdRes
    override val type: Int = R.id.container
    val noteId = data.id
    override var identifier: Long = noteId.toLong()

    override fun getViewHolder(v: View): ViewHolder = ViewHolder(v)

    class ViewHolder(view: View) : FastAdapter.ViewHolder<NoteItem>(view) {
        private val title =
            view.findViewById<TextView>(R.id.title).apply { visibility = View.VISIBLE }
        private val date =
            view.findViewById<TextView>(R.id.date).apply { visibility = View.VISIBLE }
        private val content =
            view.findViewById<TextView>(R.id.note).apply { visibility = View.VISIBLE }
        private val scheduledDate =
            view.findViewById<TextView>(R.id.scheduling).apply { visibility = View.GONE }
        private val checkBox =
            view.findViewById<ImageView>(R.id.checkbox).apply { visibility = View.GONE }
        private val cardView = view.findViewById<MaterialCardView>(R.id.cardView)

        override fun bindView(item: NoteItem, payloads: List<Any>) {
            if (item.data.title.isNullOrEmpty())
                title.visibility = View.GONE
            else {
                title.visibility = View.VISIBLE
                title.text = item.data.title
            }
            if (item.data.content.isNullOrEmpty())
                content.visibility = View.GONE
            else {
                content.visibility = View.VISIBLE
                content.text = item.data.content
            }
            if (item.isSelected) {
                checkBox.visibility = View.VISIBLE
                cardView.strokeWidth = 8
            } else {
                checkBox.visibility = View.GONE
                cardView.strokeWidth = 0
            }
            date.text = item.data.creationDate.userLocaleDate
            item.data.scheduledDate?.let {
                scheduledDate.visibility = View.VISIBLE
                scheduledDate.text = it.userLocaleDateTime
                scheduledDate.paintFlags =
                    if (Calendar.getInstance().time.after(it)) Paint.STRIKE_THRU_TEXT_FLAG
                    else scheduledDate.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                if (Calendar.getInstance().time.after(it))
                    scheduledDate.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_notifications_off,
                        0,
                        0,
                        0
                    )
                else
                    scheduledDate.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_alert,
                        0,
                        0,
                        0
                    )
            } ?: kotlin.run { scheduledDate.visibility = View.GONE }
        }

        override fun unbindView(item: NoteItem) {
            title.text = null
            date.text = null
            content.text = null
            scheduledDate.text = null
        }
    }
}