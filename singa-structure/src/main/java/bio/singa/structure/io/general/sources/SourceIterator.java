package bio.singa.structure.io.general.sources;

import java.util.Iterator;
import java.util.List;

/**
 * @author cl
 */
public interface SourceIterator<SourceContent, ContentType> extends Iterator<SourceContent> {

    List<SourceContent> getSources();

    ContentType getContent(SourceContent source);

    boolean hasChain();

    String getChain();

    List<String> getChains();

}
