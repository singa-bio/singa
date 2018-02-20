package de.bioforscher.singa.simulation.events;

import de.bioforscher.singa.chemistry.descriptive.entities.ChemicalEntity;
import de.bioforscher.singa.core.events.UpdateEventListener;
import de.bioforscher.singa.features.model.QuantityFormatter;
import de.bioforscher.singa.features.parameters.EnvironmentalParameters;
import de.bioforscher.singa.features.quantities.MolarConcentration;
import de.bioforscher.singa.simulation.model.compartments.CellSection;
import de.bioforscher.singa.simulation.model.graphs.AutomatonNode;

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
    private final boolean printEntityInformation;
    private final Map<AutomatonNode, BufferedWriter> registeredWriters;
    private final List<ChemicalEntity<?>> observedEntities;

    private QuantityFormatter<Time> timeFormatter = new QuantityFormatter<>(MILLI(SECOND), false);
    private QuantityFormatter<MolarConcentration> concentrationFormatter = new QuantityFormatter<>(MOLE_PER_LITRE, false);

    public EpochUpdateWriter(Path workspacePath, Path folder, Set<ChemicalEntity<?>> entitiesToObserve) {
        this(workspacePath, folder, entitiesToObserve, true);
    }

    public EpochUpdateWriter(Path workspacePath, Path folder, Set<ChemicalEntity<?>> entitiesToObserve, boolean printEntityInformation) {
        this.workspacePath = workspacePath;
        this.folder = folder;
        createFolderStructure();
        observedEntities = initializeOrdering(entitiesToObserve);
        this.printEntityInformation = printEntityInformation;
        registeredWriters = new HashMap<>();

    }

    private List<ChemicalEntity<?>> initializeOrdering(Set<ChemicalEntity<?>> unorderedEntities) {
        return new ArrayList<>(unorderedEntities);
    }

    public void addNodeToObserve(AutomatonNode node) throws IOException {
        Path file = createFile("node_" + node.getIdentifier() + ".csv");
        BufferedWriter writer = Files.newBufferedWriter(file);
        registeredWriters.put(node, writer);
        node.setObserved(true);
        writeHeader(node);
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

    private void writeHeader(AutomatonNode node) throws IOException {
        StringBuilder sb = new StringBuilder();
        if (printEntityInformation) {
            sb.append(COMMENT_CHARACTER)
                    .append(" Node ")
                    .append(node.getIdentifier())
                    .append(LINEBREAK)
                    .append(prepareEntityInformation());
        }
        sb.append(prepareColumnHeader(node));
        appendContent(node, sb.toString());
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

    private String prepareColumnHeader(AutomatonNode node) {
        Set<CellSection> referencedSections = node.getAllReferencedSections();
        StringBuilder sb = new StringBuilder();
        sb.append("elapsed time (").append(EnvironmentalParameters.getTimeStep().getUnit()).append(")").append(SEPARATOR_CHARACTER);
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

    private void appendContent(AutomatonNode node, String content) throws IOException {
        registeredWriters.get(node).write(content);
    }

    public void closeWriters() {
        for (BufferedWriter bufferedWriter : registeredWriters.values()) {
            try {
                bufferedWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onEventReceived(NodeUpdatedEvent event) {
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
            appendContent(node, sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
