# VelocityChat

一个我的世界 Velocity 群组跨服聊天的插件，不需要向子服安装额外的插件，聊天支持彩色文字，但是配置文件不支持懒~彩色文字可参照[此链接](https://wiki.biligame.com/mc/%E6%A0%BC%E5%BC%8F%E5%8C%96%E4%BB%A3%E7%A0%81)，使用 `&` 符号来代替 `§`,以及提供玩家连接服务器和切换子服务器的提示

## 使用说明

- 将从 `Releases` 页面下载的插件放入 Velocity 端的 `plugins` 文件夹中，重启或开启 Velocity 端。

- 首次运行会在 `plugins` 文件夹中生成 `velocitychat` 文件夹，推荐使用 `Notepad3` 等编辑器来编辑其目录下的 `config.toml` 配置文件

- 本插件配置文件 `config.toml` 初始内容

  ```toml
  # 主前缀
  main_prefix = "§8[§6testServerName§8]"

  # 在此处填写 MCDR 命令的前缀,支持多个MCDR命令前缀，如果没有使用 MCDR 开服的请保持默认，如果使用 MCDR 开服请根据实际情况填写
  mcdr_command_prefix = ["!!"]

  # 子服前缀
  [sub_prefix]
  lobby = "§8[§alobby§8]"
  ```

## 更新事项

在每次升级中请将原来的 `config.toml` 重命名或者移动到其他文件夹，然后根据根据实际情况填写配置（懒）

## 已知问题（可能带有解决方案）

### 聊天消息出现在 Action Bar（物品栏上方）

- 复现：
  - Velocity 版本：Development 版 #161
  - ViaVersion 版本：稳定版 4.3.1
  - 子服版本： 1.18.2
  - 客户端版本：1.19
- 解决方案：使用 ViaVersion 最新 DEV 版，到 [此处](https://ci.viaversion.com/job/ViaVersion-DEV/) 下载

## 更新日志

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
