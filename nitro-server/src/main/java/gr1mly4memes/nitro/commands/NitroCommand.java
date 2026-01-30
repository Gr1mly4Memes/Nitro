package gr1mly4memes.nitro.commands;

import gr1mly4memes.nitro.commands.subcommands.MSPTCommand;
import gr1mly4memes.nitro.commands.subcommands.ReloadCommand;
import io.papermc.paper.command.CommandUtil;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.PluginManager;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.RED;

public final class NitroCommand extends Command {
    static final String BASE_PERM = "bukkit.command.nitro.";
    // subcommand label -> subcommand
    private static final Map<String, NitroSubcommand> SUBCOMMANDS = Util.make(() -> {
        final Map<Set<String>, NitroSubcommand> commands = new HashMap<>();
        commands.put(Set.of("mspt"), new MSPTCommand());
        commands.put(Set.of("reload"), new ReloadCommand());

        return commands.entrySet().stream()
                .flatMap(entry -> entry.getKey().stream().map(s -> Map.entry(s, entry.getValue())))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    });
    private static final Set<String> COMPLETABLE_SUBCOMMANDS = SUBCOMMANDS.entrySet().stream().filter(entry -> entry.getValue().tabCompletes()).map(Map.Entry::getKey).collect(Collectors.toSet());

    public NitroCommand(final String name) {
        super(name);
        this.description = "Nitro related commands";
        this.usageMessage = "/nitor [" + String.join(" | ", SUBCOMMANDS.keySet()) + "]";
        final List<String> permissions = new ArrayList<>();
        permissions.add("bukkit.command.nitro");
        permissions.addAll(SUBCOMMANDS.keySet().stream().map(s -> BASE_PERM + s).toList());
        this.setPermission(String.join(";", permissions));
        final PluginManager pluginManager = Bukkit.getServer().getPluginManager();
        for (final String perm : permissions) {
            if (pluginManager.getPermission(perm) == null) {
                pluginManager.addPermission(new Permission(perm, PermissionDefault.OP));
            }
        }
    }

    private static boolean testPermission(final CommandSender sender, final String permission) {
        if (sender.hasPermission(BASE_PERM + permission) || sender.hasPermission("bukkit.command.nitro")) {
            return true;
        }
        sender.sendMessage(Bukkit.permissionMessage());
        return false;
    }

    @NotNull

    @Override
    public List<String> tabComplete(final @NotNull CommandSender sender, final @NotNull String alias, final String[] args, final @Nullable Location location) throws IllegalArgumentException {
        if (args.length <= 1) {
            return CommandUtil.getListMatchingLast(sender, args, COMPLETABLE_SUBCOMMANDS);
        }

        final @Nullable Pair<String, NitroSubcommand> subCommand = resolveCommand(args[0]);
        if (subCommand != null) {
            return subCommand.second().tabComplete(sender, subCommand.first(), Arrays.copyOfRange(args, 1, args.length));
        }

        return Collections.emptyList();
    }

    @Override
    public boolean execute(final @NotNull CommandSender sender, final @NotNull String commandLabel, final String[] args) {
        if (!testPermission(sender)) {
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(text("Usage: " + this.usageMessage, RED));
            return false;
        }
        final Pair<String, NitroSubcommand> subCommand = resolveCommand(args[0]);

        if (subCommand == null) {
            sender.sendMessage(text("Usage: " + this.usageMessage, RED));
            return false;
        }

        if (!testPermission(sender, subCommand.first())) {
            return true;
        }
        final String[] choppedArgs = Arrays.copyOfRange(args, 1, args.length);
        return subCommand.second().execute(sender, subCommand.first(), choppedArgs);
    }

    @Nullable
    private static Pair<String, NitroSubcommand> resolveCommand(String label) {
        label = label.toLowerCase(Locale.ENGLISH);
        NitroSubcommand subCommand = SUBCOMMANDS.get(label);

        if (subCommand != null) {
            return Pair.of(label, subCommand);
        }

        return null;
    }
}
