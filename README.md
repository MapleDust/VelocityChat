# VelocityChat

- 中文
- [English](./README_en.md)

我的世界 [Velocity](https://papermc.io/software/velocity) 群组跨服聊天插件。

## 请帮忙润色英文README！目前使用 DeepL 翻译。

## 功能

- 跨服聊天
    - 聊天消息支持 MiniMessage 格式的颜色、修饰和重置代码，[点击此处](https://docs.advntr.dev/minimessage/format.html)
      获取更多信息。（可配置，默认启用）
    - 玩家加入游戏时默认开启全局聊天。（可配置，默认启用）
- 玩家连接、退出、切换服务器时发送全局消息。（可配置消息格式）
- 客户端 ping（刷新服务器列表）时向客户端发送当前在线玩家。（可配置，默认停用）
- Tab 列表显示所有在线玩家。（可配置，默认停用）
- 日志记录玩家指令。（可配置，默认停用）
- 日志记录子服务器聊天内容。（可配置，默认启用）
- 日志记录玩家 `tell` 聊天内容。（可配置，默认停用）
- 允许玩家使用 `glist` 指令。（可配置，默认启用）
- 默认开启全局聊天。（可配置，默认启用）
- 全局聊天时覆盖本地聊天。（可配置，默认启用）
- 消息以指定字符串开头时取消跨服发送。（可配置）
- 多语言支持。 （可配置）

### 指令

- `/vchat reload` 重载，由玩家使用时需要权限 `velocity.command.admin` 。

#### 仅玩家

- `/vchat channel` 获取当前聊天频道。
- `/vchat local` 切换到本地聊天，退出后重置。
    - 别名 `/lcl`（可配置）
- `/vchat local <聊天消息>` 发送到本地聊天。
    - 别名 `/lcl <聊天消息>`（可配置）
    - 在发送消息时带前缀 `\ `（可配置）
- `/vchat global` 切换到全局聊天，退出后重置。
    - 别名 `/glb`（可配置）
- `/vchat global <聊天消息>` 发送到全局聊天。
    - 别名 `/glb <聊天消息>`（可配置）
    - 在发送消息时带前缀 `<`（可配置）
- `/vchat tellconsole <私信消息>` 向后台发送私信。（这好像没什么用）
    - 别名 `/tellconsole <私信消息>`（可配置）

#### 仅控制台

- `/vchat broadcast <广播消息>` 发送广播
    - 别名 `/br <广播消息>`（可配置）
- `/vchat notify <子服务器ID> <通知消息>` 向子服务器发送通知
    - 别名 `/notify <子服务器ID> <通知消息>`（可配置）
- `/vchat tell <玩家ID> <私信消息>` 向玩家发送私信
    - 别名 `/tell <玩家ID> <私信消息>`（可配置）

## 文件

位于 `plugins/VelocityChat` 目录下。

### 配置  `config.toml`

- 见文件内。

### 语言文件

- 区分默认和自定义语言文件。
- 自定义语言文件的优先级高于默认语言文件

#### 默认语言文件 `langs/default/*.properties`

- 现在会强制刷新默认语言文件。

#### 自定义语言文件 `langs/custom/*.properties`

- 你可以在这里覆盖任何默认语言文件中的翻译！
- 默认聊天格式：`qu_an.chat.message.chat.default=<聊天格式>`

- 服务器聊天格式：`qu_an.chat.message.chat.server.<子服务器id>=<聊天格式>`
    - 例：
        - `qu_an.chat.message.chat.server.lobby=§8[§r{0}§8|§r{1}]§r<{2}§r> {3}`
        - `qu_an.chat.message.chat.server.survival=§8[§r{0}§8]§r<{2}§r> {3}`
    - 未指定服务器聊天格式时使用默认聊天格式。

- 子服务器名称：`qu_an.chat.server.name.<子服务器id>=<子服务器名称>`
    - 例：
        - `qu_an.chat.server.name.lobby=大厅`
        - `qu_an.chat.server.name.survival=§a生存服`
    - 子服务器名称不存在时默认为子服务器id。