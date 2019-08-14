package com.haifeng.example.iamgeupload.message


/**
 * 上传信息类
 */
class UploadCallBackMessage {


    var key: String? = ""

    var url: String? = ""
    override fun toString(): String {
        return "UploadCallBackMessage(key=$key, url=$url)"
    }


}