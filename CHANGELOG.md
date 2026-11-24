# Changelog

Graphene Auto's own versions, newest first. Like Gearslip, it is alpha software. Its version
numbers are its own, not Gearslip's; each release says which Gearslip release it brings in.

## 0.3.0-alpha (2026-11-24)

### Added
- A Colours setting next to App theme: Material You colours from the wallpaper (the default),
  or Graphene Auto's own palette. It applies to the phone and the car screen.
- A new icon of its own, a graphene hexagon, replacing Gearslip's gear. The "Works with" mark
  uses it too.

### Changed
- Home's cards share one style: a failure shows as a red dot and a warning as an amber sign.
  Debug builds tag their version in yellow instead of colouring the whole header.

### Fixed
- A music app that can't play until its screen has been opened once (Metrolist) gets opened
  on the phone after its first failure, and Play is pressed again.

## 0.2.0 (2026-11-21)

Brings in Gearslip 0.4.0-alpha.

### Added
- Call someone in one tap from the car: add up to 8 contacts under Car › Apps on the phone, and
  each gets a tile in the Phone group of the car's app menu, which can be pinned to the nav bar.
- Cars that turn Bluetooth music off while Android Auto runs get their music through Graphene
  Auto, remembered per car.
- A call rings on the car, with its answer button, even when another app holds the
  call-screening role; a mute set on the phone's call screen shows on the car.
- From Gearslip: a car web browser with ad blocking, a map behind place lists, any app as a
  music app or messenger, one navigation card with lanes, recent calls, hourly weather, and the
  car audio choice under Settings.

### Fixed
- A car that presses Play right after connecting (Renault R-Link 2) no longer ends the session.
- The audio capture consent is asked while Graphene Auto is in front, so the drive isn't silent.
- Music comes back on the car speakers when it resumes after the car took them (radio).
- A ringing call can be declined from the car even after call screening.
- The session stops when the car ends it, and the car's touches are handled in order.
- Hiding an app also unpins it from the nav bar; SimpMusic's dead Browse tile is gone.
- From Gearslip: the phone's Bluetooth address is asked for on the phone and sent as soon as
  the car connects; screen sharing and audio capture stop when the car leaves; music reaches
  cars that acknowledge audio slowly; a car that hangs up during setup gets a shorter request
  next time; video restarts when the car changes its screen layout.

### Not taken from Gearslip
- The usage notes and their privacy policy, Weblate links, and the version read from git tags.
- 32-bit ARM and x86_64 builds: Graphene Auto stays arm64 only.

## 0.1.0 (2026-11-06)

The fork: Graphene Auto starts from [Gearslip](https://github.com/twinksoftware/Gearslip)
0.3.40-alpha.

### Changed
- Renamed to Graphene Auto, with its own package name, `io.github.psychobarge.grapheneauto`.
  It keeps none of Gearslip's settings. Both apps define the same car permission, so Android
  installs only one of them at a time: uninstall Gearslip first.
- The voice assistant answers to "hey Graphene Auto" and "quit Graphene Auto".
- Update checks look at this repository's releases, and are off by default.
- Links to the source code, release notes, and car list point to this repository.

### Removed
- Usage notes: the opt-in statistics, their setup page, their Settings section, and the
  "it worked with your car" card. Nothing is sent to a statistics server.
- Donation links and the donation prompt.
- Links to Gearslip's Matrix room and Weblate translations.
- The contributor license agreement and the commercial license notice, which belong to
  Twink Software LLC.
