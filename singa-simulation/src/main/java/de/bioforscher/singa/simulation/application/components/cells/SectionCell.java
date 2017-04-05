package de.bioforscher.singa.simulation.application.components.cells;

import de.bioforscher.singa.simulation.application.IconProvider;
import de.bioforscher.singa.simulation.application.renderer.ColorManager;
import de.bioforscher.singa.simulation.model.compartments.CellSection;
import de.bioforscher.singa.simulation.model.compartments.EnclosedCompartment;
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
public class SectionCell extends ListCell<CellSection> {

    private GridPane grid = new GridPane();
    private Label name = new Label();
    private Label identifier = new Label();
    private Label legendIndicator = new Label();

    public SectionCell() {
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
    }

    private void updateLegendIndicator(CellSection cellSection) {
        if (cellSection instanceof EnclosedCompartment) {
            this.legendIndicator.setText(IconProvider.FontAwesome.ICON_SQUARE_FULL);
        } else {
            this.legendIndicator.setText(IconProvider.FontAwesome.ICON_SQUARE_EMPTY);
        }
        this.legendIndicator.setTextFill(ColorManager.getInstance().getColor(cellSection));
    }

    private void addControlsToGrid() {
        this.grid.add(this.legendIndicator, 0, 0, 1, 2);
        this.grid.add(this.name, 1, 0);
        this.grid.add(this.identifier, 1, 1);
    }

    @Override
    public void updateItem(CellSection entity, boolean empty) {
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

    private void addContent(CellSection cellSection) {
        setText(null);
        updateLegendIndicator(cellSection);
        this.name.setText(cellSection.getName());
        this.identifier.setText(cellSection.getIdentifier());
        setGraphic(this.grid);
        this.name.setText(cellSection.getName());
        setGraphic(this.grid);
    }

}
