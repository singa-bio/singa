package de.bioforscher.singa.chemistry.parser.pdb.structures;

/**
 * Options for faster or more detailed parsing.
 *
 * @author cl
 */
public class StructureParserOptions {

    /**
     * Creates edges in the graph.
     */
    private boolean createEdges = true;

    /**
     * Retrieves additional information from CIF files.
     */
    private boolean retrieveLigandInformation = true;

    /**
     * Tries to saturate the residue with hydrogen atoms if they are in the given map of atoms.
     */
    private boolean connectHydrogens = false;

    /**
     * Omits all hydrogen (and eventually deuterium) atoms. Those atoms are not added to the resulting residue.
     */
    private boolean omitHydrogens = false;

    /**
     * Uses the filename as the title of the parsed structure.
     */
    private boolean inferTitleFromFileName = false;

    /**
     * Tries to find a pdbid in the file name and uses it as the identifier of the structure.
     */
    private boolean inferIdentifierFromFileName = false;

    /**
     * Setting this option true will connect the molecule with bonds. Turning the option {@code false} will result in
     * faster parsing, if those information are not strictly required.<br><br>
     * Default value: {@code true}
     *
     * @return The value of this option.
     */
    public boolean isCreatingEdges() {
        return this.createEdges;
    }

    /**
     * Setting this option true will connect the molecule with bonds. Turning the option {@code false} will result in
     * faster parsing, if those information are not strictly required.<br><br>
     * Default value: {@code true}
     *
     * @param createEdges {@code true} to turn this option on, {@code false} to turn it off.
     */
    public void createEdges(boolean createEdges) {
        this.createEdges = createEdges;
    }

    /**
     * Setting this option to {@code true} will retrieve additional information from cif files from the PDB. This
     * enables the detection modified amino acids and nucleotides als well as the retrieval of additional information
     * for ligands and the connection of ligand atoms. Turning the option {@code false} will result in faster parsing,
     * if those information are not strictly required.<br><br>
     * Default value: {@code true}
     *
     * @return The value of this option.
     */
    public boolean isRetrievingLigandInformation() {
        return this.retrieveLigandInformation;
    }

    /**
     * Setting this option to {@code true} will retrieve additional information from cif files from the PDB. This
     * enables the detection modified amino acids and nucleotides als well as the retrieval of additional information
     * for ligands and the connection of ligand atoms. Turning the option {@code false} will result in faster parsing,
     * if those information are not strictly required.<br><br>
     * Default value: {@code true}
     *
     * @param retrieveLigandInformation {@code true} to turn this option on, {@code false} to turn it off.
     */
    public void retrieveLigandInformation(boolean retrieveLigandInformation) {
        this.retrieveLigandInformation = retrieveLigandInformation;
    }

    /**
     * Setting this option to {@code true} will try to connect hydrogen atom tho their respective partners.<br><br>
     * Default value: {@code false}
     *
     * @return The value of this option.
     */
    public boolean isConnectingHydrogens() {
        return this.connectHydrogens;
    }

    /**
     * Setting this option to {@code true} will try to connect hydrogen atom tho their respective partners.<br><br>
     * Default value: {@code false}
     *
     * @param connectHydrogens {@code true} to turn this option on, {@code false} to turn it off.
     */
    public void connectHydrogens(boolean connectHydrogens) {
        this.connectHydrogens = connectHydrogens;
    }

    /**
     * Setting this option to {@code true} will omit all hydrogen (and eventually deuterium) atoms in amino acids and
     * nucleotides.
     * Default value: {@code false}
     *
     * @return {@code true} to turn this option on, {@code false} to turn it off.
     */
    public boolean isOmittingHydrogen() {
        return this.omitHydrogens;
    }

    /**
     * Setting this option to {@code true} will omit all hydrogen (and eventually deuterium) atoms in amino acids and
     * nucleotides.
     * Default value: {@code false}
     *
     * @param omitHydrogens {@code true} to turn this option on, {@code false} to turn it off.
     */
    public void omitHydrogens(boolean omitHydrogens) {
        this.omitHydrogens = omitHydrogens;
    }

    /**
     * Setting this option to {@code true} will set the title of the structure to the file name.
     * Default value: {@code false}
     *
     * @return {@code true} to turn this option on, {@code false} to turn it off.
     */
    public boolean isInferringTitleFromFileName() {
        return this.inferTitleFromFileName;
    }

    /**
     * Setting this option to {@code true} will set the title of the structure to the file name.
     * Default value: {@code false}
     *
     * @param inferTitleFromFileName {@code true} to turn this option on, {@code false} to turn it off.
     */
    public void inferTitleFromFileName(boolean inferTitleFromFileName) {
        this.inferTitleFromFileName = inferTitleFromFileName;
    }

    /**
     * Setting this option to {@code true} will try parse any pdb identifier from the file name.
     * Default value: {@code false}
     *
     * @return {@code true} to turn this option on, {@code false} to turn it off.
     */
    public boolean isInferringIdentifierFromFileName() {
        return this.inferIdentifierFromFileName;
    }

    /**
     * Setting this option to {@code true} will try parse any pdb identifier from the file name.
     * Default value: {@code false}
     *
     * @param inferIdentifierFromFileName {@code true} to turn this option on, {@code false} to turn it off.
     */
    public void inferIdentifierFromFileName(boolean inferIdentifierFromFileName) {
        this.inferIdentifierFromFileName = inferIdentifierFromFileName;
    }
}
