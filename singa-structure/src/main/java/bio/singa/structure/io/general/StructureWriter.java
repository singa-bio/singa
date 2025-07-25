package bio.singa.structure.io.general;

import bio.singa.structure.model.cif.CifStructure;
import bio.singa.structure.model.interfaces.*;
import bio.singa.structure.model.general.LinkEntry;
import bio.singa.structure.model.pdb.PdbStructure;
import bio.singa.structure.model.general.AuthLeafIdentifier;
import bio.singa.structure.model.general.Structures;

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

    // TODO option for short and long ter records

    public static PDBCoverageStep pdb() {
        return new PDBRepresentationBuilder();
    }

    public static XYZCoverageStep xyz() {
        return new XYZRepresentationBuilder();
    }

    // TODO CIF/BCIF writing

    private StructureWriter() {

    }

    public interface PDBCoverageStep {

        PDBSubstructureStep substructure(LeafSubstructure leafSubstructure);

        PDBSubstructureStep substructure(LeafSubstructureContainer container);

        PDBSubstructureStep substructures(Collection<? extends LeafSubstructure> leafSubstructures);

        OptionsStep structure(Structure structure);

    }

    public interface XYZCoverageStep {

        OutputStep substructure(LeafSubstructure leafSubstructure);

        OutputStep substructure(LeafSubstructureContainer container);

        OutputStep substructures(Collection<? extends LeafSubstructure> leafSubstructures);

        OutputStep structure(Structure structure);

    }

    public interface PDBSubstructureStep extends OptionsStep {

        PDBSubstructureStep title(String title);

        PDBSubstructureStep pdbIdentifier(String pdbIdentifier);

        PDBSubstructureStep links(Collection<LinkEntry> linkEntries);

    }

    public interface OptionsStep {

        OutputStep renumberSubstructures(Map<AuthLeafIdentifier, Integer> renumberingMap);

        OutputStep settings(StructureRepresentationOptions.Setting... settings);

        OutputStep defaultSettings();

    }

    public interface OutputStep {

        Structure getStructure();

        String writeToString();

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
        public PDBSubstructureStep substructures(Collection<? extends LeafSubstructure> leafSubstructures) {
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
        public OutputStep renumberSubstructures(Map<AuthLeafIdentifier, Integer> renumberingMap) {
            options.setRenumberingMap(renumberingMap);
            options.setRenumberingSubstructures(true);
            prepareInformationToWrite();
            return null;
        }

        @Override
        public OutputStep settings(StructureRepresentationOptions.Setting... settings) {
            options.applySettings(settings);
            prepareInformationToWrite();
            return this;
        }

        @Override
        public OutputStep defaultSettings() {
            options = StructureRepresentationOptions.defaultSettings();
            prepareInformationToWrite();
            return this;
        }

        @Override
        public Structure getStructure() {
            return structure;
        }

        @Override
        public String writeToString() {
            return StructureRepresentationFactory.getPdbStringRepresentation(structure, options);
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
                linkEntries.forEach(linkEntry -> ((PdbStructure) structure).addLinkEntry(linkEntry));
            }
            // apply renumbering
            if (structure != null && structure instanceof CifStructure) {
                // renumbering cif structures strictly requires atom renumbering because of chain termination records
                options.setRenumberingAtoms(true);
            }
            if (options.isRenumberingAtoms() && options.isRenumberingSubstructures() && options.isRenumberChains()) {
                structure = StructureRenumberer.renumberEverything(structure);
                return;
            }
            if (options.isRenumberingAtoms()) {
                structure = StructureRenumberer.renumberAtomsConsecutively(structure, options.isRenumberChains());
            }
            if (options.isRenumberingSubstructures()) {
                structure = StructureRenumberer.renumberLeaveSubstructuresWithMap(structure, options.getRenumberingMap());
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

        private Collection<? extends LeafSubstructure> leafSubstructures;
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
        public OutputStep substructures(Collection<? extends LeafSubstructure> leafSubstructures) {
            this.leafSubstructures = leafSubstructures;
            return this;
        }

        @Override
        public OutputStep structure(Structure structure) {
            leafSubstructures = structure.getAllLeafSubstructures();
            return this;
        }

        @Override
        public Structure getStructure() {
            return Structures.toStructure(leafSubstructures, "", "");
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

}
