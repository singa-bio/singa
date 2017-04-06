package de.bioforscher.singa.simulation.application.wizards;

import de.bioforscher.singa.simulation.modules.reactions.model.Reaction;
import de.bioforscher.singa.simulation.parser.sbml.SabioRKParserService;
import javafx.event.ActionEvent;
import javafx.scene.Parent;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A wizard used to search and add a reaction to the automaton.
 *
 * @author Christoph Leberecht
 */
public class NewReactionWizard extends Wizard {

    private Stage owner;
    private Set<Reaction> reactionsToAdd;

    public NewReactionWizard(Stage owner) {
        super(new ChooseMethodPage(), new GetSuggestionsPage());
        this.owner = owner;
    }

    @Override
    public void finish() {
        this.owner.close();
        // TODO prepare reactions for output
    }

    @Override
    public void cancel() {
        this.owner.close();
    }

    public Set<Reaction> getReactions() {
        return this.reactionsToAdd;
    }

    public void setReactions(Set<Reaction> reactionsToAdd) {
        this.reactionsToAdd = reactionsToAdd;
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
                (observableValue, oldToggle, newToggle) -> this.nextButton.setDisable(false));

    }

    @Override
    public Parent getContent() {
        this.rbSABIO = new RadioButton("Automatically using SABIO-RK.");
        this.rbManual = new RadioButton("Manually using the user interface.");
        this.rbManual.setTextFill(Color.GRAY);
        return new VBox(this.rbSABIO, this.rbManual);
    }

    @Override
    public void navigateToNextPage() {
        if (this.tgMethods.getSelectedToggle().equals(this.rbSABIO)) {
            super.navigateToNextPage();
        }
    }
}

class GetSuggestionsPage extends WizardPage {

    private TextField tfSearch;

    public GetSuggestionsPage() {
        super("Search Reactions");
        setDescription("You can search the SABIO-RK Reaction Database for Biochemical Reactions. Enter a search term and choose the desiered entry.");

    }

    @Override
    public Parent getContent() {

        BorderPane root = new BorderPane();

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

            count++;
        }
    }


}

