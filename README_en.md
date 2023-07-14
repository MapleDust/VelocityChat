# VelocityChat

- [中文](./README.md)
- English

A Cross-server chat plugin for Velocity proxy

## Features

- Cross-server chat
    - (configurable, default true) Enable color code
      . check [Minecraft Wiki](https://minecraft.fandom.com/zh/wiki/%E6%A0%BC%E5%BC%8F%E5%8C%96%E4%BB%A3%E7%A0%81) for
      more information
      , use `&` instead of `§` in game.
    - `/vchat local` Send local message (to current server), alias (configurable) `/br`.
    - `/vchat broadcast` Send global message, alias (configurable) `/lc`.
- Message of player joined, left and switch server.
- (configurable, default false) Send sample players when client refreshing multiplayer games.
- (configurable, default false) Show all proxy players on tab list.
- (configurable, default true) Log player command.
- (configurable, default true) enable command `glist`.
- (configurable, default true) enable default global chat.
- (configurable) Send local message if chat message is starts with matched string.
- (configurable) translations

## Files

Locate in `plugins/VelocityChat`

### Configuration `config.toml`

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