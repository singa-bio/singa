package bio.singa.simulation.model.modules.concentration;

import bio.singa.simulation.model.modules.concentration.scope.DependentUpdate;
import bio.singa.simulation.model.modules.concentration.scope.IndependentUpdate;
import bio.singa.simulation.model.modules.concentration.scope.SemiDependentUpdate;
import bio.singa.simulation.model.modules.concentration.scope.UpdateScope;
import bio.singa.simulation.model.modules.concentration.specifity.EntitySpecific;
import bio.singa.simulation.model.modules.concentration.specifity.SectionSpecific;
import bio.singa.simulation.model.modules.concentration.specifity.UpdatableSpecific;
import bio.singa.simulation.model.modules.concentration.specifity.UpdateSpecificity;

import java.util.Objects;

/**
 * Handles module creation with certain {@link UpdateScope} and {@link UpdateSpecificity}.
 *
 * @author cl
 */
public class ModuleFactory {

    /**
     * The type of {@link UpdateScope}.
     */
    public enum Scope {
        /**
         * References {@link DependentUpdate}.
         */
        NEIGHBOURHOOD_DEPENDENT,

        /**
         * References {@link IndependentUpdate}.
         */
        NEIGHBOURHOOD_INDEPENDENT,

        /**
         * References {@link SemiDependentUpdate}.
         */
        SEMI_NEIGHBOURHOOD_DEPENDENT
    }

    /**
     * The type of {@link UpdateSpecificity}.
     */
    public enum Specificity {
        /**
         * References {@link EntitySpecific}.
         */
        ENTITY_SPECIFIC,

        /**
         * References {@link SectionSpecific}.
         */
        SECTION_SPECIFIC,

        /**
         * References {@link UpdatableSpecific}.
         */
        UPDATABLE_SPECIFIC
    }

    /**
     * Creates a new module with the specified setup.
     *
     * @param moduleCLass The implementation of the module.
     * @param scope The scope of the module.
     * @param specificity The specificity of the module.
     * @param <ModuleImplementation> The type of the implementation.
     * @return The instantiated module.
     */
    public static <ModuleImplementation extends ConcentrationBasedModule> ModuleImplementation setupModule(Class<ModuleImplementation> moduleCLass, Scope scope, Specificity specificity) {
        // instantiate new module
        ModuleImplementation module = null;
        try {
            module = moduleCLass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        Objects.requireNonNull(module);
        // define scope
        UpdateScope updateScope = null;
        switch (scope) {
            case NEIGHBOURHOOD_DEPENDENT:
                updateScope = new DependentUpdate(module);
                break;
            case NEIGHBOURHOOD_INDEPENDENT:
                updateScope = new IndependentUpdate(module);
                break;
            case SEMI_NEIGHBOURHOOD_DEPENDENT:
                updateScope = new SemiDependentUpdate(module);
                break;
        }
        module.setScope(updateScope);
        // define specificity
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
