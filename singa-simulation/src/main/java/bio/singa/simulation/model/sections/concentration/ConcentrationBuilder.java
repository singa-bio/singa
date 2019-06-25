package bio.singa.simulation.model.sections.concentration;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.chemistry.entities.SmallMolecule;
import bio.singa.features.model.Evidence;
import bio.singa.features.quantities.MolarConcentration;
import bio.singa.features.units.UnitRegistry;
import bio.singa.simulation.model.sections.CellRegion;
import bio.singa.simulation.model.sections.CellRegions;
import bio.singa.simulation.model.sections.CellSubsection;
import tech.units.indriya.quantity.Quantities;

import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.quantity.Area;
import java.util.Arrays;
import java.util.List;

import static bio.singa.features.units.UnitProvider.*;
import static bio.singa.simulation.model.sections.CellSubsections.CYTOPLASM;
import static bio.singa.simulation.model.sections.CellSubsections.VESICLE_LUMEN;
import static tech.units.indriya.unit.MetricPrefix.MICRO;
import static tech.units.indriya.unit.Units.METRE;

/**
 * @author cl
 */
public class ConcentrationBuilder {

    public static EntityStep regular() {
        return new SectionConcentrationBuilder();
    }

    public interface EntityStep {

        RegionStep entity(ChemicalEntity entity);

    }

    public interface RegionStep {

        ConcentrationStep subsection(CellSubsection subsection);

        SubsectionStep region(CellRegion region);

    }

    public interface SubsectionStep {

        ConcentrationStep subsection(CellSubsection subsection);

    }

    public interface ConcentrationStep {

        BuildStep concentration(Quantity<MolarConcentration> concentration);

        BuildStep molecules(double numberOfMolecules);

        UnitStep concentrationValue(double value);

    }

    public interface UnitStep {

        BuildStep unit(Unit<MolarConcentration> concentrationUnit);

        default BuildStep milliMolar() {
            return unit(MILLI_MOLE_PER_LITRE);
        }

        default BuildStep microMolar() {
            return unit(MICRO_MOLE_PER_LITRE);
        }

        default BuildStep nanoMolar() {
            return unit(NANO_MOLE_PER_LITRE);
        }

    }


    public interface BuildStep {

        BuildStep evidence(Evidence evidence);

        InitialConcentration build();

    }

    public static class SectionConcentrationBuilder implements EntityStep, RegionStep, SubsectionStep, ConcentrationStep, UnitStep, BuildStep {

        private double concentrationValue;

        private final SectionConcentration sectionConcentration;

        public SectionConcentrationBuilder() {
            sectionConcentration = new SectionConcentration();
        }

        @Override
        public RegionStep entity(ChemicalEntity entity) {
            sectionConcentration.setEntity(entity);
            return this;
        }

        @Override
        public ConcentrationStep subsection(CellSubsection subsection) {
            sectionConcentration.setSubsection(subsection);
            return this;
        }

        @Override
        public SubsectionStep region(CellRegion region) {
            sectionConcentration.setRegion(region);
            return this;
        }

        @Override
        public BuildStep concentration(Quantity<MolarConcentration> concentration) {
            sectionConcentration.setConcentration(concentration);
            return this;
        }

        @Override
        public BuildStep molecules(double numberOfMolecules) {
            double concentration = MolarConcentration.moleculesToConcentration(numberOfMolecules);
            return concentration(UnitRegistry.concentration(concentration));
        }

        @Override
        public UnitStep concentrationValue(double value) {
            concentrationValue = value;
            return this;
        }

        @Override
        public BuildStep unit(Unit<MolarConcentration> concentrationUnit) {
            return concentration(UnitRegistry.concentration(concentrationValue, concentrationUnit));
        }

        @Override
        public BuildStep evidence(Evidence evidence) {
            sectionConcentration.setEvidence(evidence);
            return this;
        }

        @Override
        public InitialConcentration build() {
            return sectionConcentration;
        }

    }

    public static NodeIdentifierStep fixed() {
        return new FixedConcentrationBuilder();
    }

    public interface FixedEntityStep {

        SubsectionStep entity(ChemicalEntity entity);

    }

    public interface NodeIdentifierStep {

        FixedEntityStep nodeIdentifiers(List<String> identifiers);

        FixedEntityStep nodeIdentifiers(String... identifiers);

    }

    public static class FixedConcentrationBuilder implements NodeIdentifierStep, FixedEntityStep, SubsectionStep, ConcentrationStep, UnitStep, BuildStep {

        private double concentrationValue;

        private final FixedConcentration fixedConcentration;

        public FixedConcentrationBuilder() {
            fixedConcentration = new FixedConcentration();
        }

        @Override
        public SubsectionStep entity(ChemicalEntity entity) {
            fixedConcentration.setEntity(entity);
            return this;
        }

        @Override
        public ConcentrationStep subsection(CellSubsection subsection) {
            fixedConcentration.setSubsection(subsection);
            return this;
        }

        @Override
        public BuildStep concentration(Quantity<MolarConcentration> concentration) {
            fixedConcentration.setConcentration(concentration);
            return this;
        }

        @Override
        public BuildStep molecules(double numberOfMolecules) {
            double concentration = MolarConcentration.moleculesToConcentration(numberOfMolecules);
            return concentration(UnitRegistry.concentration(concentration));
        }

        @Override
        public UnitStep concentrationValue(double value) {
            concentrationValue = value;
            return this;
        }

        @Override
        public BuildStep unit(Unit<MolarConcentration> concentrationUnit) {
            return concentration(UnitRegistry.concentration(concentrationValue, concentrationUnit));
        }

        @Override
        public BuildStep evidence(Evidence evidence) {
            fixedConcentration.setEvidence(evidence);
            return this;
        }

        @Override
        public FixedEntityStep nodeIdentifiers(List<String> identifiers) {
            fixedConcentration.setIdentifiers(identifiers);
            return this;
        }

        @Override
        public FixedEntityStep nodeIdentifiers(String... identifiers) {
            return nodeIdentifiers(Arrays.asList(identifiers));
        }

        @Override
        public InitialConcentration build() {
            return fixedConcentration;
        }

    }

    public static MembraneEntityStep membrane() {
        return new MembraneConcentrationBuilder();
    }

    public interface MembraneEntityStep {
        MembraneRegionStep entity(ChemicalEntity entity);
    }

    public interface MembraneRegionStep {
        MoleculeStep region(CellRegion region);
    }

    public interface MoleculeStep {
        AreaStep molecules(double molecules);
    }

    public interface AreaStep {
        BuildStep area(Quantity<Area> area);
    }

    public static class MembraneConcentrationBuilder implements MembraneEntityStep, MembraneRegionStep, MoleculeStep, AreaStep, BuildStep {

        private final MembraneConcentration membraneConcentration;

        public MembraneConcentrationBuilder() {
            membraneConcentration = new MembraneConcentration();
        }

        @Override
        public MembraneRegionStep entity(ChemicalEntity entity) {
            membraneConcentration.setEntity(entity);
            return this;
        }

        @Override
        public MoleculeStep region(CellRegion region) {
            if (!region.hasMembrane()) {
                throw new IllegalArgumentException("The cell region for membrane concentrations must contain any membrane subsection.");
            }
            membraneConcentration.setRegion(region);
            return this;
        }

        @Override
        public AreaStep molecules(double molecules) {
            membraneConcentration.setNumberOfMolecules(molecules);
            return this;
        }

        @Override
        public BuildStep area(Quantity<Area> area) {
            membraneConcentration.setArea(area);
            return this;
        }

        @Override
        public BuildStep evidence(Evidence evidence) {
            membraneConcentration.setEvidence(evidence);
            return this;
        }

        @Override
        public InitialConcentration build() {
            return membraneConcentration;
        }

    }

    public static void main(String[] args) {

        SmallMolecule entity = SmallMolecule.create("001").build();

        InitialConcentration concentration01 = ConcentrationBuilder.regular()
                .entity(entity)
                .region(CellRegions.EXTRACELLULAR_REGION)
                .subsection(VESICLE_LUMEN)
                .concentrationValue(10)
                .microMolar()
                .build();

        System.out.println(concentration01);

        InitialConcentration concentration02 = ConcentrationBuilder.regular()
                .entity(entity)
                .subsection(CYTOPLASM)
                .concentrationValue(1)
                .nanoMolar()
                .build();

        System.out.println(concentration02);

        InitialConcentration concentration03 = ConcentrationBuilder.regular()
                .entity(entity)
                .subsection(CYTOPLASM)
                .molecules(200)
                .build();

        System.out.println(concentration03);

        InitialConcentration concentration04 = ConcentrationBuilder.fixed()
                .nodeIdentifiers("n(9,1)")
                .entity(entity)
                .subsection(CYTOPLASM)
                .concentrationValue(100)
                .milliMolar()
                .build();

        System.out.println(concentration04);

        InitialConcentration concentration05 = ConcentrationBuilder.membrane()
                .entity(entity)
                .region(CellRegions.CELL_OUTER_MEMBRANE_REGION)
                .molecules(100)
                .area(Quantities.getQuantity(10, MICRO(METRE).pow(2).asType(Area.class)))
                .build();

        System.out.println(concentration05);

    }


}
