/**
 * The simulations allows for the definition of models and simulations based on modules, that define cellular processes.
 * A spatial component can be defined using a graph like structure spanned over the simulation space. Each node of the
 * graph is responsible for the simulation of a subsection of the simulation space. Modules for reactions, different
 * types of diffusion, and transport processes are available. The SimulationManager can be used to conveniently write
 * trajectories of the simulation, define termination conditions.
 *
 * The next major update will bring major improvements and refactorings to this package.
 *
 * @author cl
 */
package bio.singa.simulation;