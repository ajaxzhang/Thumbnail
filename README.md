# 利用aliyun sdk专业版实现生成视频缩略图

## 1. 导入aliyun sdk
### 1. 工程中点File->New-> Import moudle   选择下载下来的AliyunSDK-RCE <br>
### 2. 在build.gradle中添加 <br>
```
dependencies {
    implementation project(':AliyunSdk-RCE')
}
```
### 3. 将下载下来的sdk中的Libs文件夹 copy到工程src/main/jniLibs/下，在build.gradle中添加<br>
```
    sourceSets.main {
        jni.srcDirs = []
        jniLibs.srcDir "src/main/jniLibs"
    }
```
### 4. 在app的Application类中导入sdk的核心so库：<br>
```
public class MainApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        System.loadLibrary("live-openh264");
        System.loadLibrary("QuCore-ThirdParty");
        System.loadLibrary("QuCore");
    }
}
```

## 2. 实现生成视频缩略图
### 1. 调用系统的Intent.ACTION_PICK选择视频<br>
### 2. 获取选择视频的MediaInfo duration，通过android.media.MediaMetadataRetriever系统类获取<br>
### 3. 实例化aliyun提供的api  AliyunIThumbnailFetcher， 设置params 生成的缩略图的大小，之后调用requestThumbnailImage，传入要获取某一duration时间点，通过回调方法onThumbnailReady获取com.aliyun.common.media.ShareableBitmap<br>
### 4. shareableBitmap.getData()可获取缩略图bitmap，最后显示到ImageView中
