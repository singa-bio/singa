<img src="singa_logo_text.png" height="125"/>

[![Build Status](https://travis-ci.org/singa-bio/singa.svg?branch=master)](https://travis-ci.org/singa-bio/singa)
![License: GPL v3](https://img.shields.io/badge/License-GPL%20v3-blue.svg)
[![MMTF support](https://img.shields.io/badge/MMTF-supported-blue.svg)](https://mmtf.rcsb.org/)
[![DOI](https://zenodo.org/badge/DOI/10.5281/zenodo.1320146.svg)](https://doi.org/10.5281/zenodo.1320146)

SiNGA (**Si**mulation of **N**atural Systems using **G**raph **A**utomata) is an open-source library containing tools especially for structural bioinformatics and systems biology.

Many aspects of the API are under development. API changes are frequent, never the less we try to keep them minimal.

## Quick start
SiNGA is deployed to the [Maven Central Repository](https://mvnrepository.com/artifact/bio.singa). Simply add the desired modules to your ```pom.xml```:

```xml
<dependencies>
    <dependency>
        <groupId>bio.singa</groupId>
        <artifactId>singa-[module]</artifactId>
        <version>0.5.0</version>
    </dependency>
    <!-- more dependencies -->
</dependencies>
```
## Documentation
The full JavaDocs are available at [cleberecht.github.io/singa-doc](https://cleberecht.github.io/singa-doc).

## Requirements
Make sure you have the following tools and libraries installed:
- Java 8 or later

For _p_-value calculation for Fit3D: 
- R installation 3.4.x or later
- local package installation privileges or the `sfsmisc` package pre-installed

## Contributors
 - Christoph Leberecht | christoph.leberecht(at)hs-mittweida.de | https://github.com/cleberecht
 - Florian Kaiser | contact(at)fkaiser.bio | https://github.com/fkaiserbio

## Projects using SiNGA
 - Fit3D - template-based detection of small structural motifs | https://github.com/fkaiserbio/fit3d
 - MMM - template-free detection of substructure conservation | https://github.com/fkaiserbio/mmm
 - PNAtor - generation of PNA aptamer structures | https://github.com/eisold/PNAtor
