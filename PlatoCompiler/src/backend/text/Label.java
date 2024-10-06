package backend.text;

public class Label extends TextAssembly {
    private final String label;

    public Label(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return label + ":";
    }
}
