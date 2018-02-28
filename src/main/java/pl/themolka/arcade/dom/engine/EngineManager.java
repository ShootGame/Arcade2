package pl.themolka.arcade.dom.engine;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Managing {@link DOMEngine}s by their file extensions specified in the
 * {@link FileExtensions} annotation.
 */
public class EngineManager {
    private final Map<String, DOMEngine> engines = new HashMap<>();

    public DOMEngine forFile(File file) throws EngineNotInstalledException {
        return this.forFile(file.getName());
    }

    public DOMEngine forFile(String file) throws EngineNotInstalledException {
        DOMEngine engine = this.getEngine(FilenameUtils.getExtension(file));
        if (engine != null) {
            return engine;
        }

        throw new EngineNotInstalledException("No engine installed for file: " + file);
    }

    public DOMEngine getEngine(String fileExtension) {
        return fileExtension != null && !fileExtension.isEmpty()
                ? this.engines.get(fileExtension.toLowerCase())
                : null;
    }

    public Set<DOMEngine> getEngines() {
        return new HashSet<>(this.engines.values());
    }

    public Set<String> getSupportedFileExtensions() {
        return this.engines.keySet();
    }

    public void registerDefault() {
        this.registerDefault(JDOMEngine.class);
        this.registerDefault(PropertiesEngine.class);
    }

    public void registerEngine(DOMEngine engine, String... fileExtensions) {
        for (String fileExtension : fileExtensions) {
            if (!fileExtension.isEmpty()) {
                this.engines.put(fileExtension, engine);
            }
        }
    }

    private void registerDefault(Class<? extends DOMEngine> clazz) {
        try {
            FileExtensions extensions = clazz.getAnnotation(FileExtensions.class);
            if (extensions != null) {
                String[] accept = extensions.value();

                if (accept != null && accept.length != 0) {
                    this.registerEngine(clazz.newInstance(), accept);
                }
            }
        } catch (ReflectiveOperationException ex) {
            ex.printStackTrace();
        }
    }
}
