package com.example.jvmclassloader.jvm;

import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;

/**
 * -XX:+PrintGC
 *
 * @author Jack_Ou  created on 2021/1/22.
 */
public class GcTest {

    public Object instance =null;
    //占据内存，便于判断分析GC
    private byte[] bigSize = new byte[50*1024*1024];

    public static void main(String[] args) {
        GcTest objectA = new GcTest();//objectA 会放入局部变量表作为GCRoots
        GcTest objectB = new GcTest();//objectB 会放入局部变量表作为GCRoots
        //相互引用
        objectA.instance = objectB;
        objectB.instance = objectA;
        //切断可达
        objectA =null;
        objectB =null;
        //强制垃圾回收
        System.gc();
        WeakReference<GcTest> test = new WeakReference<>(objectA);
    }
}
