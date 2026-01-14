package gr1mly4memes.nitro.commands.subcommands;

import gr1mly4memes.nitro.NitroConfig;
import gr1mly4memes.nitro.commands.NitroSubcommand;
import net.minecraft.server.MinecraftServer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.io.File;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.GREEN;

public class ReloadCommand implements NitroSubcommand {
    @Override
    public boolean execute(CommandSender sender, String subCommand, String[] args) {
        MinecraftServer server = MinecraftServer.getServer();
        NitroConfig.init((File) server.options.valueOf("nitro-settings"));
        Command.broadcastCommandMessage(sender, text("Nitro config reload complete.", GREEN));
        return false;
    }
}
