package de.bioforscher.simulation.application.components.cells;

import de.bioforscher.simulation.application.IconProvider;
import de.bioforscher.simulation.application.components.cards.PlotCard;
import de.bioforscher.simulation.application.components.controlpanles.PlotControlPanel;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * Created by Christoph on 27.07.2016.
 */
public class PlotCell extends ListCell<PlotCard> {

    private PlotControlPanel plotControlPanel;

    private GridPane grid = new GridPane();
    private Label name = new Label();
    private Label legendIndicator = new Label();

    private ContextMenu contextMenu = new ContextMenu();
    private MenuItem deleteItem = new MenuItem();

    public PlotCell(PlotControlPanel plotControlPanel) {
        this.plotControlPanel = plotControlPanel;
        configureGrid();
        configureLegendIndicator();
        configureName();
        configureContextMenu();
        addControlsToGrid();
        this.setOnMouseClicked(event -> {
            this.plotControlPanel.setSelectedPlot(this.getItem());
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
        this.legendIndicator.setFont(IconProvider.FONT_AWESOME_SMALL);
        this.legendIndicator.setText(IconProvider.FontAwesome.ICON_LINE_CHART);
    }

    private void configureContextMenu() {
        this.deleteItem.setText("Remove");
        this.deleteItem.setOnAction(event -> getListView().getItems().remove(this.getItem()));
        this.contextMenu.getItems().addAll(this.deleteItem);
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
        final int nodeIdentifier = entity.getPlot().getReferencedNode().getIdentifier();
        this.name.setText("C" + nodeIdentifier);
        this.setTooltip(new Tooltip("Concentration Plot for Node " + nodeIdentifier));
        setContextMenu(this.contextMenu);
        setGraphic(this.grid);
    }

}
