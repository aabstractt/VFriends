package net.vicnix.friends.session;

public class SessionPermission {

    private final String name;

    private final String prefix;

    private final Integer size;

    public SessionPermission(String name, String prefix, Integer size) {
        this.name = name;

        this.prefix = prefix;

        this.size = size;
    }

    public String getName() {
        return name;
    }

    public String getPrefix() {
        return prefix;
    }

    public Integer getSize() {
        return size;
    }
}
