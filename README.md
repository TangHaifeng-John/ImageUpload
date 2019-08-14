### 仿微信朋友圈的图片上传，图片上传缓存，断网后重新连接网络或者APP重启，自动上传未上传的图片


Config的阿里云配置需要自己添加
```
public class Config {



    public static final String OSS_ENDPOINT = "***********";


    public static final String BUCKET_NAME = "***********";
    public static final String OSS_ACCESS_KEY_ID = "***********";
    public static final String OSS_ACCESS_KEY_SECRET = "***********";


}

```

里面涉及到的技术点

 - Kotlin的使用
 - 阿里云OSS文件上传
 - LiveData和ViewModel

