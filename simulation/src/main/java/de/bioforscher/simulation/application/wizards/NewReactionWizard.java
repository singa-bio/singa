package de.bioforscher.simulation.application.wizards;

import de.bioforscher.simulation.deprecated.EnzymeReaction;
import de.bioforscher.simulation.parser.SabioRKParserService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A wizard used to search and add a reaction to the automaton.
 *
 * @author Christoph Leberecht
 */
public class NewReactionWizard extends Wizard {

    private Stage owner;
    private EnzymeReaction reaction = null;

    public NewReactionWizard(Stage owner) {
        super(new ChooseMethodPage(), new GetSuggestionsPage(),
                new YourReactionPage());
        this.owner = owner;
    }

    @Override
    public void finish() {
        this.owner.close();
        if (this.getCurrentPage().getClass()
                .equals(YourReactionPage.class)) {
            setReaction(((YourReactionPage) this.getCurrentPage())
                    .getReaction());
        }
    }

    @Override
    public void cancel() {
        System.out.println("Cancelled");
        this.owner.close();
    }

    public EnzymeReaction getReaction() {
        return this.reaction;
    }

    public void setReaction(EnzymeReaction reaction) {
        this.reaction = reaction;
    }

}

class ChooseMethodPage extends WizardPage {

    private RadioButton rbSABIO;
    private RadioButton rbManual;
    private ToggleGroup tgMethods = new ToggleGroup();

    public ChooseMethodPage() {
        super("Choose a method to create your reaction.");
        setDescription("The Reaction can either be created using data from the SABIO-RK Biochemical Reaction Kinetics Database or it can be described manually using the graphical user interface.");

        this.nextButton.setDisable(true);
        this.finishButton.setDisable(true);

        this.rbSABIO.setToggleGroup(this.tgMethods);
        this.rbManual.setToggleGroup(this.tgMethods);

        this.tgMethods.selectedToggleProperty().addListener(
                (observableValue, oldToggle, newToggle) -> {
                    this.nextButton.setDisable(false);
                });

    }

    @Override
    public Parent getContent() {

        this.rbSABIO = new RadioButton("Automatically using SABIO-RK.");
        this.rbManual = new RadioButton("Manually using the user interface.");

        return new VBox(this.rbSABIO, this.rbManual);
    }

    @Override
    public void nextPage() {
        if (this.tgMethods.getSelectedToggle().equals(this.rbSABIO)) {
            super.nextPage();
        }
    }
}

class GetSuggestionsPage extends WizardPage {

    private TextField tfSearch;

    private TableView<EnzymeReaction> tbResults;
    private ObservableList<EnzymeReaction> results;

    public GetSuggestionsPage() {
        super("Search Reactions");
        setDescription("You can search the SABIO-RK Reaction Database for Biochemical Reactions. Enter a search term and choose the desiered entry.");

        this.nextButton.disableProperty().bind(
                this.tbResults.getSelectionModel().selectedItemProperty().isNull());
        this.finishButton.disableProperty().bind(
                this.tbResults.getSelectionModel().selectedItemProperty().isNull());
    }

    @Override
    public Parent getContent() {

        BorderPane root = new BorderPane();

        HBox searchBox = new HBox();
        searchBox.setPadding(new Insets(5));
        searchBox.setSpacing(5);

        this.tfSearch = new TextField("Search Term (e.g. glycolysis classical)");
        // FIXME tfSearch size should be dynamically fit to Component
        this.tfSearch.setPrefSize(400, 20);

        Button btnSearch = new Button();
        btnSearch.setId("btnSearch");
        btnSearch.setOnAction(this::searchReactions);

        btnSearch.setMinSize(30, 20);

        searchBox.getChildren().addAll(this.tfSearch, btnSearch);

        this.tbResults = new TableView<>();

        this.results = FXCollections.observableArrayList();
        this.tbResults.setItems(this.results);

        TableColumn<EnzymeReaction, String> reactionCol = new TableColumn<>(
                "Reaction");
        reactionCol.setCellValueFactory(c -> new SimpleStringProperty(c
                .getValue().getReactionString()));
        reactionCol.setMinWidth(300);
        reactionCol.setPrefWidth(300);

        TableColumn<EnzymeReaction, String> kCatCol = new TableColumn<>(
                "kCat");
        kCatCol.setCellValueFactory(c -> new SimpleStringProperty(String
                .valueOf(c.getValue().getEnzyme().getTurnoverNumber())));
        kCatCol.setMinWidth(200);

        TableColumn<EnzymeReaction, String> kmCol = new TableColumn<>(
                "kM");
        kmCol.setCellValueFactory(c -> new SimpleStringProperty(String
                .valueOf(c.getValue().getEnzyme().getMichaelisConstant())));
        kmCol.setMinWidth(200);

        this.tbResults.getColumns().add(reactionCol);
        this.tbResults.getColumns().add(kCatCol);
        this.tbResults.getColumns().add(kmCol);

        root.setTop(searchBox);
        root.setCenter(this.tbResults);

        //root.getStylesheets().addAll(
        //			this.getClass().getResource("wizard.css").toExternalForm());
        return new VBox(root);
    }

    public void searchReactions(ActionEvent event) {
        Client client = ClientBuilder.newClient();
        WebTarget target = client
                .target("http://sabiork.h-its.org/sabioRestWebServices/");
        WebTarget suggestionsPath = target.path("searchKineticLaws")
                .path("kinlaws");
        // Only supported reactions with kCat and km are retrieved
        WebTarget suggestionsQuery = suggestionsPath.queryParam(
                "q",
                "Pathway:" + "%22"
                        + this.tfSearch.getText().replaceAll(" ", "%20")
                        + "%22%20AND%20Parametertype:%22kCat%22%20AND%20Parametertype:%22km%22");
        Invocation.Builder invocationBuilder = suggestionsQuery
                .request(MediaType.TEXT_PLAIN);
        Response response = invocationBuilder.get();

        Pattern pattern = Pattern.compile("<SabioEntryID>(\\d+)");
        String suggestions = response.readEntity(String.class);

        Matcher matcher = pattern.matcher(suggestions);

        int count = 0;

        while (matcher.find() && count < 10) {
            String sabioId = matcher.group(1);
            SabioRKParserService sabiorkParser = new SabioRKParserService("EntryID:"
                    + sabioId);
            EnzymeReaction reaction = sabiorkParser.fetchReaction();
            this.results.add(reaction);
            count++;
        }
    }

    @Override
    public void nextPage() {
        YourReactionPage page = (YourReactionPage) getWizard().getNextPage();
        EnzymeReaction reaction = this.tbResults.getSelectionModel()
                .getSelectedItem();
        page.setReaction(reaction);
        ((NewReactionWizard) getWizard()).setReaction(reaction);
        super.nextPage();
    }

}

class YourReactionPage extends WizardPage {

    private EnzymeReaction reaction;
    private Text reactionText;

    public YourReactionPage() {
        super("Your Reaction.");
        setDescription("Here you are able to review your reaction. By clicking finish the Reaction will be applied to your graph.");
    }

    @Override
    public Parent getContent() {

        StackPane stack = new StackPane();
        this.reactionText = new Text(" ");
        this.reactionText.setId("txtReaction");

        stack.getChildren().add(this.reactionText);
        VBox.setVgrow(stack, Priority.ALWAYS);

        File f = new File("D:/projects/simulation/target/classes/wizard.css");
        this.getStylesheets().add("file:///" + f.getAbsolutePath().replace("\\", "/"));
        // this.getStylesheets().addAll(
        //		this.getClass().getResource("wizard.css").toExternalForm());

        return stack;
    }

    public EnzymeReaction getReaction() {
        return this.reaction;
    }

    public void setReaction(EnzymeReaction reaction) {
        this.reaction = reaction;
        this.reactionText.setText(reaction.getReactionString());
    }

}
