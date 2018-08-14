package org.bcia.julongchain.common.util;

import org.junit.Test;

/**
 * 类描述
 *
 * @author zhouhui
 * @date 2018/07/15
 * @company Dingxuan
 */
public class CommLockTest {

    private CommLock lock;

    @Test
    public void tryLock() {
        long beginTime = System.currentTimeMillis();

        new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                lock.unLock();
                System.out.println("unLock: " + (System.currentTimeMillis() - beginTime));
            }
        }.start();

        lock = new CommLock(13000);
        lock.tryLock(new CommLock.TimeoutCallback() {
            @Override
            public void onTimeout() {
                System.out.println("onTimeout: " + (System.currentTimeMillis() - beginTime));
            }
        });

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void unLock() {
    }
}