/*
 * Peterson.java
 *
 * Created on January 20, 2006, 10:36 PM
 *
 * From "Multiprocessor Synchronization and Concurrent Data Structures",
 * by Maurice Herlihy and Nir Shavit.
 * Copyright 2006 Elsevier Inc. All rights reserved.
 */

/**
 * Peterson lock
 * @author Maurice Herlihy
 */
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.TimeUnit;
import java.util.*;
import java.lang.*;
import java.lang.ThreadLocal ;
import java.lang.Object;
class Peterson implements Lock {
  private volatile boolean[] flag = new boolean[2];
  private volatile int victim;
  private int r = 5;
  private volatile int[] k = {0, 0};
  public void lock() {
    int i = ((int) Thread.currentThread().getId())%2;
    int j = 1-i;
    flag[i] = true;
    victim  = i;
    while (flag[j]&&victim==i) {} // spin
    while(k[j]>=r){victim = i;}//spin
    System.out.println(i+" "+k[1-i]);
    k[1-i]++;
  }
  public void unlock() {
    int i = ((int) Thread.currentThread().getId())%2;
    flag[i] = false;
    k[i] = 0;
  }
  // Any class implementing Lock must provide these methods
  public Condition newCondition() {
    throw new java.lang.UnsupportedOperationException();
  }
  public boolean tryLock(long time,
      TimeUnit unit)
      throws InterruptedException {
    throw new java.lang.UnsupportedOperationException();
  }
  public boolean tryLock() {
    throw new java.lang.UnsupportedOperationException();
  }
  public void lockInterruptibly() throws InterruptedException {
    throw new java.lang.UnsupportedOperationException();
  }
}