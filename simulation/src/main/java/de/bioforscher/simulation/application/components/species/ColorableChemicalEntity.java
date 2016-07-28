package de.bioforscher.simulation.application.components.species;

import de.bioforscher.chemistry.descriptive.ChemicalEntity;
import javafx.scene.paint.Color;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Christoph on 26.07.2016.
 */
public class ColorableChemicalEntity {

    private ChemicalEntity entity;
    private Color color;
    private boolean visible;

    public ColorableChemicalEntity(ChemicalEntity entity) {
        this(entity, generateRandomColor());
    }

    public ColorableChemicalEntity(ChemicalEntity entity, Color color) {
        this.entity = entity;
        this.color = color;
        this.visible = true;
    }

    public ChemicalEntity getEntity() {
        return this.entity;
    }

    public void setEntity(ChemicalEntity entity) {
        this.entity = entity;
    }

    public Color getColor() {
        return this.color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public String getHexColor() {
        return String.format("#%02X%02X%02X",
                (int) (this.color.getRed() * 255),
                (int) (this.color.getGreen() * 255),
                (int) (this.color.getBlue() * 255));
    }

    public boolean isVisible() {
        return this.visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    private static Color generateRandomColor() {
        return Color.rgb(ThreadLocalRandom.current().nextInt(255),
                ThreadLocalRandom.current().nextInt(255),
                ThreadLocalRandom.current().nextInt(255));
    }
}
