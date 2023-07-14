# VelocityChat

- 中文
- [English](./README_en.md)

我的世界 Velocity 群组跨服聊天插件

## 功能

- 跨服聊天
    - （可配置，默认开启）聊天消息支持彩色文字
      。彩色文字可参照[此链接](https://minecraft.fandom.com/zh/wiki/%E6%A0%BC%E5%BC%8F%E5%8C%96%E4%BB%A3%E7%A0%81)
      ，在游戏中使用 `&`符号代替 `§`
    - `/vchat local` 像本地服务器发送聊天消息，别名（可配置）`/br`
    - `/vchat broadcast` 发送全局聊天消息，别名（可配置）`/lc`
- 玩家连接、退出、切换服务器时发送全局消息
- （可配置，默认关闭）客户端 ping（刷新服务器列表）时向客户端发送当前在线玩家
- （可配置，默认关闭）Tab 列表显示所有在线玩家
- （可配置，默认开启）日志记录玩家指令
- （可配置，默认开启）向玩家开放“glist”指令权限
- （可配置，默认开启）默认开启全局聊天
- （可配置）消息以指定字符串开头时取消跨服发送
- （可配置）多语言翻译支持

## 文件

位于`plugins/VelocityChat`目录下

### 配置 `config.toml`

- 见文件内

### 语言文件

- 区分默认和自定义语言文件
- 自定义语言文件的优先级高于默认语言文件

#### 默认语言文件 `langs/default/*.properties`

- 现在会强制刷新默认语言文件

#### 自定义语言文件 `langs/custom/*.properties`

- 你可以在这里覆盖任何默认语言文件中的翻译！
- 默认聊天格式：`qu_an.chat.message.chat.default=<聊天格式>`

- 服务器聊天格式：`qu_an.chat.message.chat.server.<子服务器id>=<聊天格式>`
    - 例：
        - `qu_an.chat.message.chat.server.lobby=§8[§r{0}§8|§r{1}]§r<{2}§r> {3}`
        - `qu_an.chat.message.chat.server.survival=§8[§r{0}§8]§r<{2}§r> {3}`
    - 未指定服务器聊天格式时使用默认聊天格式

- 子服务器名称：`qu_an.chat.server.name.<子服务器id>=<子服务器名称>`
    - 例：
        - `qu_an.chat.server.name.lobby=大厅`
        - `qu_an.chat.server.name.survival=§a生存服`
    - 子服务器名称不存在时默认为子服务器id

## 已知问题（已过时）

### 聊天消息出现在 Action Bar（物品栏上方）

- 复现：
    - Velocity 版本：Development 版 #161
    - ViaVersion 版本：稳定版 4.3.1
    - 子服版本： 1.18.2
    - 客户端版本：1.19
- 解决方案：使用 ViaVersion 最新版，到 [此处](https://ci.viaversion.com/job/ViaVersion/) 下载

## 开发者相关

依赖项（发布的插件中已经包含该依赖）：[衢安 Libs](https://gitee.com/virtual-qu-an/qu-an-libs)