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
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.List;

public class SearchSpeciesPanel extends GridPane {

    private static final int default_results_per_page = 4;

    private List<Species> speciesList;

    private TextField searchField;

    private Pagination searchResults;
    private int resultsPerPage;

    private BioGraphSimulation owner;

    public SearchSpeciesPanel(BioGraphSimulation owner) {
        this.owner = owner;
        this.resultsPerPage = default_results_per_page;
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

        Button searchButton = new Button("Search");
        searchButton.setOnAction(this::search);
        this.add(searchButton, 2, 0, 1, 1);

        Separator separator = new Separator();
        separator.setOrientation(Orientation.HORIZONTAL);
        this.add(separator, 0, 1, 3, 1);

    }

    private void search(ActionEvent event) {
        this.getChildren().remove(searchResults);
        String searchTerm = this.searchField.getText();
        ChEBISearchService searchService = new ChEBISearchService(searchTerm);
        this.speciesList = searchService.search();
        this.searchResults = new Pagination((int) Math.ceil((double) (speciesList.size()) / (double) (resultsPerPage)));
        this.add(this.searchResults, 0, 2, 3, 1);

        if (speciesList.size() > 0) {
            this.searchResults.setPageFactory(this::createPage);
        } else {
            this.searchResults.setPageFactory(this::createNoResultsPage);
        }
    }

    private VBox createNoResultsPage(Integer pageIndex) {
        VBox box = new VBox(1);
        Text nothingFoundText = new Text("Sorry, we were not able to find " + "\"" + this.searchField.getText() + "\" in the ChEBI Database.");
        box.getChildren().add(nothingFoundText);
        return box;
    }

    private VBox createPage(Integer pageIndex) {
        VBox box = new VBox(5);
        int page = pageIndex * this.resultsPerPage;
        int i = page;
        while (i < page + resultsPerPage && i < speciesList.size()) {
            SpeciesCard card = new SpeciesCard(this.speciesList.get(i), this.owner);
            box.getChildren().add(card);
            i++;
        }
        return box;
    }

}
