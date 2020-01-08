package Exporter.JsonExporter;

public class JCategory {

    private String title;
    private String hierarchy;

    public JCategory() {
    }

    public JCategory(String title, String hierarchy) {
        this.title = title;
        this.hierarchy = hierarchy;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getHierarchy() {
        return hierarchy;
    }

    public void setHierarchy(String hierarchy) {
        this.hierarchy = hierarchy;
    }
}
