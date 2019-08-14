package com.haifeng.example.iamgeupload.db

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
class UploadEntity {

    @PrimaryKey(autoGenerate = true)
    var id: Int = 0

    var path: String? = null
    var oldPath: String? = null

    var isUpload: Boolean? = false

    /**
     * 是否压缩
     */
    var isCompress: Boolean? = false


    var key: String? = ""

    var url: String? = ""
    override fun toString(): String {
        return "UploadEntity(id=$id, path=$path, oldPath=$oldPath, isUpload=$isUpload, isCompress=$isCompress, key=$key, url=$url)"
    }


}