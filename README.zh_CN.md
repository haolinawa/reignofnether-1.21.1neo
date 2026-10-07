# 下界之治（Reign of Nether）— Minecraft 1.21.1 / NeoForge 移植版

这是 **[Reign of Nether](https://github.com/SoLegendary/reignofnether)** 到 **Minecraft 1.21.1 + NeoForge** 的非官方社区移植版。
原版 mod（Minecraft 1.20.1，Forge）作者是 **SoLegendary**，所有设计、美术资源与玩法都归功于他。

**[English README](README.md)**

---

## 简介

《下界之治》受到 2000 年代初期经典即时战略游戏（星际争霸、魔兽争霸、帝国时代）的启发，用原版 Minecraft 里已有的模型和素材，把 Minecraft 变成一款 RTS。

它并不刻意照搬这些游戏，而是追求"只有 Minecraft 才有的"设计：例如建筑血量与已放置的方块数量成正比，所有单位都基于原版生物——灾厄村民、苦力怕、猪灵等等。

## 下载

**最新发布：[beta-25 — 1.5.0-1.21.1-beta-25](https://github.com/haolinawa/reignofnether-1.21.1neo/releases/tag/beta-25)**（预发布 / 测试版）

| | |
|---|---|
| 移植版版本 | `1.5.0-1.21.1-beta-25` |
| 文件名 | `reignofnether-1.5.0-1.21.1-beta-25.jar` |
| 直接下载 | [reignofnether-1.5.0-1.21.1-beta-25.jar](https://github.com/haolinawa/reignofnether-1.21.1neo/releases/download/beta-25/reignofnether-1.5.0-1.21.1-beta-25.jar) |
| 全部发布 | https://github.com/haolinawa/reignofnether-1.21.1neo/releases |

每个版本都会单独发一个 Release，tag 与版本后缀一致（例如 `beta-25`）。

下载 jar 放进 `mods` 文件夹即可，也可以参考下面的"从源码构建"。

想玩**原版 1.20.1 Forge** 版本？请到作者那边下载：

- CurseForge: https://www.curseforge.com/minecraft/mc-mods/reign-of-nether-rts-in-minecraft
- Modrinth: https://modrinth.com/mod/reign-of-nether-rts

## 运行要求

- Minecraft **1.21.1**
- **NeoForge 21.1.x**（开发与测试基于 **21.1.255**）
- **Java 21** —— NeoForge 21.1 面向 Java 21；Java 17 或 25 不受支持，可能导致游戏异常甚至崩溃。

## 安装步骤

1. 安装 **Minecraft 1.21.1 对应的 NeoForge 21.1.x** —— https://neoforged.net/
2. 确认启动器使用的是 **Java 21**。
3. 把 `reignofnether-1.5.0-1.21.1-beta-25.jar` 放进 `mods` 文件夹：
   - Windows：`%appdata%\.minecraft\mods`
   - Linux / macOS：`~/.minecraft/mods`
4. 用 NeoForge 1.21.1 配置启动游戏。

该 mod 客户端与服务端通用，同一个 jar 两边都能用。

## 从源码构建

```bash
git clone https://github.com/haolinawa/reignofnether-1.21.1neo.git
cd reignofnether-1.21.1neo
./gradlew build          # Windows 用 gradlew.bat build
```

- 需要 **JDK 21**。
- 产物：`build/libs/reignofnether-1.5.0-1.21.1-beta-25.jar`
- 产物文件名取自 `gradle.properties` 里的 `mod_version`；**每次构建都把 `-beta-N` 递增**，这样每个发布版本都能区分。
- `./gradlew runClient` 启动开发客户端，`./gradlew runServer` 启动开发服务端。

## 移植说明

- 从 1.20.1 Forge 源码移植而来，玩法力求与原版一致。
- 原版 Forge 的"重置 / 握手"机制（不重启游戏重置注册表）**尚未**重新实现。
- `LEGENDARY` / `MYTHIC` 两种品质目前显示为 `EPIC`：1.21.1 移除了 Forge 可扩展的 `Rarity` 枚举，NeoForge 的枚举扩展还没有接入。
- 世界内血条只在实体**受伤**时显示，与原版行为一致。

## 鸣谢

- **SoLegendary** —— 原版《下界之治》mod、设计与美术资源。
- **haolinawa** —— 1.21.1 / NeoForge 移植与维护。
- **DeepSeek Harness (deepseek-flash)** —— 协助完成移植并修复运行时崩溃。

## 许可证

GNU General Public License v3.0 —— 见 [LICENSE.txt](LICENSE.txt)。
本移植版是原项目的衍生作品，因此沿用同一许可证。

## 链接

- 原版 mod 仓库：https://github.com/SoLegendary/reignofnether
- 原版 mod Discord：https://discord.com/jJV5zK3hT9
- **本移植版**的问题反馈：https://github.com/haolinawa/reignofnether-1.21.1neo/issues

---
