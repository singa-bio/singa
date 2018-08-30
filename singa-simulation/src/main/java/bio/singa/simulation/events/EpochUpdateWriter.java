package bio.singa.simulation.events;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.core.events.UpdateEventListener;
import bio.singa.features.model.QuantityFormatter;
import bio.singa.features.quantities.MolarConcentration;
import bio.singa.simulation.model.graphs.AutomatonNode;
import bio.singa.simulation.model.modules.concentration.ConcentrationBasedModule;
import bio.singa.simulation.model.modules.concentration.ConcentrationDelta;
import bio.singa.simulation.model.sections.CellSubsection;
import bio.singa.simulation.model.simulation.Simulation;
import bio.singa.simulation.model.simulation.SimulationManager;
import bio.singa.simulation.model.simulation.Updatable;

import javax.measure.quantity.Time;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static bio.singa.features.units.UnitProvider.MOLE_PER_CUBIC_MICROMETRE;
import static bio.singa.features.units.UnitProvider.MOLE_PER_LITRE;
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
public class EpochUpdateWriter implements UpdateEventListener<UpdatableUpdatedEvent> {

    /**
     * The character indication a comment line.
     */
    private static final char COMMENT_CHARACTER = '#';

    /**
     * The character separating different values.
     */
    private static final char SEPARATOR_CHARACTER = ',';

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
    private final Map<Updatable, BufferedWriter> concentrationWriters;

    /**
     * The writers for deltas or changes.
     */
    private final Map<Updatable, BufferedWriter> deltaWriters;

    /**
     * The entities that should be observed.
     */
    private final List<ChemicalEntity> observedEntities;

    /**
     * The modules deltas should be recorded for.
     */
    private final List<ConcentrationBasedModule> observedModules;

    /**
     * The formatter for time based values.
     */
    private QuantityFormatter<Time> timeFormatter = new QuantityFormatter<>(MILLI(SECOND), false);

    /**
     * The formatter for concentration based values.
     */
    private QuantityFormatter<MolarConcentration> concentrationFormatter = new QuantityFormatter<>(MOLE_PER_LITRE, false);


    private Simulation simulation;

    /**
     * Creates a new {@link EpochUpdateWriter}. A extended header is printed in each file.
     *
     * @param workspacePath The location of the simulation workspace.
     * @param folder The folder, where the files written by this class should be located.
     * @param entitiesToObserve The entities to observe.
     * @param modulesToObserve The modules to record deltas from.
     */
    public EpochUpdateWriter(Simulation simulation, Path workspacePath, Path folder, Set<ChemicalEntity> entitiesToObserve, Set<ConcentrationBasedModule> modulesToObserve) {
        this(simulation, workspacePath, folder, entitiesToObserve, modulesToObserve, true);
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
    public EpochUpdateWriter(Simulation simulation, Path workspacePath, Path folder, Set<ChemicalEntity> entitiesToObserve, Set<ConcentrationBasedModule> modulesToObserve, boolean printHeader) {
        this.simulation = simulation;
        this.workspacePath = workspacePath;
        this.folder = folder;
        createFolderStructure();
        observedEntities = initializeOrdering(entitiesToObserve);
        observedModules = initializeOrdering(modulesToObserve);
        this.printHeader = printHeader;
        concentrationWriters = new HashMap<>();
        deltaWriters = new HashMap<>();
    }

    public QuantityFormatter<Time> getTimeFormatter() {
        return timeFormatter;
    }

    public void setTimeFormatter(QuantityFormatter<Time> timeFormatter) {
        this.timeFormatter = timeFormatter;
    }

    public QuantityFormatter<MolarConcentration> getConcentrationFormatter() {
        return concentrationFormatter;
    }

    public void setConcentrationFormatter(QuantityFormatter<MolarConcentration> concentrationFormatter) {
        this.concentrationFormatter = concentrationFormatter;
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
     *
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
     *
     * @return The column header.
     */
    private String prepareDeltaColumnHeader() {
        return "elapsed_time" + SEPARATOR_CHARACTER +
                "module" + SEPARATOR_CHARACTER +
                "cell_section" + SEPARATOR_CHARACTER +
                "chemical_entity" + SEPARATOR_CHARACTER +
                "delta_average";
    }

    /**
     * Writes the header for concentration files.
     *
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
     *
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
     *
     * @param node The node.
     * @return The column header.
     */
    private String prepareConcentrationColumnHeader(AutomatonNode node) {
        return "elapsed time" + SEPARATOR_CHARACTER +
                "species" + SEPARATOR_CHARACTER +
                "compartment" + SEPARATOR_CHARACTER +
                "concentration" + LINEBREAK;
    }

    /**
     * Appends content to the concentration file associated to a node.
     *
     * @param node The corresponding node.
     * @param content The content to be written.
     * @throws IOException The the file could not be written.
     */
    private void appendConcentrationContent(Updatable node, String content) throws IOException {
        concentrationWriters.get(node).write(content);
    }

    /**
     * Appends content to the delta file associated to a node.
     *
     * @param node The corresponding node.
     * @param content The content to be written.
     * @throws IOException The the file could not be written.
     */
    private void appendDeltaContent(Updatable node, String content) throws IOException {
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
    public void onEventReceived(UpdatableUpdatedEvent event) {
        appendConcentrationContent(event);
        appendDeltaContent(event);
    }

    /**
     * Appends the latest concentrations to the prepared files.
     *
     * @param event The event.
     */
    private void appendConcentrationContent(UpdatableUpdatedEvent event) {
        Updatable node = event.getUpdatable();
        Set<CellSubsection> referencedSections = node.getAllReferencedSections();
        StringBuilder sb = new StringBuilder();

        for (ChemicalEntity entity : observedEntities) {
            for (CellSubsection cellSection : referencedSections) {
                sb.append(timeFormatter.format(event.getTime())).append(SEPARATOR_CHARACTER)
                        .append(entity.getIdentifier()).append(SEPARATOR_CHARACTER)
                        .append(cellSection.getIdentifier()).append(SEPARATOR_CHARACTER)
                        .append(concentrationFormatter.format(node.getConcentration(cellSection, entity)))
                        .append(LINEBREAK);
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
     *
     * @param event The event.
     */
    private void appendDeltaContent(UpdatableUpdatedEvent event) {
        Updatable updatable = event.getUpdatable();
        List<ConcentrationDelta> previousObservedDeltas = simulation.getPreviousObservedDeltas(updatable);

        String collect = previousObservedDeltas.stream()
                .collect(Collectors.groupingBy(delta -> delta.getModule().toString() + SEPARATOR_CHARACTER +
                        delta.getCellSubsection().getIdentifier() + SEPARATOR_CHARACTER +
                        delta.getChemicalEntity().getIdentifier()))
                .entrySet()
                .stream()
                .map(entry -> timeFormatter.format(event.getTime()) + SEPARATOR_CHARACTER
                        + entry.getKey() + SEPARATOR_CHARACTER
                        + entry.getValue().stream()
                        .mapToDouble(delta -> delta.getQuantity().to(MOLE_PER_CUBIC_MICROMETRE).getValue().doubleValue())
                        .average()
                        .orElse(Double.NaN))
                .collect(Collectors.joining(LINEBREAK, "", LINEBREAK));

        // write
//        StringBuilder sb = new StringBuilder();
//        for (ConcentrationDelta delta : updatable.getPotentialConcentrationDeltas()) {
//            if (observedModules.contains(delta.getModule()) && observedEntities.contains(delta.getChemicalEntity())) {
//                sb.append(timeFormatter.format(event.getTime())).append(SEPARATOR_CHARACTER)
//                        .append(delta.getModule()).append(SEPARATOR_CHARACTER)
//                        .append(delta.getChemicalEntity().getIdentifier()).append(SEPARATOR_CHARACTER)
//                        .append(delta.getCellSubsection().getIdentifier()).append(SEPARATOR_CHARACTER)
//                        .append(Environment.DELTA_FORMATTER.format(delta.getQuantity())).append(LINEBREAK);
//            }
//        }
        try {
            appendDeltaContent(updatable, collect);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
