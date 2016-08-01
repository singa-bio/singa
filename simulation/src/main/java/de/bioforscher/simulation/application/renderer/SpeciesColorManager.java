package de.bioforscher.simulation.application.renderer;

import de.bioforscher.chemistry.descriptive.ChemicalEntity;
import de.bioforscher.simulation.application.components.species.ColorableChemicalEntity;
import javafx.scene.paint.Color;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Christoph on 01.08.2016.
 */
public class SpeciesColorManager {

    private static SpeciesColorManager instance;

    private Map<ChemicalEntity, ColorableChemicalEntity> assignedColors;

    public static SpeciesColorManager getInstance() {
        if (instance == null) {
            synchronized (SpeciesColorManager.class) {
                instance = new SpeciesColorManager();
            }
        }
        return instance;
    }

    private SpeciesColorManager() {
        this.assignedColors = new HashMap<>();
    }

    public void initializeEntity(ChemicalEntity entity, Color color) {
        if (!this.containsEntity(entity)) {
            this.assignedColors.put(entity, new ColorableChemicalEntity(entity, color));
        }
    }

    public Color getColor(ChemicalEntity entity) {
        return this.assignedColors.get(entity).getColor();
    }

    public void setColor(ChemicalEntity entity, Color color) {
        if (this.containsEntity(entity)) {
            this.assignedColors.get(entity).setColor(color);
        } else {
            initializeEntity(entity, color);
        }
    }

    public boolean getVisibility(ChemicalEntity entity) {
        return this.assignedColors.get(entity).isVisible();
    }

    public void setVisibility(ChemicalEntity entity, boolean visibility) {
        this.assignedColors.get(entity).setVisible(visibility);
    }

    public boolean containsEntity(ChemicalEntity entity) {
        return this.assignedColors.containsKey(entity);
    }

    public boolean containsColor(Color color) {
        return this.assignedColors.containsValue(color);
    }

}
