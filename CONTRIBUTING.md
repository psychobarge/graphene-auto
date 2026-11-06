# Contributing to Graphene Auto

Bug reports from real cars help most.

## Reporting a bug

Open an issue on [GitHub](https://github.com/psychobarge/graphene-auto/issues) and include:

- your car's make, model, and year, and whether the head unit is the original one;
- your phone and GrapheneOS version; and
- the log from the drive. Open Logs on the phone app's home screen and tap Share log.

## Building

You need Android Studio, or the Android SDK with JDK 21.

```
./gradlew :gearslip:assembleDebug
```

The APK lands at `build/GrapheneAuto-debug.apk`. It installs as Graphene Auto Debug, next to
the regular app.

## Pull requests

- Keep each pull request to one change, and explain the problem it solves.
- Say how you tested it: which car or head unit, which phone, and which apps.
- A feature must work with ordinary Android permissions and on-device consent dialogs: no
  adb, root, or Shizuku.

## License

Graphene Auto is licensed under AGPL-3.0, and your contributions are too.
