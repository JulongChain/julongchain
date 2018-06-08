/*
 * Copyright Dingxuan. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

		 http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package org.bcia.julongchain.core.ledger.kvledger.txmgmt.rwsetutil;

import com.google.protobuf.ByteString;
import org.apache.commons.lang3.ArrayUtils;
import org.bcia.julongchain.common.exception.LedgerException;
import org.bcia.julongchain.common.log.JavaChainLog;
import org.bcia.julongchain.common.log.JavaChainLogFactory;
import org.bcia.julongchain.core.ledger.util.Util;
import org.bcia.julongchain.protos.ledger.rwset.kvrwset.KvRwset;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 莫尔克树
 *
 * @author sunzongyu
 * @date 2018/04/18
 * @company Dingxuan
 */
public class MerkleTree {
    private static final JavaChainLog logger  = JavaChainLogFactory.getLog(MerkleTree.class);
    private static final int LEAF_LEVEL = 1;

    private Map<Integer, List<byte[]>> tree;
    private int maxLevel;
    private int maxDegree;

    public MerkleTree(int maxDegree) throws LedgerException{
        if(maxDegree < 2){
            throw new LedgerException("MerkleTree should not be less than 2 height");
        }
        this.tree = new HashMap<>();
        this.maxLevel = 1;
        this.maxLevel = maxDegree;
    }

    public void update(byte[] nextLeafLevelHash) throws LedgerException{
        logger.debug("Before update. Tree's max level is " + tree.size());
        tree.get(LEAF_LEVEL).add(nextLeafLevelHash);
        for (int currentLelvel = LEAF_LEVEL;; currentLelvel++) {
            List<byte[]> currenLevelHashes = tree.get(currentLelvel);
            if(currenLevelHashes.size() < maxDegree){
                logger.debug("After update. Tree's max level is " + tree.size());
                return;
            }
            byte[] nextLevelHash = computeCombinedHash(currenLevelHashes);
            tree.remove(currentLelvel);
            int nextLevel = currentLelvel + 1;
            tree.get(nextLevel).add(nextLevelHash);
            if(nextLevel > maxLevel){
                maxDegree = nextLevel;
            }
            currentLelvel = nextLevel;
        }
    }

    public void done() throws LedgerException{
        logger.debug("Before done.");
        int currentLevel = LEAF_LEVEL;
        byte[] hash = null;
        while(currentLevel < maxLevel){
            List<byte[]> currentLevelHashes = tree.get(currentLevel);
            switch (currentLevelHashes.size()){
                case 0:
                    currentLevel++;
                    break;
                case 1:
                    hash = currentLevelHashes.get(0);
                    break;
                default:
                    hash = computeCombinedHash(currentLevelHashes);
            }
            tree.remove(currentLevel);
            currentLevel++;
            tree.get(currentLevel).add(hash);
        }
        List<byte[]> finalHash = tree.get(maxLevel);
        if(finalHash.size() > maxDegree){
           tree.remove(maxLevel);
           maxLevel++;
           byte[] combinedHash = computeCombinedHash(finalHash);
           List<byte[]> l = new ArrayList<>();
           l.add(combinedHash);
           tree.put(maxLevel, l);
        }
        logger.debug("After done.");
    }

    public KvRwset.QueryReadsMerkleSummary getSummery(){
        return setMaxLevelHashes(KvRwset.QueryReadsMerkleSummary.newBuilder(), getMaxLevelHashes())
                .setMaxDegree(maxDegree)
                .setMaxLevel(maxLevel)
                .build();
    }

    public List<byte[]> getMaxLevelHashes(){
        return tree.get(maxLevel);
    }

    public boolean isEmpty(){
        return maxLevel == 1 && tree.get(maxLevel).size() == 0;
    }

    @Override
    public String toString() {
        return "tree" + tree;
    }

    public static byte[] computeCombinedHash(List<byte[]> hashes) throws LedgerException{
        byte[] combinedHash = new byte[]{};
        for(byte[] h : hashes){
            combinedHash = ArrayUtils.addAll(combinedHash, h);
        }
        //TODO compute hash
        //TODO SM3 Hash
        return Util.getHashBytes(combinedHash);
    }

    private KvRwset.QueryReadsMerkleSummary.Builder setMaxLevelHashes(KvRwset.QueryReadsMerkleSummary.Builder builder, List<byte[]> list){
        for (int i = 0; i < list.size(); i++) {
            builder.setMaxLevelHashes(i, ByteString.copyFrom(list.get(i)));
        }
        return builder;
    }

    public Map<Integer, List<byte[]>> getTree() {
        return tree;
    }

    public void setTree(Map<Integer, List<byte[]>> tree) {
        this.tree = tree;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public void setMaxLevel(int maxLevel) {
        this.maxLevel = maxLevel;
    }

    public int getMaxDegree() {
        return maxDegree;
    }

    public void setMaxDegree(int maxDegree) {
        this.maxDegree = maxDegree;
    }
}
