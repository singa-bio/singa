# SiNGA
SiNGA (**Si**mulation of **N**atural **S**ystems using **G**raph **A**utomata) desires to be a open-source project to model and simulate natural systems. We try to create a lightweight, explicit, and clear framework to tackle problems occurring in life sciences.

Everything is under constant development, currently many sections are unfinished and only conditionally stable. We invite you to test the code and issue problems and complaints.

## Our tenets
* explicit is better than implicit
* good code is better than good documentation
* flat is better than nested
* readability is more important than performance
* have a minimal number of external dependencies

## Current structure
SiNGA is currently divided into six modules intended for different areas of application. The sections listed in the following part are less likely to change, so some of the packages you might want to use are:

### Module: Core 
You probably won't need this package if, you a not planning to use other SiNGA modules. I contains multiple classes and interfaces to provide a uniform basis for problem independent (if there is such a thing) programming. 

##### Events
The *event emitter* and *event listener* interfaces can be used to implement an event-based pattern without having to inherit classes.

##### Identifier
The *identifier* model can be used to make objects identifiable using a uniformly formatted sting. Currently implemented are classes to take care of ChEBI, PubChem, EC and UniProt Identifier.

##### Utility
*Pairs* can be used to reliably return tow related objects from a method.  
*Range* is a pair of bounded objects that can be used to check whether something lies inside or outside of this range.  
A *label* is an object that identifies the location or position of something in the object that implements this interface.

### Mathematics
The mathematics package is designed for the convenient and uniform handling of everything mathematical.

##### Concepts
The most important interfaces of this package. We try to implement mathematical concepts such as *addition*, *subtraction* and *inversion* as interfaces to allow for unrestricted implementation of those concepts for usage in *vectors*, *matrices*, *scalars*, and so on. Those interfaces also provide default convenience methods, that are for instance able to sum over vectors or matrices.

##### Vectors
Multidimensional vectors and convenience classes for Scalars(1D), 2D and 3D are provided. Calculations are implemented as concepts. 

##### Matrices
Different matrix classes are defined to represent often used attributes (e.g. *square* and *symmetric matrices*). The different classes can freely be converted into each other. They also operate concept based and can be used in combination with vectors.

##### Metrics
The metrics package provides interfaces to implement different *metrics* that can be applied to *metrizable* objects. Implementations are provided for *angular distance*, *cosine similarity*, *jaccard metric*, *minkowski metric* (e.g. euclidean and manhattan) for vectors, and the *shortest path metric* for graphs. The metric provider supplies static ready to use metrics.

##### Geometry
The implemented section of geometrical objects is not only intended for rendering but also for calculation. You are able to retrieve points from parabolas or lines, calculate intersections and request certain attributes.

##### Graphs
Graphs are collections of objects that are connected by edges (as we a sure you all know). Nodes are modeled to "know" their neighbors and have a position, that can or cannot be assigned. Some simple graph oriented calculations can be done.

### Chemistry

### Simulation

### Units

### Javafx
