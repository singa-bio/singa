package bio.singa.simulation.trajectories.flat;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.core.events.UpdateEventListener;
import bio.singa.features.formatter.*;
import bio.singa.features.quantities.MolarConcentration;
import bio.singa.simulation.events.UpdatableUpdatedEvent;
import bio.singa.simulation.model.modules.concentration.ConcentrationDelta;
import bio.singa.simulation.model.sections.CellSubsection;
import bio.singa.simulation.model.simulation.Simulation;
import bio.singa.simulation.model.simulation.SimulationManager;
import bio.singa.simulation.model.simulation.Updatable;
import bio.singa.simulation.trajectories.Recorders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.measure.Unit;
import javax.measure.quantity.Time;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static bio.singa.features.units.UnitProvider.MOLE_PER_LITRE;
import static bio.singa.simulation.trajectories.Recorders.appendTimestampedFolder;
import static bio.singa.simulation.trajectories.Recorders.createDirectories;
import static tec.uom.se.unit.MetricPrefix.MILLI;
import static tec.uom.se.unit.Units.SECOND;

/**
 * This class can be used to write the concentration of chemical entities of a node and the changes applied to the node
 * to a file. The updates are written each time a event is received. The events can be scheduled by using the
 * {@link SimulationManager#setUpdateEmissionCondition(Predicate)}. A node is observed by calling
 * {@link FlatUpdateRecorder#addUpdatableToObserve(Updatable)}.
 *
 * @author cl
 */
public class FlatUpdateRecorder implements UpdateEventListener<UpdatableUpdatedEvent> {

    private static final Logger logger = LoggerFactory.getLogger(FlatUpdateRecorder.class);

    private static final String LINE_SEPARATOR = System.lineSeparator();
    private static final String VALUE_SEPARATOR = ",";

    private final String deltaHeader;
    private final String concentrationHeader;

    /**
     * The path to the user defined workspace.
     */
    private Path workspacePath;

    /**
     * The directory for the current simulation.
     */
    private Path directory;

    /**
     * The writers for concentration files.
     */
    private Map<Updatable, BufferedWriter> concentrationWriters;

    /**
     * The writers for deltas or changes.
     */
    private Map<Updatable, BufferedWriter> deltaWriters;

    /**
     * The formatter for time based values.
     */
    private QuantityFormatter<Time> timeFormatter = new GeneralQuantityFormatter<>(MILLI(SECOND), false);

    /**
     * The formatter for concentration based values.
     */
    private GeneralConcentrationFormatter concentrationFormatter = ConcentrationFormatter.create(MOLE_PER_LITRE);

    private Simulation simulation;

    public FlatUpdateRecorder() {
        concentrationWriters = new HashMap<>();
        deltaWriters = new HashMap<>();
        deltaHeader = String.join(VALUE_SEPARATOR, "elapsed_time", "module", "cell_section", "chemical_entity", "delta_average")
                .concat(LINE_SEPARATOR);
        concentrationHeader = String.join(VALUE_SEPARATOR,"elapsed time", "species" , "compartment" , "concentration")
                .concat(LINE_SEPARATOR);
    }

    public static WorkspaceStep create() {
        return new EpochUpdateWriterBuilder();
    }

    public Path getWorkspaceFolder() {
        return directory;
    }

    /**
     * Initialized the directory for the current simulation.
     */
    private void createFolderStructure(boolean timestamped) {
        if (timestamped) {
            directory = appendTimestampedFolder(directory);
        }
        createDirectories(directory);
    }

    /**
     * Initializes a updatable that will be observed during simulation. Also prepares the files that will be written during
     * simulation.
     *
     * @param updatable The updatable to be observed.
     * @throws IOException If the file can not be created.
     */
    public void addUpdatableToObserve(Updatable updatable) throws IOException {
        logger.info("Observing updatable {}, results will be written to {}.", updatable.getStringIdentifier(), directory);
        // add concentration writers
        Path concentrationFile = Recorders.createFile(directory, updatable.getStringIdentifier() + "_concentrations.csv");
        BufferedWriter concentrationWriter = Files.newBufferedWriter(concentrationFile);
        concentrationWriters.put(updatable, concentrationWriter);
        writeConcentrationFileHeader(updatable);
        // add delta writers
        Path deltaFile = Recorders.createFile(directory, updatable.getStringIdentifier() + "_deltas.csv");
        BufferedWriter deltaWriter = Files.newBufferedWriter(deltaFile);
        deltaWriters.put(updatable, deltaWriter);
        writeDeltaFileHeader(updatable);
    }

    /**
     * Writes the header for delta files.
     *
     * @param updatable The updatable.
     * @throws IOException If the file could not be written.
     */
    private void writeDeltaFileHeader(Updatable updatable) throws IOException {
        appendDeltaContent(updatable, deltaHeader);
    }

    /**
     * Writes the header for concentration files.
     *
     * @param updatable The node.
     * @throws IOException If the file could not be written.
     */
    private void writeConcentrationFileHeader(Updatable updatable) throws IOException {
        appendConcentrationContent(updatable, concentrationHeader);
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
        logger.info("Simulation observation successfully written to {}.", directory);
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

        for (ChemicalEntity entity : node.getConcentrationContainer().getReferencedEntities()) {
            for (CellSubsection cellSection : referencedSections) {
                sb.append(timeFormatter.format(event.getTime())).append(VALUE_SEPARATOR)
                        .append(entity.getIdentifier()).append(VALUE_SEPARATOR)
                        .append(cellSection.getIdentifier()).append(VALUE_SEPARATOR)
                        .append(concentrationFormatter.format(node.getConcentrationContainer().get(cellSection, entity)))
                        .append(LINE_SEPARATOR);
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
                .collect(Collectors.groupingBy(delta -> delta.getModule().toString() + VALUE_SEPARATOR +
                        delta.getCellSubsection().getIdentifier() + VALUE_SEPARATOR +
                        delta.getChemicalEntity().getIdentifier()))
                .entrySet()
                .stream()
                .map(entry -> timeFormatter.format(event.getTime()) + VALUE_SEPARATOR
                        + entry.getKey() + VALUE_SEPARATOR
                        + concentrationFormatter.format(entry.getValue().stream()
                        .mapToDouble(ConcentrationDelta::getValue)
                        .average()
                        .orElse(Double.NaN)))
                .collect(Collectors.joining(LINE_SEPARATOR, "", LINE_SEPARATOR));

        try {
            appendDeltaContent(updatable, collect);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public interface WorkspaceStep {
        DirectoryStep workspace(Path workspace);
    }

    public interface DirectoryStep {
        SimulationStep directory(String directory, boolean timeStamped);
        SimulationStep directory(String directory);
    }

    public interface SimulationStep {
        ConcentrationUnitStep simulation(Simulation simulation);
    }

    public interface ConcentrationUnitStep {
        TimeUnitStep concentrationFormat(GeneralConcentrationFormatter quantityFormatter);
        TimeUnitStep concentrationUnit(Unit<MolarConcentration> concentrationUnit);

        FlatUpdateRecorder build();
    }

    public interface TimeUnitStep {
        BuildStep timeFormat(QuantityFormatter<Time> timeFormatter);
        BuildStep timeUnit(Unit<Time> timeUnit);
        FlatUpdateRecorder build();

    }

    public interface BuildStep {
        FlatUpdateRecorder build();
    }

    public static class EpochUpdateWriterBuilder implements WorkspaceStep, DirectoryStep, SimulationStep, ConcentrationUnitStep, TimeUnitStep, BuildStep {

        private FlatUpdateRecorder writer;

        public EpochUpdateWriterBuilder() {
            writer = new FlatUpdateRecorder();
        }

        @Override
        public DirectoryStep workspace(Path workspace) {
            writer.workspacePath = workspace;
            return this;
        }

        @Override
        public SimulationStep directory(String directory, boolean timestamped) {
            writer.directory = writer.workspacePath.resolve(directory);
            writer.createFolderStructure(timestamped);
            return this;
        }

        @Override
        public SimulationStep directory(String directory) {
            return directory(directory, true);
        }

        @Override
        public ConcentrationUnitStep simulation(Simulation simulation) {
            writer.simulation = simulation;
            return this;
        }

        @Override
        public TimeUnitStep concentrationFormat(GeneralConcentrationFormatter concentrationFormatter) {
            writer.concentrationFormatter = concentrationFormatter;
            return this;
        }

        @Override
        public TimeUnitStep concentrationUnit(Unit<MolarConcentration> concentrationUnit) {
            writer.concentrationFormatter = ConcentrationFormatter.create(concentrationUnit);
            return this;
        }

        @Override
        public BuildStep timeFormat(QuantityFormatter<Time> timeFormatter) {
            writer.timeFormatter = timeFormatter;
            return this;
        }

        @Override
        public BuildStep timeUnit(Unit<Time> timeUnit) {
            writer.timeFormatter = GeneralQuantityFormatter.forUnit(timeUnit);
            return this;
        }

        @Override
        public FlatUpdateRecorder build() {
            return writer;
        }

    }

}
