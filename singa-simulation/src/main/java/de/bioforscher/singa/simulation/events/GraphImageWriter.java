package de.bioforscher.singa.simulation.events;

import de.bioforscher.singa.chemistry.descriptive.entities.ChemicalEntity;
import de.bioforscher.singa.core.events.UpdateEventListener;
import de.bioforscher.singa.simulation.renderer.AutomatonGraphRenderer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * @author cl
 */
public class GraphImageWriter implements UpdateEventListener<GraphUpdatedEvent> {

    /**
     * The path to the user defined workspace.
     */
    private final Path workspacePath;

    /**
     * The folder for the current simulation.
     */
    private final Path folder;

    /**
     * The entities that should be observed.
     */
    private final List<ChemicalEntity> observedEntities;

    AutomatonGraphRenderer renderer;

    public GraphImageWriter(Path workspacePath, Path folder, List<ChemicalEntity> observedEntities) {
        this.workspacePath = workspacePath;
        this.folder = folder;
        this.observedEntities = observedEntities;
        createFolderStructure();
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
            for (ChemicalEntity observedEntity : observedEntities) {
                Files.createDirectory(workspaceFolder.resolve(observedEntity.getIdentifier().getIdentifier().replace("(\\W|^_)*", "_")));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onEventReceived(GraphUpdatedEvent event) {

    }
}
