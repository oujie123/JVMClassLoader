package com.example.jvmclassloader;

import android.app.Application;
import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;

/**
 * @Author: Jack Ou
 * @CreateDate: 2020/8/2 11:09
 * @UpdateUser: 更新者
 * @UpdateDate: 2020/8/2 11:09
 * @UpdateRemark: 更新说明
 */
public class MyApplication extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        String patchPath = getExternalCacheDir() +File.separator + "a.dex";
        //将需要补丁dex加载到dexPathList的elements[]中
        HotFix.installPatchDex(this,new File(patchPath));
    }
}
