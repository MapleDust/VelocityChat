# VelocityChat

一个我的世界Velocity群组跨服聊天的插件，不需要向子服安装额外的插件

- 使用说明

  - 将从`Releases`上下载的插件放入Velocity端根目录的`plugins`文件夹里

  - 首次运行会在`plugins`文件里生成`velocitychat`文件夹下的`config.toml`配置文件，推荐使用`Notepad++`来编辑

  - 本插件`config.toml`配置文件说明

    ```toml
    #主前缀
    mainprefix = "§8[§6testServerName§8]"
    #子服前缀,注意lobby一定是你在velocity里配置的子服名称，否则导致不生效甚至消息发不出去
    [subprefix]
    lobby = "§8[§alobby§8]"
    ```

- 更新日志

  - v1.0.0
    - 添加根据配置文件，向聊天栏发送带有前缀的消息
