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
 * Created by Javinator9889 on 17/06/20 - Notes.
 */
package com.javinator9889.notes.data.room

import androidx.lifecycle.LiveData
import androidx.room.*
import java.util.*


@Dao
interface NoteDao {
    @Query("SELECT * FROM note ORDER BY date ASC")
    fun getAll(): LiveData<List<Note>>

    @Query("SELECT * FROM note WHERE scheduling > :from")
    suspend fun getPendingAlarms(from: Date = Calendar.getInstance().time): List<Note>

    @Query("SELECT * FROM note WHERE id == :id")
    suspend fun get(id: Int): Note?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(vararg notes: Note)

    @Update
    suspend fun update(note: Note)

    @Delete
    suspend fun delete(note: Note)

    @Query("DELETE FROM note WHERE id == :id")
    suspend fun delete(id: Int)

    @Query("DELETE FROM note")
    suspend fun deleteAll()
}