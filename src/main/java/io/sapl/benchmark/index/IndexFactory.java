package io.sapl.benchmark.index;

import io.sapl.interpreter.DefaultSAPLInterpreter;
import io.sapl.interpreter.EvaluationContext;
import io.sapl.interpreter.functions.AnnotationFunctionContext;
import io.sapl.interpreter.pip.AnnotationAttributeContext;
import io.sapl.prp.PrpUpdateEvent;
import io.sapl.prp.filesystem.FileSystemPrpUpdateEventSource;
import io.sapl.prp.index.ImmutableParsedDocumentIndex;
import io.sapl.prp.index.canonical.CanonicalImmutableParsedDocumentIndex;
import io.sapl.prp.index.naive.NaiveImmutableParsedDocumentIndex;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;

@Slf4j
@UtilityClass
public class IndexFactory {

    private static final EvaluationContext PDP_SCOPED_EVALUATION_CONTEXT =
            new EvaluationContext(new AnnotationAttributeContext(), new AnnotationFunctionContext(), new HashMap<>());


    public ImmutableParsedDocumentIndex indexByTypeForDocumentsIn(IndexType indexType, String policiesFolder) {
        switch (indexType) {
            case NAIVE:
                return naiveIndexForDocumentsIn(policiesFolder);
            case CANONICAL:
                //fall through
            default:
                return canonicalIndexForDocumentsIn(policiesFolder);
        }
    }

    public ImmutableParsedDocumentIndex naiveIndexForDocumentsIn(String policiesFolder) {
        return new NaiveImmutableParsedDocumentIndex().apply(fetchInitialUpdateEvent(policiesFolder));
    }

    public ImmutableParsedDocumentIndex canonicalIndexForDocumentsIn(String policiesFolder) {
        return new CanonicalImmutableParsedDocumentIndex(PDP_SCOPED_EVALUATION_CONTEXT)
                .apply(fetchInitialUpdateEvent(policiesFolder));
    }

    private PrpUpdateEvent fetchInitialUpdateEvent(String policiesFolder) {
        return new FileSystemPrpUpdateEventSource(policiesFolder, new DefaultSAPLInterpreter()).getUpdates()
                .doOnNext(update -> log.debug("Initialize index with update event: {}", update)).blockFirst();
    }

}
