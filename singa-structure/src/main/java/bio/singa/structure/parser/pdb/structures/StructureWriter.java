package bio.singa.structure.parser.pdb.structures;

import bio.singa.structure.model.families.StructuralFamily;
import bio.singa.structure.model.interfaces.*;
import bio.singa.structure.model.mmtf.MmtfAminoAcid;
import bio.singa.structure.model.oak.LinkEntry;
import bio.singa.structure.model.oak.OakStructure;
import bio.singa.structure.model.oak.PdbLeafIdentifier;
import bio.singa.structure.model.oak.Structures;
import org.rcsb.mmtf.dataholders.MmtfStructure;
import org.rcsb.mmtf.encoder.AdapterToStructureData;
import org.rcsb.mmtf.encoder.WriterUtils;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.*;
import java.util.stream.Collectors;

public class StructureWriter {

    // TODO also add REMARK 80 to be written
    // TODO option for short and long ter records

    public static PDBCoverageStep pdb() {
        return new PDBRepresentationBuilder();
    }

    public static XYZCoverageStep xyz() {
        return new XYZRepresentationBuilder();
    }

    public static MMTFCoverageStep mmtf() {
        return new MMTFRepresentationBuilder();
    }

    private StructureWriter() {

    }

    public interface PDBCoverageStep {

        PDBSubstructureStep substructure(LeafSubstructure leafSubstructure);

        PDBSubstructureStep substructure(LeafSubstructureContainer container);

        PDBSubstructureStep substructures(Collection<LeafSubstructure> leafSubstructures);

        OptionsStep structure(Structure structure);

    }

    public interface XYZCoverageStep {

        OutputStep substructure(LeafSubstructure leafSubstructure);

        OutputStep substructure(LeafSubstructureContainer container);

        OutputStep substructures(Collection<LeafSubstructure> leafSubstructures);

        OutputStep structure(Structure structure);

    }

    public interface MMTFCoverageStep {

        MMTFOutputStep structure(Structure structure);

    }

    public interface PDBSubstructureStep extends OptionsStep {

        PDBSubstructureStep title(String title);

        PDBSubstructureStep pdbIdentifier(String pdbIdentifier);

        PDBSubstructureStep links(Collection<LinkEntry> linkEntries);

    }

    public interface OptionsStep extends OutputStep {

        OutputStep renumberSubstructures(Map<PdbLeafIdentifier, Integer> renumberingMap);

        OutputStep settings(StructureRepresentationOptions.Setting... settings);

        OutputStep defaultSettings();

    }

    public interface OutputStep {

        String writeToString();

        void writeToPath(Path path) throws IOException;

    }

    public interface MMTFOutputStep {

        void writeToPath(Path path) throws IOException;

    }

    static class PDBRepresentationBuilder implements PDBCoverageStep, PDBSubstructureStep, OptionsStep, OutputStep {

        private Structure structure;
        private List<LeafSubstructure> leafSubstructures;
        private String title = "";
        private String pdbIdentifier = "";
        private List<LinkEntry> linkEntries;

        private StructureRepresentationOptions options;
        private Path destination;

        public PDBRepresentationBuilder() {
            options = StructureRepresentationOptions.defaultSettings();
        }

        @Override
        public PDBSubstructureStep substructure(LeafSubstructure leafSubstructure) {
            return substructures(Collections.singletonList(leafSubstructure));
        }

        @Override
        public PDBSubstructureStep substructure(LeafSubstructureContainer container) {
            return substructures(container.getAllLeafSubstructures());
        }

        @Override
        public PDBSubstructureStep substructures(Collection<LeafSubstructure> leafSubstructures) {
            this.leafSubstructures = new ArrayList<>(leafSubstructures);
            return this;
        }

        @Override
        public OptionsStep structure(Structure structure) {
            this.structure = structure;
            return this;
        }

        @Override
        public PDBSubstructureStep title(String title) {
            this.title = title;
            return this;
        }

        @Override
        public PDBSubstructureStep pdbIdentifier(String pdbIdentifier) {
            this.pdbIdentifier = pdbIdentifier;
            return this;
        }

        @Override
        public PDBSubstructureStep links(Collection<LinkEntry> linkEntries) {
            this.linkEntries = reduceToRelevantLinks(linkEntries);
            return this;
        }

        @Override
        public OutputStep renumberSubstructures(Map<PdbLeafIdentifier, Integer> renumberingMap) {
            options.setRenumberingMap(renumberingMap);
            options.setRenumberingSubstructures(true);
            return null;
        }

        @Override
        public OutputStep settings(StructureRepresentationOptions.Setting... settings) {
            options.applySettings(settings);
            return this;
        }

        @Override
        public OutputStep defaultSettings() {
            options = StructureRepresentationOptions.defaultSettings();
            return this;
        }

        @Override
        public String writeToString() {
            prepareInformationToWrite();
            if (options.isRenumberingAtoms()) {
                structure = StructureRenumberer.renumberAtomsConsecutively(structure);
            }
            if (options.isRenumberingSubstructures()) {
                structure = StructureRenumberer.renumberLeaveSubstructuresWithMap(structure, options.getRenumberingMap());
            }
            return StructureRepresentation.composePdbRepresentation(structure);
        }

        private void prepareInformationToWrite() {
            if (leafSubstructures != null) {
                if (pdbIdentifier.isEmpty()) {
                    LeafSubstructure leafSubstructure = leafSubstructures.iterator().next();
                    pdbIdentifier = leafSubstructure.getIdentifier().getStructureIdentifier();
                }
                structure = Structures.toStructure(leafSubstructures, pdbIdentifier, title);
            }
            if (linkEntries != null) {
                linkEntries.forEach(linkEntry -> ((OakStructure) structure).addLinkEntry(linkEntry));
            }
        }

        @Override
        public void writeToPath(Path destination) throws IOException {
            this.destination = destination;
            prepareTarget();
            Files.write(destination, writeToString().getBytes(StandardCharsets.UTF_8));
        }

        private void prepareTarget() {
            if (destination != null) {
                try {
                    Files.createDirectories(destination.getParent());
                } catch (IOException e) {
                    throw new UncheckedIOException("unable to create directory to write structure", e);
                }
            }
        }

        private List<LinkEntry> reduceToRelevantLinks(Collection<LinkEntry> linkEntries) {
            ArrayList<LinkEntry> reducedLinkEntries = new ArrayList<>();
            for (LinkEntry linkEntry : linkEntries) {
                if (leafSubstructures.contains(linkEntry.getFirstLeafSubstructure()) && leafSubstructures.contains(linkEntry.getSecondLeafSubstructure())) {
                    reducedLinkEntries.add(linkEntry);
                }
            }
            return reducedLinkEntries;
        }

    }

    static class XYZRepresentationBuilder implements XYZCoverageStep, OutputStep {

        private static final DecimalFormat coordinateFormat = new DecimalFormat("0.00000", new DecimalFormatSymbols(Locale.US));

        private Collection<LeafSubstructure> leafSubstructures;
        private Path destination;

        @Override
        public OutputStep substructure(LeafSubstructure leafSubstructure) {
            return substructures(Collections.singletonList(leafSubstructure));
        }

        @Override
        public OutputStep substructure(LeafSubstructureContainer container) {
            return substructures(container.getAllLeafSubstructures());
        }

        @Override
        public OutputStep substructures(Collection<LeafSubstructure> leafSubstructures) {
            this.leafSubstructures = leafSubstructures;
            return this;
        }

        @Override
        public OutputStep structure(Structure structure) {
            leafSubstructures = structure.getAllLeafSubstructures();
            return this;
        }

        @Override
        public String writeToString() {
            List<Atom> atoms = leafSubstructures.stream()
                    .flatMap(leaf -> leaf.getAllAtoms().stream())
                    .collect(Collectors.toList());
            StringBuilder builder = new StringBuilder();
            builder.append(atoms.size())
                    .append(System.lineSeparator())
                    .append(System.lineSeparator());
            for (Atom atom : atoms) {
                builder.append(atom.getElement().getSymbol()).append("\t")
                        .append(coordinateFormat.format(atom.getPosition().getX())).append("\t")
                        .append(coordinateFormat.format(atom.getPosition().getY())).append("\t")
                        .append(coordinateFormat.format(atom.getPosition().getZ())).append(System.lineSeparator());
            }
            return builder.toString();
        }

        @Override
        public void writeToPath(Path destination) throws IOException {
            this.destination = destination;
            prepareTarget();
            Files.write(destination, writeToString().getBytes(StandardCharsets.UTF_8));
        }

        private void prepareTarget() {
            if (destination != null) {
                try {
                    Files.createDirectories(destination.getParent());
                } catch (IOException e) {
                    throw new UncheckedIOException("unable to create directory to write structure", e);
                }
            }
        }

    }

    static class MMTFRepresentationBuilder implements MMTFCoverageStep, MMTFOutputStep {

        private Structure structure;
        private Path destination;

        @Override
        public MMTFOutputStep structure(Structure structure) {
            this.structure = structure;
            return this;
        }

        @Override
        public void writeToPath(Path destination) throws IOException {
            this.destination = destination;
            prepareTarget();
            WriterUtils.writeDataToFile(prepareMmtfStructure(), destination);
        }

        private void prepareTarget() {
            if (destination != null) {
                try {
                    Files.createDirectories(destination.getParent());
                } catch (IOException e) {
                    throw new UncheckedIOException("unable to create directory to write structure", e);
                }
            }
        }

        private AdapterToStructureData prepareMmtfStructure() {
            AdapterToStructureData structureAdapterInterface = new AdapterToStructureData();
            // init structure
            structureAdapterInterface.initStructure(
                    0,
                    structure.getAllAtoms().size(),
                    structure.getAllLeafSubstructures().size(),
                    structure.getAllChains().size(),
                    structure.getAllModels().size(),
                    structure.getPdbIdentifier().toLowerCase());
            structureAdapterInterface.setMmtfProducer("SiNGA");

            // TODO currently we do not consider header information
            // add header information
            structureAdapterInterface.setHeaderInfo(
                    0.0F,
                    0.0F,
                    ((float) structure.getResolution()),
                    structure.getTitle(),
                    "yyyy/mm/dd",
                    "yyyy/mm/dd",
                    new String[]{"xtal"});

            // handle all models
            List<Model> allModels = structure.getAllModels();
            for (int i = 0; i < allModels.size(); i++) {
                Model model = allModels.get(i);
                List<Chain> allChains = model.getAllChains();
                structureAdapterInterface.setModelInfo(i, allChains.size());
                // handle all chains
                for (Chain chain : allChains) {
                    List<LeafSubstructure> leafSubstructures = chain.getAllLeafSubstructures();
                    // TODO here we presumably need a mapping between "real" chain names and internal IDs as first argument
                    structureAdapterInterface.setChainInfo(chain.getChainIdentifier(), chain.getChainIdentifier(), leafSubstructures.size());
                    for (LeafSubstructure leafSubstructure : leafSubstructures) {
                        List<Atom> atoms = leafSubstructure.getAllAtoms();

                        char insertionCode = leafSubstructure.getIdentifier().getInsertionCode();
                        StructuralFamily family = leafSubstructure.getFamily();
                        char oneLetterCode = family.getOneLetterCode().charAt(0);
                        // TODO correct vocabulary has to be found for polymerType
                        // TODO sequenceIndex corresponds to SEQRES number which we do not consider for the moment
                        // TODO secStrucType is an integer and follows the DSSP numenclature, BioJava: DsspType
                        structureAdapterInterface.setGroupInfo(
                                leafSubstructure.getThreeLetterCode(),
                                leafSubstructure.getIdentifier().getSerial(),
                                insertionCode,
                                "L-peptide linking",
                                leafSubstructure.getAllAtoms().size(),
                                0,
                                oneLetterCode,
                                leafSubstructure.getIdentifier().getSerial(),
                                leafSubstructure instanceof MmtfAminoAcid ? ((MmtfAminoAcid) leafSubstructure).getSecondaryStructure().getMmtfCode() : -1);
                        for (Atom atom : atoms) {
                            // TODO currently alternate location, and occupancy are ignored
                            structureAdapterInterface.setAtomInfo(atom.getAtomName(), atom.getAtomIdentifier(), MmtfStructure.UNAVAILABLE_CHAR_VALUE,
                                    ((float) atom.getPosition().getX()), ((float) atom.getPosition().getY()), ((float) atom.getPosition().getZ()),
                                    1.0F, ((float) atom.getBFactor()), atom.getElement().getSymbol().toUpperCase(), atom.getElement().getCharge());
                            // TODO here we should add bonds
                            // structureAdapterInterface.setGroupBond();
                        }
                    }
                }
            }
            structureAdapterInterface.finalizeStructure();
            return structureAdapterInterface;
        }

    }

}
