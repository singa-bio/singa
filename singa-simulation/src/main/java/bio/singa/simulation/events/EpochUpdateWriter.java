package bio.singa.simulation.events;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.core.events.UpdateEventListener;
import bio.singa.features.formatter.GeneralQuantityFormatter;
import bio.singa.features.formatter.QuantityFormatter;
import bio.singa.features.quantities.MolarConcentration;
import bio.singa.simulation.model.modules.UpdateModule;
import bio.singa.simulation.model.modules.concentration.ConcentrationBasedModule;
import bio.singa.simulation.model.modules.concentration.ConcentrationDelta;
import bio.singa.simulation.model.modules.concentration.imlementations.ComplexBuildingReaction;
import bio.singa.simulation.model.modules.concentration.imlementations.Reaction;
import bio.singa.simulation.model.sections.CellSubsection;
import bio.singa.simulation.model.simulation.Simulation;
import bio.singa.simulation.model.simulation.SimulationManager;
import bio.singa.simulation.model.simulation.Updatable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.measure.quantity.Time;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static bio.singa.features.units.UnitProvider.MOLE_PER_LITRE;
import static tec.uom.se.unit.MetricPrefix.MILLI;
import static tec.uom.se.unit.Units.SECOND;

/**
 * This class can be used to write the concentration of chemical entities of a node and the changes applied to the node
 * to a file. The updates are written each time a event is received. The events can be scheduled by using the
 * {@link SimulationManager#setUpdateEmissionCondition(Predicate)}. A node is observed by calling
 * {@link EpochUpdateWriter#addUpdatableToObserve(Updatable)}.
 *
 * @author cl
 */
public class EpochUpdateWriter implements UpdateEventListener<UpdatableUpdatedEvent> {

    private static final Logger logger = LoggerFactory.getLogger(EpochUpdateWriter.class);

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
    private static final String LINEBREAK = System.lineSeparator();

    /**
     * The path to the user defined workspace.
     */
    private Path workspacePath;

    /**
     * The folder for the current simulation.
     */
    private Path folder;

    /**
     * The writers for concentration files.
     */
    private Map<Updatable, BufferedWriter> concentrationWriters;

    /**
     * The writers for deltas or changes.
     */
    private Map<Updatable, BufferedWriter> deltaWriters;

    /**
     * The entities that should be observed.
     */
    private List<ChemicalEntity> observedEntities;

    /**
     * The modules deltas should be recorded for.
     */
    private List<ConcentrationBasedModule> observedModules;

    /**
     * The formatter for time based values.
     */
    private QuantityFormatter<Time> timeFormatter = new GeneralQuantityFormatter<>(MILLI(SECOND), false);

    /**
     * The formatter for concentration based values.
     */
    private QuantityFormatter<MolarConcentration> concentrationFormatter = new GeneralQuantityFormatter<>(MOLE_PER_LITRE, false);


    private Simulation simulation;
    private Path workspaceFolder;

    public EpochUpdateWriter() {
        concentrationWriters = new HashMap<>();
        deltaWriters = new HashMap<>();
    }

    public static WorkspaceStep create() {
        return new EpochUpdateWriterBuilder();
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
    private void createFolderStructure(boolean timestamped) {
        workspaceFolder = workspacePath.resolve(folder);
        if (timestamped) {
            Date date = Calendar.getInstance().getTime();
            String timeStamp = new SimpleDateFormat("yyyy-MM-dd'T'HH-mm-ss'Z'").format(date);
            workspaceFolder = workspaceFolder.resolve(timeStamp);
        }
        try {
            if (!Files.exists(workspaceFolder)) {
                Files.createDirectories(workspaceFolder);
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
        Path file = workspaceFolder.resolve(fileName);
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
     * Initializes a updatable that will be observed during simulation. Also prepares the files that will be written during
     * simulation.
     *
     * @param updatable The updatable to be observed.
     * @throws IOException If the file can not be created.
     */
    public void addUpdatableToObserve(Updatable updatable) throws IOException {
        logger.info("Observing updatable {}, results will be written to {}.", updatable.getStringIdentifier(), workspaceFolder);
        // add concentration writers
        Path concentrationFile = createFile("node_" + updatable.getStringIdentifier() + "_concentrations.csv");
        BufferedWriter concentrationWriter = Files.newBufferedWriter(concentrationFile);
        concentrationWriters.put(updatable, concentrationWriter);
        writeConcentrationFileHeader(updatable);
        // add delta writers
        Path deltaFile = createFile("node_" + updatable.getStringIdentifier() + "_deltas.csv");
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
        appendDeltaContent(updatable, prepareDeltaColumnHeader());
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
     * @param updatable The node.
     * @throws IOException If the file could not be written.
     */
    private void writeConcentrationFileHeader(Updatable updatable) throws IOException {
        appendConcentrationContent(updatable, prepareConcentrationColumnHeader());
    }

    /**
     * Creates a String for the column header of concentration files.
     *
     * @return The column header.
     */
    private String prepareConcentrationColumnHeader() {
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
        logger.info("Simulation observation successfully written to {}.", workspaceFolder);
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
                        .append(concentrationFormatter.format(node.getConcentrationContainer().get(cellSection, entity)))
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
                .collect(Collectors.groupingBy(delta -> getModuleRepresentativeString(delta.getModule()) + SEPARATOR_CHARACTER +
                        delta.getCellSubsection().getIdentifier() + SEPARATOR_CHARACTER +
                        delta.getChemicalEntity().getIdentifier()))
                .entrySet()
                .stream()
                .map(entry -> timeFormatter.format(event.getTime()) + SEPARATOR_CHARACTER
                        + entry.getKey() + SEPARATOR_CHARACTER
                        + concentrationFormatter.format(entry.getValue().stream()
                        .mapToDouble(ConcentrationDelta::getValue)
                        .average()
                        .orElse(Double.NaN)))
                .collect(Collectors.joining(LINEBREAK, "", LINEBREAK));

        try {
            appendDeltaContent(updatable, collect);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getModuleRepresentativeString(UpdateModule module) {
        if (module instanceof Reaction) {
            return ((Reaction) module).getReactionString();
        } else if (module instanceof ComplexBuildingReaction) {
            return ((ComplexBuildingReaction) module).getReactionString();
        } else {
            return module.toString();
        }
    }

    public Path getWorkspaceFolder() {
        return workspaceFolder;
    }

    public interface WorkspaceStep {

        FolderStep workspace(Path workspace);

    }

    public interface FolderStep {

        SimulationStep folder(Path folder, boolean timeStamped);

    }

    public interface SimulationStep {

        EntitiesStep simulation(Simulation simulation);

    }

    public interface EntitiesStep {

        ModulesStep entities(Set<ChemicalEntity> observedEntities);

        ModulesStep allEntities();

        EpochUpdateWriter build();

    }

    public interface ModulesStep {

        ConcentrationUnitStep modules(Set<ConcentrationBasedModule> observedModules);

        ConcentrationUnitStep allModules();

        EpochUpdateWriter build();

    }

    public interface ConcentrationUnitStep {

        TimeUnitStep concentrationFormat(QuantityFormatter<MolarConcentration> quantityFormatter);

        EpochUpdateWriter build();

    }

    public interface TimeUnitStep {

        BuildStep timeFormat(QuantityFormatter<Time> timeFormatter);

        EpochUpdateWriter build();

    }

    public interface BuildStep {

        EpochUpdateWriter build();

    }

    public static class EpochUpdateWriterBuilder implements WorkspaceStep, FolderStep, SimulationStep, EntitiesStep, ModulesStep, ConcentrationUnitStep, TimeUnitStep, BuildStep {

        private EpochUpdateWriter writer;

        public EpochUpdateWriterBuilder() {
            writer = new EpochUpdateWriter();
        }

        @Override
        public FolderStep workspace(Path workspace) {
            writer.workspacePath = workspace;
            return this;
        }

        @Override
        public SimulationStep folder(Path folder, boolean timestamped) {
            writer.folder = folder;
            writer.createFolderStructure(timestamped);
            return this;
        }

        @Override
        public EntitiesStep simulation(Simulation simulation) {
            writer.simulation = simulation;
            return this;
        }

        @Override
        public ModulesStep entities(Set<ChemicalEntity> observedEntities) {
            writer.observedEntities = initializeOrdering(observedEntities);
            return this;
        }

        @Override
        public ModulesStep allEntities() {
            writer.observedEntities = new ArrayList<>(writer.simulation.getChemicalEntities());
            return this;
        }

        @Override
        public ConcentrationUnitStep modules(Set<ConcentrationBasedModule> observedModules) {
            writer.observedModules = initializeOrdering(observedModules);
            return this;
        }

        @Override
        public ConcentrationUnitStep allModules() {
            writer.observedModules = writer.simulation.getModules().stream()
                    .filter(ConcentrationBasedModule.class::isInstance)
                    .map(ConcentrationBasedModule.class::cast)
                    .collect(Collectors.toList());
            return this;
        }

        @Override
        public TimeUnitStep concentrationFormat(QuantityFormatter<MolarConcentration> concentrationFormatter) {
            writer.concentrationFormatter = concentrationFormatter;
            return this;
        }

        @Override
        public BuildStep timeFormat(QuantityFormatter<Time> timeFormatter) {
            writer.timeFormatter = timeFormatter;
            return this;
        }

        @Override
        public EpochUpdateWriter build() {
            return writer;
        }

    }

}
