# VelocityChat

我的世界 Velocity 群组跨服聊天插件

## 功能

- 跨服聊天
  - 聊天消息支持彩色文字 。彩色文字可参照[此链接](https://wiki.biligame.com/mc/%E6%A0%BC%E5%BC%8F%E5%8C%96%E4%BB%A3%E7%A0%81)，在游戏中使用 `&`
  符号代替 `§`
- 玩家连接、退出、切换服务器时发送全局消息
- （可选）客户端 ping（刷新服务器列表）时向客户端发送当前在线玩家
- （可选）Tab 列表显示所有在线玩家
- （可选）日志记录玩家消息
- （可选）日志记录玩家指令
- （可配置）消息以指定字符串开头时取消跨服发送
- （可配置）多语言翻译支持

## 文件

位于`plugins/VelocityChat`目录下

### config.toml

- 见文件内

### langs/*.properties

- 语言文件

## 使用说明

- 将从 `Releases` 页面下载的插件放入 Velocity 端的 `plugins` 文件夹中，重启或开启 Velocity 端。

- 首次运行会在 `plugins` 文件夹中生成 `VelocityChat` 文件夹，推荐使用 `Notepad3` 等编辑器编辑 `config.toml` 配置文件

## 已知问题（可能带有解决方案）

### 聊天消息出现在 Action Bar（物品栏上方）

- 复现：
    - Velocity 版本：Development 版 #161
    - ViaVersion 版本：稳定版 4.3.1
    - 子服版本： 1.18.2
    - 客户端版本：1.19
- 解决方案：使用 ViaVersion 最新 DEV 版，到 [此处](https://ci.viaversion.com/job/ViaVersion-DEV/) 下载

## 更新日志

- v1.4.0
    - 支持自定义语言文件
- v1.3.0
    - 支持自动纠错和恢复配置文件
- v1.2.0
    - 支持多个 MCDR 命令前缀
- v1.1.1
    - 修复玩家消息颜色代码解析
- v1.1.0
    - 优化代码和修复聊天格式
    - 添加玩家加入或者退出服务器和切换子服务器提示
- v1.0.1
    - 修复使用 MCDR 开服的服务端，MCDR 命令失效
- v1.0.0
    - 添加根据配置文件，向聊天栏发送带有前缀的消息
