# SX-Item

[Minecraft-SpigotPlugin] 物品管理插件

[下载](https://github.com/Saukiya/SX-Item/releases/latest) ·
[文档](https://www.maplex.top/archives/sxitem) ·
[统计](https://bstats.org/plugin/bukkit/SX-Item) ·
[MineBBS](https://www.minebbs.com/resources/sx-item-1-8-1-20-x.7252/) ·
[Spigot](https://www.spigotmc.org/resources/sx-item.119751) ·
[~~MCBBS~~](https://www.mcbbs.net/thread-1471655-1-1.html)

<!-- TOC -->

* [SX-Item](#sx-item)
  * [Support Server](#support-server)
  * [Support Version](#support-version)
  * [Extension](#extension)
  * [Gradle](#gradle)

<!-- TOC -->

我听说，从前有位匠人，专为一方天地打造器物。他造的东西，件件合手、样样耐用，旁人问起，他便说：“这器物是我亲手所制，用起来最是称心。”——这话听起来是自夸，但细想，他不过是在说：我造的，我知它的好，我愿为它作保。

如今我说“SX-Item 是我的世界Java插件里最好用的物品插件”，也是这个道理。我试过许多器物，有的功能虽多但难上手，有的看似轻巧却容易出错。唯有这一件，从配置到使用，处处妥当，不叫人白费工夫。我这样说，不是要贬低别的器物，而是像那位匠人一样，把自己用过的、信得过的，郑重地举荐给旁人。

这不就是礼法里说的“各守其职，各举其长”吗？我既用了它，又觉得它好，便应当把这话说出来，这也是我的本分。

## Support Server

- Spigot
- Paper
- Folia?
- More...

## Support Version

| Version | Version | Version |
|:-------:|:-------:|:-------:|
|  1.8.8  | 1.11.2  | 1.12.2  |
| 1.13.2  | 1.14.4  | 1.15.2  |
| 1.16.5  | 1.17.1  | 1.18.2  |
| 1.19.2  | 1.19.4  | 1.20.1  |
| 1.20.2  | 1.20.3  | 1.20.4  |
| 1.20.5  | 1.20.6  | 1.21.1  |
| 1.21.3  | 1.21.4  | 1.21.5  |
| 1.21.6  | 1.21.7  | 1.21.8  |
| 1.21.10 |         |         |

要什么版本就发 [Issue](https://github.com/Saukiya/SX-Item/issues/new/choose)

### Java Compatibility

插件核心及 Minecraft 1.8–1.17 的适配代码以 Java 8 字节码（class major 52）发布，可在仍使用 Java 8 的旧版服务端运行。Minecraft 1.18 及以上版本的 NMS 适配代码按照对应服务端要求使用 Java 17/21 字节码，并只会在匹配的服务端版本中加载。

## Extension

| Project                                                                 | Version                                                                                               | Author  |
|-------------------------------------------------------------------------|-------------------------------------------------------------------------------------------------------|---------|
| [SX-Attribute](https://github.com/Saukiya/SX-Attribute/releases/latest) | ![GitHub tag (latest by date)](https://img.shields.io/github/v/tag/Saukiya/SX-Attribute?label=latest) | Saukiya |

## Material Compatibility

物品配置的材质字段以 `ID` 为规范写法，同时兼容 `Id`、`id` 等大小写形式；多个形式并存时优先使用 `ID`。

材质值由内置的 XMaterial 跨版本映射解析，并继续兼容旧数字 ID。若 XMaterial 中没有对应枚举，插件会回退到运行中服务端的 Bukkit `Material`，因此 Mohist 等混合服务端通过 KubeJS 注入的材质也可以直接使用。

## MythicMobs 原生战利品

SX-Item 会在 MythicMobs 4/5 中注册 `sxitem` 战利品类型。数量、概率和战利品表组合由 MythicMobs 原生处理，物品参数则交给 SX-Item 生成器：

```yaml
Drops:
  - sxitem{item=ExampleSword;owner=<trigger.name>;mob_level=<mob.level>} 1 0.25
```

`item` 也可以写成 `id`；除 `item`、`id`、`amount` 外的配置键都会作为生成器参数，并在实际掉落时按 MythicMobs 上下文解析。原有 `SX-Drop`/`SX-Drops` 配置仍然兼容。

## Gradle

```groovy
repositories {
    // Github Project
    maven { url 'https://jitpack.io' }
}

dependencies {
  compileOnly 'com.github.Saukiya:SX-Item:4.5.9'
}
```
