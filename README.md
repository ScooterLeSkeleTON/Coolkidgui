# RBMK Reactor Mod (Forge 1.20.1)

A much more advanced, cinematic RBMK-inspired reactor mod scaffold with disasters, fallout, and operator controls.

## What is included now

- IntelliJ/Gradle-ready Forge project (`src/main/java`, `src/main/resources`)
- Reactor architecture blocks:
  - `rbmkreactor:reactor_core`
  - `rbmkreactor:control_console`
  - `rbmkreactor:graphite_casing`
- Advanced reactor simulation:
  - positive void coefficient
  - xenon + iodine pit dynamics
  - fuel burnup, structural integrity, containment damage
  - graphite fire escalation
  - mode-based operation (startup, ascent, grid load, emergency shutdown)
- Disaster simulation:
  - pressure ruptures
  - catastrophic blast chain with secondary hotspots
  - persistent fallout seeding
- Radiation simulation:
  - per-zone fallout decay
  - chunk contamination map
  - dose-based effect amplification
- Equipment:
  - Geiger counter item that reports local exposure

## Controls

- Place a `reactor_core` block.
- Place a `control_console` near it (within 12 blocks).
- Right-click console to cycle operation profile:
  - `STANDBY` -> `STARTUP` -> `POWER_ASCENT` -> `GRID_LOAD` -> `EMERGENCY_SHUTDOWN` -> `DISASTER`.

## Run in IntelliJ IDEA

1. Open the folder.
2. Import as Gradle project.
3. Use Java 17 SDK.
4. Run Gradle task: `runClient`.

## Upload/import this to GitHub

From this project directory:

```bash
git init
git add .
git commit -m "Initial RBMK mod"
git branch -M main
git remote add origin https://github.com/<your-user>/<your-repo>.git
git push -u origin main
```

If this repo is already initialized, skip `git init` and just set/add the remote + push.

## Notes

- Binary textures were removed to keep this repo text-only friendly.
- Models currently reuse vanilla Minecraft textures; replace with custom assets later for your final video art pass.
