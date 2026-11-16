# Changelog

## Unreleased

### Added
- Call someone in one tap from the car: add up to 8 contacts under Car › Apps on the phone, and
  each gets a tile in the Phone group of the car's app menu, which can be pinned to the nav bar.

## Fork from Gearslip

Graphene Auto starts from [Gearslip](https://github.com/twinksoftware/Gearslip) 0.3.40-alpha.

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
