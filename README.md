# VelocityChat

一个我的世界 Velocity 群组跨服聊天的插件，不需要向子服安装额外的插件，聊天支持彩色文字，但是配置文件不支持懒~彩色文字可参照[此链接](https://wiki.biligame.com/mc/%E6%A0%BC%E5%BC%8F%E5%8C%96%E4%BB%A3%E7%A0%81)，使用`&`符号来代替`§`

- 使用说明

  - 将从 `Releases` 上下载的插件放入 Velocity 端根目录的 `plugins` 文件夹里

  - 首次运行会在 `plugins` 文件里生成 `velocitychat` 文件夹下的 `config.toml` 配置文件，推荐使用 `Notepad++` 来编辑

  - 本插件 `config.toml` 配置文件说明

    ```toml
    # 主前缀
    main_prefix = "§8[§6testServerName§8]"
    # 在此处填写 MCDR 命令的前缀,如果没有使用 MCDR 开服的请保持默认，如果使用 MCDR 开服请根据实际情况填写
    mcdr_command_prefix = "!!"
    # 子服前缀
    [sub_prefix]
    lobby = "§8[§alobby§8]"
    ```

- 更新事项

  - 在每次升级中请将原来的 `config.toml` 重命名或者移动到其他文件夹，然后根据根据实际情况填写配置（懒）
  
- 更新日志

  - v1.0.1
    - 修复使用 MCDR 开服的服务端，MCDR 命令失效
  - v1.0.0
    - 添加根据配置文件，向聊天栏发送带有前缀的消息
