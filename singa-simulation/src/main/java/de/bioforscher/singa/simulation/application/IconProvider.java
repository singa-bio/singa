package de.bioforscher.singa.simulation.application;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.text.Font;

import java.io.InputStream;

/**
 * @author cl
 */
public class IconProvider {

    private static final String FONT_AWESOME_NAME = "fontawesome-webfont.ttf";
    public static final Font FONT_AWESOME_SMALL = Font.loadFont(getResourceAsStream(FONT_AWESOME_NAME), 16);
    public static final Font FONT_AWESOME_LARGE = Font.loadFont(getResourceAsStream(FONT_AWESOME_NAME), 24);

    private static final String MOLECULE_ICON_NAME = "icon_small_molecule_32x.png";
    private static final String MOLECULE_ICON_PATH = getResourceAsString(MOLECULE_ICON_NAME);
    public static final Image MOLECULE_ICON_IMAGE = new Image(getResourceAsStream(MOLECULE_ICON_NAME));

    private static final String PROTEIN_ICON_NAME = "icon_protein_32x.png";
    private static final String PROTEIN_ICON_PATH = getResourceAsString(PROTEIN_ICON_NAME);
    public static final Image PROTEIN_ICON_IMAGE = new Image(getResourceAsStream(PROTEIN_ICON_NAME));

    private static final String COMPLEX_ICON_NAME = "icon_complex_32x.png";
    private static final String COMPLEX_ICON_PATH = getResourceAsString(COMPLEX_ICON_NAME);
    public static final Image COMPLEX_ICON_IMAGE = new Image(getResourceAsStream(COMPLEX_ICON_NAME));

    private static final String GENERIC_REACTION_ICON_NAME = "icon_reaction_generic_32x.png";
    private static final String GENERIC_REACTION_ICON_PATH = getResourceAsString(GENERIC_REACTION_ICON_NAME);
    public static final Image GENERIC_REACTION_ICON_IMAGE = new Image(getResourceAsStream(GENERIC_REACTION_ICON_NAME));

    private static final String REACTIONS_ICON_NAME = "icon_reactions_128x.png";
    private static final String REACTIONS_ICON_PATH = getResourceAsString(REACTIONS_ICON_NAME);
    public static final Image REACTIONS_ICON_IMAGE = new Image(getResourceAsStream(REACTIONS_ICON_NAME));

    private static final String DIFFUSION_ICON_NAME = "icon_diffusion_128x.png";
    private static final String DIFFUSION_ICON_PATH = getResourceAsString(DIFFUSION_ICON_NAME);
    public static final Image DIFFUSION_ICON_IMAGE = new Image(getResourceAsStream(DIFFUSION_ICON_NAME));

    private static InputStream getResourceAsStream(String resource) {
        return IconProvider.class.getResourceAsStream(resource);
    }

    private static String getResourceAsString(String resource) {
        return IconProvider.class.getResource(resource).getPath();
    }

    public static class FontAwesome {

        public static final String ICON_DOWNLOAD = "\uf01a";
        public static final String ICON_PLAY = "\uf04b";
        public static final String ICON_PAUSE = "\uf04c";
        public static final String ICON_COGS = "\uf085";
        public static final String ICON_EXCHANGE = "\uf0ec";
        public static final String ICON_DOT_CIRCLE = "\uf192";
        public static final String ICON_LINE_CHART = "\uf201";
        public static final String ICON_FILE_XML = "\uf1c9";
        public static final String ICON_DATABASE = "\uf1c0";
        public static final String ICON_SQUARE_FULL = "\uf0c8";
        public static final String ICON_SQUARE_EMPTY = "\uf096";

        // adapted from: http://www.jensd.de/wordpress/?p=132

        public static Button createIconButton(String iconName) {
            return createIconButton(iconName, 16);
        }

        public static Button createIconButton(String iconName, int iconSize) {
            Label icon = createIconLabel(iconName);
            return new Button("", icon);
        }

        public static Label createIconLabel(String iconName) {
            return createIconLabel(iconName, 16);
        }

        public static Label createIconLabel(String iconName, int iconSize) {
            Label icon = new Label(iconName);
            icon.setFont(IconProvider.FONT_AWESOME_SMALL);
            return icon;
        }
    }



}
