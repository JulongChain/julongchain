package org.bcia.julongchain.core.commiter;

import org.bcia.julongchain.common.exception.ValidateException;
import org.bcia.julongchain.node.entity.Group;
import org.bcia.julongchain.protos.common.Common;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * 提交者验证器测试
 *
 * @author zhouhui
 * @date 2018/10/21
 * @company Dingxuan
 */
public class CommitterValidatorTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void validate() throws ValidateException {
        Group group = new Group();
        group.setGroupId("myGroup");

        ICommitterValidator committerValidator = new CommitterValidator(new CommitterSupport(group));

        Common.Block block = Common.Block.getDefaultInstance();

        thrown.expect(ValidateException.class);
        committerValidator.validate(block);
    }
}