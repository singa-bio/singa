package de.bioforscher.simulation.modules;

import de.bioforscher.simulation.application.IconProvider;
import javafx.scene.image.Image;

/**
 * Created by Christoph on 03.08.2016.
 */
public enum AvailableModule {

    FREE_DIFFUSION(IconProvider.DIFFUSION_ICON_IMAGE, "Free Diffusion"),
    CHEMICAL_REACTION(IconProvider.REACTIONS_ICON_IMAGE, "Chemical Reactions");

    private final Image icon;
    private final String representativeName;

    AvailableModule(Image icon, String representativeName) {
        this.icon = icon;
        this.representativeName = representativeName;
    }

    public Image getIcon() {
        return this.icon;
    }

    public String getRepresentativeName() {
        return this.representativeName;
    }

}
