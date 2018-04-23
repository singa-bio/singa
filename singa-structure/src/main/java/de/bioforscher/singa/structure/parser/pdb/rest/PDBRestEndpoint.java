package de.bioforscher.singa.structure.parser.pdb.rest;

import de.bioforscher.singa.core.parser.AbstractHTMLParser;

/**
 * @author fk
 */
public abstract class PDBRestEndpoint<ResultType> extends AbstractHTMLParser<ResultType> {

    protected abstract String getEndpoint();
}
