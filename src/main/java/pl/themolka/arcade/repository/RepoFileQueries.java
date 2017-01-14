package pl.themolka.arcade.repository;

import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.commons.command.CommandContext;
import pl.themolka.commons.command.CommandInfo;
import pl.themolka.commons.command.Commands;
import pl.themolka.commons.command.ConsoleSender;
import pl.themolka.commons.session.Session;

import java.util.ArrayList;
import java.util.List;

public class RepoFileQueries extends Commands {
    private final ArcadePlugin plugin;

    private final RepoFile info;
    private final RepositoriesModule module;

    public RepoFileQueries(ArcadePlugin plugin, RepoFile info, RepositoriesModule module) {
        this.plugin = plugin;

        this.info = info;
        this.module = module;
    }

    @CommandInfo(name = "define", min = 1, flags = {
            "environment",
            "server-name"})
    public void defineQuery(Session<ConsoleSender> sender, CommandContext context) {
        if (!this.filter(context)) {
            return;
        }

        RepositoryParser<?> parser = this.module.getParser(context.getParams(0));
        if (parser != null) {
            this.info.setParser(parser);
        }
    }

    @CommandInfo(name = "exclude", min = 1, flags = {
            "environment",
            "server-name"})
    public void excludeQuery(Session<ConsoleSender> sender, CommandContext context) {
        if (!this.filter(context)) {
            return;
        }

        List<String> array = this.array(context);
    }

    @CommandInfo(name = "include", min = 1, flags = {
            "environment",
            "server-name"})
    public void includeQuery(Session<ConsoleSender> sender, CommandContext context) {
        if (!this.filter(context)) {
            return;
        }

        List<String> array = this.array(context);
    }

    @CommandInfo(name = "import", min = 1, flags = {
            "environment",
            "server-name"})
    public void importQuery(Session<ConsoleSender> sender, CommandContext context) {
        if (!this.filter(context)) {
            return;
        }

        List<String> array = this.array(context);
        if (array != null) {
            for (String name : array) {

            }
        }
    }

    private List<String> array(CommandContext context) {
        return this.array(context, 0);
    }

    private List<String> array(CommandContext context, int start) {
        List<String> array = new ArrayList<>();
        for (int i = start; i < context.getParamsLength(); i++) {
            String value = context.getParam(i);
            if (value.equals("*")) {
                return null;
            }

            array.add(value);
        }

        return array;
    }

    private boolean filter(CommandContext context) {
        for (String flag : context.getFlags()) {
            switch (flag) {
                case "environment":
                    if (!this.plugin.getEnvironment().getType().prettyName().equals(context.getFlag(flag))) {
                        return false;
                    }
                    break;
                case "server-name":
                    if (!this.plugin.getServerName().equals(context.getFlag(flag))) {
                        return false;
                    }
                    break;
            }
        }

        return true;
    }
}
