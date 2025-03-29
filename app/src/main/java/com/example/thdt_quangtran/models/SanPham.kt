package com.example.thdt_quangtran.models

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "SanPham")
data class SanPham(
    @PrimaryKey(autoGenerate = true)
    val maSanPham: Int = 0,
    val tenSanPham: String,
    val model: String,
    val donViTinh: String,
    val phanLoai: String,
    val soLuongTonKho: Int,
    val giaBan: Long,
    val giaNhap: Long,
    val moTaSanPham: String? = null,
    val hinhAnhSanPham: ByteArray? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readInt(),
        parcel.readLong(),
        parcel.readLong(),
        parcel.readString(),
        parcel.createByteArray()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(maSanPham)
        parcel.writeString(tenSanPham)
        parcel.writeString(model)
        parcel.writeString(donViTinh)
        parcel.writeString(phanLoai)
        parcel.writeInt(soLuongTonKho)
        parcel.writeLong(giaBan)
        parcel.writeLong(giaNhap)
        parcel.writeString(moTaSanPham)
        parcel.writeByteArray(hinhAnhSanPham)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<SanPham> {
        override fun createFromParcel(parcel: Parcel): SanPham {
            return SanPham(parcel)
        }

        override fun newArray(size: Int): Array<SanPham?> {
            return arrayOfNulls(size)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SanPham

        if (maSanPham != other.maSanPham) return false
        if (tenSanPham != other.tenSanPham) return false
        if (model != other.model) return false
        if (donViTinh != other.donViTinh) return false
        if (phanLoai != other.phanLoai) return false
        if (soLuongTonKho != other.soLuongTonKho) return false
        if (giaBan != other.giaBan) return false
        if (giaNhap != other.giaNhap) return false
        if (moTaSanPham != other.moTaSanPham) return false
        if (hinhAnhSanPham != null) {
            if (other.hinhAnhSanPham == null) return false
            if (!hinhAnhSanPham.contentEquals(other.hinhAnhSanPham)) return false
        } else if (other.hinhAnhSanPham != null) return false

        return true
    }

    override fun hashCode(): Int {
        var result = maSanPham
        result = 31 * result + tenSanPham.hashCode()
        result = 31 * result + model.hashCode()
        result = 31 * result + donViTinh.hashCode()
        result = 31 * result + phanLoai.hashCode()
        result = 31 * result + soLuongTonKho
        result = 31 * result + giaBan.hashCode()
        result = 31 * result + giaNhap.hashCode()
        result = 31 * result + (moTaSanPham?.hashCode() ?: 0)
        result = 31 * result + (hinhAnhSanPham?.contentHashCode() ?: 0)
        return result
    }
}