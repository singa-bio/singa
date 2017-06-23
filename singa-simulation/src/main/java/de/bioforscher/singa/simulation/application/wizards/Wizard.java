package de.bioforscher.singa.simulation.application.wizards;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.layout.StackPane;

import java.util.Optional;
import java.util.Stack;

/**
 * An implementation of a simple wizard. Modified after
 * http://www.java2s.com/Code/Java/JavaFX/StackPanebasedwizard.htm
 *
 * @author cl
 */
public abstract class Wizard extends StackPane {

    private static final int UNDEFINED = -1;
    private int currentPageIndex = UNDEFINED;

    private ObservableList<WizardPage> pages = FXCollections.observableArrayList();
    private Stack<Integer> history = new Stack<>();

    public Wizard(WizardPage... pages) {
        this.pages.addAll(pages);
        this.setPadding(new Insets(10, 10, 10, 10));
        navigateTo(0);
    }

    public WizardPage getCurrentPage() {
        return this.pages.get(this.currentPageIndex);
    }

    public Optional<WizardPage> getNextPage() {
        if (hasNextPage()) {
            return Optional.of(this.pages.get(this.currentPageIndex + 1));
        } else {
            return Optional.empty();
        }
    }

    public boolean hasNextPage() {
        return (this.currentPageIndex < this.pages.size() - 1);
    }

    public boolean hasPriorPage() {
        return !this.history.isEmpty();
    }

    public void navigateTo(String pageIdentifier) {
        WizardPage target = this.pages.stream()
                .filter(page -> page.getId().equals(pageIdentifier))
                .findAny()
                .orElseThrow(IllegalArgumentException::new);
        int nextPageIdentifier = this.pages.indexOf(target);
        if (nextPageIdentifier != UNDEFINED) {
            navigateTo(nextPageIdentifier);
        }

    }

    public void navigateTo(int pageIdentifier) {
        navigateTo(pageIdentifier, true);
    }

    public void navigateTo(int pageIdentifier, boolean pushHistory) {
        if (pageIdentifier < 0 || pageIdentifier >= this.pages.size())
            return;
        if (this.currentPageIndex != UNDEFINED) {
            if (pushHistory) {
                this.history.push(this.currentPageIndex);
            }
        }

        WizardPage nextPage = this.pages.get(pageIdentifier);
        this.currentPageIndex = pageIdentifier;
        getChildren().clear();
        getChildren().add(nextPage);
        nextPage.manageButtons();
    }

    public void navigateToPriorPage() {
        if (hasPriorPage()) {
            navigateTo(this.history.pop(), false);
        }
    }

    public void navigateToNextPage() {
        if (hasNextPage()) {
            navigateTo(this.currentPageIndex + 1);
        }
    }

    public abstract void finish();

    public abstract void cancel();

}
