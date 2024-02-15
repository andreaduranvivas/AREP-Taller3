package arep.clase.nuevasFunciones;

public class StaticFiles {
    private String directory;

    public StaticFiles() {
        this.directory = "target/classes/public";
    }

    public void location(String path) {
        this.directory = path;
    }

    public String getDirectory() {
        return directory;
    }
}
