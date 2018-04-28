package org.bcia.javachain.common.util.proto;

import com.google.protobuf.InvalidProtocolBufferException;
import org.bcia.javachain.common.exception.JavaChainException;
import org.bcia.javachain.common.exception.ValidateException;
import org.bcia.javachain.common.genesis.GenesisBlockFactory;
import org.bcia.javachain.node.cmd.group.GroupJoinCmd;
import org.bcia.javachain.protos.common.Common;
import org.bcia.javachain.protos.common.Configtx;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.*;

/**
 * 对象
 *
 * @author zhouhui
 * @date 2018/4/27
 * @company Dingxuan
 */
public class BlockUtilsTest {
    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @Test
    public void getBlockFromBlockBytes() {
    }

    @Test
    public void getGroupIDFromBlock() {
    }

    @Test
    public void extractEnvelope() {
    }

    @Test
    public void getLastConfigIndexFromBlock() throws JavaChainException, InvalidProtocolBufferException {
        Common.Block genesisBlock = new GenesisBlockFactory(Configtx.ConfigTree.getDefaultInstance()).getGenesisBlock("myGroup");
        long lastConfigIndex = BlockUtils.getLastConfigIndexFromBlock(genesisBlock);
        Assert.assertEquals(0L, lastConfigIndex);
    }

    @Test
    public void getMetadataFromBlock() throws InvalidProtocolBufferException, ValidateException {
        Common.Block emptyBlock = BlockUtils.newEmptyBlock();
        Common.Metadata metadata = BlockUtils.getMetadataFromBlock(emptyBlock, Common.BlockMetadataIndex.LAST_CONFIG_VALUE);
        Assert.assertThat(metadata, Matchers.notNullValue());

        Common.LastConfig lastConfig = Common.LastConfig.parseFrom(metadata.getValue());
        Assert.assertThat(lastConfig, Matchers.notNullValue());

        expectedEx.expect(ValidateException.class);
        expectedEx.expectMessage("can not be null");
        BlockUtils.getMetadataFromBlock(null, Common.BlockMetadataIndex.LAST_CONFIG_VALUE);
    }
}