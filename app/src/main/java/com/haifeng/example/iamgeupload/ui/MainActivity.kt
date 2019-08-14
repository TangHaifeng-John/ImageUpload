package com.haifeng.example.iamgeupload.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import com.haifeng.example.iamgeupload.*
import com.haifeng.example.iamgeupload.db.UploadEntity
import com.haifeng.example.iamgeupload.message.UploadImageMessage
import com.haifeng.example.iamgeupload.tool.LogTool
import com.haifeng.example.iamgeupload.tool.ToastUtil
import com.haifeng.example.iamgeupload.vm.ImageViewModel

import kotlinx.android.synthetic.main.activity_main.*
import top.limuyang2.photolibrary.activity.LPhotoPickerActivity
import top.limuyang2.photolibrary.engine.LGlideEngine
import top.limuyang2.photolibrary.util.LPPImageType
import com.hjq.permissions.OnPermission
import com.hjq.permissions.XXPermissions
import com.hjq.permissions.Permission

import org.greenrobot.eventbus.EventBus

/**
 * 主界面
 */
class MainActivity : AppCompatActivity() {

    var adapter: PictureListAdapter? = null
    val CHOOSE_PHOTO_REQUEST: Int = 3
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        init()

    }


    /**
     * 初始化
     */
    private fun init() {
        select.setOnClickListener {
            checkPermission()
        }
        jump.setOnClickListener {
            val intent = Intent(this@MainActivity, ImageListActivity::class.java)
            startActivity(intent)
        }

        val manager = GridLayoutManager(this, 4)



        listview.layoutManager = manager

        adapter = PictureListAdapter(this)
        listview.adapter = adapter
        val imageViewModel = ViewModelProviders.of(this).get(ImageViewModel::class.java)
        imageViewModel.loadData()


        imageViewModel.liveData?.observe(this,
            Observer<List<UploadEntity>> { pictureList -> notifyDataChanged(pictureList) })
    }


    /**
     * 数据更新
     */
    private fun notifyDataChanged(entityList: List<UploadEntity>?) {

        LogTool.i("图片数量:" + entityList?.size)
        adapter?.updateData(entityList)
    }

    /**
     * 判断权限
     */
    fun checkPermission() {
        if (XXPermissions.isHasPermission(this, Permission.Group.STORAGE)) {
            openImageSelect()
        } else {

            XXPermissions.with(this)
                // 可设置被拒绝后继续申请，直到用户授权或者永久拒绝
                //.constantRequest()
                // 支持请求6.0悬浮窗权限8.0请求安装权限
                //.permission(Permission.SYSTEM_ALERT_WINDOW, Permission.REQUEST_INSTALL_PACKAGES)
                // 不指定权限则自动获取清单中的危险权限

                .permission(Permission.Group.STORAGE)
                .request(object : OnPermission {

                    override fun hasPermission(granted: List<String>, isAll: Boolean) {
                        if (isAll) {
                            openImageSelect()
                        }
                    }


                    override fun noPermission(denied: List<String>, quick: Boolean) {
                        if (quick) {
                            ToastUtil.showShortToast("被永久拒绝授权，请手动授予权限")
                            //如果是被永久拒绝就跳转到应用权限系统设置页面
                            XXPermissions.gotoPermissionSettings(this@MainActivity);
                        } else {
                            ToastUtil.showShortToast("获取权限失败")
                        }
                    }
                })
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                CHOOSE_PHOTO_REQUEST -> {
                    val selectedPhotos: kotlin.collections.ArrayList<String>? =
                        LPhotoPickerActivity.getSelectedPhotos(data)




                    selectedPhotos?.let {

                        if (selectedPhotos.size > 0) {
                            ToastUtil.showShortToast("图片选择成功 数量${it.size}")
                            val uploadImageMessage = UploadImageMessage()
                            uploadImageMessage.pathList = it
                            EventBus.getDefault().post(uploadImageMessage)


                        } else {
                            ToastUtil.showShortToast("未选择图片")
                        }

                    }


                }


            }
        }

    }

    /**
     * 打开图片选择界面
     */
    fun openImageSelect() {
        val intent = LPhotoPickerActivity.IntentBuilder(this)
            .maxChooseCount(20) //最大多选数目
            .columnsNumber(4) //以几列显示图片
            .imageType(LPPImageType.ofAll()) //需要显示的图片类型(webp/PNG/GIF/JPG)
            .pauseOnScroll(false) //滑动时，是否需要暂停图片加载
            .isSingleChoose(false) //单选模式
            .imageEngine(LGlideEngine()) //添加自定义的图片加载引擎(库中已经自带Glide加载引擎，如果你不需要自定义，可不添加此句)
            .selectedPhotos(ArrayList<String>()) //已选择的图片数组
            .build()

        startActivityForResult(intent, CHOOSE_PHOTO_REQUEST)

    }

}
