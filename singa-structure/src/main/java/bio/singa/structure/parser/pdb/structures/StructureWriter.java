package bio.singa.structure.parser.pdb.structures;


import bio.singa.structure.model.families.StructuralFamily;
import bio.singa.structure.model.interfaces.*;
import bio.singa.structure.model.oak.*;
import org.rcsb.mmtf.dataholders.MmtfStructure;
import org.rcsb.mmtf.encoder.AdapterToStructureData;
import org.rcsb.mmtf.encoder.WriterUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;


/**
 * A class to write Structures objects to PDB format.
 *
 * @author fk
 */
public class StructureWriter {

    private static final Logger logger = LoggerFactory.getLogger(StructureWriter.class);

    /**
     * Prevent instantiation.
     */
    private StructureWriter() {

    }

    /**
     * Writes a given {@link LeafSubstructureContainer} in PDB format.
     *
     * @param leafSubstructureContainer The {@link LeafSubstructureContainer} to be written.
     * @param outputPath The output {@link Path}.
     * @throws IOException If the path cannot be written.
     */
    public static void writeLeafSubstructureContainer(LeafSubstructureContainer leafSubstructureContainer, Path outputPath) throws IOException {
        logger.info("Writing branch substructure {} to {}.", leafSubstructureContainer, outputPath);
        writeLeafSubstructures(leafSubstructureContainer.getAllLeafSubstructures(), outputPath);
    }

    /**
     * Writes a given list of {@link LeafSubstructure}s in PDB format.
     *
     * @param leafSubstructures The list of {@link LeafSubstructure}s to be written.
     * @param outputPath The output {@link Path}.
     * @throws IOException If the path cannot be written.
     */
    public static void writeLeafSubstructures(List<LeafSubstructure<?>> leafSubstructures, Path outputPath) throws IOException {
        logger.info("Writing {} leaf substructures to {}.", leafSubstructures.size(), outputPath);
        Files.createDirectories(outputPath.getParent());
        Files.write(outputPath, StructureRepresentation.composePdbRepresentation(leafSubstructures).getBytes());
    }

    /**
     * Writes a {@link OakStructure} in PDB format.
     *
     * @param structure The {@link OakStructure} to be written.
     * @param outputPath The output {@link Path}.
     * @throws IOException If the path cannot be written.
     */
    public static void writeStructure(OakStructure structure, Path outputPath) throws IOException {
        logger.info("Writing structure {} to {}.", structure, outputPath);
        Files.createDirectories(outputPath.getParent());
        Files.write(outputPath, StructureRepresentation.composePdbRepresentation(structure).getBytes());
    }

    /**
     * Writes a {@link OakStructure} in MMTF format.
     *
     * @param structure The {@link OakStructure} to be written.
     * @param outputPath The output {@link Path}.
     * @throws IOException If the path cannot be written.
     */
    public static void writeMMTFStructure(OakStructure structure, Path outputPath) throws IOException {
        AdapterToStructureData structureAdapterInterface = new AdapterToStructureData();
        // init structure
        structureAdapterInterface.initStructure(0, structure.getAllAtoms().size(),
                structure.getAllLeafSubstructures().size(),
                structure.getAllChains().size(),
                structure.getAllModels().size(),
                structure.getPdbIdentifier().toLowerCase());
        structureAdapterInterface.setMmtfProducer("SiNGA");

        // TODO currently we do not consider header information
        // add header information
        structureAdapterInterface.setHeaderInfo(0.0F, 0.0F, 0.0F, structure.getTitle(), "yyyy/mm/dd", "yyyy/mm/dd", new String[]{"xtal"});

        // handle all models
        List<Model> allModels = structure.getAllModels();
        for (int i = 0; i < allModels.size(); i++) {
            Model model = allModels.get(i);
            List<Chain> allChains = model.getAllChains();
            structureAdapterInterface.setModelInfo(i, allChains.size());
            // handle all chains
            for (Chain chain : allChains) {
                List<LeafSubstructure<?>> leafSubstructures = chain.getAllLeafSubstructures();
                // TODO here we presumably need a mapping between "real" chain names and internal IDs as first argument
                structureAdapterInterface.setChainInfo(chain.getChainIdentifier(), chain.getChainIdentifier(), leafSubstructures.size());
                for (int j = 0; j < leafSubstructures.size(); j++) {
                    LeafSubstructure<?> leafSubstructure = leafSubstructures.get(j);
                    List<Atom> atoms = leafSubstructure.getAllAtoms();
                    char insertionCode = leafSubstructure.getInsertionCode();
                    if (insertionCode == '\u0000') {
                        insertionCode = MmtfStructure.UNAVAILABLE_CHAR_VALUE;
                    }
                    StructuralFamily family = leafSubstructure.getFamily();
                    char oneLetterCode = family.getOneLetterCode().charAt(0);
                    // TODO correct vocabulary has to be found for polymerType
                    // TODO sequenceIndex corresponds to SEQRES number which we do not consider for the moment
                    // TODO secStrucType is an integer and follows the DSSP numenclature, BioJava: DsspType
                    structureAdapterInterface.setGroupInfo(leafSubstructure.getThreeLetterCode().toUpperCase(), leafSubstructure.getSerial(), insertionCode, "L-peptide linking", leafSubstructure.getAllAtoms().size(), 0, oneLetterCode, leafSubstructure.getSerial(), -1);
                    for (Atom atom : atoms) {
                        // TODO currently alternate location, occupancy, and B-factor are ignored
                        structureAdapterInterface.setAtomInfo(atom.getAtomName(), atom.getAtomIdentifier(), MmtfStructure.UNAVAILABLE_CHAR_VALUE,
                                ((float) atom.getPosition().getX()), ((float) atom.getPosition().getY()), ((float) atom.getPosition().getZ()),
                                1.0F, 0.0F, atom.getElement().getSymbol().toUpperCase(), atom.getElement().getCharge());
                        // TODO here we should add bonds
                        // addBonds(atom, atoms, allAtoms);
                    }
                }
            }
        }
        structureAdapterInterface.finalizeStructure();
        Files.createDirectories(outputPath.getParent());
        WriterUtils.writeDataToFile(structureAdapterInterface, outputPath);
    }


    public static void writeWithConsecutiveNumbering(OakStructure structure, Path outputPath) throws IOException {
        OakStructure renumberedStructure = prepareForConsecutiveRewrite(structure);
        writeStructure(renumberedStructure, outputPath);
    }

    private static OakStructure prepareForConsecutiveRewrite(OakStructure structure) {
        logger.debug("Renumbering structure consecutively.");
        OakStructure renumberedStructure = new OakStructure();
        renumberedStructure.setPdbIdentifier(structure.getPdbIdentifier());
        int identifier = 1;
        for (Model model : structure.getAllModels()) {
            OakModel renumberedModel = new OakModel(model.getModelIdentifier());
            renumberedStructure.addModel(renumberedModel);
            // consecutive parts
            for (Chain chain : model.getAllChains()) {
                OakChain oakChain = (OakChain) chain;
                OakChain renumberedChain = new OakChain(chain.getChainIdentifier());
                renumberedModel.addChain(renumberedChain);
                for (LeafSubstructure leafSubstructure : oakChain.getConsecutivePart()) {
                    OakLeafSubstructure renumberedLeafSubstructure = LeafSubstructureFactory.createLeafSubstructure(leafSubstructure.getIdentifier(), leafSubstructure.getFamily());
                    renumberedChain.addLeafSubstructure(renumberedLeafSubstructure, true);
                    for (Atom atom : leafSubstructure.getAllAtoms()) {
                        OakAtom renumberedAtom = new OakAtom(identifier, atom.getElement(), atom.getAtomName(), atom.getPosition());
                        logger.trace("Renumbering atom {} to {}.", atom.getAtomIdentifier(), renumberedAtom.getAtomIdentifier());
                        renumberedLeafSubstructure.addAtom(renumberedAtom);
                        identifier++;
                    }
                }
                logger.trace("Keeping identifier {} for terminator token.", identifier);
                identifier++;
            }
            // nonconsecutive parts
            for (Chain chain : model.getAllChains()) {
                OakChain oakChain = (OakChain) chain;
                OakChain renumberedChain = (OakChain) renumberedModel.getChain(chain.getChainIdentifier()).get();
                for (LeafSubstructure leafSubstructure : oakChain.getNonConsecutivePart()) {
                    OakLeafSubstructure renumberedLeafSubstructure = LeafSubstructureFactory.createLeafSubstructure(leafSubstructure.getIdentifier(), leafSubstructure.getFamily());
                    renumberedLeafSubstructure.setAnnotatedAsHetAtom(true);
                    renumberedChain.addLeafSubstructure(renumberedLeafSubstructure);
                    for (Atom atom : leafSubstructure.getAllAtoms()) {
                        OakAtom renumberedAtom = new OakAtom(identifier, atom.getElement(), atom.getAtomName(), atom.getPosition());
                        renumberedLeafSubstructure.addAtom(renumberedAtom);
                        logger.trace("Renumbering atom {} to {}.", atom.getAtomIdentifier(), renumberedAtom.getAtomIdentifier());
                        identifier++;
                    }
                }
            }
        }

        return renumberedStructure;
    }

    public static void writeToXYZ(AtomContainer atomContainer, Path outputPath) throws IOException {
        StringBuilder builder = new StringBuilder();
        DecimalFormat coordinateFormat = new DecimalFormat("0.00000", new DecimalFormatSymbols(Locale.US));
        builder.append(atomContainer.getAllAtoms().size())
                .append(System.lineSeparator())
                .append(System.lineSeparator());

        for (Atom atom : atomContainer.getAllAtoms()) {
            builder.append(atom.getElement().getSymbol()).append("\t")
                    .append(coordinateFormat.format(atom.getPosition().getX())).append("\t")
                    .append(coordinateFormat.format(atom.getPosition().getY())).append("\t")
                    .append(coordinateFormat.format(atom.getPosition().getZ())).append(System.lineSeparator());
        }

        Files.createDirectories(outputPath.getParent());
        Files.write(outputPath, builder.toString().getBytes());

    }

}
