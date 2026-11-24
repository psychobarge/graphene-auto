<img src="docs/icon/icon.png" alt="" width="96" align="right">

# Graphene Auto

An Android Auto alternative for GrapheneOS, forked from
[Gearslip](https://github.com/twinksoftware/Gearslip).

![Vela Maps navigating on the car screen, with the music player beside the map](docs/screenshots/navigation.png)

Graphene Auto speaks the Android Auto protocol straight to your car, over USB or wireless,
with no Google software in the path. Map, music, and messaging apps draw their car screens
through it, and it adds a player, calls, voice, and a web browser of its own.

> [!WARNING]
> This is **alpha software**, built on an alpha upstream. It can drop the connection, freeze
> the car screen, or fail to start. Set it up while parked, and keep another way to navigate.

## How this fork differs from Gearslip

- **Focused on GrapheneOS.** It is built and tested for GrapheneOS first. Stock Android may
  work, but isn't the target.
- **No usage notes.** The opt-in usage statistics and all their code are gone. Nothing is
  sent to any statistics server.
- **No app recommendations.** It hosts whatever car-ready apps you install, and suggests none.
- **Updates come from this repository, and are off by default.** Turn on automatic checks in
  Settings if you want them.
- **Its own additions**, such as one-tap calls to favourite contacts, music through the app
  for cars that cut Bluetooth audio, fixes for the Renault R-Link 2, and a colour palette of
  its own. [CHANGELOG.md](CHANGELOG.md) lists them all, release by release.

## What works

| | |
|---|---|
| **Cars** | Most cars, over USB or wireless. Some cars from about 2020 on need a certificate of your own. [CARS.md](CARS.md) lists the cars Gearslip drivers have confirmed. |
| **Maps** | Organic Maps, Vela, OsmAnd, Waze, Flitsmeister, MAPS.ME, and MapQuest on the car screen, alerts included. Turns from Google Maps or Waze on the phone show in the nav bar and on the instrument cluster. |
| **Music** | Any app built for car screens, with Now Playing, Up Next, search, and lyrics. Spotify plays through the built-in player. |
| **Calls** | Dial or search contacts, and answer or decline on the car screen. Calls use the car's Bluetooth. |
| **Messages** | Read aloud on arrival, with a reply by voice or a quick reply. |
| **Voice** | "Navigate to the nearest gas station", "play Daft Punk", "call Mom". Speech stays on the phone. Commands are in English for now. |
| **Controls** | Touch, steering wheel buttons, control knobs, and D-pads. |
| **Head units with Android** | It can be the home screen of an Android head unit, or of a spare phone or tablet on the dash. Turn on Use as launcher in Settings > Connection. |
| **Doesn't work** | Spotify's and Google Messages' own car screens, which accept only Android Auto. TIDAL lets only approved apps browse its library. |

## See it

| | |
|---|---|
| ![The launcher, with every car app on the phone](docs/screenshots/launcher.png) | ![Metrolist's song, art, and controls in the player](docs/screenshots/music.png) |
| Your car apps in one place. | A music app in the built-in player. |
| ![A new message card over Vela Maps](docs/screenshots/message.png) | ![Synced lyrics beside the player](docs/screenshots/lyrics.png) |
| A new message, read aloud at a tap, over the map. | Lyrics light up word by word as the song plays. |
| ![The voice assistant listening over Vela Maps](docs/screenshots/assistant.png) | ![Three routes to a coffee shop in Vela Maps](docs/screenshots/places.png) |
| Ask for a place, a song, or an app. | Pick a route. Each one shows its time and distance. |
| ![DuckDuckGo open in the web browser](docs/screenshots/web.png) | |
| Browse the web while parked. | |

## Install

1. Install the APK from [the releases page](https://github.com/psychobarge/graphene-auto/releases).
   Uninstall Gearslip first if you have it: both define the same car permission, so Android
   refuses to install one while the other is there.
2. Open it. Setup asks where your Android Auto certificate comes from, then for one
   permission at a time, each with a plain reason.
3. Plug the phone into the car's USB port. In a car with wireless Android Auto, pair the
   phone with the car over Bluetooth, then tap Connect wirelessly and pick the car.

### Verify your download

Check that your copy is genuine with [AppVerifier](https://github.com/soupslurpr/AppVerifier),
or compare with the output of `apksigner verify --print-certs`:

```
io.github.psychobarge.grapheneauto
53:56:44:29:A7:26:13:63:83:0F:E7:39:42:1D:19:24:34:CE:D3:F8:2C:51:75:9C:FF:6B:E5:19:70:E8:DF:B9
```

### GrapheneOS notes

- **USB-C port control** must not be set to **Charging-only**. That setting blocks USB
  data, so the car never sees the phone, even with a data cable.
- No adb, root, or Shizuku is needed. Every feature relies on an ordinary Android
  permission or an on-device consent dialog.

### Waze on the car screen

Waze accepts only Android Auto as a host. Patch it with [Morphe](https://morphe.software) to
let Graphene Auto in:

1. In Morphe, add the patch source
   [waze-gearslip-morphe-patch](https://github.com/VoltoxderEchte/waze-gearslip-morphe-patch).
2. Patch Waze (Morphe can start from the copy already installed) and apply **Allow Gearslip**,
   plus **Fix speed drops** if your phone has no Google Play services.
3. Set **Accepted hosts** to this custom value, which allows Graphene Auto and Gearslip:
   ```
   53564429a7261363830fe739421d192434ced3f82c51759cff6be51970e8dfb9,io.github.psychobarge.grapheneauto;4cc56ebdd13a54d260eca9b7e2b2ae6b30e2f225bfda8f508f0709edf084948b,app.seb3thehacker.gearslip
   ```
4. Uninstall the Play Store copy, install the patched one, and turn off its auto-update in
   the Play Store.

**For privacy**, also add the
[bearinmindcat/morphe-patches](https://github.com/bearinmindcat/morphe-patches) source and
apply its Waze patches **Remove telemetry**, **Hide ads**, and **Remove permissions**. Mixing
two sources needs Morphe's expert mode, and can fail on some phones: if it does, patch in two
passes, bearinmindcat first, then Allow Gearslip on the result. Pick a Waze version both
sources support, and skip **Change app name** if it renames the package.

## Network access

The app goes online only for these:

- **Update checks**, against this repository's GitHub releases. Off unless you turn them on.
- **The Android Auto certificate**, if you choose to download the one the
  [aasdk](https://github.com/opencardev/aasdk) project publishes.
- **Voice models**, downloaded once from Hugging Face.
- **Weather**, from Open-Meteo, for the dashboard.
- **Lyrics**, from lrclib.net, Kugou, and lyrics-api.boidu.dev.
- **The web browser**, which loads the pages you open.

## Good to know

- **Music reaches the car over Bluetooth or through the app.** Setup asks which. Bluetooth
  is the default. Through the app, sound travels over the car connection and sounds a
  little clearer, but each drive Android asks you to share your screen, which is the only
  way an ordinary app can pass another app's audio along. The screen is never read.
- **Apps without a car screen** can still reach the car through a separate screen-sharing
  mode.
- **It works with [LIVI](https://github.com/f-io/LIVI),** the open-source head unit used in
  homemade car dashboards.

## Building

```
./gradlew :gearslip:assembleDebug
```

The APK lands at `build/GrapheneAuto-debug.apk`.

A release build needs a signing key. Copy `keystore.properties.example` to
`keystore.properties`, fill in your own values, and run
`./gradlew :gearslip:assembleRelease`. Git ignores both the properties file and the
keystore.

[docs/BUILDING_APPS.md](docs/BUILDING_APPS.md) specifies how to write apps it can render.

## Credits

Graphene Auto is a modified version of [Gearslip](https://github.com/twinksoftware/Gearslip)
by Twink Software LLC and contributors. Nearly all of the code is theirs. Please report
issues with this fork [here](https://github.com/psychobarge/graphene-auto/issues), not
upstream.

What it knows of the Android Auto protocol comes from public open-source projects such as
[aasdk](https://github.com/opencardev/aasdk) and open-android-auto, and it contains none
of Google's Android Auto code. It ships without a certificate.

## Trademarks

Android, Android Auto, Google Messages, and Waze are trademarks of Google LLC. Spotify is
a trademark of Spotify AB. Gearslip is a name of Twink Software LLC. This project is not
affiliated with, endorsed by, or sponsored by any of them.

## License

AGPL-3.0, see [LICENSE](LICENSE).
Copyright (C) 2026 Twink Software LLC and contributors.
Modifications copyright (C) 2026 psychobarge.
