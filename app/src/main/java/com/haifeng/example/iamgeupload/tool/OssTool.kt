package com.haifeng.example.iamgeupload.tool

import android.content.Context
import android.os.Environment
import android.util.Log
import com.alibaba.sdk.android.oss.ClientConfiguration
import com.alibaba.sdk.android.oss.ClientException
import com.alibaba.sdk.android.oss.OSSClient
import com.alibaba.sdk.android.oss.ServiceException
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback
import com.alibaba.sdk.android.oss.common.OSSLog
import com.alibaba.sdk.android.oss.common.auth.OSSCustomSignerCredentialProvider
import com.alibaba.sdk.android.oss.common.utils.OSSUtils
import com.alibaba.sdk.android.oss.model.ResumableUploadRequest
import com.alibaba.sdk.android.oss.model.ResumableUploadResult
import com.haifeng.example.iamgeupload.common.Config
import com.haifeng.example.iamgeupload.message.UploadCallBackMessage
import com.haifeng.example.iamgeupload.vm.UploadManager
import org.greenrobot.eventbus.EventBus
import java.io.File


/**
 * 单例模式，上传工具类
 */
class OssTool private constructor() {


    var oss: OSSClient? = null

    /**
     * 初始化上传工具
     */
    fun init(applicationContext: Context) {


        val provider = object : OSSCustomSignerCredentialProvider() {
            override fun signContent(content: String): String {


                return OSSUtils.sign(
                    Config.OSS_ACCESS_KEY_ID,
                    Config.OSS_ACCESS_KEY_SECRET, content)
            }
        }

        val conf = ClientConfiguration()
        conf.connectionTimeout = 15 * 1000// 连接超时，默认15秒
        conf.socketTimeout = 15 * 1000// socket超时，默认15秒
        conf.maxConcurrentRequest = 5 // 最大并发请求书，默认5个
        conf.maxErrorRetry = 2 // 失败后最大重试次数，默认2次
        oss = OSSClient(applicationContext, Config.OSS_ENDPOINT, provider)
        OSSLog.enableLog()


    }

    /**
     * 开始上传
     */
    fun uploadImageList(path: String?, key: String?) {


        val recordDirectory = Environment.getExternalStorageDirectory().getAbsolutePath() + "/oss_record/"
        val recordDir = File(recordDirectory)
// 要保证目录存在，如果不存在则主动创建
        if (!recordDir.exists()) {
            recordDir.mkdirs()
        }

        LogTool.i("需要上传图片$path")
        val put = ResumableUploadRequest(Config.BUCKET_NAME, key, path)


        val resumableTask = oss?.asyncResumableUpload(put
            , object : OSSCompletedCallback<ResumableUploadRequest, ResumableUploadResult> {
                override fun onSuccess(request: ResumableUploadRequest?, result: ResumableUploadResult?) {
                    val url = oss?.presignPublicObjectURL(Config.BUCKET_NAME, key)
                    url?.let {
                        Log.d("oss", it)

                    }

                    val uploadCallBackMessage = UploadCallBackMessage()

                    uploadCallBackMessage.key = key
                    uploadCallBackMessage.url = url

                    EventBus.getDefault().post(uploadCallBackMessage)
                    LogTool.i("上传图片成功$url")


                    UploadManager.getInstance().uploadSuccess(uploadCallBackMessage)
                }

                override fun onFailure(
                    request: ResumableUploadRequest?,
                    clientException: ClientException?,
                    serviceException: ServiceException?
                ) {
                    // 请求异常。
                    clientException?.printStackTrace()
                    if (serviceException != null) {
                        // 服务异常。
                        Log.e("oss", serviceException.errorCode)
                        Log.e("oss", serviceException.requestId)
                        Log.e("oss", serviceException.hostId)
                        Log.e("oss", serviceException.rawMessage)
                    }


                    key?.let {
                        UploadManager.getInstance().uploadFail(it)

                    }
                }

            })

//        resumableTask.cancel()


    }


    companion object {
        @Volatile
        private var instance: OssTool? = null

        fun getInstance() =
            instance ?: synchronized(this) {
                instance
                    ?: OssTool().also { instance = it }
            }
    }
}