package pl.themolka.arcade.command;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Commands {
    public static final String DEFAULT_PREFIX = "";

    private final Map<String, Command> commandMap = new HashMap<>();
    private final ConsoleSender console;
    private String prefix;

    public Commands(ConsoleSender console) {
        this(console, null);
    }

    public Commands(String prefix) {
        this(null, prefix);
    }

    public Commands(ConsoleSender console, String prefix) {
        this.console = console;
        this.prefix = prefix;
    }

    public Command getCommand(String command) {
        return this.commandMap.get(command.toLowerCase());
    }

    public Set<String> getCommandNames() {
        return this.commandMap.keySet();
    }

    public List<Command> getCommands() {
        List<Command> commands = new ArrayList<>();

        for (Command command : this.commandMap.values()) {
            if (!commands.contains(command)) {
                commands.add(command);
            }
        }

        return commands;
    }

    public ConsoleSender getConsole() {
        return console;
    }

    public String getPrefix() {
        if (this.hasPrefix()) {
            return this.prefix;
        }

        return DEFAULT_PREFIX;
    }

    public void handleCommand(Sender sender, CommandContext context) {
        try {
            if (context.getCommand().isClientOnly() && sender instanceof ConsoleSender) {
                throw new CommandConsoleException();
            } else if (context.getCommand().hasPermission() && !sender.hasPermission(context.getCommand().getPermission())) {
                throw new CommandPermissionException();
            } else if (context.getCommand().getMin() > context.getParamsLength()) {
                throw new CommandUsageException("Zbyt malo argumentow.");
            } else {
                context.getCommand().handleCommand(sender, context);
            }
        } catch (CommandConsoleException ex) {
            String level = "gry";
            if (ex.isConsoleLevel()) {
                level = "konsoli";
            }

            sender.sendError("Ta komenda moze zostac wykonana tylko z poziomu " + level + ".");
        } catch (CommandPermissionException ex) {
            String permission = ".";
            if (ex.getPermission() != null) {
                permission = " - " + ex.getPermission();
            }

            sender.sendError("Nie posiadasz odpowiednich uprawnien" + permission);
        } catch (CommandUsageException ex) {
            if (ex.getMessage() != null) {
                sender.sendError(ex.getMessage());
            }

            sender.sendError("/" + context.getCommand().getUsage());
        } catch (CommandException ex) {
            if (ex.getMessage() != null) {
                sender.sendError(ex.getMessage());
            } else {
                sender.sendError("Nie udalo sie wykonac komendy, poniewaz popelniono jakis blad.");
            }

            if (ex.getCause() != null) {
                Throwable cause = ex.getCause();
                sender.sendError(cause.getClass().getSimpleName() + ": " + cause.getMessage());
            }
        } catch (NumberFormatException ex) {
            sender.sendError("Musisz podac liczbe, nie ciag znakow!");
        } catch (Throwable th) {
            sender.sendError("Wykryto niespodziewany blad - powiadom o tym administracje!");
            sender.sendError(th.getClass().getName() + ": " + th.getLocalizedMessage());
            th.printStackTrace();
        }
    }

    public void handleCommand(Sender sender, Command command, String label, String[] args) {
        if (sender != null && command != null && label != null) {
            this.handleCommand(sender, command, label, args, new CommandContextParser());
        }
    }

    public void handleCommand(Sender sender, Command command, String label, String[] args, CommandContext.IContextParser parser) {
        this.handleCommand(sender, parser.parse(command, label, args));
    }

    public List<String> handleCompleter(Sender sender, CommandContext context) {
        try {
            if (context.getCommand().isClientOnly() && sender instanceof ConsoleSender) {
                throw new CommandConsoleException();
            } else if (context.getCommand().hasPermission() && !sender.hasPermission(context.getCommand().getPermission())) {
                throw new CommandPermissionException(context.getCommand().getPermission());
            } else {
                return context.getCommand().handleCompleter(sender, context);
            }
        } catch (CommandConsoleException ex) {
            String level = "gry";
            if (ex.isConsoleLevel()) {
                level = "konsoli";
            }

            sender.sendError("Ta komenda moze zostac wykonana tylko z poziomu " + level + ".");
        } catch (CommandPermissionException ex) {
            String permission = ".";
            if (ex.getPermission() != null) {
                permission = " - " + ex.getPermission();
            }

            sender.sendError("Nie posiadasz odpowiednich uprawnien" + permission);
        } catch (CommandUsageException ex) {
            if (ex.getMessage() != null) {
                sender.sendError(ex.getMessage());
            }

            sender.sendError("/" + context.getCommand().getUsage());
        } catch (CommandException ex) {
            if (ex.getMessage() != null) {
                sender.sendError(ex.getMessage());
            } else {
                sender.sendError("Nie udalo sie wykonac komendy, poniewaz popelniono jakis blad.");
            }
        } catch (NumberFormatException ex) {
            sender.sendError("Musisz podac liczbe, nie ciag znakow!");
        } catch (Throwable th) {
            sender.sendError("Wykryto niespodziewany blad - powiadom o tym administracje!");
            sender.sendError(th.getClass().getName() + ": " + th.getLocalizedMessage());
            th.printStackTrace();
        }

        return null;
    }

    public List<String> handleCompleter(Sender sender, Command command, String label, String[] args) {
        List<String> results = null;
        if (sender != null && command != null && label != null) {
            results = this.handleCompleter(sender, command, label, args, new CommandContextParser());
        }

        if (results != null) {
            Collections.sort(results);
            return results;
        } else {
            return new ArrayList<>();
        }
    }

    public List<String> handleCompleter(Sender sender, Command command, String label, String[] args, CommandContext.IContextParser parser) {
        return this.handleCompleter(sender, parser.parse(command, label, args));
    }

    public boolean hasPrefix() {
        return this.prefix != null;
    }

    public void registerCommand(Command command) {
        for (String name : command.getName()) {
            this.commandMap.put(name, command);
        }
    }

    public void registerCommandClass(Class clazz) {
        for (Method method : clazz.getDeclaredMethods()) {
            method.setAccessible(true);

            Annotation annotation = method.getDeclaredAnnotation(CommandInfo.class);
            if (annotation != null) {
                this.registerCommandMethod(method, null, (CommandInfo) annotation);
            }
        }
    }

    public void registerCommandClasses(Class... classes) {
        for (Class clazz : classes) {
            this.registerCommandClass(clazz);
        }
    }

    public void registerCommandObject(Object object) {
        for (Method method : object.getClass().getDeclaredMethods()) {
            method.setAccessible(true);

            Annotation annotation = method.getDeclaredAnnotation(CommandInfo.class);
            if (annotation != null) {
                this.registerCommandMethod(method, object, (CommandInfo) annotation);
            }
        }
    }

    public void registerCommandObjects(Object... objects) {
        for (Object object : objects) {
            this.registerCommandObject(object);
        }
    }

    public void registerCommandMethod(Method method, Object object, CommandInfo info) {
        Method completer = null;
        if (!info.completer().isEmpty()) {
            try {
                completer = object.getClass().getDeclaredMethod(info.completer(), Sender.class, CommandContext.class);
                completer.setAccessible(true);
            } catch (NoSuchMethodException ignored) {
            }
        }

        this.registerCommand(new Command(
                info.name(),
                info.description(),
                info.min(),
                info.usage(),
                info.clientOnly(),
                info.flags(), info.permission(),
                method,
                object,
                completer
        ));
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }
}
