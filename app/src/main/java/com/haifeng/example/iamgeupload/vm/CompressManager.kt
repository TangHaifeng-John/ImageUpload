package com.haifeng.example.iamgeupload.vm

import android.content.Context
import android.util.Log
import com.haifeng.example.iamgeupload.common.ImageApplication
import com.haifeng.example.iamgeupload.db.UploadDB
import com.haifeng.example.iamgeupload.db.UploadEntity
import com.haifeng.example.iamgeupload.tool.LogTool
import com.haifeng.example.iamgeupload.tool.RxJavaUtils
import com.wgd.gdcp.gdcplibrary.GDCompressA
import com.wgd.gdcp.gdcplibrary.GDCompressImageListenerA
import com.wgd.gdcp.gdcplibrary.GDConfig
import com.wgd.gdcp.gdcplibrary.GDImageBean
import io.reactivex.Observable

import java.io.File

import java.util.*

/**
 * 严肃哦管理类
 */
class CompressManager private constructor() {


    var compressMap: HashMap<String, GDImageBean> = HashMap()


    var keyMap: HashMap<String, UploadEntity> = HashMap()


    /**
     * 开始压缩
     */
    fun startConpress(context: Context, uploadList: java.util.ArrayList<UploadEntity>) {
        val gdCompressA = GDCompressA(context, object : GDCompressImageListenerA {
            override fun OnSuccess(gdImageBean: GDImageBean?) {

                LogTool.i("压缩图片成功")
                gdImageBean?.let {
                    removeEntity(it)
                    val entity = keyMap[gdImageBean.getmGDConfig().savePath]
                    entity?.path = it.getmGDConfig().savePath
                    entity?.isCompress = true


                    Log.i("haifeng", "压缩图片成功" + entity.toString())
                    UploadDB.getInstance().uploadDao.updateEntity(entity)


                    keyMap.remove(it.getmGDConfig().savePath)
                    checkFinish()

                }


            }

            override fun OnError(gdImageBean: GDImageBean?) {
                LogTool.i("压缩图片失败")

                gdImageBean?.let {
                    removeEntity(it)
                }

                checkFinish()
            }

        })









        for (entity in uploadList) {
            val bean = GDImageBean()
            val config = GDConfig()
            config.setmPath(entity.oldPath)
            config.savePath = context.cacheDir.absolutePath + File.separator + entity.key
            bean.setmGDConfig(config)
            addCompressBean(bean)
            entity?.let {
                keyMap.put(config.savePath, it)
            }
            gdCompressA.start(bean)
        }
    }

    /**
     * 检查是否压缩
     */
    fun checkNoCompressList() {


        Observable.just(true)
            .compose(RxJavaUtils.io())
            .subscribe {
                val list = UploadDB.getInstance().uploadDao.getAllEntityList(false, false)


                val entityList = ArrayList<UploadEntity>()
                entityList.addAll(list)

                if (entityList.size > 0) {
                    LogTool.i("还有${entityList.size}张图片未压缩，现在开始压缩")
                    ImageApplication.getInstance()?.let {
                        startConpress(it.applicationContext, entityList)
                    }
                } else {

                    LogTool.i("未上传的图片，已经全部压缩完成")
                    UploadManager.getInstance().checkNotUploadImageList()
                }
            }


    }

    fun addCompressBean(gdImageBean: GDImageBean) {
        gdImageBean?.let {
            compressMap.put(gdImageBean.getmGDConfig().getmPath(), gdImageBean)

        }


    }


    fun removeEntity(uploadEntity: GDImageBean) {
        uploadEntity?.let {
            removeEntityByKey(uploadEntity.getmGDConfig().getmPath())

        }
    }


    fun removeEntityByKey(key: String) {
        compressMap.remove(key)
        LogTool.i("移除图片:" + key)
    }


    companion object {
        @Volatile
        private var instance: CompressManager? = null

        fun getInstance() =
            instance ?: synchronized(this) {
                instance
                    ?: CompressManager().also { instance = it }
            }
    }


    /**
     * 检查是否完成
     */
    private fun checkFinish() {
        if (isCompressFinish()) {
            LogTool.i("============所有的图片全部压缩完成!============")

            UploadManager.getInstance().checkNotUploadImageList()
        }
    }


    /**
     * 是否压缩完成
     */
    @Synchronized
    fun isCompressFinish(): Boolean {

        return compressMap.isEmpty()

    }


}


