## 聚龙链（JulongChain）平台
聚龙链平台是一个基于Java的开源联盟链区块链底层技术平台。该项目旨在使用符合国家密码管理要求的国密算法和证书体系，打造一个数据防篡改、账本分布共享、系统安全可靠的开源区块链基础设施平台，为金融、政务、能源等重点领域区块链应用提供平台支撑。


## 链接
[Jira任务管理系统](http://jira.bcia.net.cn:8082/)

## 如何编译
编译环境：  <br/>
集成开发环境：IntelliJ IDEA 2017.3.3  <br/>
JAVA开发环境：JDK 1.8.0_151  <br/>
Maven依赖管理工具：Maven 3.5.2  <br/>
代码版本管理工具：Git  <br/>

编译步骤：  <br/>
1.从GitHub上下载项目源码。打开IntelliJ IDEA,选择菜单File -> New -> Project from Version Control -> Git打开Clone Repository对话框  <br/>在URL文本框输入本项目的地址<i>https://github.com/JulongChain/julongchain.git</i>，然后点击"Test"按钮，测试链接连接是否成功。待连接成功后，在Directory文本框输入或选择项目工作文件夹。最后，点击Clone开始下载源码  <br/>
  
2.添加框架支持：项目名字右键 => Add Framework Support,Java EE version选择Java EE 8,勾选Maven选项  <br/>

3.设置Project环境：选择菜单File => Project Structure… => Project,Project SDK选择已安装的JDK 1.8  <br/>
  Project language level 修改为8  <br/>
  
4.Maven导入依赖包：pom.xml右键 => Maven => Reimport  <br/>

5.编译：在Maven Projects中展开julongchain => Lifecycle,选择双击compile编译  <br/>
<br/>



## 当前版本
当前版本为0.8。

## 开源说明 <a name="license"></a>

聚龙链平台使用Apache License, Version 2.0 (Apache-2.0), located in the [LICENSE](LICENSE) file.

## 命令行支持

启动Consenter服务  <br/>
<i>  java -jar julongchain.jar consenter start  </i>

启动Node服务  <br/>
<i>  java -jar julongchain.jar node start  </i>

查询Node服务状态  <br/>
<i>  java -jar julongchain.jar node status  </i>

创建群组  <br/>
<i>  java -jar julongchain.jar group create -c 127.0.0.1:7050 -g myGroup  </i><br/>
  参数说明：  <br/> 
     -c 共识节点地址  <br/>
     -g 群组名称     <br/>

加入群组  <br/>
<i>  java -jar julongchain.jar group join -t 127.0.0.1:7051 -b /opt/BCIA/JulongChain/myGroup.block  </i><br/>
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
<i>  java -jar julongchain.jar contract install -t 127.0.0.1:7051 -n mycc -v 1.0 -p /root/julongchain/mycc_src  </i><br/>
  参数说明：  <br/>
   -t 要安装智能合约的目标节点地址 <br/>
   -n 智能合约名称      <br/>
   -v 智能合约版本      <br/>
   -p 智能合约源码路径   <br/>
   
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
<i>  java -jar julongchain.jar contract invoke -t 127.0.0.1:7051 -c 127.0.0.1:7050 -g myGroup -n mycc -i "{'args':['invoke','a','b',
'10']}"  </i><br/>
  参数说明：  <br/>
   -t 要执行智能合约的目标节点地址 <br/>
   -c 共识节点地址               <br/>
   -g 群组名称                  <br/>
   -n 智能合约名称               <br/>
   -i 智能合约invoke方法入参   <br/>

查询智能合约（不产生新的区块）  <br/>
<i>  java -jar julongchain.jar contract query -t 127.0.0.1:7051 -g myGroup -n mycc -i "{'args':['query','a']}"  </i><br/>
  参数说明：  <br/>
   -t 要查询智能合约的目标节点地址 <br/>
   -g 群组名称                  <br/>
   -n 智能合约名称               <br/>
   -i 智能合约invoke方法入参      <br/>
123
