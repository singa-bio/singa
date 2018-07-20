package bio.singa.structure.parser.pdb.rest;

import bio.singa.core.parser.AbstractHTMLParser;

/**
 * @author fk
 */
public abstract class PDBRestEndpoint<ResultType> extends AbstractHTMLParser<ResultType> {

    protected abstract String getEndpoint();
}
