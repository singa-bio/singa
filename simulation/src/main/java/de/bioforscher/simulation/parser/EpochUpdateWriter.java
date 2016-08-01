package de.bioforscher.simulation.parser;

import de.bioforscher.chemistry.descriptive.Species;
import de.bioforscher.core.events.UpdateEventListener;
import de.bioforscher.simulation.model.BioNode;
import de.bioforscher.simulation.model.NodeUpdatedEvent;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class can be used to write the concentrations of a node to a file while
 * simulating.
 *
 * @author Christoph Leberecht
 */
public class EpochUpdateWriter implements UpdateEventListener<NodeUpdatedEvent> {

    public static char COMMENT_CHARCTER = '#';
    public static char SEPERATOR_CHARACTER = ',';
    public static char LINEBREAK = '\n';

    private Path workspacePath;
    private Path folder;
    private boolean printSpeciesInformation;
    private Map<BioNode, BufferedWriter> registeredWriters;
    private Map<String, Species> speciesToObserve;
    private List<Species> orderingOfSpecies;

    public EpochUpdateWriter(Path workspacePath, Path folder, Map<String, Species> speciesToObserve) throws IOException {
        this(workspacePath, folder, speciesToObserve, true);
    }

    public EpochUpdateWriter(Path workspacePath, Path folder, Map<String, Species> speciesToObserve, boolean printSpeciesInformation) throws IOException {
        this.workspacePath = workspacePath;
        this.folder = folder;
        createFolderStructure();
        this.speciesToObserve = speciesToObserve;
        this.printSpeciesInformation = printSpeciesInformation;
        this.orderingOfSpecies = new ArrayList<>();
        this.registeredWriters = new HashMap<>();
        initializeOrdering();
    }

    private void initializeOrdering() {
        this.orderingOfSpecies.addAll(this.speciesToObserve.values());
    }

    public void addNodeToObserve(BioNode node) throws IOException {
        Path file = createFile("node_" + node.getIdentifier() + ".csv");
        BufferedWriter writer = Files.newBufferedWriter(file, StandardOpenOption.APPEND);
        this.registeredWriters.put(node, writer);
        writeHeader(node);
    }

    private void createFolderStructure() {
        Path workspacefolder = Paths.get(this.workspacePath.toString(), this.folder.toString());
        try {
            if (!Files.exists(workspacefolder)) {
                Files.createDirectory(workspacefolder);
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
        sb.append(COMMENT_CHARCTER + " Node " + node.getIdentifier());
        if (this.printSpeciesInformation) {
            sb.append(prepareSpeciesInformation());
        }
        sb.append(prepareSpeciesHeader());
        appendContent(node, sb.toString());
    }

    private String prepareSpeciesInformation() {
        StringBuilder sb = new StringBuilder();
        for (Species species : this.orderingOfSpecies) {
            sb.append(COMMENT_CHARCTER + " " + species.getName() + " " + species.getIdentifier() + LINEBREAK);
        }
        return sb.toString();
    }

    private String prepareSpeciesHeader() {
        StringBuilder sb = new StringBuilder();
        int count = 0;
        for (Species species : this.orderingOfSpecies) {
            if (count < this.orderingOfSpecies.size() - 1) {
                sb.append(species.getName() + SEPERATOR_CHARACTER);
            } else {
                sb.append(species.getName() + LINEBREAK);
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
        sb.append(Integer.toString(event.getEpoch())).append(SEPERATOR_CHARACTER);
        int count = 0;
        for (Species species : this.orderingOfSpecies) {
            if (count < event.getNode().getConcentrations().size() - 1) {
                sb.append(Double.toString(event.getNode().getConcentrations().get(species).getValue().doubleValue())).append(SEPERATOR_CHARACTER);
            } else {
                sb.append(Double.toString(event.getNode().getConcentrations().get(species).getValue().doubleValue())).append(LINEBREAK);
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
