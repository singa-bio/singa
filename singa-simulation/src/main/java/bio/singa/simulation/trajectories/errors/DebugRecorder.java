package bio.singa.simulation.trajectories.errors;

import bio.singa.core.events.UpdateEventListener;
import bio.singa.simulation.events.GraphUpdatedEvent;
import bio.singa.simulation.trajectories.Recorders;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author cl
 */
public class DebugRecorder implements UpdateEventListener<GraphUpdatedEvent> {

    private Path debugDirectory;
    private Path debugFile;
    private Map<Long, List<String>> information;

    public DebugRecorder() {
        information = new HashMap<>();
    }

    public Path getDebugDirectory() {
        return debugDirectory;
    }

    public void setDebugDirectory(Path debugDirectory) {
        this.debugDirectory = debugDirectory;
    }

    public void prepare() {
        Recorders.createDirectories(debugDirectory);
        debugFile = Recorders.createFile(debugDirectory,"debug.log");
    }

    public void addInformation(long epoch, String newInformation) {
        if (!information.containsKey(epoch)) {
            information.put(epoch, new ArrayList<>());
        }
        information.get(epoch).add(newInformation);
    }

    public void clear() {
        information.clear();
    }

    @Override
    public void onEventReceived(GraphUpdatedEvent event) {
        StringBuilder debugStringBuilder = new StringBuilder();
        for (Map.Entry<Long, List<String>> entry : information.entrySet()) {
            long epoch = entry.getKey();
            debugStringBuilder.append("epoch ")
                    .append(epoch)
                    .append(System.lineSeparator());
            List<String> strings = entry.getValue();
            debugStringBuilder.append(String.join(System.lineSeparator(), strings))
                    .append(System.lineSeparator());
        }
        try {
            Files.write(debugFile, debugStringBuilder.toString().getBytes(), StandardOpenOption.APPEND);
        } catch (IOException e) {
            e.printStackTrace();
        }
        information.clear();
    }
}
