/**
 * The features module allows to annotate additional information to objects. Features can be automatically resolved by
 * writing FeatureProviders, Features are also recursively resolved if all necessary FeatureProviders are available.
 * Multiple ways to resolve a single Feature can be implemented using priority groups.
 *
 * Identifiers for databases are implemented as features und can be resolved from databases to access other databases.
 * Additionally, identifiers can be checked for validity and retrieved from a list of string using their inherent
 * pattern.
 *
 * Quantities and Units for are specified with the units of measurements library. Commonly used units and natural
 * constants are deposited here als well.
 *
 * @author cl
 */
package bio.singa.features;