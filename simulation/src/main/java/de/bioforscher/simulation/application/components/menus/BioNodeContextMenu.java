package de.bioforscher.simulation.application.components.menus;

import de.bioforscher.simulation.application.BioGraphSimulation;
import de.bioforscher.simulation.application.components.plots.ConcentrationPlot;
import de.bioforscher.simulation.application.components.cards.PlotCard;
import de.bioforscher.simulation.model.graphs.BioNode;
import de.bioforscher.simulation.model.compartments.NodeState;
import de.bioforscher.simulation.modules.model.Simulation;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BioNodeContextMenu extends ContextMenu {

    private static final Logger logger = LoggerFactory.getLogger(BioNodeContextMenu.class);

    private final BioGraphSimulation owner;
    private BioNode node;

    private CustomMenuItem header = new CustomMenuItem();
    private MenuItem delete = new MenuItem();
    private MenuItem observe = new MenuItem();
    private Menu stateMenu;
    private ToggleGroup stateGroup;

    public BioNodeContextMenu(BioNode node, BioGraphSimulation owner) {
        this.node = node;
        this.owner = owner;
        configureHeader();
        configureStatesMenu();
        configureDeleteItem();
        configureObserve();
        addItemsToMenu();
    }

    private void configureHeader() {
        Text headerText = new Text(this.node.toString());
        headerText.setStyle("-fx-font-weight: bold;");
        this.header.setContent(headerText);
    }

    private void configureDeleteItem() {
        this.delete.setText("Delete");
        this.delete.setOnAction(this::deleteNode);
    }

    private void configureObserve() {
        this.observe.setText("Observe");
        this.observe.setOnAction(this::observeNode);
    }

    private void configureStatesMenu() {
        this.stateMenu = new Menu("Set State...");
        this.stateGroup = new ToggleGroup();
        // add menuItem for every state
        for (NodeState state: NodeState.values()) {
            RadioMenuItem stateItem = setupStateMenuItem(state);
            this.stateMenu.getItems().add(stateItem);
        }
    }

    private RadioMenuItem setupStateMenuItem(final NodeState state) {
        RadioMenuItem itemCompound = new RadioMenuItem();
        itemCompound.setText(state.name());
        itemCompound.setUserData(state);
        itemCompound.setToggleGroup(this.stateGroup);
        itemCompound.setOnAction(this::setState);
        return itemCompound;
    }

    private void addItemsToMenu() {
        this.getItems().addAll(this.header, this.delete, this.observe, this.stateMenu);
    }

    private void deleteNode(ActionEvent event) {
        logger.debug("Removing node {} from currently displayed graph ...",this.node.getIdentifier());
        this.owner.getGraph().removeNode(this.node.getIdentifier());
        this.owner.redrawGraph();
    }

    private void observeNode(ActionEvent event) {
        this.node.setObserved(true);
        Simulation simulation = this.owner.getSimulation();
        ConcentrationPlot plot = new ConcentrationPlot(simulation.getChemicalEntities(), this.node, simulation);
        simulation.getListeners().add(plot);
        this.owner.getPlotControlPanel().getPlotCards().add(new PlotCard(this.owner.getSimulation(), plot));
        this.owner.redrawGraph();
    }

    private void setState(ActionEvent event) {
        this.node.setState(((NodeState)((RadioMenuItem)event.getSource()).getUserData()));
        this.owner.redrawGraph();
    }

    public BioNode getNode() {
        return this.node;
    }

    public void setNode(BioNode node) {
        this.node = node;
    }

}
