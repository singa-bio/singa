package de.bioforscher.singa.simulation.events;

import de.bioforscher.singa.chemistry.descriptive.entities.ChemicalEntity;
import de.bioforscher.singa.core.events.UpdateEventListener;
import de.bioforscher.singa.features.parameters.EnvironmentalParameters;
import de.bioforscher.singa.features.quantities.MolarConcentration;
import de.bioforscher.singa.features.quantities.QuantityFormatter;
import de.bioforscher.singa.simulation.model.compartments.CellSection;
import de.bioforscher.singa.simulation.model.graphs.AutomatonNode;
import de.bioforscher.singa.simulation.modules.model.Delta;
import de.bioforscher.singa.simulation.modules.model.Module;
import de.bioforscher.singa.simulation.modules.model.SimulationManager;

import javax.measure.quantity.Time;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Predicate;

import static de.bioforscher.singa.features.units.UnitProvider.MOLE_PER_LITRE;
import static tec.uom.se.unit.MetricPrefix.MILLI;
import static tec.uom.se.unit.Units.SECOND;

/**
 * This class can be used to write the concentration of chemical entities of a node and the changes applied to the node
 * to a file. The updates are written each time a event is received. The events can be scheduled by using the
 * {@link SimulationManager#setUpdateEmissionCondition(Predicate)}. A node is observed by calling
 * {@link EpochUpdateWriter#addNodeToObserve(AutomatonNode)}.
 *
 * @author cl
 */
public class EpochUpdateWriter implements UpdateEventListener<NodeUpdatedEvent> {

    /**
     * The character indication a comment line.
     */
    private static final char COMMENT_CHARACTER = '#';

    /**
     * The character separating different values.
     */
    private static final char SEPARATOR_CHARACTER = ',';

    /**
     * The character separating sections in nodes.
     */
    private static final char SECTION_SPACER = '.';

    /**
     * The line separator of the current system.
     */
    private static final String LINEBREAK = System.getProperty("line.separator");

    /**
     * The path to the user defined workspace.
     */
    private final Path workspacePath;

    /**
     * The folder for the current simulation.
     */
    private final Path folder;

    /**
     * Determines whether general entity information should be printed in the header.
     */
    private final boolean printHeader;

    /**
     * The writers for concentration files.
     */
    private final Map<AutomatonNode, BufferedWriter> concentrationWriters;

    /**
     * The writers for deltas or changes.
     */
    private final Map<AutomatonNode, BufferedWriter> deltaWriters;

    /**
     * The entities that should be observed.
     */
    private final List<ChemicalEntity> observedEntities;

    /**
     * The modules deltas should be recorded for.
     */
    private final List<Module> observedModules;

    /**
     * The formatter for time based values.
     */
    private QuantityFormatter<Time> timeFormatter = new QuantityFormatter<>(MILLI(SECOND), false);

    /**
     * The formatter for concentration based values.
     */
    private QuantityFormatter<MolarConcentration> concentrationFormatter = new QuantityFormatter<>(MOLE_PER_LITRE, false);

    /**
     * Creates a new {@link EpochUpdateWriter}. A extended header is printed in each file.
     *
     * @param workspacePath The location of the simulation workspace.
     * @param folder The folder, where the files written by this class should be located.
     * @param entitiesToObserve The entities to observe.
     * @param modulesToObserve The modules to record deltas from.
     */
    public EpochUpdateWriter(Path workspacePath, Path folder, Set<ChemicalEntity> entitiesToObserve, Set<Module> modulesToObserve) {
        this(workspacePath, folder, entitiesToObserve, modulesToObserve, true);
    }

    /**
     * Creates a new {@link EpochUpdateWriter}.
     *
     * @param workspacePath The location of the simulation workspace.
     * @param folder The folder, where the files written by this class should be located.
     * @param entitiesToObserve The entities to observe.
     * @param modulesToObserve The modules to record deltas from.
     * @param printHeader True, if an extended header should be printed.
     */
    public EpochUpdateWriter(Path workspacePath, Path folder, Set<ChemicalEntity> entitiesToObserve, Set<Module> modulesToObserve, boolean printHeader) {
        this.workspacePath = workspacePath;
        this.folder = folder;
        createFolderStructure();
        observedEntities = initializeOrdering(entitiesToObserve);
        observedModules = initializeOrdering(modulesToObserve);
        this.printHeader = printHeader;
        concentrationWriters = new HashMap<>();
        deltaWriters = new HashMap<>();
    }

    /**
     * Takes care that the ordering of entities is the same across all files by converting the set to a list.
     *
     * @param unorderedEntities The unordered entities.
     * @param <ValueType> The type of the values in the set.
     * @return The ordered entities.
     */
    private static <ValueType> List<ValueType> initializeOrdering(Set<ValueType> unorderedEntities) {
        return new ArrayList<>(unorderedEntities);
    }

    /**
     * Initialized the folder for the current simulation.
     */
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

    /**
     * Creates a new file in the current folder.
     *
     * @param fileName The name of the file.
     * @return The path to the newly created file.
     */
    private Path createFile(String fileName) {
        Path file = workspacePath.resolve(folder).resolve(fileName);
        if (!Files.exists(file)) {
            try {
                Files.createFile(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }

    /**
     * Initializes a node that will be observed during simulation. Also prepares the files that will be written during
     * simulation.
     *
     * @param node The node to be observed.
     * @throws IOException If the file can not be created.
     */
    public void addNodeToObserve(AutomatonNode node) throws IOException {
        // add concentration writers
        Path concentrationFile = createFile("node_" + node.getIdentifier() + "_concentrations.csv");
        BufferedWriter concentrationWriter = Files.newBufferedWriter(concentrationFile);
        concentrationWriters.put(node, concentrationWriter);
        writeConcentrationFileHeader(node);
        // add delta writers
        Path deltaFile = createFile("node_" + node.getIdentifier() + "_deltas.csv");
        BufferedWriter deltaWriter = Files.newBufferedWriter(deltaFile);
        deltaWriters.put(node, deltaWriter);
        writeDeltaFileHeader(node);
    }

    /**
     * Writes the header for delta files.
     * @param node The node.
     * @throws IOException If the file could not be written.
     */
    private void writeDeltaFileHeader(AutomatonNode node) throws IOException {
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

    /**
     * Creates a String for the column header of delta files.
     * @return The column header.
     */
    private String prepareDeltaColumnHeader() {
        return "elapsed_time" + SEPARATOR_CHARACTER +
                "time_step_size" + SEPARATOR_CHARACTER +
                "module" + SEPARATOR_CHARACTER +
                "chemical_entity" + SEPARATOR_CHARACTER +
                "cell_section" + SEPARATOR_CHARACTER +
                "delta" + SEPARATOR_CHARACTER +
                "delta_adjusted" + LINEBREAK;
    }

    /**
     * Writes the header for concentration files.
     * @param node The node.
     * @throws IOException If the file could not be written.
     */
    private void writeConcentrationFileHeader(AutomatonNode node) throws IOException {
        StringBuilder sb = new StringBuilder();
        if (printHeader) {
            sb.append(COMMENT_CHARACTER)
                    .append(" Node ")
                    .append(node.getIdentifier())
                    .append(LINEBREAK)
                    .append(prepareEntityInformation());
        }
        sb.append(prepareConcentrationColumnHeader(node));
        appendConcentrationContent(node, sb.toString());
    }

    /**
     * Creates a String with information about the observed entities.
     * @return The entity information.
     */
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

    /**
     * Creates a String for the column header of concentration files.
     * @param node The node.
     * @return The column header.
     */
    private String prepareConcentrationColumnHeader(AutomatonNode node) {
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

    /**
     * Appends content to the concentration file associated to a node.
     * @param node The corresponding node.
     * @param content The content to be written.
     * @throws IOException The the file could not be written.
     */
    private void appendConcentrationContent(AutomatonNode node, String content) throws IOException {
        concentrationWriters.get(node).write(content);
    }

    /**
     * Appends content to the delta file associated to a node.
     * @param node The corresponding node.
     * @param content The content to be written.
     * @throws IOException The the file could not be written.
     */
    private void appendDeltaContent(AutomatonNode node, String content) throws IOException {
        deltaWriters.get(node).write(content);
    }

    /**
     * Close all associated writers. Usually called after simulation finishes.
     */
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

    /**
     * Appends the latest concentrations to the prepared files.
     * @param event The event.
     */
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

    /**
     * Appends the latest deltas to the prepared files.
     * @param event The event.
     */
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
