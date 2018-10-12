package org.bcia.julongchain.consenter;

import org.bcia.julongchain.common.exception.ConsenterException;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

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
    @Test
    public void versionTest() throws ConsenterException {
        String[] args={"version"};
        Consenter.main(args);
    }
    @Test
    public void errorArgsTest() {
        String[] args={"test"};
        try {
            Consenter.main(args);
        } catch (ConsenterException e) {
            assertEquals("[Consenter]args is error!", e.getMessage());
        }
    }
    @Test
    public void nullArgsTest() {
        String[] args={};
        try {
            Consenter.main(args);
        } catch (ConsenterException e) {
            assertEquals("[Consenter]Need more args!", e.getMessage());
        }
    }
}