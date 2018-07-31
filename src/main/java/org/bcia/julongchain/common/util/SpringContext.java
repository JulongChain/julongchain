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

import org.springframework.beans.BeansException;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Spring支持
 *
 * @author zhouhui
 * @date 2018/4/28
 * @company Dingxuan
 */
public class SpringContext {
    private static final String SPRING_CONFIG_PATH = "applicationContext.xml";

    /**
     * 单例模式，供全局使用Spring
     */
    private static SpringContext instance;

    private ClassPathXmlApplicationContext applicationContext;

    /**
     * 私有化构造函数
     */
    private SpringContext() {
        applicationContext = new ClassPathXmlApplicationContext(SPRING_CONFIG_PATH);
    }

    public static SpringContext getInstance() {
        if (instance == null) {
            synchronized (SpringContext.class) {
                if (instance == null) {
                    instance = new SpringContext();
                }
            }
        }

        return instance;
    }

    /**
     * 调用方式：如SpringContext.getInstance().getBean(PolicyNode.class);
     *
     * @param requiredType
     * @param <T>
     * @return
     * @throws BeansException
     */
    public <T> T getBean(Class<T> requiredType) throws BeansException {
        return applicationContext.getBean(requiredType);
    }

}
