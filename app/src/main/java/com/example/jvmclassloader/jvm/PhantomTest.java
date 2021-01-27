package com.example.jvmclassloader.jvm;

import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;

/**
 * @author Jack_Ou  created on 2021/1/22.
 */
public class PhantomTest {

    public static void main(String[] args) {
        ReferenceQueue<String> queue = new ReferenceQueue<String>();
        PhantomReference<String> pr = new PhantomReference<String>(new String("Hello World!"), queue);
        System.out.println(pr.get());
    }
}
