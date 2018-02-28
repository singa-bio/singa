package de.bioforscher.singa.simulation.events;

import de.bioforscher.singa.chemistry.descriptive.entities.ChemicalEntity;
import de.bioforscher.singa.core.events.UpdateEventListener;
import de.bioforscher.singa.features.model.QuantityFormatter;
import de.bioforscher.singa.features.parameters.EnvironmentalParameters;
import de.bioforscher.singa.features.quantities.MolarConcentration;
import de.bioforscher.singa.simulation.model.compartments.CellSection;
import de.bioforscher.singa.simulation.model.graphs.AutomatonNode;
import de.bioforscher.singa.simulation.modules.model.Delta;
import de.bioforscher.singa.simulation.modules.model.Module;

import javax.measure.quantity.Time;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static de.bioforscher.singa.features.units.UnitProvider.MOLE_PER_LITRE;
import static tec.uom.se.unit.MetricPrefix.MILLI;
import static tec.uom.se.unit.Units.SECOND;

/**
 * This class can be used to write the concentrations of a node to a file while
 * simulating. This appends ech time
 *
 * @author cl
 */
public class EpochUpdateWriter implements UpdateEventListener<NodeUpdatedEvent> {

    private static final char COMMENT_CHARACTER = '#';
    private static final char SEPARATOR_CHARACTER = ',';
    private static final char SECTION_SPACER = '.';
    private static final String LINEBREAK = System.getProperty("line.separator");

    private final Path workspacePath;
    private final Path folder;
    private final boolean printHeader;

    private final Map<AutomatonNode, BufferedWriter> concentrationWriters;
    private final Map<AutomatonNode, BufferedWriter> deltaWriters;

    private final List<ChemicalEntity<?>> observedEntities;
    private final List<Module> observedModules;

    private QuantityFormatter<Time> timeFormatter = new QuantityFormatter<>(MILLI(SECOND), false);
    private QuantityFormatter<MolarConcentration> concentrationFormatter = new QuantityFormatter<>(MOLE_PER_LITRE, false);

    public EpochUpdateWriter(Path workspacePath, Path folder, Set<ChemicalEntity<?>> entitiesToObserve, Set<Module> modulesToObserve) {
        this(workspacePath, folder, entitiesToObserve, modulesToObserve, true);
    }

    public EpochUpdateWriter(Path workspacePath, Path folder, Set<ChemicalEntity<?>> entitiesToObserve, Set<Module> modulesToObserve, boolean printHeader) {
        this.workspacePath = workspacePath;
        this.folder = folder;
        createFolderStructure();
        observedEntities = initializeOrdering(entitiesToObserve);
        observedModules = initializeOrdering(modulesToObserve);
        this.printHeader = printHeader;
        concentrationWriters = new HashMap<>();
        deltaWriters = new HashMap<>();
    }

    private <Type> List<Type> initializeOrdering(Set<Type> unorderedEntities) {
        return new ArrayList<>(unorderedEntities);
    }

    private void createFolderStructure() {
        Path workspaceFolder = workspacePath.resolve(folder);
        try {
            if (!Files.exists(workspaceFolder)) {
                Files.createDirectory(workspaceFolder);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Path createFile(String fileName) throws IOException {
        Path file = workspacePath.resolve(folder).resolve(fileName);
        if (!Files.exists(file)) {
            Files.createFile(file);
        }
        return file;
    }

    public void addNodeToObserve(AutomatonNode node) throws IOException {
        // add concentration writers
        Path concentrationFile = createFile("node_" + node.getIdentifier() + "_concentrations.csv");
        BufferedWriter concentrationWriter = Files.newBufferedWriter(concentrationFile);
        concentrationWriters.put(node, concentrationWriter);
        writeEntityHeader(node);
        // add delta writers
        Path deltaFile = createFile("node_" + node.getIdentifier() + "_deltas.csv");
        BufferedWriter deltaWriter = Files.newBufferedWriter(deltaFile);
        deltaWriters.put(node, deltaWriter);
        writeDeltaHeader(node);
    }

    private void writeDeltaHeader(AutomatonNode node) throws IOException {
        StringBuilder sb = new StringBuilder();
        if (printHeader) {
            sb.append(COMMENT_CHARACTER)
                    .append(" Node ")
                    .append(node.getIdentifier())
                    .append(LINEBREAK);
        }
        sb.append(prepareDeltaColumnHeader());
        appendDeltaContent(node, sb.toString());
    }

    private String prepareDeltaColumnHeader() {
        return "elapsed_time" + SEPARATOR_CHARACTER +
                "time_step_size" + SEPARATOR_CHARACTER +
                "module" + SEPARATOR_CHARACTER +
                "chemical_entity" + SEPARATOR_CHARACTER +
                "cell_section" + SEPARATOR_CHARACTER +
                "delta" + SEPARATOR_CHARACTER +
                "delta_adjusted" + LINEBREAK;
    }

    private void writeEntityHeader(AutomatonNode node) throws IOException {
        StringBuilder sb = new StringBuilder();
        if (printHeader) {
            sb.append(COMMENT_CHARACTER)
                    .append(" Node ")
                    .append(node.getIdentifier())
                    .append(LINEBREAK)
                    .append(prepareEntityInformation());
        }
        sb.append(prepareEntityColumnHeader(node));
        appendConcentrationContent(node, sb.toString());
    }

    private String prepareEntityInformation() {
        StringBuilder sb = new StringBuilder();
        for (ChemicalEntity entity : observedEntities) {
            sb.append(COMMENT_CHARACTER)
                    .append(" ")
                    .append(entity.getName())
                    .append(" ")
                    .append(entity.getIdentifier())
                    .append(LINEBREAK);
        }
        return sb.toString();
    }

    private String prepareEntityColumnHeader(AutomatonNode node) {
        Set<CellSection> referencedSections = node.getAllReferencedSections();
        StringBuilder sb = new StringBuilder();
        sb.append("elapsed time").append(SEPARATOR_CHARACTER);
        int count = 0;
        int size = observedEntities.size() * referencedSections.size() - 1;
        for (ChemicalEntity entity : observedEntities) {
            for (CellSection cellSection : referencedSections) {
                if (count < size) {
                    sb.append(entity.getIdentifier())
                            .append(SECTION_SPACER)
                            .append(cellSection.getIdentifier())
                            .append(SEPARATOR_CHARACTER);
                } else {
                    sb.append(entity.getIdentifier())
                            .append(SECTION_SPACER)
                            .append(cellSection.getIdentifier()).
                            append(LINEBREAK);
                }
                count++;
            }
        }
        return sb.toString();
    }

    private void appendConcentrationContent(AutomatonNode node, String content) throws IOException {
        concentrationWriters.get(node).write(content);
    }

    private void appendDeltaContent(AutomatonNode node, String content) throws IOException {
        deltaWriters.get(node).write(content);
    }

    public void closeWriters() {
        for (BufferedWriter bufferedWriter : concentrationWriters.values()) {
            try {
                bufferedWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        for (BufferedWriter bufferedWriter : deltaWriters.values()) {
            try {
                bufferedWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void onEventReceived(NodeUpdatedEvent event) {
        appendConcentrationContent(event);
        appendDeltaContent(event);
    }

    private void appendConcentrationContent(NodeUpdatedEvent event) {
        AutomatonNode node = event.getNode();
        Set<CellSection> referencedSections = node.getAllReferencedSections();
        StringBuilder sb = new StringBuilder();
        sb.append(timeFormatter.format(event.getTime())).append(SEPARATOR_CHARACTER);
        int count = 0;
        int size = observedEntities.size() * referencedSections.size() - 1;
        for (ChemicalEntity entity : observedEntities) {
            for (CellSection cellSection : referencedSections) {
                if (count < size) {
                    sb.append(concentrationFormatter.format(node.getAvailableConcentration(entity, cellSection)))
                            .append(SEPARATOR_CHARACTER);
                } else {
                    sb.append(concentrationFormatter.format(node.getAvailableConcentration(entity, cellSection)))
                            .append(LINEBREAK);
                }
                count++;
            }
        }
        try {
            appendConcentrationContent(node, sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void appendDeltaContent(NodeUpdatedEvent event) {
        AutomatonNode node = event.getNode();
        StringBuilder sb = new StringBuilder();
        for (Delta delta : node.getPotentialDeltas()) {
            if (observedModules.contains(delta.getModule()) && observedEntities.contains(delta.getChemicalEntity())) {
                sb.append(timeFormatter.format(event.getTime())).append(SEPARATOR_CHARACTER)
                        .append(EnvironmentalParameters.getTimeStep().getValue().doubleValue()).append(SEPARATOR_CHARACTER)
                        .append(delta.getModule()).append(SEPARATOR_CHARACTER)
                        .append(delta.getChemicalEntity().getIdentifier()).append(SEPARATOR_CHARACTER)
                        .append(delta.getCellSection().getIdentifier()).append(SEPARATOR_CHARACTER)
                        .append(EnvironmentalParameters.DELTA_FORMATTER.format(delta.getQuantity())).append(SEPARATOR_CHARACTER)
                        .append(delta.getQuantity().to(MOLE_PER_LITRE).getValue().doubleValue() / EnvironmentalParameters.getTimeStep().getValue().doubleValue()).append(LINEBREAK);
            }
        }
        try {
            appendDeltaContent(node, sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
