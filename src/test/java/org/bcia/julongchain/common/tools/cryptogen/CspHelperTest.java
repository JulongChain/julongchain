/**
 * Copyright BCIA. All Rights Reserved.
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
package org.bcia.julongchain.common.tools.cryptogen;

import org.bcia.julongchain.common.exception.JulongChainException;
import org.bcia.julongchain.common.tools.cryptogen.utils.MockKey;
import org.bcia.julongchain.csp.intfs.IKey;
import org.bouncycastle.util.encoders.Hex;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.*;


/**
 * CspHelper 测试类
 *
 * @author chenhao, liuxifeng
 * @date 2018/7/12
 * @company Excelsecu
 */
public class CspHelperTest {
    private String testDir;

    {
        try {
            Path tempDirPath = Files.createTempDirectory(null);
            testDir = Paths.get(tempDirPath.toString(), "csp-test").toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void loadPrivateKey() throws JulongChainException {
        IKey priv = CspHelper.generatePrivateKey(testDir);
        String filePath = Paths.get(testDir, Hex.toHexString(priv.ski()) + "_sk").toString();
        File pkFile = new File(filePath);
        assertTrue(pkFile.exists());

        IKey loadedPriv = CspHelper.loadPrivateKey(testDir);
        assertNotNull(loadedPriv);
        assertEquals(Hex.toHexString(priv.ski()), Hex.toHexString(loadedPriv.ski()));

        FileUtil.removeAll(testDir);
    }

    @Test
    public void generatePrivateKey() throws JulongChainException {
        System.out.println(testDir);
        IKey priv = CspHelper.generatePrivateKey(testDir);

        assertNotNull(priv);
        assertTrue(priv.isPrivate());
        File pkFile = new File(Paths.get(testDir, Hex.toHexString(priv.ski()) + "_sk").toUri());
        assertTrue(pkFile.exists());

        FileUtil.removeAll(testDir);
    }

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void getECPublicKey() throws JulongChainException {
        IKey priv = CspHelper.generatePrivateKey(testDir);

        // 使用不复合要求的key的异常测试
        expectedException.expect(JulongChainException.class);
        priv = new MockKey(null, null, new MockKey());
        CspHelper.getSM2PublicKey(priv);

        priv = new MockKey(null, null, new MockKey(null, "bytesErr", null));

        expectedException.expect(JulongChainException.class);
        expectedException.expectMessage(((MockKey) priv).getBytesErr());
        CspHelper.getSM2PublicKey(priv);

        priv = new MockKey("pubKeyErr", null, new MockKey());

        expectedException.expectMessage(((MockKey) priv).getPubKeyErr());
        expectedException.expect(JulongChainException.class);
        CspHelper.getSM2PublicKey(priv);

        FileUtil.removeAll(testDir);
    }
}