package de.bioforscher.simulation.application.components.species;

import de.bioforscher.simulation.application.IconProvider;
import de.bioforscher.simulation.application.components.plots.ConcentrationPlot;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * Created by Christoph on 26.07.2016.
 */
public class ColoredEntityCell extends ListCell<ColorableChemicalEntity> {

    private ConcentrationPlot plot;

    private GridPane grid = new GridPane();
    private Label name = new Label();
    private Label legendIndicator = new Label();

    private ContextMenu contextMenu = new ContextMenu();
    private MenuItem hideItem = new MenuItem();

    public ColoredEntityCell(ConcentrationPlot plot) {
        this.plot = plot;
        configureGrid();
        configureLegendIndicator();
        configureName();
        configureContextMenu();
        addControlsToGrid();
    }

    private void configureGrid() {
        this.grid.setHgap(10);
        this.grid.setVgap(4);
        this.grid.setPadding(new Insets(0, 10, 0, 10));
    }

    private void configureName() {
        this.name.setFont(Font.font(null, FontWeight.BOLD, 12));
    }

    private void configureLegendIndicator() {
        this.legendIndicator.setFont(IconProvider.FONT_AWESOME);
        this.legendIndicator.setText(IconProvider.FontAwesome.ICON_DOT_CIRCLE);
    }

    private void configureContextMenu() {
        this.hideItem.setText("Hide");
        this.hideItem.setOnAction(this::toggleVisibility);
        this.contextMenu.getItems().addAll(this.hideItem);
    }

    private void addControlsToGrid() {
        this.grid.add(this.legendIndicator, 0, 0);
        this.grid.add(this.name, 1, 0);
    }

    @Override
    public void updateItem(ColorableChemicalEntity entity, boolean empty) {
        super.updateItem(entity, empty);
        if (empty) {
            clearContent();
        } else {
            addContent(entity);
        }
    }

    private void clearContent() {
        setText(null);
        setGraphic(null);
        setContextMenu(null);
    }

    private void toggleVisibility(ActionEvent event) {
        ColorableChemicalEntity entity = this.getItem();
        if (entity.isVisible()) {
            entity.setVisible(false);
            this.hideItem.setText("Show");
            this.plot.hideSeries(entity);
            this.setStyle("-fx-control-inner-background: #d0d0d0;");
        } else {
            entity.setVisible(true);
            this.hideItem.setText("Hide");
            this.plot.showSeries(entity);
            this.setStyle("-fx-control-inner-background: #f4f4f4;");
        }
    }

    private void addContent(ColorableChemicalEntity entity) {
        setText(null);
        this.legendIndicator.setTextFill(entity.getColor());
        this.name.setText(entity.getEntity().getName());
        setContextMenu(this.contextMenu);
        setGraphic(this.grid);
    }

}
