package com.example.jvmclassloader;

import android.app.Application;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;

/**
 * 原理：
 * 1.先从app类加载器(pathClassLoader)中获取pathList成员变量
 * 2.从pathList对象中获取elements[]
 * 3.调用makePathElements创建element
 * 4.然后重新组装elements，将补丁包element放在第一个
 * 5.然后将elements重新赋值到pathList中去
 *
 * @Author: Jack Ou
 * @CreateDate: 2020/8/2 11:11
 * @UpdateUser: 更新者
 * @UpdateDate: 2020/8/2 11:11
 * @UpdateRemark: 此只用于热修复原理测试，目前我的手机是10.0系统，只适配Android N以上的场景
 */
public class HotFix {

    public static final String TAG = HotFix.class.getSimpleName();
    public static ArrayList<File> dexFiles = new ArrayList<>();

    public static void installPatchDex(Application application, File patchDexFile){
        if (patchDexFile.exists()){
            dexFiles.add(patchDexFile);
        } else {
            Log.e(TAG,"patch dex file doesn't exist.");
            return;
        }
        //1.获得应用的类加载器，即pathClassLoader
        ClassLoader pathClassLoader = application.getClassLoader();

        //在CustomeClassLoader中实现2 3 4 5步骤的操作
        try {
            //CustomeClassLoader.inject(application,pathClassLoader,dexFiles);
            CustomeClassLoader.inject(application,pathClassLoader,dexFiles);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }
}
