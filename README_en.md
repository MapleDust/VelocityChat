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

- See in the file

### Langs

#### Default `langs/default/*.properties`

- The default language file is now forced to refresh

#### Custom `langs/custom/*.properties`

- You can replace any default translation here.  

Some of the translations:

- Fallback chat format：`qu_an.chat.message.chat.default=<chat_format>`

- Server exclusive chat format：`qu_an.chat.message.chat.server.<子服务器id>=<chat_format>`
    - Examples：
        - `qu_an.chat.message.chat.server.lobby=§8[§r{0}§8|§r{1}]§r<{2}§r> {3}`
        - `qu_an.chat.message.chat.server.survival=§8[§r{0}§8]§r<{2}§r> {3}`
    - Use fallback chat format when missing.

- Server name：`qu_an.chat.server.name.<子服务器id>=<子服务器名称>`
    - Examples：
        - `qu_an.chat.server.name.lobby=Lobby`
        - `qu_an.chat.server.name.survival=§aSurvival Server`
    - Use server id when missing.

## Development

Dependency (has been included in the released plugin)：[衢安 Libs](https://gitee.com/virtual-qu-an/qu-an-libs)
