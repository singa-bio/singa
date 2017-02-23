package de.bioforscher.simulation.application.components.cells;

import de.bioforscher.simulation.application.IconProvider;
import de.bioforscher.simulation.application.renderer.ColorManager;
import de.bioforscher.simulation.model.compartments.Compartment;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * @author cl
 */
public class CompartmentCell extends ListCell<Compartment> {

    private GridPane grid = new GridPane();
    private Label name = new Label();
    private Label identifier = new Label();
    private Label legendIndicator = new Label();

    public CompartmentCell() {
        configureGrid();
        configureName();
        configureLegendIndicator();
        configureIdentifier();
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

    private void configureIdentifier() {
        this.identifier.setTextFill(Color.DARKGRAY);
    }

    private void configureLegendIndicator() {
        this.legendIndicator.setFont(IconProvider.FONT_AWESOME_LARGE);
        this.legendIndicator.setText(IconProvider.FontAwesome.ICON_CUBE);
    }

    private void addControlsToGrid() {
        this.grid.add(this.legendIndicator, 0, 0, 1, 2);
        this.grid.add(this.name, 1, 0);
        this.grid.add(this.identifier, 1, 1);
    }

    @Override
    public void updateItem(Compartment entity, boolean empty) {
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
    }

    private void addContent(Compartment compartment) {
        setText(null);
        this.legendIndicator.setTextFill(ColorManager.getInstance().getColor(compartment));
        this.name.setText(compartment.getName());
        this.identifier.setText(compartment.getIdentifier());
        setGraphic(this.grid);
        this.name.setText(compartment.getName());
        setGraphic(this.grid);
    }

}
