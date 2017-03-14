package de.bioforscher.simulation.application.renderer;

import de.bioforscher.chemistry.descriptive.ChemicalEntity;
import de.bioforscher.simulation.application.components.entities.ColorableChemicalEntity;
import de.bioforscher.simulation.model.compartments.CellSection;
import javafx.scene.paint.Color;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author cl
 */
public class ColorManager {

    private static ColorManager instance;

    private Map<ChemicalEntity, ColorableChemicalEntity> entityColorMap;
    private Map<String, Color> cellSectionColorMap;

    public static ColorManager getInstance() {
        if (instance == null) {
            synchronized (ColorManager.class) {
                instance = new ColorManager();
            }
        }
        return instance;
    }

    private ColorManager() {
        this.entityColorMap = new HashMap<>();
        this.cellSectionColorMap = new HashMap<>();
    }

    public static Color generateRandomColor() {
        return Color.rgb(ThreadLocalRandom.current().nextInt(255),
                ThreadLocalRandom.current().nextInt(255),
                ThreadLocalRandom.current().nextInt(255));
    }

    public static String getHexColor(Color color) {
        return String.format("#%02X%02X%02X",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));
    }

    public void initializeEntity(ChemicalEntity entity, Color color) {
        if (!this.containsEntity(entity)) {
            this.entityColorMap.put(entity, new ColorableChemicalEntity(entity, color));
        }
    }

    public Color getColor(ChemicalEntity entity) {
        return this.entityColorMap.get(entity).getColor();
    }

    public Color getColor(CellSection cellSection) {
        return getSectionColor(cellSection.getIdentifier());
    }

    public Color getSectionColor(String compartmentIdentifier) {
        return this.cellSectionColorMap.get(compartmentIdentifier);
    }

    public void setColor(ChemicalEntity entity, Color color) {
        if (this.containsEntity(entity)) {
            this.entityColorMap.get(entity).setColor(color);
        } else {
            initializeEntity(entity, color);
        }
    }

    public void setColor(CellSection cellSection, Color color) {
        this.cellSectionColorMap.put(cellSection.getIdentifier(), color);
    }

    public boolean getVisibility(ChemicalEntity entity) {
        return this.entityColorMap.get(entity).isVisible();
    }

    public void setVisibility(ChemicalEntity entity, boolean visibility) {
        this.entityColorMap.get(entity).setVisible(visibility);
    }

    private boolean containsEntity(ChemicalEntity entity) {
        return this.entityColorMap.containsKey(entity);
    }

}
