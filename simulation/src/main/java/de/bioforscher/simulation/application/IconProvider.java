package de.bioforscher.simulation.application;

import javafx.scene.image.Image;

import java.io.InputStream;

/**
 * Created by Christoph on 04.07.2016.
 */
public class IconProvider {

    public static final String MOLECULE_ICON_NAME = "icon_small_molecule_32x.png";
    public static final String MOLECULE_ICON_PATH = getResourceAsString(MOLECULE_ICON_NAME);
    public static final Image MOLECULE_ICON_IMAGE = new Image(getResourceAsStream(MOLECULE_ICON_NAME));

    public static final String GENERIC_REACTION_ICON_NAME = "icon_reaction_generic_32x.png";
    public static final String GENERIC_REACTION_ICON_PATH = getResourceAsString(GENERIC_REACTION_ICON_NAME);
    public static final Image GENERIC_REACTION_ICON_IMAGE = new Image(getResourceAsStream(GENERIC_REACTION_ICON_NAME));

    private static InputStream getResourceAsStream(String resource) {
        return IconProvider.class.getResourceAsStream(resource);
    }

    private static String getResourceAsString(String resource) {
        return IconProvider.class.getResource(resource).getPath();
    }

}
