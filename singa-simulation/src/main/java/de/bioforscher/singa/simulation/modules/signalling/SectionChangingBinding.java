package de.bioforscher.singa.simulation.modules.signalling;

import de.bioforscher.singa.chemistry.descriptive.entities.ChemicalEntity;
import de.bioforscher.singa.chemistry.descriptive.entities.ComplexedChemicalEntity;
import de.bioforscher.singa.simulation.model.compartments.CellSectionState;
import de.bioforscher.singa.simulation.modules.model.AbstractSectionSpecificModule;
import de.bioforscher.singa.simulation.modules.model.Simulation;

/**
 * @author cl
 */
public class SectionChangingBinding extends AbstractSectionSpecificModule {

    public static BindeeSelection inSimulation(Simulation simulation) {
        return new BindingBuilder(simulation);
    }

    private ChemicalEntity binder;
    private CellSectionState binderSection;
    private ChemicalEntity bindee;
    private CellSectionState bindeeSection;

    private ComplexedChemicalEntity complex;

    /**
     * Creates a new section independent module for the given simulation.
     *
     * @param simulation The simulation.
     */
    private SectionChangingBinding(Simulation simulation) {
        super(simulation);
    }

    private void initialize() {
        complex = new ComplexedChemicalEntity.Builder(binder.getIdentifier().getIdentifier()+"-"+bindee.getIdentifier().getIdentifier())
                .addAssociatedPart(binder)
                .addAssociatedPart(bindee)
                .build();
        // reference entities for this module
        addReferencedEntity(bindee);
        addReferencedEntity(binder);
        addReferencedEntity(complex);
        // reference module in simulation
        addModuleToSimulation();
    }

    public ComplexedChemicalEntity getComplex() {
        return complex;
    }

    public interface BindeeSelection {
        BindeeSectionSelection of (ChemicalEntity bindee);
    }

    public interface BindeeSectionSelection {
        BinderSelection in(CellSectionState bindeeSection);
    }

    public interface BinderSelection {
        BinderSectionSelection by(ChemicalEntity binder);
    }

    public interface BinderSectionSelection {
        SectionChangingBinding to(CellSectionState binderSection);
    }

    public static class BindingBuilder implements BinderSelection, BinderSectionSelection, BindeeSelection, BindeeSectionSelection {

        private SectionChangingBinding module;

        public BindingBuilder(Simulation simulation) {
            module = new SectionChangingBinding(simulation);
        }

        @Override
        public BindeeSectionSelection of(ChemicalEntity bindee) {
            module.bindee = bindee;
            return this;
        }

        @Override
        public BinderSelection in(CellSectionState binderSection) {
            module.binderSection = binderSection;
            return this;
        }

        @Override
        public BinderSectionSelection by(ChemicalEntity binder) {
            module.binder = binder;
            return this;
        }

        @Override
        public SectionChangingBinding to(CellSectionState bindeeSection) {
            module.binderSection = bindeeSection;
            module.initialize();
            return module;
        }



    }

}
