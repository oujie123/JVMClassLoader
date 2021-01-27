package com.example.jvmclassloader.jvm;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Jack_Ou  created on 2021/1/22.
 */
public class SoftReferenceTest {

    public static void main(String[] args) {
        Student jackOu = new Student("JackOu", "man", 18);
        SoftReference<Student> soft = new SoftReference<>(jackOu); // 创建软引用对象
        jackOu = null;//切掉强引用
        System.out.println("Gc前，我还在?  ->" + (soft.get() != null));
        System.gc();
        System.out.println("Gc后，我还在?  ->" + (soft.get() != null));

        List<byte[]> list = new ArrayList<>(); // 制造oom
        try {
            for (int i = 0; i < 10; i++) {
                System.out.println("我还活着么？ ->" + (soft.get() != null));
                list.add(new byte[2 * 1024 * 1024]);
                Thread.sleep(10);
            }
        } catch (Throwable t) {
            System.out.println("OOM了，我还活着么？-> " + (soft.get() != null));
        }
    }
}
