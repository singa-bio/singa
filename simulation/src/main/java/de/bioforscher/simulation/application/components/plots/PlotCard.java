package de.bioforscher.simulation.application.components.plots;

import com.sun.javafx.stage.StageHelper;
import de.bioforscher.chemistry.descriptive.ChemicalEntity;
import de.bioforscher.simulation.application.IconProvider;
import de.bioforscher.simulation.application.components.chemicalEntities.ColoredEntityCell;
import de.bioforscher.simulation.modules.model.Simulation;
import de.bioforscher.simulation.events.EpochUpdateWriter;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.*;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.transform.Transform;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Paths;

/**
 * Created by Christoph on 20.07.2016.
 */
public class PlotCard extends GridPane {

    private ConcentrationPlot plot;
    private Simulation simulation;

    private HBox toolBar = new HBox();
    private ListView<ChemicalEntity<?>> speciesList = new ListView<>();
    private MenuButton optionsMenu = new MenuButton("", IconProvider.FontAwesome.createIconLabel(IconProvider.FontAwesome.ICON_COGS));;

    public PlotCard(Simulation simulation, ConcentrationPlot plot) {
        this.plot = plot;
        this.simulation = simulation;
        configureGrid();
        configurePlot();
        configureToolBar();
        configureSpeciesList();
        configureContextMenu();
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

    private void configureContextMenu() {
        CheckMenuItem menuItem = new CheckMenuItem("Save data to file");
        menuItem.setOnAction(this::configureDataExport);
        this.optionsMenu.getItems().add(menuItem);
    }

    private void configurePlot() {
        this.plot.setLegendVisible(false);
    }

    private void configureToolBar() {
        this.optionsMenu.setTooltip(new Tooltip("Options (currently not defined - but feel free to submit a request)"));
        this.optionsMenu.setPopupSide(Side.LEFT);
        Button export = IconProvider.FontAwesome.createIconButton(IconProvider.FontAwesome.ICON_DOWNLOAD);
        export.setTooltip(new Tooltip("Export"));
        export.setOnAction(this::exportPlot);
        this.toolBar.setAlignment(Pos.TOP_RIGHT);
        this.toolBar.setPadding(new Insets(10, 0, 10, 0));
        this.toolBar.setSpacing(5);
        this.toolBar.getChildren().addAll(this.optionsMenu, export);
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

    private void exportPlot(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export Plot to png");
        fileChooser.setInitialFileName("concentrations_node_"+this.getPlot().getReferencedNode().getIdentifier()+"" +
                ".png");
        File file = fileChooser.showSaveDialog(null);
        if (file != null) {
            this.plot.setLegendVisible(true);
            SnapshotParameters parameters = new SnapshotParameters();
            parameters.setTransform(Transform.scale(4, 4));
            WritableImage snapShot = this.plot.snapshot(parameters, null);
            this.plot.setLegendVisible(false);
            try {
                if (!file.getName().endsWith(".png")) {
                    file = new File(file+".png");
                }
                ImageIO.write(SwingFXUtils.fromFXImage(snapShot, null), "png", file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void configureDataExport(ActionEvent event) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select a folder to save the data of this observation.");
        File directory = directoryChooser.showDialog(StageHelper.getStages().iterator().next());
        if (directory != null) {
            try {
                EpochUpdateWriter writer = new EpochUpdateWriter(directory.toPath(), Paths.get("Current Simulation"), this.simulation.getChemicalEntities());
                writer.addNodeToObserve(this.plot.getReferencedNode());
                this.simulation.setWriter(writer);
            } catch (IOException e) {
                throw new UncheckedIOException("Could not use the selected folder to set up the update writer.", e);
            }
        }
    }

    public ConcentrationPlot getPlot() {
        return this.plot;
    }

    public ListView<ChemicalEntity<?>> getSpeciesList() {
        return this.speciesList;
    }
}
