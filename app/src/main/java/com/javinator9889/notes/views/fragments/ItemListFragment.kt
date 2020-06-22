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
package com.javinator9889.notes.views.fragments

import android.os.Bundle
import android.text.Editable
import android.view.View
import androidx.annotation.LayoutRes
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import androidx.lifecycle.whenCreated
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.javinator9889.notes.R
import com.javinator9889.notes.data.handlers.ItemListHandler
import com.javinator9889.notes.data.viewmodels.NoteViewModel
import com.javinator9889.notes.views.activites.base.BaseMainFragment
import kotlinx.android.synthetic.main.notes_list_view.*
import kotlinx.coroutines.launch


class ItemListFragment : BaseMainFragment(), SwipeRefreshLayout.OnRefreshListener {
    @LayoutRes
    override val layoutId: Int = R.layout.notes_list_view
    private val noteViewModel: NoteViewModel by activityViewModels()
    private lateinit var itemListHandler: ItemListHandler

    init {
        lifecycleScope.launch {
            whenCreated {
                noteViewModel.allNotes.observe(this@ItemListFragment) {
                    itemListHandler.onNoteChanged(it)
                    progressBar.visibility = View.INVISIBLE
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        progressBar.visibility = View.VISIBLE
        itemListHandler =
            ItemListHandler(this, noteViewModel, activity.findViewById(R.id.fab), this)
        itemListHandler.setupViews()
        itemListHandler.recyclerViewAdapter.onViewCreated(view, savedInstanceState)
        if (savedInstanceState != null) {
            progressBar.visibility = View.INVISIBLE
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        itemListHandler.recyclerViewAdapter.onSaveInstanceState(outState)
    }

    override fun onRefresh() = itemListHandler.onRefresh()

    fun onSearchTermInput(input: Editable?) {
        itemListHandler.filter(input)
    }
}