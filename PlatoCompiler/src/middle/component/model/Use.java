package middle.component.model;

public class Use {
    private User user;
    // 被使用的值
    private Value usee;

    public Use(User user, Value usee) {
        this.user = user;
        this.usee = usee;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Value getUsee() {
        return usee;
    }

    public void setUsee(Value usee) {
        this.usee = usee;
    }
}
