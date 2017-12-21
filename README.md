<img src="singa_logo_text.png" height="125"/>

[![Build Status](https://travis-ci.org/cleberecht/singa.svg?branch=master)](https://travis-ci.org/cleberecht/singa)

SiNGA (**Si**mulation of **N**atural Systems using **G**raph **A**utomata) is an open-source library containing tools especially for structural bioinformatics and systems biology.

Many aspects of the API are under development. API changes are frequent, never the less we try to keep them minimal.

## Quick start
SiNGA is deployed to the [Maven Central Repository](https://mvnrepository.com/artifact/de.bioforscher.singa). Simply add the desired modules to your ```pom.xml```:

```xml
<dependencies>
    <dependency>
        <groupId>de.bioforscher.singa</groupId>
        <artifactId>singa-all</artifactId>
        <version>0.3.0</version>
    </dependency>
    <!-- more dependencies -->
</dependencies>
```

## Current structure
SiNGA is currently divided into six modules:

### Module: Mathematics

The mathematics package focuses on graphs, linear algebra and geometry.

##### Graphs
[Get started](https://github.com/cleberecht/singa/wiki/Graphs-(Mathematics))

In general a Graph is a data structure that contains objects in which some or all of the objects are in some way related to each other. The objects in this structure are called nodes and the relationships between them are referred to as edges. Graphs can be visualized using the *JavaFX* module.

##### Concepts
[Get started](https://github.com/cleberecht/singa/wiki/Concepts-(Mathematics))

SiNGA implement mathematical concepts such as *addition*, *subtraction* and *inversion* as interfaces to allow for unrestricted implementation of those concepts for usage in *vectors*, *matrices*, *scalars*, and so on. Those interfaces also provide default convenience methods.

##### Vectors
[Get started](https://github.com/cleberecht/singa/wiki/Vectors-(Mathematics))

Multidimensional vectors and convenience classes for scalars, 2D and 3D are provided. Calculations are implemented as concepts.

##### Metrics
[Get started](https://github.com/cleberecht/singa/wiki/Metrics-(Mathematics))

The metrics package provides interfaces to implement different *metrics* that can be applied to *metrizable* objects. Using default methods it is possible to create distance matrices and search for closet objects in collections of metrizable objects.

##### Matrices
Different classes handle different matrix forms such as *square* and *symmetric matrices*. They operate concept based and can be used in combination with vectors. Labeled matrices can be used to quickly retrieve values from matrices using labels. Different matrix decompositions are available such as *SVD* and *QRD*.

##### Geometry
The implemented section of geometrical objects is not only intended for rendering but also for calculation. You are able to retrieve points from parabolas or lines, calculate intersections and request attributes.

### Module: Chemistry

##### Elements
The elements package provides all elements including detailed information. It is possible to create isotopes and ions from elements.

##### Databases
Database access and parsers are available for the *PDB*, *UniProt*, *ChEBI*, *UniChem*, and *PubChem* databases.

##### Physical
The physical representations of chemical entities is approached a graph view point ([see here](https://github.com/cleberecht/singa/wiki/Structure-model-(Chemistry))). You are able to fetch structures from the pdb database ([see here](https://github.com/cleberecht/singa/wiki/Structure-parsing-(Chemistry))), align structures and search for structural motifs ([see here](https://github.com/cleberecht/singa/wiki/Structure-Alignments-(Chemistry))).

### Module: Simulation
This module is providing a possibility to simulate different cellular processes using an approach similar to cellular automata: the eponymic graph automata. In a modular approach, a simulation is created with a spatial and temporal component. Entities in the automaton are subject to forces exerted by predefined phenomena such as diffusion, membrane transport or chemical reactions. 

### Module: Features
The features package provides the possibility to automatically retrieve *Features* of chemical entities from databases. Additionally the [Units of Measurement](https://github.com/unitsofmeasurement) project is used to attach correct units to features if necessary.

### Module: Javafx
The javafx package contains renderer based interfaces, that provide default implementations to draw the geometric shapes and graphs implemented in the mathematics package. A very simple protein structure viewer is also available.

### Module: Core 
You probably won't need this package if, you a not planning to use other SiNGA modules. I contains multiple classes and interfaces to provide a uniform basis for problem independent (if there is such a thing) programming.

##### Events
The *event emitter* and *event listener* interfaces can be used to implement an event-based pattern without having to inherit classes.

##### Identifier
The *identifier* model can be used to make objects identifiable using a uniformly formatted sting. The identifier checks itself against a predefined static pattern and throws an exception when the identifier is incorrectly formatted. Currently several identifiers used across biological and chemical databases are implemented.

##### Utility
*Pair*s can be used to handle two entwined objects.
*Range* is a pair of bounded objects that can be used to check whether something lies inside or outside of this range.
A *label* is an object that identifies the location or position of something in the object that implements this interface.