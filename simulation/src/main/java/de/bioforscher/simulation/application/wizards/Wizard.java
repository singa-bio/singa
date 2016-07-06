package de.bioforscher.simulation.application.wizards;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;

import java.util.Stack;

/**
 * An implementation of a simple wizard. Modified after
 * http://www.java2s.com/Code/Java/JavaFX/StackPanebasedwizard.htm
 *
 * @author Christoph Leberecht
 */
public class Wizard extends StackPane {

    private static final int UNDEFINED = -1;
    private int curPageIdx = UNDEFINED;

    private ObservableList<WizardPage> pages = FXCollections
            .observableArrayList();
    private Stack<Integer> history = new Stack<>();

    public Wizard(WizardPage... pages) {
        this.pages.addAll(pages);
        this.setPadding(new Insets(10, 10, 10, 10));
        navigateTo(0);
    }

    public void nextPage() {
        if (hasNextPage()) {
            navigateTo(this.curPageIdx + 1);
        }
    }

    public WizardPage getCurrentPage() {
        return this.pages.get(this.curPageIdx);
    }

    public WizardPage getNextPage() {
        if (hasNextPage()) {
            return this.pages.get(this.curPageIdx + 1);
        } else {
            return null;
        }
    }

    public void priorPage() {
        if (hasPriorPage()) {
            navigateTo(this.history.pop(), false);
        }
    }

    public boolean hasNextPage() {
        return (this.curPageIdx < this.pages.size() - 1);
    }

    public boolean hasPriorPage() {
        return !this.history.isEmpty();
    }

    public void navigateTo(int nextPageIdx, boolean pushHistory) {
        if (nextPageIdx < 0 || nextPageIdx >= this.pages.size())
            return;
        if (this.curPageIdx != UNDEFINED) {
            if (pushHistory) {
                this.history.push(this.curPageIdx);
            }
        }

        WizardPage nextPage = this.pages.get(nextPageIdx);
        this.curPageIdx = nextPageIdx;
        getChildren().clear();
        getChildren().add(nextPage);
        nextPage.manageButtons();
    }

    public void navigateTo(int nextPageIdx) {
        navigateTo(nextPageIdx, true);
    }

    public void navigateTo(String id) {
        Node page = lookup("#" + id);
        if (page != null) {
            int nextPageIdx = this.pages.indexOf(page);
            if (nextPageIdx != UNDEFINED) {
                navigateTo(nextPageIdx);
            }
        }
    }

    public void finish() {

    }

    public void cancel() {

    }
}
