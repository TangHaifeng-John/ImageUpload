package com.haifeng.example.iamgeupload.vm

import androidx.annotation.WorkerThread
import com.haifeng.example.iamgeupload.message.UploadCallBackMessage
import com.haifeng.example.iamgeupload.db.UploadDB
import com.haifeng.example.iamgeupload.db.UploadEntity
import com.haifeng.example.iamgeupload.tool.LogTool
import com.haifeng.example.iamgeupload.tool.OssTool
import com.haifeng.example.iamgeupload.tool.RxJavaUtils
import com.haifeng.example.iamgeupload.tool.ToastUtil
import io.reactivex.Observable

import org.greenrobot.eventbus.EventBus

import java.util.*
import kotlin.collections.ArrayList


class UploadManager private constructor() {


    var uploadMap: HashMap<String, UploadEntity> = HashMap()

    init {
        EventBus.getDefault().post(this)

    }

    /**
     * 添加上传信息到缓存中
     */
    fun addUploadEntity(uploadEntity: UploadEntity) {
        uploadEntity?.let {
            uploadMap.put(it.key!!, uploadEntity)

        }


    }


    /**
     * 移除上传信息缓存
     */
    fun removeEntity(uploadEntity: UploadEntity) {
        uploadEntity?.let {
            removeEntityByKey(it.key!!)

        }
    }


    /**
     * 移除上传信息缓存
     */
    fun removeEntityByKey(key: String) {
        uploadMap.remove(key)
        LogTool.i("移除图片:" + key)
    }


    /**
     * 检查未上传的图片
     */

    fun checkNotUploadImageList() {


        Observable.just(true)
            .compose(RxJavaUtils.io())
            .subscribe {
                val list = UploadDB.getInstance().uploadDao.getAllEntityList(false, true)


                for (entity in list) {


                    addUploadEntity(entity)

                }

                uploadMap?.let {
                    if (it.size!! > 0) {


                        LogTool.i("需要上传的图片数量:" + uploadMap.size)

                        val list = ArrayList<UploadEntity>()

                        for (entry in it.values) {
                            list.add(entry)
                        }
                        startUploadToServer(list)

                    } else {
                        LogTool.i("数据库中没有需要上传的图片!")
                    }
                }
            }


    }


    companion object {
        @Volatile
        private var instance: UploadManager? = null

        fun getInstance() =
            instance ?: synchronized(this) {
                instance
                    ?: UploadManager().also { instance = it }
            }
    }


    /**
     * 上传图片
     */
    @WorkerThread
    fun uploadSuccess(uploadCallBackMessage: UploadCallBackMessage) {


        ToastUtil.showShortToast("上传成功${uploadCallBackMessage.toString()}")
        val uploadEntity = UploadEntity()

        uploadEntity.key = uploadCallBackMessage.key
        uploadEntity.isUpload = true
        uploadEntity.url = uploadCallBackMessage.url

        LogTool.i("图片上传完成!")

        update(uploadEntity)

        removeEntity(uploadEntity)

        LogTool.i("更新数据库完成!")

        checkFinish()


    }

    /**
     * 检查是否完成
     */
    private fun checkFinish() {
        if (isUploadFinish()) {
            LogTool.i("============上传任务完成!============")

            ToastUtil.showShortToast("上传任务完成")
        }
    }


    @Synchronized
    fun isUploadFinish(): Boolean {

        return uploadMap.isEmpty()

    }


    /**
     * 上传失败回调
     */

    fun uploadFail(key: String) {
        LogTool.i("============图片上传失败!============")
        removeEntityByKey(key)

        checkFinish()

    }

    /**
     * 更新上传信息
     */
    fun update(uploadEntity: UploadEntity) {
        uploadEntity?.let {
            UploadDB.getInstance().uploadDao.updateUploadStatus(it.isUpload, it.key)
            UploadDB.getInstance().uploadDao.updateUrl(uploadEntity.url, it.key)
        }

    }

    /**
     * 开始上传图片到服务器
     */
    fun startUploadToServer(uploadEntityList: ArrayList<UploadEntity>) {

        uploadEntityList?.let {


            for (uploadEntity in it) {
                uploadEntity.run {
                    OssTool.getInstance().uploadImageList(this.path, uploadEntity.key)

                }

            }
        }


    }


}


