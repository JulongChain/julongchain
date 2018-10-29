package org.bcia.julongchain.common.configtx.util;

import org.bcia.julongchain.BaseJunit4Test;
import org.bcia.julongchain.common.configtx.ConfigComparable;
import org.bcia.julongchain.common.exception.ValidateException;
import org.bcia.julongchain.protos.common.Configtx;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.Matchers.hasKey;
import static org.junit.Assert.assertThat;

/**
 * 配置映射单元测试
 *
 * @author zhouhui
 * @date 2018/10/22
 * @company Dingxuan
 */
public class ConfigMapUtilsTest extends BaseJunit4Test {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void toComparableMap() {
        Configtx.ConfigTree sampleConfigTree = generateSampleConfigTree();

        try {
            Map<String, ConfigComparable> comparableMap = ConfigMapUtils.toComparableMap(sampleConfigTree, "Group");

            //测试map中是否还有指定键
            assertThat(comparableMap, hasKey("[Tree]   /Group"));
            assertThat(comparableMap, hasKey("[Tree]   /Group/1"));
            assertThat(comparableMap, hasKey("[Tree]   /Group/1/1.1"));
            assertThat(comparableMap, hasKey("[Tree]   /Group/1/1.1/1.1.1"));
            assertThat(comparableMap, hasKey("[Value]  /Group/1/1.1/1.1.1/Value111"));
            assertThat(comparableMap, hasKey("[Tree]   /Group/1/1.1/1.1.2"));
            assertThat(comparableMap, hasKey("[Value]  /Group/1/1.1/1.1.2/Value112"));
            assertThat(comparableMap, hasKey("[Tree]   /Group/1/1.2"));
            assertThat(comparableMap, hasKey("[Tree]   /Group/1/1.2/1.2.1"));
            assertThat(comparableMap, hasKey("[Value]  /Group/1/1.2/1.2.1/Value121"));
            assertThat(comparableMap, hasKey("[Tree]   /Group/1/1.2/1.2.2"));
            assertThat(comparableMap, hasKey("[Value]  /Group/1/1.2/1.2.2/Value122"));

            assertThat(comparableMap, hasKey("[Tree]   /Group/2"));
            assertThat(comparableMap, hasKey("[Tree]   /Group/2/2.1"));
            assertThat(comparableMap, hasKey("[Tree]   /Group/2/2.1/2.1.1"));
            assertThat(comparableMap, hasKey("[Value]  /Group/2/2.1/2.1.1/Value211"));
            assertThat(comparableMap, hasKey("[Tree]   /Group/2/2.1/2.1.2"));
            assertThat(comparableMap, hasKey("[Value]  /Group/2/2.1/2.1.2/Value212"));
            assertThat(comparableMap, hasKey("[Tree]   /Group/2/2.2"));
            assertThat(comparableMap, hasKey("[Tree]   /Group/2/2.2/2.2.1"));
            assertThat(comparableMap, hasKey("[Value]  /Group/2/2.2/2.2.1/Value221"));
            assertThat(comparableMap, hasKey("[Tree]   /Group/2/2.2/2.2.2"));
            assertThat(comparableMap, hasKey("[Value]  /Group/2/2.2/2.2.2/Value222"));

            assertSame(comparableMap.size(), 23);

        } catch (ValidateException e) {
            e.printStackTrace();
        }


    }

    /**
     * 获得样例配置树
     *
     * @return
     */
    public static Configtx.ConfigTree generateSampleConfigTree() {
        /*
1
 \_ 1.1
     \_ 1.1.1
     \_ 1.1.2
 \_ 1.2
     \_ 1.2.1
     \_ 1.2.2
2
 \_ 2.1
     \_ 2.1.1
     \_ 2.1.2
 \_ 2.2
     \_ 2.2.1
     \_ 2.2.2
*/
        Configtx.ConfigTree.Builder configTreeBuilder111 = Configtx.ConfigTree.newBuilder();
        configTreeBuilder111.putValues("Value111", Configtx.ConfigValue.newBuilder().build());
        Configtx.ConfigTree.Builder configTreeBuilder112 = Configtx.ConfigTree.newBuilder();
        configTreeBuilder112.putValues("Value112", Configtx.ConfigValue.newBuilder().build());
        Configtx.ConfigTree.Builder configTreeBuilder121 = Configtx.ConfigTree.newBuilder();
        configTreeBuilder121.putValues("Value121", Configtx.ConfigValue.newBuilder().build());
        Configtx.ConfigTree.Builder configTreeBuilder122 = Configtx.ConfigTree.newBuilder();
        configTreeBuilder122.putValues("Value122", Configtx.ConfigValue.newBuilder().build());
        Configtx.ConfigTree.Builder configTreeBuilder211 = Configtx.ConfigTree.newBuilder();
        configTreeBuilder211.putValues("Value211", Configtx.ConfigValue.newBuilder().build());
        Configtx.ConfigTree.Builder configTreeBuilder212 = Configtx.ConfigTree.newBuilder();
        configTreeBuilder212.putValues("Value212", Configtx.ConfigValue.newBuilder().build());
        Configtx.ConfigTree.Builder configTreeBuilder221 = Configtx.ConfigTree.newBuilder();
        configTreeBuilder221.putValues("Value221", Configtx.ConfigValue.newBuilder().build());
        Configtx.ConfigTree.Builder configTreeBuilder222 = Configtx.ConfigTree.newBuilder();
        configTreeBuilder222.putValues("Value222", Configtx.ConfigValue.newBuilder().build());

        Configtx.ConfigTree.Builder configTreeBuilder11 = Configtx.ConfigTree.newBuilder();
        configTreeBuilder11.putChilds("1.1.1", configTreeBuilder111.build());
        configTreeBuilder11.putChilds("1.1.2", configTreeBuilder112.build());
        Configtx.ConfigTree.Builder configTreeBuilder12 = Configtx.ConfigTree.newBuilder();
        configTreeBuilder12.putChilds("1.2.1", configTreeBuilder121.build());
        configTreeBuilder12.putChilds("1.2.2", configTreeBuilder122.build());
        Configtx.ConfigTree.Builder configTreeBuilder21 = Configtx.ConfigTree.newBuilder();
        configTreeBuilder21.putChilds("2.1.1", configTreeBuilder211.build());
        configTreeBuilder21.putChilds("2.1.2", configTreeBuilder212.build());
        Configtx.ConfigTree.Builder configTreeBuilder22 = Configtx.ConfigTree.newBuilder();
        configTreeBuilder22.putChilds("2.2.1", configTreeBuilder221.build());
        configTreeBuilder22.putChilds("2.2.2", configTreeBuilder222.build());

        Configtx.ConfigTree.Builder configTreeBuilder1 = Configtx.ConfigTree.newBuilder();
        configTreeBuilder1.putChilds("1.1", configTreeBuilder11.build());
        configTreeBuilder1.putChilds("1.2", configTreeBuilder12.build());
        Configtx.ConfigTree.Builder configTreeBuilder2 = Configtx.ConfigTree.newBuilder();
        configTreeBuilder2.putChilds("2.1", configTreeBuilder21.build());
        configTreeBuilder2.putChilds("2.2", configTreeBuilder22.build());

        Configtx.ConfigTree.Builder configTreeBuilder = Configtx.ConfigTree.newBuilder();
        configTreeBuilder.putChilds("1", configTreeBuilder1.build());
        configTreeBuilder.putChilds("2", configTreeBuilder2.build());
        return configTreeBuilder.build();
    }

    @Test
    public void validateConfigPath() throws ValidateException {
        //验证为空时的异常
        String[] nullPaths = new String[]{null, "", "   "};
        for (String path : nullPaths) {
            try {
                ConfigMapUtils.validateConfigPath(path);
            } catch (Exception e) {
                assertThat(e.getMessage(), containsString("can not be null"));
            }
        }

        //验证长度溢出时的异常
        String[] maxPaths = new String[]{"s" + new String(new byte[249])};
        for (String path : maxPaths) {
            try {
                ConfigMapUtils.validateConfigPath(path);
            } catch (Exception e) {
                assertThat(e.getMessage(), containsString("can not be longer than max length"));
            }
        }

        //验证为相对路径时的异常
        String[] IllegalPaths = new String[]{".", ".."};
        for (String path : IllegalPaths) {
            try {
                ConfigMapUtils.validateConfigPath(path);
            } catch (Exception e) {
                assertThat(e.getMessage(), containsString("Illegal path"));
            }
        }

        //验证为错误路径格式时的异常
        String[] wrongPaths = new String[]{"%&", "$3"};
        for (String path : wrongPaths) {
            try {
                ConfigMapUtils.validateConfigPath(path);
            } catch (Exception e) {
                assertThat(e.getMessage(), containsString("Wrong config path"));
            }
        }

        //验证为正常格式时无异常
        String[] normalPaths = new String[]{"123", ".56-123"};
        for (String path : normalPaths) {
            ConfigMapUtils.validateConfigPath(path);
        }
    }

    @Test
    public void restoreConfigTree() {
    }
}