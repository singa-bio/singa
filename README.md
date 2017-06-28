<img src="singa_logo_text.png" height="125"/>

[![Build Status](https://travis-ci.org/cleberecht/singa.svg?branch=master)](https://travis-ci.org/cleberecht/singa)

SiNGA (**Si**mulation of **N**atural **S**ystems using **G**raph **A**utomata) is an open-source library containing
tools especially for structural bioinformatics and systems biology.

Many aspects are under development. API changes might be frequent, never the less we try to keep them minimal.

## Our tenets
* explicit is better than implicit
* good code is better than good documentation
* flat is better than nested
* readability is more important than performance

## Current structure
SiNGA is currently divided into six modules intended for different areas of application.

### Module: Core 
You probably won't need this package if, you a not planning to use other SiNGA modules. I contains multiple classes
and interfaces to provide a uniform basis for problem independent (if there is such a thing) programming.

##### Events
The *event emitter* and *event listener* interfaces can be used to implement an event-based pattern without having to
inherit classes.

##### Identifier
The *identifier* model can be used to make objects identifiable using a uniformly formatted sting. Currently 
implemented are several identifiers used across biological and chemical databases.

##### Utility
*Pair*s can be used to reliably return tow related objects from a method.
*Range* is a pair of bounded objects that can be used to check whether something lies inside or outside of this range.
A *label* is an object that identifies the location or position of something in the object that implements this interface.

### Module: Mathematics
[Get started](https://github.com/cleberecht/singa/wiki/Mathematics-(Package))

The mathematics package is designed for the convenient and uniform handling of everything mathematical.

##### Concepts
[Get started](https://github.com/cleberecht/singa/wiki/Concepts-(Mathematics))

SiNGA implement mathematical concepts such as *addition*, *subtraction* and *inversion* as interfaces to allow for
unrestricted implementation of those concepts for usage in *vectors*, *matrices*, *scalars*, and so on. Those
interfaces also provide default convenience methods, that are for instance able to sum over vectors or matrices.

##### Vectors
[Get started](https://github.com/cleberecht/singa/wiki/Vectors-(Mathematics))

Multidimensional vectors and convenience classes for Scalars, 2D and 3D are provided. Calculations are implemented as 
concepts. 

##### Matrices
Different matrix classes are defined to represent often used attributes (e.g. *square* and *symmetric matrices*). The
different types of matrices can freely be converted into each other. They also operate concept based and can be used in
combination with vectors.

##### Metrics
[Get started](https://github.com/cleberecht/singa/wiki/Metrics-(Mathematics))

The metrics package provides interfaces to implement different *metrics* that can be applied to *metrizable* objects.
Implementations are provided for *angular distance*, *cosine similarity*, *jaccard metric*, *minkowski metric* 
(e.g. euclidean and manhattan) for vectors, and the *shortest path metric* for graphs. The metric provider class
supplies static ready to use metrics.

##### Geometry
The implemented section of geometrical objects is not only intended for rendering but also for calculation. You are
able to retrieve points from parabolas or lines, calculate intersections and request certain attributes.

##### Graphs
Graphs are collections of objects that are connected by edges (as we are sure you all know). Nodes are modeled to hold
references of their neighbors and have a position specified by a vector. Some simple graph oriented calculations can
be done. Such as finding shortest paths

### Module: Chemistry
[Get started](https://github.com/cleberecht/singa/wiki/Chemistry-(Package))

The chemistry package is roughly divided into an descriptive and physical approach to represent chemical entities.

##### Elements
The elements package provides all elements with some often used attributes. It is also possible to quickly create
isotopes and ions from elements.

##### Parser
Chemical entities can be parsed from the ChEBI Database and imported from PubChem XML Files.

##### Physical
The physical representations of chemical entities approaches the problem from a graph theoretic side 
([see here](https://github.com/cleberecht/singa/wiki/Structure-model-(Chemistry))).
You are able to fetch structures from the pdb database 
([see here](https://github.com/cleberecht/singa/wiki/Structure-model-(Chemistry))), align structures and seach for
structural motifs ([see here](https://github.com/cleberecht/singa/wiki/Structure-Alignments-(Chemistry))).

### Module: Simulation
This module is currently combining an api to simulate different cellular processes using an approach similar to 
cellular automata; the eponymic graph automata, and an javafx implementations (application class BioGraphSimulation)
as a gui.

In a modular approach, a simulation is created (examples can be found in the SimulationExampleProvider class)
with a spatial and temporal component. A underlying graph automaton is specified, where each node is capable of
holding a certain concentration of species. Furthermore different phenomena (e.g. diffusion and chemical reactions)
can be applied to change those concentrations.

### Module: Features
The features package provides the possibility to automatically retrieve certain *Features* from databases. Additionally
[Units of Measurement](https://github.com/unitsofmeasurement) project is used to attach correct units to features if
necessary. Some systems biology specific units are defined and some Utilities are implemented.

### Module: Javafx
The javafx package contains a renderer based interface, that provides default implementations to draw the
geometric shapes and graphs implemented in the mathematics package. A very simple protein viewer is also available.
