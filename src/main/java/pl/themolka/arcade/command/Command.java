package pl.themolka.arcade.command;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class Command {
    private final String[] name;
    private String description;
    private int min;
    private String usage;
    private boolean clientOnly;
    private final String[] flags;
    private String permission;
    private final Method method;
    private final Object classObject;
    private final Method completer;

    public Command(String[] name,
                   String description,
                   int min,
                   String usage,
                   boolean clientOnly,
                   String[] flags,
                   String permission,
                   Method method,
                   Object classObject,
                   Method completer) {
        this.name = name;
        this.description = description;
        this.min = min;
        this.usage = usage;
        this.clientOnly = clientOnly;
        this.flags = flags;
        this.permission = permission;
        this.method = method;
        this.classObject = classObject;
        this.completer = completer;
    }

    public String getCommand() {
        return this.getName()[0];
    }

    public String[] getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

    public int getMin() {
        return this.min;
    }

    public String getUsage() {
        return (this.getCommand() + " " + this.usage).trim();
    }

    public String[] getFlags() {
        return this.flags;
    }

    public String getPermission() {
        return this.permission;
    }

    public Method getMethod() {
        return this.method;
    }

    public Object getClassObject() {
        return this.classObject;
    }

    public Method getCompleter() {
        return this.completer;
    }

    public void handleCommand(Sender sender, CommandContext context) throws Throwable {
        if (this.getMethod() == null) {
            return;
        }

        try {
            this.getMethod().invoke(this.getClassObject(), sender, context);
        } catch (InvocationTargetException ex) {
            throw ex.getTargetException();
        }
    }

    public List<String> handleCompleter(Sender sender, CommandContext context) throws Throwable {
        if (this.getCompleter() == null) {
            return null;
        }

        try {
            this.getCompleter().setAccessible(true);
            Object result = this.getCompleter().invoke(this.getClassObject(), sender, context);

            if (result instanceof List) {
                List<String> results = new ArrayList<>();
                for (String string : (List<String>) result) {
                    results.add(string);
                }

                return results;
            }
        } catch (InvocationTargetException ex) {
            throw ex.getTargetException();
        }

        return null;
    }

    public boolean hasCompleter() {
        return this.completer != null;
    }

    public boolean hasFlag(String flag) {
        for (String f : this.flags) {
            if (f.equals(flag)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasPermission() {
        return this.permission != null && !this.permission.isEmpty();
    }

    public boolean isClientOnly() {
        return this.clientOnly;
    }

    public void setClientOnly(boolean clientOnly) {
        this.clientOnly = clientOnly;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public void setUsage(String usage) {
        this.usage = usage;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }
}