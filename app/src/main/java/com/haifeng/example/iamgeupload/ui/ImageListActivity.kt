package com.haifeng.example.iamgeupload.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import com.haifeng.example.iamgeupload.*
import com.haifeng.example.iamgeupload.db.UploadEntity
import com.haifeng.example.iamgeupload.tool.LogTool
import com.haifeng.example.iamgeupload.vm.ImageViewModel

import kotlinx.android.synthetic.main.activity_image.*
import kotlinx.android.synthetic.main.activity_main.listview


/**
 * 已上传的图片列表
 */
class ImageListActivity : AppCompatActivity() {

    var adapter: PictureListAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image)


        init()

    }


    /**
     * 初始化
     */
    private fun init() {
        finish.setOnClickListener {
            finish()
        }


        val manager = GridLayoutManager(this, 4)



        listview.layoutManager = manager

        adapter = PictureListAdapter(this)
        listview.adapter = adapter


        val imageViewModel = ViewModelProviders.of(this).get(ImageViewModel::class.java)
        imageViewModel.loadUploadList()


        imageViewModel.uploadLiveDatra?.observe(this,
            Observer<List<UploadEntity>> { pictureList -> notifyDataChanged(pictureList) })
    }


    /**
     * 更新数据
     */
    private fun notifyDataChanged(entityList: List<UploadEntity>?) {

        LogTool.i("图片数量:" + entityList?.size)
        adapter?.updateData(entityList)
    }


}
