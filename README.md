## 聚龙链（JulongChain）平台
聚龙链平台是一个基于Java的开源联盟链区块链底层技术平台。该项目旨在使用符合国家密码管理要求的国密算法和证书体系，打造一个数据防篡改、账本分布共享、系统安全可靠的开源区块链基础设施平台，为金融、政务、能源等重点领域区块链应用提供平台支撑。

## 链接
[Jira任务管理系统](http://jira.bcia.net.cn:8082/)

[Gitlab代码库管理系统](http://gitlab.bcia.net.cn:6060/)

## 当前版本
当前版本为0.8。

## 开源说明
聚龙链平台使用Apache License, Version 2.0 (Apache-2.0), located in the [LICENSE](LICENSE) file.

## 如何编译
编译环境：  <br/>
集成开发环境： IntelliJ IDEA 2017.3.3  <br/>
JAVA开发环境：JDK 1.8.0_151  <br/>
Maven依赖管理工具：Maven 3.5.2  <br/>
代码版本管理工具：Git  <br/>

编译步骤：  <br/>
1.从GitHub上下载项目源码。
<ul>
<li>打开IntelliJ IDEA,选择菜单File -> New -> Project from Version Control -> Git打开Clone Repository对话框。</li>
<li>在URL文本框输入本项目的地址<i>https://github.com/JulongChain/julongchain.git</i>，然后点击"Test"按钮，测试链接连接是否成功。</li>
<li>待连接成功后，在Directory文本框输入或选择项目工作文件夹。</li>
<li>最后，点击Clone开始下载源码。</li>
</ul>

2.添加框架支持。
<ul>
<li>项目名字右键 -> Add Framework Support。</li>
<li>Java EE version选择Java EE 8,勾选Maven选项。</li>
</ul>

3.设置Project环境。
<ul>
<li>选择菜单File -> Project Structure, 打开项目结构窗口。</li>
<li>左边导航选择Project,Project SDK选择已安装的JDK 1.8，Project language level 修改为8。</li>
<li>左边导航选择Modules，右边窗口会展开源码目录，展开并选中src/main/proto目录，并点击斜上方的"Resources"，可将其标识为资源。点击“Apply”或“OK”保存并退出。</li>
</ul>
  
4.Maven导入依赖包。
<ul>
<li>找到pom.xml，右键在下拉菜单选择Maven -> Reimport,等待Maven下载完成。</li>
</ul>

5.编译源码。
<ul>
<li>在Maven Projects视图中展开julongchain -> Lifecycle,选择双击compile进行编译。</li>
</ul>

## 如何打包运行
前置条件：<br/>
系统已经安装JDK8和Docker，配置好JDK 1.8的环境变量。<br/>
开发好智能合约，也可直接下载系统智能合约样例<i>https://github.com/JulongChain/julongchain-smartcontract-ab-java-0.8</i>,使用时只保留src和pom.xml文件即可。<br/>

详细步骤：
<ul>
<li>1、在Maven Projects视图中展开julongchain -> Lifecycle,选择双击package进行打包，可得到julongchain.jar。</li>
<li>2、使用WinRAR等压缩工具将julongchain.jar打开，将里面的lib文件夹拖拽拷贝至与julongchain.jar平级目录。</li>
<li>3、将项目源码根目录下的msp和config两个文件夹拷贝至与julongchain.jar平级目录。</li>
<li>4、使用java -jar julongchain.jar命令执行该Jar文件。具体命令详见下文"命令行支持"章节。</li>
<li>5、最佳执行顺序：(1)启动Consenter节点->(2)启动Node节点->(3)创建群组->(4)加入群组->(5)安装智能合约->(6)实例化智能合约->(7)调用智能合约。其中(1)、(2)、(3)必须在不同的进程。在同一台服务器可采用不同的命令行窗口。</li>
</ul>

## 命令行支持
启动Consenter节点  <br/>
<i>  java -jar julongchain.jar consenter start  </i>

启动Node节点  <br/>
<i>  java -jar julongchain.jar node start  </i>

查询Node节点服务状态  <br/>
<i>  java -jar julongchain.jar node status  </i>

创建群组  <br/>
<i>  java -jar julongchain.jar group create -c 127.0.0.1:7050 -g myGroup  </i><br/>
  参数说明：  <br/> 
     -c 共识节点地址  <br/>
     -g 群组名称     <br/>

加入群组  <br/>
<i>  java -jar julongchain.jar group join -t 127.0.0.1:7051 -b /home/bcia/Julongchain/myGroup.block  </i><br/>
  参数说明：  <br/>
      -t 要加入群组的目标节点地址 <br/>
      -b 创世区块保存的文件地址  <br/>
   
列出已加入的群组  <br/>
<i>  java -jar julongchain.jar group list -t 127.0.0.1:7051  </i><br/>
  参数说明：  <br/>
      -t 要查询的目标节点地址 <br/>

查询当前群组链信息<br/>
         <i>  java -jar julongchain.jar group info -t 127.0.0.1:7051 -g myGroup  </i><br/>
   参数说明：  <br/>
      -t 要查询的目标节点地址 <br/>
      -g 群组名称           <br/>

安装智能合约  <br/>
<i>  java -jar julongchain.jar contract install -t 127.0.0.1:7051 -n mycc -v 1.0 -p /home/bcia/julongchain-smartcontract-ab-java-0.8  </i><br/>
  参数说明：  <br/>
   -t 要安装智能合约的目标节点地址 <br/>
   -n 智能合约名称      <br/>
   -v 智能合约版本      <br/>
   -p 智能合约源码路径（需包含src子目录和pom.xml文件）   <br/>
   
实例化智能合约  <br/>
<i>  java -jar julongchain.jar contract instantiate -t 127.0.0.1:7051 -c 127.0.0.1:7050 -g myGroup -n mycc -v 1.0  -i 
"{'args':['init','a','100','b','200']}" -P "OR	('Org1MSP.member','Org2MSP.member')"  </i><br/>
  参数说明：  <br/>
   -t 要实例化智能合约的目标节点地址 <br/>
   -c 共识节点地址             <br/>
   -g 群组名称                <br/>
   -n 智能合约名称             <br/>
   -v 智能合约版本             <br/>
   -i 智能合约init方法入参      <br/>
   -P 背书策略                 <br/>
   
调用智能合约  <br/>
<i>  java -jar julongchain.jar contract invoke -t 127.0.0.1:7051 -c 127.0.0.1:7050 -g myGroup -n mycc -i "{'args':['move','a','b',
'10']}"  </i><br/>
  参数说明：  <br/>
   -t 要执行智能合约的目标节点地址 <br/>
   -c 共识节点地址               <br/>
   -g 群组名称                  <br/>
   -n 智能合约名称              <br/>
   -i 智能合约invoke方法入参   <br/>

查询智能合约（不产生新的区块）  <br/>
<i>  java -jar julongchain.jar contract query -t 127.0.0.1:7051 -g myGroup -n mycc -i "{'args':['query','a']}"  </i><br/>
  参数说明：  <br/>
   -t 要查询智能合约的目标节点地址 <br/>
   -g 群组名称                  <br/>
   -n 智能合约名称               <br/>
   -i 智能合约invoke方法入参      <br/>
