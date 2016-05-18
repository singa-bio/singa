package de.bioforscher.simulation.application.wizards;

import javafx.event.ActionEvent;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.io.File;

/**
 * A page in a wizard. Modified from
 * http://www.java2s.com/Code/Java/JavaFX/StackPanebasedwizard.htm
 *
 * @author Christoph Leberecht
 */
public abstract class WizardPage extends VBox {

    protected Button priorButton = new Button("Previous");
    protected Button nextButton = new Button("Next");
    protected Button cancelButton = new Button("Cancel");
    protected Button finishButton = new Button("Finish");

    private TextFlow description;

    public WizardPage(String title) {
        this.setId(title);
        this.setSpacing(5);
        final Text titleText = new Text(title);
        titleText.setId("PAGE_TITLE");
        this.getChildren().add(titleText);

        this.description = new TextFlow();
        this.description.setId("PAGE_DESCRIPTION");
        this.getChildren().add(description);

        final Separator separatorTop = new Separator();
        separatorTop.setId("SEPARATOR_TOP");
        this.getChildren().add(separatorTop);

        Region spring = new Region();
        VBox.setVgrow(spring, Priority.ALWAYS);
        getChildren().addAll(getContent(), spring, getFooter());

        this.priorButton.setOnAction(this::priorPage);
        this.nextButton.setOnAction(this::nextPage);
        this.cancelButton.setOnAction(actionEvent -> getWizard().cancel());
        this.finishButton.setOnAction(actionEvent -> getWizard().finish());

        // TODO Static reference is not okay
        File f = new File("D:/projects/simulation/target/classes/wizard.css");
        this.getStylesheets().add("file:///" + f.getAbsolutePath().replace("\\", "/"));
    }

    private HBox getFooter() {
        Region spring = new Region();
        HBox.setHgrow(spring, Priority.ALWAYS);
        HBox footer = new HBox(5);
        this.cancelButton.setCancelButton(true);
        this.finishButton.setDefaultButton(true);
        footer.getChildren().addAll(spring, this.priorButton, this.nextButton,
                this.cancelButton, this.finishButton);
        return footer;
    }

    public abstract Parent getContent();

    public void setDescription(String description) {
        this.description.getChildren().add(new Text(description));
    }

    public boolean hasNextPage() {
        return getWizard().hasNextPage();
    }

    public boolean hasPriorPage() {
        return getWizard().hasPriorPage();
    }

    public void nextPage(ActionEvent event) {
        nextPage();
    }

    public void nextPage() {
        getWizard().nextPage();
    }

    public void priorPage(ActionEvent event) {
        getWizard().priorPage();
    }

    public void navigateToPage(String pageIdentifier) {
        getWizard().navigateTo(pageIdentifier);
    }

    public Wizard getWizard() {
        return (Wizard) getParent();
    }

    public void manageButtons() {
        if (!hasPriorPage()) {
            this.priorButton.setDisable(true);
        }
        if (!hasNextPage()) {
            this.nextButton.setDisable(true);
        }
    }

}
