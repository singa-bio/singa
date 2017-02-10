package de.bioforscher.simulation.parser;

import de.bioforscher.chemistry.descriptive.ChemicalEntity;
import de.bioforscher.core.events.UpdateEventListener;
import de.bioforscher.simulation.model.BioNode;
import de.bioforscher.simulation.model.NodeUpdatedEvent;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;

/**
 * This class can be used to write the concentrations of a node to a file while
 * simulating.
 *
 * @author Christoph Leberecht
 */
public class EpochUpdateWriter implements UpdateEventListener<NodeUpdatedEvent> {

    private static char COMMENT_CHARACTER = '#';
    private static char SEPARATOR_CHARACTER = ',';
    private static String LINEBREAK = System.getProperty("line.separator");

    private Path workspacePath;
    private Path folder;
    private boolean printEntityInformation;
    private Map<BioNode, BufferedWriter> registeredWriters;
    private List<ChemicalEntity<?>> observedEntities;

    public EpochUpdateWriter(Path workspacePath, Path folder, Set<ChemicalEntity<?>> entitiesToObserve) throws IOException {
        this(workspacePath, folder, entitiesToObserve, true);
    }

    public EpochUpdateWriter(Path workspacePath, Path folder, Set<ChemicalEntity<?>> entitiesToObserve, boolean printEntityInformation) throws IOException {
        this.workspacePath = workspacePath;
        this.folder = folder;
        createFolderStructure();
        this.observedEntities = initializeOrdering(entitiesToObserve);
        this.printEntityInformation = printEntityInformation;
        this.registeredWriters = new HashMap<>();

    }

    private List<ChemicalEntity<?>> initializeOrdering(Set<ChemicalEntity<?>> unorderedEntities) {
        return new ArrayList<>(unorderedEntities);
    }

    public void addNodeToObserve(BioNode node) throws IOException {
        Path file = createFile("node_" + node.getIdentifier() + ".csv");
        BufferedWriter writer = Files.newBufferedWriter(file, StandardOpenOption.APPEND);
        this.registeredWriters.put(node, writer);
        writeHeader(node);
    }

    private void createFolderStructure() {
        Path workspaceFolder = Paths.get(this.workspacePath.toString(), this.folder.toString());
        try {
            if (!Files.exists(workspaceFolder)) {
                Files.createDirectory(workspaceFolder);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Path createFile(String fileName) throws IOException {
        Path file = Paths.get(this.workspacePath.toString(), this.folder.toString(), fileName);
        if (!Files.exists(file)) {
            Files.createFile(file);
        }
        return file;
    }

    private void writeHeader(BioNode node) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append(COMMENT_CHARACTER)
                .append(" Node ")
                .append(node.getIdentifier())
                .append(LINEBREAK);
        if (this.printEntityInformation) {
            sb.append(prepareEntityInformation());
        }
        sb.append(prepareEntityHeader());
        appendContent(node, sb.toString());
    }

    private String prepareEntityInformation() {
        StringBuilder sb = new StringBuilder();
        for (ChemicalEntity entity : this.observedEntities) {
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
        for (ChemicalEntity entity : this.observedEntities) {
            if (count < this.observedEntities.size() - 1) {
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

    private void appendContent(BioNode node, String content) throws IOException {
        this.registeredWriters.get(node).write(content);
    }

    @Override
    public void onEventReceived(NodeUpdatedEvent event) {
        StringBuilder sb = new StringBuilder();
        sb.append(Integer.toString(event.getEpoch())).append(SEPARATOR_CHARACTER);
        int count = 0;
        for (ChemicalEntity entity : this.observedEntities) {
            if (count < this.observedEntities.size() - 1) {
                sb.append(Double.toString(event.getNode().getConcentrations().get(entity).getValue().doubleValue()))
                        .append(SEPARATOR_CHARACTER);
            } else {
                sb.append(Double.toString(event.getNode().getConcentrations().get(entity).getValue().doubleValue()))
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
