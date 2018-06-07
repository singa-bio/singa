package de.bioforscher.singa.simulation.modules.newmodules.module;

import de.bioforscher.singa.simulation.modules.newmodules.scope.DependentUpdate;
import de.bioforscher.singa.simulation.modules.newmodules.scope.IndependentUpdate;
import de.bioforscher.singa.simulation.modules.newmodules.scope.SemiDependentUpdatable;
import de.bioforscher.singa.simulation.modules.newmodules.scope.UpdateScope;
import de.bioforscher.singa.simulation.modules.newmodules.specifity.EntitySpecific;
import de.bioforscher.singa.simulation.modules.newmodules.specifity.SectionSpecific;
import de.bioforscher.singa.simulation.modules.newmodules.specifity.UpdatableSpecific;
import de.bioforscher.singa.simulation.modules.newmodules.specifity.UpdateSpecificity;

import java.util.Objects;

/**
 * @author cl
 */
public class ModuleFactory {

    public enum Scope {
        NEIGHBOURHOOD_DEPENDENT, NEIGHBOURHOOD_INDEPENDENT, SEMI_NEIGHBOURHOOD_DEPENDENT
    }

    public enum Specificity {
        ENTITY_SPECIFIC, SECTION_SPECIFIC, UPDATABLE_SPECIFIC
    }

    public static <ModuleImplementation extends ConcentrationBasedModule> ModuleImplementation setupModule(Class<ModuleImplementation> moduleCLass, Scope scope, Specificity specificity) {
        ModuleImplementation module = null;
        try {
            module = moduleCLass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        Objects.requireNonNull(module);
        // scope
        UpdateScope updateScope = null;
        switch (scope) {
            case NEIGHBOURHOOD_DEPENDENT:
                updateScope = new DependentUpdate(module);
                break;
            case NEIGHBOURHOOD_INDEPENDENT:
                updateScope = new IndependentUpdate(module);
                break;
            case SEMI_NEIGHBOURHOOD_DEPENDENT:
                updateScope = new SemiDependentUpdatable(module);
                break;
        }
        module.setScope(updateScope);
        // specificity
        UpdateSpecificity updateSpecificity = null;
        switch (specificity) {
            case ENTITY_SPECIFIC:
                updateSpecificity = new EntitySpecific(module);
                break;
            case SECTION_SPECIFIC:
                updateSpecificity = new SectionSpecific(module);
                break;
            case UPDATABLE_SPECIFIC:
                updateSpecificity = new UpdatableSpecific(module);
                break;
        }
        module.setSpecificity(updateSpecificity);
        return module;
    }

}
