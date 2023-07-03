# VelocityChat

我的世界 Velocity 群组跨服聊天插件

## 功能

- 跨服聊天
    - 聊天消息支持彩色文字
      。彩色文字可参照[此链接](https://wiki.biligame.com/mc/%E6%A0%BC%E5%BC%8F%E5%8C%96%E4%BB%A3%E7%A0%81)
      ，在游戏中使用 `&`
      符号代替 `§`
- 玩家连接、退出、切换服务器时发送全局消息
- （可配置）客户端 ping（刷新服务器列表）时向客户端发送当前在线玩家
- （可配置）Tab 列表显示所有在线玩家
- （可配置）日志记录玩家指令
- （可配置）向玩家开放“glist”指令权限
- （可配置）默认开启全局聊天
- （可配置）消息以指定字符串开头时取消跨服发送
- （可配置）多语言翻译支持

## 文件

位于`plugins/VelocityChat`目录下

### config.toml

- 见文件内

### langs/*.properties

语言文件

- 如需恢复默认翻译，可以直接删除对应项目，然后在控制台输入“velocity reload”重载插件
- 您可以在语言文件内设置子服务器的显示名称

## 已知问题（可能带有解决方案）

### 聊天消息出现在 Action Bar（物品栏上方）

- 复现：
    - Velocity 版本：Development 版 #161
    - ViaVersion 版本：稳定版 4.3.1
    - 子服版本： 1.18.2
    - 客户端版本：1.19
- 解决方案：使用 ViaVersion 最新版，到 [此处](https://ci.viaversion.com/job/ViaVersion/) 下载

## 开发者相关

依赖项：[衢安 Libs](https://gitee.com/virtual-qu-an/qu-an-libs)