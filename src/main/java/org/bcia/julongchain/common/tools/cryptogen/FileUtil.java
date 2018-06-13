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

import org.bcia.julongchain.common.exception.JavaChainException;
import org.bcia.julongchain.common.log.JavaChainLog;
import org.bcia.julongchain.common.log.JavaChainLogFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.PosixFilePermission;
import java.util.HashSet;
import java.util.Set;

/**
 * @author chenhao, liuxifeng
 * @date 2018/4/9
 * @company Excelsecu
 */
public class FileUtil {
    private static JavaChainLog log = JavaChainLogFactory.getLog(FileUtil.class);

    public static boolean removeAll(String filePath) {
        if (!new File(filePath).exists()){
            return true;
        }
        try {
            Files.walkFileTree(Paths.get(filePath), new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Files.delete(file);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    Files.delete(dir);
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            log.error("remove " + filePath + " failed");
            return false;
        }
        return true;
    }


    public static void mkdirAll(final Path path) {
        try {
            Files.createDirectories(path);
            Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    try {
                        setPermission(file);
                    } catch (JavaChainException e) {
                        log.error(e.getMessage());
                        return FileVisitResult.TERMINATE;
                    }
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    try {
                        setPermission(dir);
                    } catch (JavaChainException e) {
                        log.error(e.getMessage());
                        return FileVisitResult.TERMINATE;
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // recursively set directory and its child directory or file permission 755
    private static void setPermission(Path path) throws JavaChainException {
        if(System.getProperty("os.name").contains("Windows")) {
            return;
        }

        Set<PosixFilePermission> filePermissions = new HashSet<>();
        filePermissions.add(PosixFilePermission.OWNER_READ);
        filePermissions.add(PosixFilePermission.OWNER_WRITE);
        filePermissions.add(PosixFilePermission.OWNER_EXECUTE);
        filePermissions.add(PosixFilePermission.GROUP_READ);
        filePermissions.add(PosixFilePermission.GROUP_EXECUTE);
        filePermissions.add(PosixFilePermission.OTHERS_READ);
        filePermissions.add(PosixFilePermission.OTHERS_EXECUTE);
        try {
            Files.setPosixFilePermissions(path, filePermissions);
        } catch (IOException e) {
            throw new JavaChainException("set directory" + path + " permission failed " + e.getMessage());
        }
    }

}
