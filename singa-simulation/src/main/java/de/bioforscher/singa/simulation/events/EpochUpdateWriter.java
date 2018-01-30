package de.bioforscher.singa.simulation.events;

import de.bioforscher.singa.chemistry.descriptive.entities.ChemicalEntity;
import de.bioforscher.singa.core.events.UpdateEventListener;
import de.bioforscher.singa.simulation.model.graphs.AutomatonNode;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;

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
    private static final String LINEBREAK = System.getProperty("line.separator");

    private final Path workspacePath;
    private final Path folder;
    private final boolean printEntityInformation;
    private final Map<AutomatonNode, BufferedWriter> registeredWriters;
    private final List<ChemicalEntity<?>> observedEntities;

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
        BufferedWriter writer = Files.newBufferedWriter(file, StandardOpenOption.APPEND);
        registeredWriters.put(node, writer);
        writeHeader(node);
    }

    private void createFolderStructure() {
        Path workspaceFolder = Paths.get(workspacePath.toString(), folder.toString());
        try {
            if (!Files.exists(workspaceFolder)) {
                Files.createDirectory(workspaceFolder);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Path createFile(String fileName) throws IOException {
        Path file = Paths.get(workspacePath.toString(), folder.toString(), fileName);
        if (!Files.exists(file)) {
            Files.createFile(file);
        }
        return file;
    }

    private void writeHeader(AutomatonNode node) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append(COMMENT_CHARACTER)
                .append(" Node ")
                .append(node.getIdentifier())
                .append(LINEBREAK);
        if (printEntityInformation) {
            sb.append(prepareEntityInformation());
        }
        sb.append(prepareEntityHeader());
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

    private String prepareEntityHeader() {
        StringBuilder sb = new StringBuilder();
        sb.append("epoch")
                .append(SEPARATOR_CHARACTER);
        int count = 0;
        for (ChemicalEntity entity : observedEntities) {
            if (count < observedEntities.size() - 1) {
                sb.append(entity.getName())
                        .append(SEPARATOR_CHARACTER);
            } else {
                sb.append(entity.getName())
                        .append(LINEBREAK);
            }
            count++;
        }
        return sb.toString();
    }

    private void appendContent(AutomatonNode node, String content) throws IOException {
        registeredWriters.get(node).write(content);
    }

    @Override
    public void onEventReceived(NodeUpdatedEvent event) {
        StringBuilder sb = new StringBuilder();
        sb.append(event.getTime().to(MILLI(SECOND)).getValue()).append(SEPARATOR_CHARACTER);
        int count = 0;
        for (ChemicalEntity entity : observedEntities) {
            if (count < observedEntities.size() - 1) {
                sb.append(Double.toString(event.getNode().getAllConcentrations().get(entity).getValue().doubleValue()))
                        .append(SEPARATOR_CHARACTER);
            } else {
                sb.append(Double.toString(event.getNode().getAllConcentrations().get(entity).getValue().doubleValue()))
                        .append(LINEBREAK);
            }
            count++;
        }
        try {
            appendContent(event.getNode(), sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
