package vertexlink.store;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public abstract class BaseStore {
  protected final Path storePath;
  protected final Properties props = new Properties();

  protected BaseStore(String fileName) {
    this.storePath = Path.of(System.getProperty("user.home"), ".vertexlink", fileName);
    load();
  }

  protected synchronized void load() {
    if (Files.exists(this.storePath)) {
      try (InputStream input = Files.newInputStream(this.storePath)) {
        this.props.load(input);
      } catch (IOException exception) {
        System.err.println("Could not load file: " + exception.getMessage());
      }
    }
  }

  protected synchronized void persist(String comments) {
    try {
      Files.createDirectories(this.storePath.getParent());
      try (OutputStream output = Files.newOutputStream(this.storePath)) {
        this.props.store(output, comments);
      }
    } catch (IOException exception) {
      System.err.println("Could not save file: " + exception.getMessage());
    }
  }
}
