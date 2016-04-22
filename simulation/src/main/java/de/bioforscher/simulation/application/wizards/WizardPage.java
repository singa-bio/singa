package de.bioforscher.simulation.application.wizards;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
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
 * A page in a wizard. Modified after
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
        // ID of this Page
        this.setId(title);

        // Title
        final Text titleText = new Text(title);
        titleText.setId("pageTitle");
        this.getChildren().add(titleText);

        // Description
        this.description = new TextFlow();
        this.description.setId("pageDescription");
        this.getChildren().add(description);

        final Separator separatorTop = new Separator();
        separatorTop.setId("seperatorTop");
        this.getChildren().add(separatorTop);

        Region spring = new Region();
        VBox.setVgrow(spring, Priority.ALWAYS);
        getChildren().addAll(getContent(), spring, getButtons());

        this.priorButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                priorPage();
            }
        });

        this.nextButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                nextPage();
            }
        });

        this.cancelButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                getWizard().cancel();
            }
        });

        this.finishButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                getWizard().finish();
            }
        });

        this.setSpacing(5);
        File f = new File("D:/projects/simulation/target/classes/wizard.css");
        this.getStylesheets().add("file:///" + f.getAbsolutePath().replace("\\", "/"));
    }

    public HBox getButtons() {

        Region spring = new Region();
        HBox.setHgrow(spring, Priority.ALWAYS);
        HBox buttonBar = new HBox(5);
        this.cancelButton.setCancelButton(true);
        this.finishButton.setDefaultButton(true);
        buttonBar.getChildren().addAll(spring, this.priorButton, this.nextButton,
                this.cancelButton, this.finishButton);
        return buttonBar;

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

    public void nextPage() {
        getWizard().nextPage();
    }

    public void priorPage() {
        getWizard().priorPage();
    }

    public void navTo(String id) {
        getWizard().navigateTo(id);
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
