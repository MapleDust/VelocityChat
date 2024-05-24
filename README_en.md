# VelocityChat

- [中文](./README.md)
- English

Minecraft [Velocity](https://papermc.io/software/velocity) Group cross-server chat plugin.

## Please help touch up the English README! Currently using DeepL for translation.

## Features

- Cross-server chat.
    - Chat messages support MiniMessage format colors, modifiers and reset
      codes, [click here](https://docs.advntr.dev/minimessage/format.html) for more info. (Configurable, enabled by
      default)
    - Global chat is enabled by default when a player joins the game. (Configurable, enabled by default)
- Send global messages when players connect, leave, or switch servers. (Configurable message format)
- (Configurable message format) Send current online player to client when client ping (refresh server list). (
  configurable, disabled by default)
- Tab list of all online players. (configurable, disabled by default)
- Logs player commands. (configurable, disabled by default)
- Log server chats. (configurable, enabled by default)
- Logs player `tell` chat content. (configurable, disabled by default)
- Allow players to use the `glist` command. (configurable, enabled by default)
- Enable global chat by default. (configurable, enabled by default)
- Override local chat for global chat. (configurable, enabled by default)
- (Configurable, enabled by default) Cancel cross-server sending when the message starts with the specified string. (
  Configurable)
- Multi-language support. (Configurable)

### Commands

- `/vchat reload` reloads and requires permission `velocity.command.admin` when used by players.

#### Player only

- `/vchat channel` Get the current chat channel.
- `/vchat local` Switch to local chat, reset when leaving.
    - Alias `/lcl` (configurable)
- `/vchat local <chat message>` Send to local chat.
    - Alias `/lcl <chat message>` (configurable)
    - Send message with prefix `\ ` (configurable)
- `/vchat global` Switch to global chat, reset on leave.
    - Alias `/glb` (configurable)
- `/vchat global <chat message>` Send to global chat.
    - Alias `/glb <chat message>` (configurable)
    - Send message with prefix `<` (configurable)
- `/vchat tellconsole <private message>` Sends a private message to the backend.
    - Alias `/tellconsole <private message>` (configurable)

#### Console only

- `/vchat broadcast <broadcast message>` Sends a broadcast
    - Alias `/br <broadcast message>` (configurable)
- `/vchat notify <subserver ID> <notify message>` Sends a notification to a subserver
    - Alias `/notify <subserver ID> <notification message>` (configurable)
- `/vchat tell <player ID> <private message>` Sends a private message to a player
    - Alias `/tell <player ID> <private message>` (configurable)

## Files

Located in the `plugins/VelocityChat` directory.

### Configuration `config.toml`

- See inside the file.

### Language files

- Distinguish between default and custom language files.
- Default language files are prioritized over custom language files.

#### default language file `langs/default/*.properties`

- The default language file is now forced to be refreshed.

#### custom language files `langs/custom/*.properties`

- You can override any translations in the default language file here!
- Default chat format: `qu_an.chat.message.chat.default=<chat_format>`

- Server chat format: `qu_an.chat.message.chat.server.<server_id>=<chat_format>`
    - Example:
        - `qu_an.chat.message.chat.server.lobby=§8[§r{0}§8|§r{1}]§r<{2}§r> {3}`
        - `qu_an.chat.message.chat.server.survival=§8[§r{0}§8]§r<{2}§r> {3}`
    - Default chat format is used when server chat format is not specified.

- Server name: `qu_an.chat.server.name.<server_id>=<server_name>`
    - Example:
        - `qu_an.chat.server.name.lobby=lobby`
        - `qu_an.chat.server.name.survival=§a survival suit`
    - Defaults to the server id if the server name does not exist.