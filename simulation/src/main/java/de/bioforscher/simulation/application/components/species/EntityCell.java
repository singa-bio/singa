package de.bioforscher.simulation.application.components.species;

import de.bioforscher.chemistry.descriptive.ChemicalEntity;
import de.bioforscher.simulation.application.IconProvider;
import javafx.geometry.Insets;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * Created by Christoph on 06.07.2016. http://www.billmann.de/2013/07/03/javafx-custom-listcell/
 */
public class EntityCell<EntityType extends ChemicalEntity> extends ListCell<EntityType> {

    private GridPane grid = new GridPane();
    private Label name = new Label();
    private Label identifier = new Label();
    private ImageView speciesImage = new ImageView();

    private ContextMenu contextMenu = new ContextMenu();

    public EntityCell() {
        configureGrid();
        configureName();
        configureIdentifier();
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

    private void configureIdentifier() {
        this.identifier.setTextFill(Color.DARKGRAY);
    }

    private void configureContextMenu() {
        MenuItem deleteItem = new MenuItem();
        deleteItem.setText("Remove");
        deleteItem.setOnAction(event -> getListView().getItems().remove(this.getItem()));
        this.contextMenu.getItems().addAll(deleteItem);
    }

    private void addControlsToGrid() {
        this.grid.add(this.speciesImage, 0, 0, 1, 2);
        this.grid.add(this.name, 1, 0);
        this.grid.add(this.identifier, 1, 1);
    }

    @Override
    public void updateItem(EntityType entity, boolean empty) {
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

    private void addContent(ChemicalEntity entity) {
        setText(null);
        this.speciesImage.setImage(IconProvider.MOLECULE_ICON_IMAGE);
        this.name.setText(entity.getName());
        this.identifier.setText(entity.getIdentifier().toString());
        setContextMenu(this.contextMenu);
        setGraphic(this.grid);
    }

}
