package bio.singa.structure.io.general;

import bio.singa.structure.model.pdb.PdbLeafIdentifier;

import java.util.HashMap;
import java.util.Map;

public class StructureRepresentationOptions {

    private boolean renumberAtoms = false;
    private boolean renumberSubstructures = false;
    private boolean renumberChains = false;

    private Map<PdbLeafIdentifier, Integer> renumberingMap;

    private StructureRepresentationOptions() {
        renumberingMap = new HashMap<>();
    }

    public static StructureRepresentationOptions defaultSettings() {
        return new StructureRepresentationOptions();
    }

    /**
     * Create a new Options object using enum constants.
     *
     * @param settings The settings.
     * @return The options.
     */
    public static StructureRepresentationOptions withSettings(StructureRepresentationOptions.Setting... settings) {
        StructureRepresentationOptions options = new StructureRepresentationOptions();
        for (StructureRepresentationOptions.Setting setting : settings) {
            setOption(options, setting);
        }
        return options;
    }

    public void applySettings(StructureRepresentationOptions.Setting... settings) {
        for (StructureRepresentationOptions.Setting setting : settings) {
            setOption(this, setting);
        }
    }

    public boolean isRenumberingAtoms() {
        return renumberAtoms;
    }

    public void setRenumberingAtoms(boolean renumberAtoms) {
        this.renumberAtoms = renumberAtoms;
    }

    public boolean isRenumberingSubstructures() {
        return renumberSubstructures;
    }

    public void setRenumberingSubstructures(boolean renumberSubstructures) {
        this.renumberSubstructures = renumberSubstructures;
    }

    public boolean isRenumberChains() {
        return renumberChains;
    }

    public void setRenumberChains(boolean renumberChains) {
        this.renumberChains = renumberChains;
    }

    public Map<PdbLeafIdentifier, Integer> getRenumberingMap() {
        return renumberingMap;
    }

    public void setRenumberingMap(Map<PdbLeafIdentifier, Integer> renumberingMap) {
        this.renumberingMap = renumberingMap;
    }

    /**
     * Sets the any option.
     *
     * @param options The options object to set.
     * @param setting The settings.
     */
    private static void setOption(StructureRepresentationOptions options, StructureRepresentationOptions.Setting setting) {
        switch (setting) {
            case RENUMBER_ATOMS_CONSECUTIVELY:
                options.renumberAtoms = true;
                break;
            case RETAIN_ATOM_NUMBERING:
                options.renumberAtoms = false;
                break;
            case RENUMBER_SUBSTRUCTURES:
                options.renumberSubstructures = true;
                break;
            case RETAIN_SUBSTRUCTURE_NUMBERING:
                options.renumberSubstructures = false;
                break;
            case RENUMBER_CHAINS_CONSECUTIVELY:
                options.renumberChains = true;
                break;
            case RETAIN_CHAIN_NUMBERING:
                options.renumberChains = false;
                break;
        }
    }

    public enum Setting {

        RENUMBER_ATOMS_CONSECUTIVELY,

        RETAIN_ATOM_NUMBERING,

        RENUMBER_SUBSTRUCTURES,

        RETAIN_SUBSTRUCTURE_NUMBERING,

        RENUMBER_CHAINS_CONSECUTIVELY,

        RETAIN_CHAIN_NUMBERING;

    }

}
