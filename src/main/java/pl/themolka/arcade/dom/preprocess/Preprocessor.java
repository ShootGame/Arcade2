package pl.themolka.arcade.dom.preprocess;

import pl.themolka.arcade.dom.Document;
import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.dom.Property;

import java.util.ArrayList;
import java.util.List;

public class Preprocessor implements Preprocess {
    private final List<Preprocess> executors = new ArrayList<>();

    @Override
    public void preprocess(Document document) throws PreprocessException {
        for (Preprocess executor : this.executors) {
            try {
                executor.preprocess(document);
            } catch (PreprocessNotSupportedException ignored) {
            }
        }
    }

    @Override
    public void preprocess(Node node) throws PreprocessException {
        for (Preprocess executor : this.executors) {
            try {
                executor.preprocess(node);
            } catch (PreprocessNotSupportedException ignored) {
            }
        }
    }

    @Override
    public void preprocess(Property property) throws PreprocessException {
        for (Preprocess executor : this.executors) {
            try {
                executor.preprocess(property);
            } catch (PreprocessNotSupportedException ignored) {
            }
        }
    }

    public List<Preprocess> getExecutors() {
        return new ArrayList<>(this.executors);
    }

    public boolean install(Preprocess executor) {
        return this.executors.add(executor);
    }
}
