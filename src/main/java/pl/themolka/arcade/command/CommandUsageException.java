package pl.themolka.arcade.command;

public class CommandUsageException extends CommandException {
    public CommandUsageException() {
        super();
    }

    public CommandUsageException(String message) {
        super(message);
    }
}
