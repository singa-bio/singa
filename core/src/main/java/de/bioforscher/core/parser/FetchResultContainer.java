package de.bioforscher.core.parser;

public class FetchResultContainer<ResultType> {

    private ResultType content;

    public FetchResultContainer(ResultType content) {
        super();
        this.content = content;
    }

    public ResultType getContent() {
        return content;
    }

    public void setContent(ResultType content) {
        this.content = content;
    }

}
