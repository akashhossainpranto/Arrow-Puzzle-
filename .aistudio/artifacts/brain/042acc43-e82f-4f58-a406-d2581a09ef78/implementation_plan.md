# Tap Away Arrows — Exact Flat-3D Game UI Transformation

This plan transforms the entire visual interface and gameplay flows of the app to match the exact aesthetic, layouts, typography, and component styling depicted in the user's flat-3D game UI design sheet (`a_clean_flat_3d_styled_game_ui_asset_sheet_on_a_t_29cd9062.png`), while ensuring 100% full, real game functionality with no dummy or placeholder data.

---

### User Review & Critical Decisions

> [!IMPORTANT]
> **Confirmed Choices from User Feedback:**
> - **Game Title**: **Tap Away Arrows** with 3D cartoon bubble typography, dual-layer drop bevels, and directional arrow accents.
> - **Economy & Currency**: Functional Gold Coin system (`★ 150`). Players start with 150 coins, earn coins for completing levels (scaled by star rating), and spend coins on hints or lives.
> - **Exact UI Components**: Custom flat-3D glossy buttons (thick bottom bevel for tactile press depth), puffy glossy red hearts, blue circular navigation buttons, and high-fidelity modals.
> - **Full Real Functionality**: Every button, modal, timer, moves counter, level select tile, and setting is fully interactive with working business logic.

---

## 1. Overview & Core Concept

**Goal**: Deliver a 1:1 realization of the user's reference design sheet. The app features:
1. **Title / Splash Screen**: "Tap Away Arrows" 3D logo, live interactive maze preview card, chunky green 3D "PLAY" button, and circular utility buttons (Settings, Stats, Shop).
2. **Gameplay Screen**:
   - Top Bar: Blue circular Home button, Level indicator, 3 glossy 3D puffy hearts, difficulty pill, blue circular Settings button.
   - Header Stats: Real-time moves counter (with paper airplane icon) and live game timer (`mm:ss`).
   - Center Maze: Elevated card with filleted rounded arrow tracks, glowing hint selection, and instant touch departure.
   - Bottom Bar: 3 large circular blue glossy buttons: **Restart**, **Hint** (with red notification badge), and **Grid** (toggling background dots).
3. **Modals & Overlays**:
   - **Level Complete**: Golden Crown banner, confetti, stats (Moves + Time), green "Next Level" button, Home, Replay, and Share buttons.
   - **Need a Hint?**: Glowing lightbulb topper, board preview with highlighted move, "Watch Ad / Spend 100 Coins" CTA, and "No, Thanks".
   - **Paused**: Resume (green), Restart (blue), Home (blue), and Exit (red).
   - **No Lives!**: 3 broken hearts, "Watch a video to get more lives!", "+3 Lives" reward button.
   - **Settings**: Sound, Music, and Vibration toggles, plus Language selector.
   - **Select Difficulty**: Easy (green), Normal (blue check), Hard (orange), and Expert (red).
4. **Level Select**: 4x5 grid of numbered 3D beveled tiles with star ratings, locked padlocks, and coin counter `[★ 150 +]`.

---

## 2. User Experience & Visual Design

### 1. Flat-3D Glossy Design System (`GameComponents.kt`)
- **Glossy 3D Button Style**:
  - Top face: vibrant gradient (e.g. bright green `#22C55E` to `#16A34A`).
  - Bottom bevel: deep contrast rim (`#15803D`) creating physical depth.
  - White specular reflection highlight on the upper hemisphere.
- **Puffy 3D Hearts**:
  - Custom vector drawing of glossy red hearts with specular white sheen; turns dim slate when lost.
- **Circular Blue Utility Buttons**:
  - Deep royal blue with cyan upper rim and white crisp icons (Home, Settings, Stats, Shop, Restart, Hint, Grid, Share).
- **Gold Coin Pill**:
  - Golden star medallion badge `[★] 150 [+]` tracking real coins.

### 2. Full Modal Architecture
All modals feature the signature dark blue arched header panel with floating 3D emblems (Crown for Win, Lightbulb for Hint, Pause icon for Pause).

---

## 3. Key Technical Decisions & Data Strategy

- **Coin Economy (`GamePreferences.kt`)**:
  - Persist `coinsCount: Int` (starts at 150).
  - Level win reward: +50 coins for 3 stars, +35 for 2 stars, +25 for 1 star.
  - Hint cost: 100 coins (or free ad recharge).
- **Gameplay Timer (`GameViewModel.kt`)**:
  - Active coroutine timer recording elapsed seconds during gameplay, formatted as `mm:ss` for the HUD and Level Complete screen.
- **Grid Toggle (`isGridVisible`)**:
  - Toggle ambient dot grid on the puzzle board via the bottom "Grid" button.

---

## 4. Technical Architecture & File Changes

```
┌─────────────────────────────────────────────────────────────┐
│                    Tap Away Arrows UI                       │
│  ┌───────────────────────┐       ┌───────────────────────┐  │
│  │     HomeScreen        │       │    LevelSelectScreen  │  │
│  │  - 3D Logo & Play     │       │  - 3D Numbered Tiles  │  │
│  │  - Circular Utility   │       │  - Coin Pill [★ 150]  │  │
│  └───────────┬───────────┘       └───────────┬───────────┘  │
│              │                               │              │
│              └───────────────┬───────────────┘              │
│                              ▼                              │
│  ┌───────────────────────────────────────────────────────┐  │
│  │                    GamePlayScreen                     │  │
│  │  - Top Bar: Home, Level, Puffy Hearts, Diff, Settings │  │
│  │  - Stats Bar: Moves (Airplane), Timer (mm:ss)         │  │
│  │  - Board: Smooth Bézier Fillets, 0ms Instant Touch    │  │
│  │  - Bottom: Circular Restart, Hint (Badge), Grid (#)   │  │
│  │  - Modals: Win (Crown), Hint, Pause, No Lives, Diff   │  │
│  └───────────────────────────┬───────────────────────────┘  │
└──────────────────────────────┼──────────────────────────────┘
                               │
                               ▼
┌─────────────────────────────────────────────────────────────┐
│              GameComponents.kt (Design System)              │
│   - Button3D: Flat-3D glossy green, blue, red, gold buttons  │
│   - CircularButton3D: Floating circular blue/white buttons   │
│   - PuffyHeart: 3D glossy red heart with specular shine     │
│   - CoinPill: Golden coin counter with [+] action            │
└──────────────────────────────┬──────────────────────────────┘
                               │
                               ▼
┌─────────────────────────────────────────────────────────────┐
│              GameViewModel.kt & GamePreferences             │
│   - Coin balance management (earn on win, spend on hints)   │
│   - Live timer (elapsedSeconds)                             │
│   - Difficulty selection & grid visibility toggle           │
└─────────────────────────────────────────────────────────────┘
```

---

## 5. Verification Plan
- **Visual Alignment**: Verify every screen matches the exact layout, colors, and button placement of the design sheet.
- **Coin & Hint Economy**: Test earning coins on level completion and spending coins/watching ads for hints.
- **Gameplay Integrity**: Verify timer, moves counter, 0ms touch speed, 150 levels, and all modal transitions.
- **Compilation & Test Suite**: Run `compile_applet` and `gradle :app:testDebugUnitTest`.
