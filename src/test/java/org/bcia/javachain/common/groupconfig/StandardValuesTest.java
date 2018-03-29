package org.bcia.javachain.common.groupconfig;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * 对象
 *
 * @author zhouhui
 * @date 2018/3/28
 * @company Dingxuan
 */
public class StandardValuesTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void valuesFromChild() {
        StandardValues.valuesFromChild(null);
    }
}