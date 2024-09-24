package backend.text;

public class Label extends TextAssembly {
    private final String label;

    public Label(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label + ":";
    }
}
