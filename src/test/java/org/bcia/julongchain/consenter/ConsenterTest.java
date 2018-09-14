package org.bcia.julongchain.consenter;

import org.bcia.julongchain.common.exception.ConsenterException;
import org.junit.Test;

/**
 * @author zhangmingyang
 * @Date: 2018/3/1
 * @company Dingxuan
 */
public class ConsenterTest {
    @Test
    public void statrTest() throws ConsenterException {
        String[] args={"start"};
        Consenter.main(args);
    }

}