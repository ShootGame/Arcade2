package pl.themolka.arcade.dom.preprocess;

import pl.themolka.arcade.dom.DOMException;
import pl.themolka.arcade.dom.Document;
import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.dom.engine.EngineManager;
import pl.themolka.arcade.dom.engine.EngineNotInstalledException;

import java.net.URI;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.Stack;

public class ImportStage implements TreePreprocessHandler {
    private static final Stack<Path> stack = new Stack<>();

    private final EngineManager engines;
    private final Preprocessor preprocessor;
    private final Document document;

    public ImportStage(EngineManager engines, Preprocessor preprocessor, Document document) {
        this.engines = Objects.requireNonNull(engines, "engines cannot be null");
        this.preprocessor = Objects.requireNonNull(preprocessor, "preprocessor cannot be null");
        this.document = Objects.requireNonNull(document, "document cannot be null");

        stack.push(document.getPath());
    }

    @Override
    public List<Node> defineNode(Node parent) {
        return parent.children("import");
    }

    @Override
    public void invokeNode(Node node) throws PreprocessException {
        if (!node.hasParent()) {
            throw new PreprocessException(node, "Node must have its parent");
        }
        Node parent = node.getParent();

        String targetPath = node.getValue();
        if (targetPath == null) {
            throw new PreprocessException(node, "Target document path not specified");
        }

        Document target;
        try {
            Path directory = this.document.getPath().getParent();
            target = readDocument(this.engines, node, directory.resolve(targetPath).toUri());
        } catch (InvalidPathException ex) {
            throw new PreprocessException(node, "Invalid document path: " + ex.getReason(), ex);
        }

        if (!target.hasPath()) {
            throw new PreprocessException(target, "Cannot resolve target document path");
        } else if (!target.hasRoot()) {
            throw new PreprocessException(target, "Target document is empty");
        }

        Path path = target.getPath();
        if (stack.search(path) != -1) {
            throw new PreprocessException(target, "Import loop detected");
        }

        stack.push(path);

        // Preprocess the target document.
        this.preprocessor.preprocess(target);

        Node.detach(node); // Remove the old <import> node.
        parent.add(target.getRoot().children()); // We don't support root node properties.
    }

    protected static Document readDocument(EngineManager engines, Node node, URI uri) throws PreprocessException {
        String uriPath = uri.getRawPath();
        if (uriPath == null) {
            throw new PreprocessException(node, "Invalid path");
        }

        try {
            return engines.forFile(uriPath).read(uri);
        } catch (EngineNotInstalledException ex) {
            throw new PreprocessException(node, "No DOM engine installed for document: " + uriPath);
        } catch (DOMException ex) {
            throw new PreprocessException(node, "Could not parse document: " + uriPath);
        } catch (Exception ex) {
            throw new PreprocessException(node, "Could not read document: " + uriPath);
        }
    }
}
