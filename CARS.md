# Cars

This list comes from the upstream [Gearslip](https://github.com/twinksoftware/Gearslip) project:
its drivers' usage notes and its own testing. Graphene Auto shares the same car protocol code,
so the same cars should work. A car shows up here once Gearslip reached its screen at least once.

The year is the one the car reports. Some cars report the year of the head unit instead,
and some don't say.

## Works

| Car | Year | Head unit |
|---|---|---|
| Audi | | MIB2+ (Alpine) |
| Audi | | Harman |
| Chevrolet, with myChevrolet | 2019, 2020, 2021 | myChevrolet |
| Chevrolet, with MyLink | 2017 | MyLink |
| Dacia | | Media Nav (LG ULC 4.1) |
| Dacia | | LG ULC 4.5 |
| Ford | 2018 | SYNC 3.0 (Panasonic) |
| Ford Fiesta | 2019 | SYNC 3.2 (Panasonic) |
| Ford Fusion | 2018 | SYNC 3.1 (Panasonic) |
| Jeep Cherokee | 2019 | Uconnect 8.4 (Delphi) |
| MG | 2019 | Desay SV |
| Ram 1500 | 2018 | Uconnect 8.4 (Panasonic) |
| Renault Clio V | 2019-2024 | Easy Link |
| Škoda | 2018 | MIB Standard2 (TechniSat) |
| Subaru Outback | 2020 | Denso |
| Toyota RAV4 | 2020 | Pioneer T1 |
| Volkswagen | 2018 | MIB Standard2 (Delphi) |
| Volkswagen | 2024 | ICAS3 |
| Volvo XC90 | 2022 | SPA Plus (Mitsubishi Electric) |
| Wuling | 2021 | Desay SV |
| [LIVI](https://github.com/f-io/LIVI), the open-source head unit | | |

## Not working yet

| Car | Year | What happens |
|---|---|---|
| Honda | 2016 | The connection drops before Graphene Auto reaches the screen. |
| Honda | 2019 | The connection drops before Graphene Auto reaches the screen. |
| Mitsubishi Attrage | 2020 | The connection drops before Graphene Auto reaches the screen. |

Some cars reject Graphene Auto's certificate before they say what they are, so they can't appear
here by name. Most of these are from around 2020 or later.

## Add your car

Graphene Auto sends no usage notes. If your car works, is missing, or is listed wrongly, open an
issue with its make, model, year, and a log from the drive.
