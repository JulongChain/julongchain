声明<br/>

1、本项目中调用gRPC框架所采用的Protobuf协议中，用到的所有Proto文件等复用了Hyperledger Fabric的Proto文本，并修改了包名和若干内容以适配
当前项目，同时保留了Hyperledger Fabric的License声明。<br/>
2、本项目中的shim模块复用了Hyperledger Fabric的shim模块的代码，做了部分修改，同时保留了Hyperledger Fabric的License声明。<br/>
3、本项目中其他部分的代码，架构上和实现上均有参照Hyperledger Fabric的架构和实现。并采用了Apache 2.0的License协议发布。Hyperledger Fabric
的Go语言源码采用的License声明如下：<br/>
/*
Copyright IBM Corp. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
*/

和

/*
Copyright IBM Corp. 2017 All Rights Reserved.
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