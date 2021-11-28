# SMScodeTcp
SMScode  is transferred from mobile phone to PC, using TCP

## conceived/构想
operators--(message)-> mobile phone-(using TCP)-(message)->PC 
运营商发送信息到手机,手机将信息用TPC协议传送到PC端

## procedure/步骤
First start tcpserver at PC
先启动PC端服务,准备监听接收
then receive messages sent by operators
运营商发送信息到手机
The tcpserver will receive the smscode transmitted by the app through TCP
手机将信息用TPC协议传送到PC端

## Functions of each module/各模块作用
smscodetcpserver.py:
Set the waiting time in the main thread, after the timeout, stop the Sub-thread
Sub-thread
According to the host's ip address, create a TCP protocol and listen. When there is a connection, receive the information and return data to the main thread
在主线程中设置等待时长(s秒),超时后将停止子程序
根据主机IP地址创建TCP协议并监听,当被连接后,收取信息并将数据返回给主线程
smscodecclient.java:
By listening the changes in the SMSbox, query the latest SMS message, determine whether the message matches, if True create a TCP connection and send the SMScode
通过监听SMS的变化,当收到信息时,查询最新一条,并进行匹配,是否满足要求,如果成立,则创建TCP连接传输捕获到的数据.

## reference参考
//https://blog.csdn.net/qq_42777804/article/details/103472426
//https://www.cnblogs.com/fuyanan/p/4767679.html

learning
学习中 
Welcome your guidance
欢迎指教
