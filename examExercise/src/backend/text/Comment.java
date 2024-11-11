package backend.text;

public class Comment extends TextAssembly {
    private final String comment;

    public Comment(String comment) {
        this.comment = comment;
    }

    @Override
    public String toString() {
        return comment;
    }
}
