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
package com.javinator9889.notes.data.repositories

import androidx.lifecycle.LiveData
import com.javinator9889.notes.data.room.Note
import com.javinator9889.notes.data.room.NoteDao

class NoteRepository(private val noteDao: NoteDao) {
    val allNotes: LiveData<List<Note>> = noteDao.getAll()

    suspend fun insert(note: Note) = noteDao.insertAll(note)
    suspend fun update(note: Note) = noteDao.update(note)
    suspend fun delete(id: Int) = noteDao.delete(id)
    suspend fun get(id: Int) = noteDao.get(id)
    suspend fun getPendingAlarms() = noteDao.getPendingAlarms()
}