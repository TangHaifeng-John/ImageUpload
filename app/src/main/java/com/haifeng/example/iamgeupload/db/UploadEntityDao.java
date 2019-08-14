package com.haifeng.example.iamgeupload.db;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;


import java.util.List;


/**
 * 上传DAO
 */
@Dao
public interface UploadEntityDao {


    /**
     * 获取所有的上传对象
     *
     * @return
     */
    @Query("SELECT * FROM uploadentity where isUpload=:isUpload and isCompress=:isCompress")
    List<UploadEntity> getAllEntityList(Boolean isUpload, Boolean isCompress);


    /**
     * 插入上传数据列表
     *
     * @param list
     */
    @Insert()
    List<Long> insertUploadEntityList(List<UploadEntity> list);


    /**
     * 更新上传状态
     *
     * @param value
     */

    @Query("update uploadentity set isUpload=:value where `key`=:key")
    void updateUploadStatus(Boolean value, String key);


    @Query("update uploadentity set url=:value where `key`=:key")
    void updateUrl(String value, String key);

    /**
     * 获取所有已经上传的图片
     */
    @Query("SELECT * FROM UploadEntity where isUpload=:isUpload")
    LiveData<List<UploadEntity>> getAllUploadList(Boolean isUpload);


    /**
     * 获取所有已经上传的图片
     */
    @Query("SELECT * FROM UploadEntity")
    LiveData<List<UploadEntity>> getAllUploadList();


    @Update()
    void updateEntity(UploadEntity uploadEntity);

    /**
     * 删除所有数据
     */
    @Query("DELETE FROM uploadentity")
    void deleteAll();

}
