package com.haifeng.example.iamgeupload.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.haifeng.example.iamgeupload.db.UploadDB
import com.haifeng.example.iamgeupload.db.UploadEntity

class ImageViewModel : ViewModel() {
    var liveData: LiveData<List<UploadEntity>>? = null

    var uploadLiveDatra: LiveData<List<UploadEntity>>? = null
    /**
     * 加载数据库所有的上传图片
     */
    fun loadData() {
        liveData = UploadDB.getInstance().uploadDao.allUploadList
    }

    /**
     * 加载已经上传的图片
     */
    fun loadUploadList() {
        uploadLiveDatra = UploadDB.getInstance().uploadDao.getAllUploadList(true)
    }
}