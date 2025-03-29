package com.example.thdt_quangtran.models

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "ChiTietGioHang",
    primaryKeys = ["maGioHang", "maSanPham"],
    foreignKeys = [
        ForeignKey(
            entity = GioHang::class,
            parentColumns = ["maGioHang"],
            childColumns = ["maGioHang"]
        ),
        ForeignKey(
            entity = SanPham::class,
            parentColumns = ["maSanPham"],
            childColumns = ["maSanPham"]
        )
    ]
)
data class ChiTietGioHang(
    val maGioHang: Int,
    val maSanPham: Int,
    var soLuong: Int,
    val giaBan: Long
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readLong()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(maGioHang)
        parcel.writeInt(maSanPham)
        parcel.writeInt(soLuong)
        parcel.writeLong(giaBan)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ChiTietGioHang> {
        override fun createFromParcel(parcel: Parcel): ChiTietGioHang {
            return ChiTietGioHang(parcel)
        }

        override fun newArray(size: Int): Array<ChiTietGioHang?> {
            return arrayOfNulls(size)
        }
    }
}