/**
 * Copyright Dingxuan. All Rights Reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.bcia.julongchain.common.util;

/**
 * 类描述
 *
 * @author zhouhui
 * @date 2018/07/13
 * @company Dingxuan
 */
public class CommLock {
    public interface TimeoutCallback {
        void onTimeout();
    }

    private int timeout;

    private volatile boolean locking;

    private byte[] lock = new byte[0];

    public CommLock(int timeout) {
        this.timeout = timeout;
    }

    public void tryLock(TimeoutCallback callback) {
        synchronized (lock) {
            locking = true;

            if (locking) {
                try {
                    lock.wait(timeout);
                } catch (InterruptedException e) {
                }
            }

            if (locking && callback != null) {
                locking = false;
                callback.onTimeout();
            }
        }
    }

    public void unLock() {
        if (locking) {
            synchronized (lock) {
                locking = false;
                lock.notifyAll();
            }
        }
    }
}