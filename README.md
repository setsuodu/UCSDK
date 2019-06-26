# UCSDK接入说明

官方文档及之外，容易出现的问题。

## 存在问题

1. 单机初始化成功，但是支付的时候提示我没有初始化
1、检查gameId是否配置
2、检查pay.png是否配置、路径是否错误、图片格式是否有私自修改
3、检查gameId和pay.png是否为同一游戏
4、如果是U3D等非原生引擎，请将ugpsdk-release-x.x.x.jar解压拷贝里面的assets到工程assets下
2. 横屏、竖屏崩溃。

## 参考文档

https://aligames.open.uc.cn/document/doc/detail/128
