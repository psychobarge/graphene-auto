# Changelog

## Fork from Gearslip

Graphene Auto starts from [Gearslip](https://github.com/twinksoftware/Gearslip) 0.3.40-alpha.

### Changed
- Renamed to Graphene Auto, with its own package name, `io.github.psychobarge.grapheneauto`.
  It installs next to Gearslip rather than over it, and keeps none of Gearslip's settings.
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
