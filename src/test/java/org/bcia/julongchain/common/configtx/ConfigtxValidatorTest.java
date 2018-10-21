package org.bcia.julongchain.common.configtx;

import com.google.protobuf.InvalidProtocolBufferException;
import org.bcia.julongchain.common.exception.ValidateException;
import org.bcia.julongchain.common.groupconfig.GroupConfigConstant;
import org.bcia.julongchain.common.policies.IPolicyManager;
import org.bcia.julongchain.protos.common.Common;
import org.bcia.julongchain.protos.common.Configtx;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.*;

/**
 * 配置交易验证器
 *
 * @author zhouhui
 * @date 2018/10/21
 * @company Dingxuan
 */
public class ConfigtxValidatorTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void validate() throws InvalidProtocolBufferException, ValidateException {
        thrown.expect(ValidateException.class);

        ConfigtxValidator configtxValidator = new ConfigtxValidator("myGroup", null,
                GroupConfigConstant.GROUP, null);
        configtxValidator.validate(Configtx.ConfigEnvelope.getDefaultInstance());
    }

    @Test
    public void proposeConfigUpdate() throws InvalidProtocolBufferException, ValidateException {
        thrown.expect(ValidateException.class);

        ConfigtxValidator configtxValidator = new ConfigtxValidator("myGroup", null,
                GroupConfigConstant.GROUP, null);
        configtxValidator.proposeConfigUpdate(Common.Envelope.newBuilder().build());
    }
}