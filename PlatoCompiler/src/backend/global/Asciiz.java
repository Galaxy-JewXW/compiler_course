package backend.global;

public class Asciiz extends GlobalAssembly {
    private final String name;
    private final String content;

    public Asciiz(String name, String content) {
        this.name = name;
        this.content = content;
    }

    @Override
    public String toString() {
        return name + ": .asciiz \"" + content + "\"";
    }
}
