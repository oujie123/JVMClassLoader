package com.example.jvmclassloader.jvm;

import java.lang.ref.WeakReference;

/**
 * @author Jack_Ou  created on 2021/1/22.
 */
public class WeakReferenceTest {

    public static void main(String[] args) {
        Student jackOu = new Student("JackOu", "man", 18);
        WeakReference<Student> weak = new WeakReference<>(jackOu);
        jackOu = null;
        System.out.println("GC前，我还活着么？ ->" + (weak.get() != null));
        System.gc();
        System.out.println("GC前，我还活着么？ ->" + (weak.get() != null));
    }
}
