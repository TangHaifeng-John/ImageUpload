package com.haifeng.example.iamgeupload.common

import android.app.Application

import android.content.Intent
import com.haifeng.example.iamgeupload.vm.UploadService
import com.haifeng.example.iamgeupload.tool.OssTool
import com.haifeng.example.iamgeupload.tool.ToastUtil


class ImageApplication : Application() {



    override fun onCreate() {
        super.onCreate()

        instance = this

        //初始化
        ToastUtil.getInstance().init(this)

        OssTool.getInstance().init(this)

        startService(Intent(this, UploadService::class.java))




    }





    companion object {
        @Volatile
        private var instance: ImageApplication? = null

        fun getInstance() =
            instance
    }


}