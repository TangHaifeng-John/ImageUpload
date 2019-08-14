package com.haifeng.example.iamgeupload.vm

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.IBinder
import com.haifeng.example.iamgeupload.message.UploadImageMessage
import com.haifeng.example.iamgeupload.db.UploadDB
import com.haifeng.example.iamgeupload.db.UploadEntity
import com.haifeng.example.iamgeupload.tool.LogTool
import com.haifeng.example.iamgeupload.tool.RxJavaUtils

import io.reactivex.Observable
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*
import java.util.concurrent.TimeUnit


/**
 * 上传服务器类
 */
class UploadService : Service() {


    private var myReceiver: MyReceiver? = null


    private inner class MyReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            executeTask()
            LogTool.i("网络状态发生变化！")

        }

    }

    override fun onCreate() {
        super.onCreate()

        EventBus.getDefault().register(this)

        startCheckNotUploadTask()

        myReceiver = MyReceiver()
        val intentFilter = IntentFilter()
        intentFilter!!.addAction(ConnectivityManager.CONNECTIVITY_ACTION)
        registerReceiver(
            myReceiver, intentFilter
        )
    }


    /**
     * 上传图片
     */
    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    fun uploadImageList(uploadImageMessage: UploadImageMessage) {


        LogTool.i("开始上传图片!")


        uploadImageMessage.pathList?.let {


            val uploadList = ArrayList<UploadEntity>()

            for (path in it) {
                val uploadEntity = UploadEntity()
                uploadEntity.oldPath = path
                uploadEntity.key = UUID.randomUUID().toString()
                uploadList.add(uploadEntity)
            }



            UploadDB.getInstance().uploadDao.insertUploadEntityList(uploadList)



            executeTask()


        }


    }

    /**
     * 开启上传定时任务,60秒执行一次
     */
    fun startCheckNotUploadTask() {

        Observable.interval(10, 60, TimeUnit.SECONDS)
            .compose(RxJavaUtils.io())

            .subscribe {
                LogTool.i("开始检查数据库没有上传完成的图片!")
                executeTask()

            }


    }

    /**
     * 执行任务
     */
    private fun executeTask() {
        if (CompressManager.getInstance().isCompressFinish() && UploadManager.getInstance().isUploadFinish()) {
            CompressManager.getInstance().checkNoCompressList()
        } else {

            LogTool.i("还有任务未完成!")
        }
    }


    override fun onBind(intent: Intent?): IBinder? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
        myReceiver?.let {
            unregisterReceiver(it)
        }
    }

}