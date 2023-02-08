package com.example.tp3

import android.os.Parcel
import android.os.Parcelable

class Station(var nom: String?, var dispo:Int?, var total:Int?, var lon:Double?, var lat:Double): Parcelable
{


    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Double::class.java.classLoader) as? Double,
        parcel.readDouble()
    ) {
    }

    // Méthode d'affichage pour la listView
    override fun toString(): String {
        return "$nom\n$dispo place(s) libre(s) sur $total"
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(nom)
        parcel.writeValue(dispo)
        parcel.writeValue(total)
        parcel.writeValue(lon)
        parcel.writeDouble(lat)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Station> {
        override fun createFromParcel(parcel: Parcel): Station {
            return Station(parcel)
        }

        override fun newArray(size: Int): Array<Station?> {
            return arrayOfNulls(size)
        }
    }
}