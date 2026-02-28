# RBMK Reactor Mod (Forge 1.20.1)

This mod is now a **much larger RBMK gameplay system** with reactor infrastructure, panel controls, disasters, and progressive radiation damage.

## Major systems now included

- **Core reactor + structure pieces**
  - Reactor Core
  - Fuel Channel blocks
  - Control Rod Columns
  - Graphite Casings
- **Plant support systems**
  - Water Pumps (cooling support)
  - Steam Turbines (consume steam output)
- **Control/UI**
  - Master Control Console opens an in-game GUI panel
  - Mode cycling, rod/coolant tuning, SCRAM button
  - Live telemetry readout (heat, pressure, flux, dose)
- **Radiation**
  - Local fallout fields + severe fallout
  - Chunk contamination accumulation and decay
  - Cumulative player dose tracking
  - Multi-stage effects: Radiation Sickness, Radiation Burn, Acute Radiation Syndrome
- **Disasters**
  - Channel ruptures
  - Catastrophic meltdown chains with secondary blasts/fires
  - Long-lived fallout spread after incidents

## Quick build setup

1. Open in IntelliJ IDEA as a Gradle project.
2. Use Java 17.
3. Run `runClient`.

## GitHub import / push

```bash
git init
git add .
git commit -m "Initial RBMK reactor mega mod"
git branch -M main
git remote add origin https://github.com/<your-user>/<your-repo>.git
git push -u origin main
```

If this directory is already a git repo, just commit and push to your existing remote.

## Build tips for your video

- Build dense fuel/control-rod arrays around the core.
- Attach multiple water pumps for cooling margin.
- Attach turbine banks to drain steam pressure.
- Use the control panel to drive from startup to grid load and trigger failure scenarios intentionally.
