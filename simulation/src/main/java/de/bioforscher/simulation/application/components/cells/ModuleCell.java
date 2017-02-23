package de.bioforscher.simulation.application.components.cells;

import de.bioforscher.simulation.modules.model.AvailableModules;
import javafx.geometry.Insets;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * Created by Christoph on 03.08.2016.
 */
public class ModuleCell extends ListCell<AvailableModules> {

    private GridPane grid = new GridPane();
    private Label name = new Label();
    private ImageView moduleImage = new ImageView();

    private ContextMenu contextMenu = new ContextMenu();

    public ModuleCell() {
        configureGrid();
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

    private void configureContextMenu() {
        MenuItem deleteItem = new MenuItem();
        deleteItem.setText("Remove");
        deleteItem.setOnAction(event -> getListView().getItems().remove(this.getItem()));
        this.contextMenu.getItems().addAll(deleteItem);
    }

    private void addControlsToGrid() {
        this.grid.add(this.moduleImage, 0, 0);
        this.grid.add(this.name, 0, 1);
    }

    @Override
    public void updateItem(AvailableModules module, boolean empty) {
        super.updateItem(module, empty);
        if (empty) {
            clearContent();
        } else {
            addContent(module);
        }
    }

    private void clearContent() {
        setText(null);
        setGraphic(null);
        setContextMenu(null);
    }

    private void addContent(AvailableModules module) {
        setText(null);
        this.moduleImage.setImage(module.getIcon());
        this.name.setText(module.getRepresentativeName());
        setContextMenu(this.contextMenu);
        setGraphic(this.grid);
    }

}
