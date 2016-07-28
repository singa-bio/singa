package de.bioforscher.simulation.application.components.plots;

import de.bioforscher.simulation.application.BioGraphSimulation;
import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;

/**
 * Created by Christoph on 27.07.2016.
 */
public class PlotPane extends GridPane {

    private BioGraphSimulation owner;

    private PlotCard currentPlotCard;
    private ListView<PlotCard> plotCards;
    private boolean initialized = false;

    public PlotPane(BioGraphSimulation owner) {
        this.owner = owner;
        this.plotCards = new ListView<>();
        // this.currentPlotCard = new PlotCard();
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
        this.plotCards.getItems().addListener(new ListChangeListener<PlotCard>() {
            @Override
            public void onChanged(Change<? extends PlotCard> change) {
                if (!PlotPane.this.initialized) {
                    PlotPane.this.initialized = true;
                    change.next();
                    setSelectedPlot(change.getAddedSubList().get(0));
                    addControlsToGrid();
                }
            }
        });
    }

    public void setSelectedPlot(PlotCard plotCard) {
        if (this.currentPlotCard != null) {
            this.getChildren().remove(1);
            this.add(plotCard, 0, 1);
        }
        this.currentPlotCard = plotCard;

        //System.out.println("Showing "+plotCard.getPlot().getReferencedNode().getIdentifier());
    }

    private void addControlsToGrid() {
        if (this.currentPlotCard != null) {
            this.add(this.plotCards, 0, 0);
            this.add(this.currentPlotCard, 0, 1);
        }
    }

    public ListView<PlotCard> getPlotCards() {
        return this.plotCards;
    }
}
