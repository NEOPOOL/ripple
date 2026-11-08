# Ripple 🌊
### Anxiety Relief, Somatic Breathing & Sensory Grounding Companion
*Published by **Vayu Labs** (`com.vayulabs.ripple`) • Contact: `meetvayu@gmail.com`  
Built for Android Phones, Wear OS Smartwatches, and architected for future iOS support.*

---

## 🌟 Overview

**Ripple** is an offline-first, low-cognitive-load application built to interrupt panic attacks, sensory overload, and chronic anxiety. It adheres to a **Minimalist Abstract Aesthetic** (soothing gradients, ambient orbs, dark obsidian backgrounds) to avoid sensory fatigue.

Just as ripples on water naturally soften and settle into glassy stillness, Ripple guides the nervous system back to baseline through evidence-based somatic breathing and tactile anchors.

### Core Modules

1. **One-Tap SOS Quick Ground**:
   - Immediate access to the evidence-based **Physiological Sigh** (2 quick inhales + 1 long exhale) to instantly decrease autonomic nervous system arousal.
   - Paced by an organic glowing, morphing orb with synchronized haptics.
2. **Breathing Rhythms**:
   - Physiological Sigh (fastest neurobiological reset)
   - Box Breathing (4-4-4-4)
   - 4-7-8 Deep Calm (pre-sleep parasympathetic sedative)
   - Resonant Coherence (5.5s - 5.5s heart rate variability optimization)
3. **Sensory Distraction Playground**:
   - **Liquid Silk**: Interactive fluid velocity simulation where gentle touch swirls pastel silk currents.
   - **Tactile Stone**: A digital worry stone that emits dynamic micro-vibrations based on thumb stroke velocity.
   - **Bubble Grid**: Translucent pastel bubbles with snappy tactile clicks that automatically regenerate.
   - **Mandala Drift**: 6-fold rotational symmetrical kaleidoscope that slowly dissolves lines after drawing.
   - **Gradient Flow**: Low-stress cognitive spectrum sorting to gently redirect active attention.
4. **Anchoring Truths & Affirmations**:
   - Categorized by emotional state (*Panic & Overwhelm*, *Self-Compassion*, *Release & Acceptance*, *Quiet Strength*).
   - Bookmarkable personal cards and daily home-screen anchor.
5. **Acoustic Shielding (Ambient Soundscapes)**:
   - Deep Brown Noise, Skylight Rain, Ocean Tide, Forest Breeze, and 432 Hz Drone.
   - Multi-track volume mixer and sleep timer.
6. **Thought Dissolver ("Burn After Writing")**:
   - Distraction-free rumination input that dissolves text into fading dust particles on demand.
7. **Wear OS Smartwatch Companion**:
   - Standalone circular watch app with silent wrist-guided haptic breathing.
   - **Wear OS Quick Tile**: 1-swipe, 1-tap start directly from the watch carousel.

---

## 🏗️ Architecture: Multiplatform Ready

The project uses a clean multi-module architecture prepared with **Kotlin Multiplatform (KMP)**:

```
pilot/
├── shared/            # KMP Core (domain models, breathing state machine, quote library)
├── androidApp/        # Android Phone & Tablet app (Jetpack Compose, Material 3, Canvas, Media3)
├── wearApp/           # Wear OS Smartwatch app (Wear Compose, TileService, Wrist Haptics)
├── spec.md            # Detailed Product & Architecture Specification
└── gradle/            # Version catalog (libs.versions.toml) and Gradle wrapper
```

### Future iOS Expansion
Because all business logic, timer flows, and domain models reside in `shared/commonMain`, the future iOS app will directly consume this shared engine using either SwiftUI or Compose Multiplatform for iOS.

---

## 🚀 Opening in Android Studio

1. **Launch Android Studio**.
2. Select **Open** and choose this project folder:  
   `c:\Users\Admin\Documents\Projects\pilot`
3. Android Studio will automatically recognize the Gradle project and trigger a Gradle Sync.
4. Choose your run configuration in the toolbar:
   - Run `androidApp` on an Android Phone / Emulator.
   - Run `wearApp` on a Wear OS Watch / Emulator.

---

## 🔒 Privacy & Safety Notice
CalmHaven is completely offline-first. Personal reflections and thoughts entered into the Dissolver are never transmitted over a network or stored on disk. CalmHaven is a self-regulation wellness tool and is not a substitute for clinical psychiatric care. A direct 24/7 crisis resource directory is provided in the app.
