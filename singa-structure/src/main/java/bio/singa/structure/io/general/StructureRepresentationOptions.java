package bio.singa.structure.io.general;

import bio.singa.structure.model.general.AuthLeafIdentifier;

import java.util.HashMap;
import java.util.Map;

public class StructureRepresentationOptions {

    private boolean renumberAtoms = false;
    private boolean renumberSubstructures = false;
    private boolean renumberChains = false;

    private boolean addRemark80 = false;
    private boolean addConnections = true;
    /**
     * Happening when 5-character ligands from mmCIF are renamed for compatibility.
     */
    private boolean addRenamedLigands = true;

    private Map<AuthLeafIdentifier, Integer> renumberingMap;

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

    public Map<AuthLeafIdentifier, Integer> getRenumberingMap() {
        return renumberingMap;
    }

    public void setRenumberingMap(Map<AuthLeafIdentifier, Integer> renumberingMap) {
        this.renumberingMap = renumberingMap;
    }

    public boolean isAddRemark80() {
        return addRemark80;
    }

    public void setAddRemark80(boolean addRemark80) {
        this.addRemark80 = addRemark80;
    }

    public boolean isAddConnections() {
        return addConnections;
    }

    public void setAddConnections(boolean addConnections) {
        this.addConnections = addConnections;
    }

    public boolean isAddRenamedLigands() {
        return addRenamedLigands;
    }

    public void setAddRenamedLigands(boolean addRenamedLigands) {
        this.addRenamedLigands = addRenamedLigands;
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
            case APPEND_REMARK_80:
                options.addRemark80 = true;
                break;
            case OMIT_REMARK_80:
                options.addRemark80 = false;
                break;
            case APPEND_ALL_LIGAND_CONNECTIONS:
                options.addConnections = true;
                break;
            case OMIT_ALL_LIGAND_CONNECTIONS:
                options.addConnections = false;
                break;
            case APPEND_RENAMED_LIGANDS:
                options.addRenamedLigands = true;
                break;
            case OMIT_RENAMED_LIGANDS:
                options.addRenamedLigands = false;
                break;
        }
    }

    public enum Setting {

        RENUMBER_ATOMS_CONSECUTIVELY,

        RETAIN_ATOM_NUMBERING,

        RENUMBER_SUBSTRUCTURES,

        RETAIN_SUBSTRUCTURE_NUMBERING,

        RENUMBER_CHAINS_CONSECUTIVELY,

        RETAIN_CHAIN_NUMBERING,

        APPEND_REMARK_80,

        OMIT_REMARK_80,

        APPEND_ALL_LIGAND_CONNECTIONS,

        OMIT_ALL_LIGAND_CONNECTIONS,

        /**
         * Write a dedicated REMARK 950 record if a 5-character ligand was renamed to "LIG". This record will hold the
         * original ligand identifier.
         */
        APPEND_RENAMED_LIGANDS,

        /**
         * Always suppress the REMARK 950 record.
         */
        OMIT_RENAMED_LIGANDS,
    }

}
