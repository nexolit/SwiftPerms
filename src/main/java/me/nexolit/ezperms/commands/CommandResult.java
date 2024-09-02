package me.nexolit.ezperms.commands;

public class CommandResult {
    public static final boolean SUCCESS = true;
    public static final boolean FAIL = false;

    private final boolean status;
    private String message = "Something went wrong";

    public CommandResult(String message, boolean status) {
        if(!message.isEmpty()) this.message = message;
        this.status = status;
    }

    public boolean getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
