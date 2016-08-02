package de.bioforscher.simulation.application.wizards;

import javafx.event.ActionEvent;
import javafx.geometry.HPos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

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

    private Text pageTitle = new Text();
    private TextFlow description = new TextFlow();

    public WizardPage(String pageTitle) {

        this.setId(pageTitle);
        this.setSpacing(5);

        configureTitle(pageTitle);

        addComponents();

        this.priorButton.setOnAction(this::navigateToPriorPage);
        this.nextButton.setOnAction(this::navigateToNextPage);
        this.cancelButton.setOnAction(actionEvent -> getWizard().cancel());
        this.finishButton.setOnAction(actionEvent -> getWizard().finish());
    }

    private void configureTitle(String pageTitle) {
        this.pageTitle.setText(pageTitle);
        this.pageTitle.setFont(Font.font(null, FontWeight.BOLD, 14));
    }

    private void addComponents() {
        getChildren().addAll(this.pageTitle, this.description);

        final Separator separatorTop = new Separator();
        separatorTop.setHalignment(HPos.CENTER);
        getChildren().add(separatorTop);

        Region spring = new Region();
        VBox.setVgrow(spring, Priority.ALWAYS);
        getChildren().addAll(getContent(), spring, getFooter());
    }

    private HBox getFooter() {
        Region spring = new Region();
        HBox.setHgrow(spring, Priority.ALWAYS);
        HBox footer = new HBox(5);
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

    public void navigateToNextPage(ActionEvent event) {
        navigateToNextPage();
    }

    public void navigateToNextPage() {
        getWizard().navigateToNextPage();
    }

    public void navigateToPriorPage(ActionEvent event) {
        navigateToPriorPage();
    }

    public void navigateToPriorPage() {
        getWizard().navigateToPriorPage();
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
