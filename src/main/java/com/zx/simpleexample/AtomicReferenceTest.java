package com.zx.simpleexample;

import java.util.concurrent.atomic.AtomicReference;

/**
 * AtomicReference类
 */
public class AtomicReferenceTest implements Runnable{

    private AtomicReference<Integer> atomicReference;


    public AtomicReferenceTest(AtomicReference<Integer> atomicReference){
        this.atomicReference = atomicReference;
    }


    @Override
    public void run() {
        for (int i = 0; i < 100; i++) {
            atomicReference.set(100 - i);
            System.out.println(Thread.currentThread().getName() + ":      " + atomicReference.get());
        }
    }

    public static void main(String [] args){
        AtomicReference<Integer> atomicReference = new AtomicReference<>();
        new Thread(new AtomicReferenceTest(atomicReference)).start();
        new Thread(new AtomicReferenceTest(atomicReference)).start();
        new Thread(new AtomicReferenceTest(atomicReference)).start();
        new Thread(new AtomicReferenceTest(atomicReference)).start();
        new Thread(new AtomicReferenceTest(atomicReference)).start();
        new Thread(new AtomicReferenceTest(atomicReference)).start();
        new Thread(new AtomicReferenceTest(atomicReference)).start();
        new Thread(new AtomicReferenceTest(atomicReference)).start();
        new Thread(new AtomicReferenceTest(atomicReference)).start();
        new Thread(new AtomicReferenceTest(atomicReference)).start();
    }
}
