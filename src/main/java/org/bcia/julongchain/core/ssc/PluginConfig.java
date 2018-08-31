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
package org.bcia.julongchain.core.ssc;

/**
 * 系统智能合约插件配置
 *
 * @author sunzongyu1
 * @date 2018/07/19
 * @company Dingxuan
 */
public class PluginConfig {
	private boolean enable;
	private String name;
	private String path;
	private boolean invokableExternal;
	private boolean invokableSC2SC;

	public boolean isEnable() {
		return enable;
	}

	public void setEnable(boolean enable) {
		this.enable = enable;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public boolean isInvokableExternal() {
		return invokableExternal;
	}

	public void setInvokableExternal(boolean invokableExternal) {
		this.invokableExternal = invokableExternal;
	}

	public boolean isInvokableSC2SC() {
		return invokableSC2SC;
	}

	public void setInvokableSC2SC(boolean invokableSC2SC) {
		this.invokableSC2SC = invokableSC2SC;
	}
}
