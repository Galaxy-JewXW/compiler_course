package backend.text;

public class Label extends TextAssembly {
    private final String label;
    private final boolean isFuncLabel;

    public Label(String label, boolean isFuncLabel) {
        this.label = label;
        this.isFuncLabel = isFuncLabel;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return label + ":";
    }
}
