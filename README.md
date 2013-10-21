flume-observation-server
========================



flume-observation-server 是flume观察器对应的服务端程序，具体使用不走如下：

1. 配置配置文件 ： 
        在classpath中找到：flume.properties 修改对应配置文件。
        flumeFolder=对应的flume日志文件夹
        flumeHistoryFolder=对应的flume历史目录文件夹
        
2.打包， 使用mvn打包，注意一定要跳过测试。
3.发布， 将打好的包上传到flume对应机器上，使用java -jar命令执行。


flume observation server
