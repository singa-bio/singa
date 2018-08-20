package bio.singa.simulation.model.modules.concentration;

import bio.singa.simulation.model.simulation.Simulation;

/**
 * @author cl
 */
public interface ModuleBuilder<ModuleType extends ConcentrationBasedModule> {

    ModuleType createModule(Simulation simulation);

    ModuleType getModule();

    ModuleType build();

}
