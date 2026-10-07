# Reign of Nether — Minecraft 1.21.1 / NeoForge port

Unofficial community port of **[Reign of Nether](https://github.com/SoLegendary/reignofnether)** to **Minecraft 1.21.1** with **NeoForge**.
The original mod (Minecraft 1.20.1, Forge) is made by **SoLegendary** — all design, assets and gameplay credit goes to them.

**[中文说明 / Chinese README](README.zh_CN.md)**

---

## About

Inspired by the classic Real Time Strategy games of the early 2000s such as StarCraft, Warcraft and Age of Empires, Reign of Nether turns Minecraft into an RTS using the same assets and models you already find in the vanilla game.

It does not try to imitate those games exactly; instead it aims for uniquely-Minecraft features. Building health is proportional to the blocks placed, and every unit is based on a vanilla mob — Illagers, Creepers, Piglins and more.

## Downloads

**Latest release: [beta-26 — 1.5.0-1.21.1-beta-26](https://github.com/haolinawa/reignofnether-1.21.1neo/releases/tag/beta-26)** (pre-release / test build)

| | |
|---|---|
| Port version | `1.5.0-1.21.1-beta-26` |
| File | `reignofnether-1.5.0-1.21.1-beta-26.jar` |
| Direct download | [reignofnether-1.5.0-1.21.1-beta-26.jar](https://github.com/haolinawa/reignofnether-1.21.1neo/releases/download/beta-26/reignofnether-1.5.0-1.21.1-beta-26.jar) |
| All releases | https://github.com/haolinawa/reignofnether-1.21.1neo/releases |

Every port version is published as its own release, tagged with its version suffix (e.g. `beta-26`).

Download the jar and drop it into your `mods` folder. You can also build it from source (see below).

Looking for the **original 1.20.1 Forge** version? Get it from the author:

- CurseForge: https://www.curseforge.com/minecraft/mc-mods/reign-of-nether-rts-in-minecraft
- Modrinth: https://modrinth.com/mod/reign-of-nether-rts

## Requirements

- Minecraft **1.21.1**
- **NeoForge 21.1.x** (developed and tested against **21.1.255**)
- **Java 21** — NeoForge 21.1 targets Java 21. Java 17 or 25 is not supported and can break the game.

## Installation

1. Install **NeoForge 21.1.x for Minecraft 1.21.1** — https://neoforged.net/
2. Make sure your launcher is set to use **Java 21**.
3. Put `reignofnether-1.5.0-1.21.1-beta-26.jar` into your `mods` folder:
   - `%appdata%\.minecraft\mods` on Windows
   - `~/.minecraft/mods` on Linux / macOS
4. Launch Minecraft with the NeoForge 1.21.1 profile.

The mod works on both the client and a dedicated server; the same jar is used for both.

## Building from source

```bash
git clone https://github.com/haolinawa/reignofnether-1.21.1neo.git
cd reignofnether-1.21.1neo
./gradlew build          # Windows: gradlew.bat build
```

- Requires **JDK 21**.
- Output: `build/libs/reignofnether-1.5.0-1.21.1-beta-26.jar`
- The artifact name comes from `mod_version` in `gradle.properties`; **increment the `-beta-N` suffix for every new build** so releases stay distinguishable.
- `./gradlew runClient` launches a development client, `./gradlew runServer` a development server.

## Port notes

- Ported from the 1.20.1 Forge sources; gameplay is intended to match the original mod.
- The original Forge "reset / handshake" mechanism (resetting registries without restarting the game) is **not** re-implemented yet.
- The `LEGENDARY` and `MYTHIC` item rarities currently display as `EPIC`: 1.21.1 removed Forge's extensible `Rarity` enum and the NeoForge enum extension is not wired up yet.
- In-world health bars are only shown for damaged entities, matching the original mod.

## Credits

- **SoLegendary** — original Reign of Nether mod, design and assets.
- **haolinawa** — 1.21.1 / NeoForge port and maintenance.
- **DeepSeek Harness (deepseek-flash)** — assisted with the port and with fixing the runtime crashes.

## License

GNU General Public License v3.0 — see [LICENSE.txt](LICENSE.txt).
This port is a derivative work of the original project, so the same license applies.

## Links

- Original mod repository: https://github.com/SoLegendary/reignofnether
- Original mod Discord: https://discord.com/jJV5zK3hT9
- Issues / bug reports for **this port**: https://github.com/haolinawa/reignofnether-1.21.1neo/issues

---
