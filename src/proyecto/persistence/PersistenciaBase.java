package proyecto.persistence;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Clase base con utilidades comunes para la persistencia CSV.
 */
public class PersistenciaBase {
    protected final Path file;

    public PersistenciaBase(Path file) throws IOException {
        this.file = file;
        if (Files.notExists(file.getParent())) {
            Files.createDirectories(file.getParent());
        }
        if (Files.notExists(file)) {
            Files.createFile(file);
        }
    }

    protected List<String> readDataLines() throws IOException {
        return Files.readAllLines(file, StandardCharsets.UTF_8)
                .stream()
                .filter(l -> !l.trim().isEmpty() && !l.trim().startsWith("#"))
                .collect(Collectors.toList());
    }

    protected void writeAllLines(List<String> lines) throws IOException {
        Files.write(file, lines, StandardCharsets.UTF_8, StandardOpenOption.TRUNCATE_EXISTING);
    }

    protected void appendLine(String line) throws IOException {
        Files.write(file, java.util.Collections.singletonList(line), StandardCharsets.UTF_8, StandardOpenOption.APPEND);
    }
}
