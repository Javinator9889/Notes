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
package com.javinator9889.notes.jobs.alarms

import android.os.Parcel
import android.os.Parcelable

data class Alarm(val id: Int, val time: Long = -1) : Parcelable {
    constructor(parcel: Parcel) : this(parcel.readInt(), parcel.readLong())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeLong(time)
    }

    override fun describeContents() = 0

    companion object CREATOR : Parcelable.Creator<Alarm> {
        override fun createFromParcel(parcel: Parcel) = Alarm(parcel)

        override fun newArray(size: Int): Array<Alarm?> = arrayOfNulls(size)
    }
}