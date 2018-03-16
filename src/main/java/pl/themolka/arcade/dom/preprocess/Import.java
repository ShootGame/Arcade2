package pl.themolka.arcade.dom.preprocess;

import pl.themolka.arcade.dom.Document;
import pl.themolka.arcade.dom.engine.EngineManager;

/**
 * Include documents from its own path.
 */
public class Import extends DocumentPreprocess {
    private final EngineManager engines;
    private final Preprocessor preprocessor;

    public Import(EngineManager engines, Preprocessor preprocessor) {
        this.engines = engines;
        this.preprocessor = preprocessor;
    }

    @Override
    public void invoke(Document document) throws PreprocessException {
        if (!document.hasPath()) {
            throw new PreprocessException(document, "Cannot resolve document path");
        }

        ImportStage stage = new ImportStage(this.engines, this.preprocessor, document);
        Preprocess preprocess = new TreePreprocess(stage);
        preprocess.preprocess(document);
    }
}
