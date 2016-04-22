package de.bioforscher.simulation.application.windows;

import de.bioforscher.chemistry.descriptive.Species;
import de.bioforscher.chemistry.parser.ChEBISearchService;
import de.bioforscher.simulation.application.BioGraphSimulation;
import de.bioforscher.simulation.application.components.SpeciesCard;
import javafx.event.ActionEvent;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Pagination;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

public class SearchSpeciesPanel extends GridPane {

    public static final int DEFAULT_RESULTS_PER_PAGE = 4;

    private List<Species> speciesList;

    private TextField searchField;
    private Button searchButton;

    private Pagination searchResults;
    private int resultsPerPage;

    private BioGraphSimulation owner;

    public SearchSpeciesPanel(BioGraphSimulation owner) {
        this.owner = owner;
        this.resultsPerPage = DEFAULT_RESULTS_PER_PAGE;
        this.speciesList = new ArrayList<>();
        initialize();
    }

    private void initialize() {
        this.setAlignment(Pos.TOP_CENTER);
        this.setHgap(10);
        this.setVgap(10);
        this.setPadding(new Insets(10, 10, 10, 10));

        ColumnConstraints column1 = new ColumnConstraints();
        column1.setHalignment(HPos.LEFT);
        column1.setHgrow(Priority.ALWAYS);
        column1.setPercentWidth(80);
        this.getColumnConstraints().add(column1);

        ColumnConstraints column2 = new ColumnConstraints();
        column2.setHalignment(HPos.RIGHT);
        column2.setHgrow(Priority.ALWAYS);
        this.getColumnConstraints().add(column2);

        this.searchField = new TextField("");
        this.searchField.setMaxWidth(Double.MAX_VALUE);
        this.add(this.searchField, 0, 0, 2, 1);

        this.searchButton = new Button("Search");

        this.searchButton.setOnAction(this::search);
        this.add(this.searchButton, 2, 0, 1, 1);

        Separator separator = new Separator();
        separator.setOrientation(Orientation.HORIZONTAL);
        this.add(separator, 0, 1, 3, 1);

        this.searchResults = new Pagination(5);
        this.add(this.searchResults, 0, 2, 3, 1);

    }

    private void search(ActionEvent event) {
        ChEBISearchService searchService = new ChEBISearchService(this.searchField.getText());
        this.speciesList = searchService.search();
        this.searchResults.setPageFactory(this::createPage);
    }

    private VBox createPage(Integer pageIndex) {
        VBox box = new VBox(5);
        int page = pageIndex * this.resultsPerPage;
        for (int i = page; i < page + this.resultsPerPage; i++) {
            SpeciesCard card = new SpeciesCard(this.speciesList.get(i), this.owner);
            box.getChildren().add(card);
        }
        return box;
    }

}
