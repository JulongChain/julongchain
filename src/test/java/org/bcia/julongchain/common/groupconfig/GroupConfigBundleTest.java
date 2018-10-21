package org.bcia.julongchain.common.groupconfig;

import com.google.protobuf.InvalidProtocolBufferException;
import org.bcia.julongchain.common.exception.PolicyException;
import org.bcia.julongchain.common.exception.ValidateException;
import org.bcia.julongchain.msp.config.ConfigFactory;
import org.bcia.julongchain.protos.common.Configtx;
import org.bcia.julongchain.tools.configtxgen.entity.GenesisConfigFactory;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.*;

/**
 * 类描述
 *
 * @author zhouhui
 * @date 2018/10/21
 * @company Dingxuan
 */
public class GroupConfigBundleTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void validateNew() throws ValidateException, PolicyException, InvalidProtocolBufferException {
        thrown.expect(ValidateException.class);

        GroupConfigBundle configBundle = new GroupConfigBundle("myGroup", null);
        GroupConfigBundle configBundle2 = new GroupConfigBundle("myGroup", null);
        configBundle.validateNew(configBundle2);

    }
}