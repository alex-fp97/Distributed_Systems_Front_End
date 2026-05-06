package MiscPackage;

import java.io.Serializable;

public final class Request implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String command;
    private final Object[] data;

    public Request(String command, Object[] data) {
        this.command = command;
        this.data = data;
    }

    public String command() { return command; }
    public Object[] data() { return data; }
}