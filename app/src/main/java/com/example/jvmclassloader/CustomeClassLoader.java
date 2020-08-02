package com.example.jvmclassloader;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import java.io.File;
import java.lang.reflect.Field;
import java.util.List;

import dalvik.system.DexFile;
import dalvik.system.PathClassLoader;

/**
 * @Author: Jack Ou
 * @CreateDate: 2020/8/2 11:27
 * @UpdateUser: 更新者
 * @UpdateDate: 2020/8/2 11:27
 * @UpdateRemark: 此类是自定义了一个classloader，实现Android N以上的类加载
 */
public class CustomeClassLoader {

    public static final String TAG = CustomeClassLoader.class.getSimpleName();

    public static ClassLoader inject(Application app, ClassLoader oldClassLoader, List<File> patchs) throws Throwable {
        //新建一个任务分发类加载器，其中这个类加载器中的新类加载器主要加载我们自己的代码
        //application类信息还是由老的类加载器完成
        DispatchClassLoader dispatchClassLoader = new DispatchClassLoader(app.getClass().getName(), oldClassLoader);

        //制造一个定制化后的类加载器
        ClassLoader newClassLoader = createNewClassLoader(app, oldClassLoader, dispatchClassLoader, patchs);

        //把自定义的类加载器放入分发类加载器中
        dispatchClassLoader.setNewClassLoader(newClassLoader);
        doInject(app,newClassLoader);
        return newClassLoader;
    }

    private static ClassLoader createNewClassLoader(Context context, ClassLoader oldClassLoader, DispatchClassLoader dispatchClassLoader, List<File> patchs) throws Exception {
        //通过反射从老的类加载器中获得pathList对象
        Field pathList = ShareReflectUtil.findField(oldClassLoader, "pathList");
        Object pathListObject = pathList.get(oldClassLoader);

        //从pathList对象中拿到dexElements
        Field dexElements = ShareReflectUtil.findField(pathListObject,"dexElements");
        Object[] elements = (Object[]) dexElements.get(pathListObject);

        //只需要拿到这个变量，后面遍历每一个element的时候用到
        Field dexFileField = ShareReflectUtil.findField(elements[0],"dexFile");

        //遍历传进来的所有dex Files
        StringBuilder dexPathBuilder = new StringBuilder();
        boolean isFirstItem = true;
        for (File patch : patchs) {
            if (isFirstItem){
                isFirstItem = false;
            } else {
                dexPathBuilder.append(File.pathSeparator);
            }
            dexPathBuilder.append(patch.getAbsolutePath());
        }

        //遍历原始的dex 文件
        String packageName = context.getPackageName();
        for (Object element : elements) {
            String dexPath = null;
            DexFile dexFile = (DexFile) dexFileField.get(element);
            if (dexFile != null) {
                dexPath = dexFile.getName();
            }
            if (dexPath == null || dexPath.isEmpty()) {
                continue;
            }
            if (!dexPath.contains("/" + packageName)) {
                continue;
            }
            //插入：文件分隔符
            if (isFirstItem) {
                isFirstItem = false;
            } else {
                dexPathBuilder.append(File.pathSeparator);
            }
            dexPathBuilder.append(dexPath);
        }
        final String allDexPath = dexPathBuilder.toString();

        //  app的native库（so） 文件目录 用于构造classloader
        Field nativeLibraryDirectoriesField = ShareReflectUtil.findField(pathListObject, "nativeLibraryDirectories");
        List<File> oldNativeLibraryDirectories = (List<File>) nativeLibraryDirectoriesField.get(pathListObject);

        //native库的目录，用于在new pathClassLoader的时候传入native库的目录
        StringBuilder libraryPathBuilder = new StringBuilder();
        isFirstItem = true;
        for (File libDir : oldNativeLibraryDirectories) {
            if (libDir == null) {
                continue;
            }
            if (isFirstItem) {
                isFirstItem = false;
            } else {
                libraryPathBuilder.append(File.pathSeparator);
            }
            libraryPathBuilder.append(libDir.getAbsolutePath());
        }

        String combinedLibraryPath = libraryPathBuilder.toString();

        //新建自定义的PathClassLoader，并返回
        ClassLoader result = new PathClassLoader(allDexPath, combinedLibraryPath, dispatchClassLoader);
        ShareReflectUtil.findField(pathListObject, "definingContext").set(pathListObject, result);
        ShareReflectUtil.findField(result, "parent").set(result, dispatchClassLoader);
        return result;
    }

    private static final class DispatchClassLoader extends ClassLoader {
        private final String mApplicationClassName;
        private final ClassLoader mOldClassLoader;

        private ClassLoader mNewClassLoader;

        private final ThreadLocal<Boolean> mCallFindClassOfLeafDirectly = new ThreadLocal<Boolean>() {
            @Override
            protected Boolean initialValue() {
                return false;
            }
        };

        DispatchClassLoader(String applicationClassName, ClassLoader oldClassLoader) {
            super(ClassLoader.getSystemClassLoader());
            mApplicationClassName = applicationClassName;
            mOldClassLoader = oldClassLoader;
        }

        void setNewClassLoader(ClassLoader classLoader) {
            mNewClassLoader = classLoader;
        }

        @Override
        protected Class<?> findClass(String name) throws ClassNotFoundException {

            if (mCallFindClassOfLeafDirectly.get()) {
                return null;
            }
            // 1、Application类不需要修复，使用原本的类加载器获得
            if (name.equals(mApplicationClassName)) {
                return findClass(mOldClassLoader, name);
            }
            // 2、加载热修复框架的类 因为不需要修复，就用原本的类加载器获得
            // 使用热修复框架之后，将框架代码放着这里
            if (name.startsWith("com.example.jvmclassloader.patch.")) {
                return findClass(mOldClassLoader, name);
            }
            try {
                return findClass(mNewClassLoader, name);
            } catch (ClassNotFoundException ignored) {
                return findClass(mOldClassLoader, name);
            }
        }

        private Class<?> findClass(ClassLoader classLoader, String name) throws ClassNotFoundException {
            try {
                //双亲委托，所以可能会stackoverflow死循环，防止这个情况
                mCallFindClassOfLeafDirectly.set(true);
                return classLoader.loadClass(name);
            } finally {
                mCallFindClassOfLeafDirectly.set(false);
            }
        }
    }

    private static void doInject(Application app, ClassLoader classLoader) throws Throwable {
        Thread.currentThread().setContextClassLoader(classLoader);

        Context baseContext = (Context) ShareReflectUtil.findField(app, "mBase").get(app);
        Object basePackageInfo = ShareReflectUtil.findField(baseContext, "mPackageInfo").get(baseContext);
        ShareReflectUtil.findField(basePackageInfo, "mClassLoader").set(basePackageInfo, classLoader);
    }
}
