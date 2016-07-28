package de.bioforscher.simulation.application.components.plots;

import de.bioforscher.simulation.application.IconProvider;
import de.bioforscher.simulation.application.components.species.ColorableChemicalEntity;
import de.bioforscher.simulation.application.components.species.ColoredEntityCell;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * Created by Christoph on 20.07.2016.
 */
public class PlotCard extends GridPane {

    private ConcentrationPlot plot;

    private HBox toolBar = new HBox();
    private ListView<ColorableChemicalEntity> speciesList = new ListView<>();

    public PlotCard(ConcentrationPlot plot) {
        this.plot = plot;
        configureGrid();
        configurePlot();
        configureToolBar();
        configureSpeciesList();
        addControlsToGrid();
    }

    private void configureGrid() {
        this.setHgap(10);
        this.setVgap(4);
        this.setPadding(new Insets(0, 10, 0, 10));
        this.setStyle("-fx-border-color: #dcdcdc;" +
                "-fx-border-radius: 5;");
    }

    private void configureSpeciesList() {
        this.speciesList.setCellFactory(param -> new ColoredEntityCell(this.plot));
        this.speciesList.setItems(this.getPlot().getObservedEntities());
    }

    private void configurePlot() {
        this.plot.setLegendVisible(false);
    }

    private void configureToolBar() {
        Button options = IconProvider.FontAwesome.createIconButton(IconProvider.FontAwesome.ICON_COGS);
        Button export = IconProvider.FontAwesome.createIconButton(IconProvider.FontAwesome.ICON_DOWNLOAD);
        Button remove = IconProvider.FontAwesome.createIconButton(IconProvider.FontAwesome.ICON_REMOVE);
        this.toolBar.setAlignment(Pos.TOP_RIGHT);
        this.toolBar.setPadding(new Insets(10, 0, 10, 0));
        this.toolBar.setSpacing(5);
        this.toolBar.getChildren().addAll(options, export, remove);
    }

    private HBox generateTitle() {
        HBox box = new HBox();
        box.setAlignment(Pos.CENTER);
        Label title = new Label("Concentration Plot for Node " + this.plot.getReferencedNode().getIdentifier());
        title.setFont(Font.font(null, FontWeight.BOLD, 14));
        box.getChildren().add(title);
        return box;
    }

    private void addControlsToGrid() {
        this.add(generateTitle(), 0, 0);
        this.add(this.plot, 0, 1);
        this.add(this.toolBar, 1, 0);
        this.add(this.speciesList, 1, 1);
    }

    public ConcentrationPlot getPlot() {
        return this.plot;
    }


}
