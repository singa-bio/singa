package de.bioforscher.simulation.application.components.plots;

import de.bioforscher.simulation.application.IconProvider;
import javafx.geometry.Insets;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * Created by Christoph on 27.07.2016.
 */
public class PlotCell extends ListCell<PlotCard> {

    private PlotPane plotPane;

    private GridPane grid = new GridPane();
    private Label name = new Label();
    private Label legendIndicator = new Label();

    private ContextMenu contextMenu = new ContextMenu();
    private MenuItem hideItem = new MenuItem();

    public PlotCell(PlotPane plotPane) {
        this.plotPane = plotPane;
        configureGrid();
        configureLegendIndicator();
        configureName();
        configureContextMenu();
        addControlsToGrid();
        this.setOnMouseClicked(event -> {
            this.plotPane.setSelectedPlot(this.getItem());
        });
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
        this.legendIndicator.setText(IconProvider.FontAwesome.ICON_LINE_CHART);
    }

    private void configureContextMenu() {
        this.hideItem.setText("Hide");
        // this.hideItem.setOnAction(this::toggleVisibility);
        this.contextMenu.getItems().addAll(this.hideItem);
    }

    private void addControlsToGrid() {
        this.grid.add(this.legendIndicator, 0, 0);
        this.grid.add(this.name, 1, 0);
    }

    @Override
    public void updateItem(PlotCard entity, boolean empty) {
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

    private void addContent(PlotCard entity) {
        setText(null);
        this.name.setText("C" + entity.getPlot().getReferencedNode().getIdentifier());
        setContextMenu(this.contextMenu);
        setGraphic(this.grid);
    }

}
