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
package org.bcia.julongchain.core.ledger.kvledger.txmgmt.statedb.statecouchdb;

import java.util.HashMap;
import java.util.Map;

/**
 * 类描述
 *
 * @author sunzongyu
 * @date 2018/09/14
 * @company Dingxuan
 */
public class Conf {
	private static final String BINARY_WRAPPER = "valueBytes";
	private static final String ID_FIELD = "_id";
	private static final String REV_FIELD = "_rev";
	private static final String VERSION_FIELD = "~version";
	private static final String DELETED_FIELD = "_deleted";
	private static final Map<String, Boolean> DB_ARTIFACTS_DIR_FILTER = new HashMap<>(32);
	private static final int QUERY_SKIP = 0;

	public static String[] getReservedFields() {
		return new String[]{ID_FIELD, REV_FIELD, VERSION_FIELD, DELETED_FIELD};
	}

	public static String getBinaryWrapper() {
		return BINARY_WRAPPER;
	}

	public static String getIdField() {
		return ID_FIELD;
	}

	public static String getRevField() {
		return REV_FIELD;
	}

	public static String getVersionField() {
		return VERSION_FIELD;
	}

	public static String getDeletedField() {
		return DELETED_FIELD;
	}

	public static Map<String, Boolean> getDbArtifactsDirFilter() {
		return DB_ARTIFACTS_DIR_FILTER;
	}

	public static int getQuerySkip() {
		return QUERY_SKIP;
	}
}

