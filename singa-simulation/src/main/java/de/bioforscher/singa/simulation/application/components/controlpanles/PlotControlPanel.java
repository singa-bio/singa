package de.bioforscher.singa.simulation.application.components.controlpanles;

import de.bioforscher.singa.simulation.application.components.cards.PlotCard;
import de.bioforscher.singa.simulation.application.components.cells.PlotCell;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author cl
 */
public class PlotControlPanel extends GridPane {

    private static final Logger logger = LoggerFactory.getLogger(PlotControlPanel.class);

    private PlotCard currentPlotCard;
    private ListView<PlotCard> plotCards;
    private boolean initialized = false;

    public PlotControlPanel() {
        logger.debug("Initializing {}", this.getClass().getSimpleName());
        this.plotCards = new ListView<>();
        configureGrid();
        configurePlots();
        configurePlotCards();
        addControlsToGrid();
    }

    public void configureGrid() {
        this.setHgap(10);
        this.setVgap(4);
        this.setPadding(new Insets(10, 10, 10, 10));
    }

    private void configurePlots() {
        this.plotCards.setCellFactory(param -> new PlotCell(this));
        this.plotCards.setMaxHeight(30);
    }

    private void configurePlotCards() {
        this.plotCards.setOrientation(Orientation.HORIZONTAL);
        this.plotCards.getItems().addListener((ListChangeListener<PlotCard>) change -> {
            if (!PlotControlPanel.this.initialized) {
                PlotControlPanel.this.initialized = true;
                change.next();
                setSelectedPlot(change.getAddedSubList().get(0));
                addControlsToGrid();
            }
        });
    }

    public void setSelectedPlot(PlotCard plotCard) {
        if (plotCard != null) {
            if (this.currentPlotCard != null) {
                this.getChildren().remove(1);
                this.add(plotCard, 0, 1);
                plotCard.getPlot().getObservedEntities().forEach(entity -> plotCard.getPlot().updateColor(entity));
                plotCard.getSpeciesList().refresh();
            }
            this.currentPlotCard = plotCard;
        }
    }

    private void addControlsToGrid() {
        if (this.currentPlotCard != null) {
            this.add(this.plotCards, 0, 0);
            this.add(this.currentPlotCard, 0, 1);
        }
    }

    public ObservableList<PlotCard> getPlotCards() {
        return this.plotCards.getItems();
    }
}
