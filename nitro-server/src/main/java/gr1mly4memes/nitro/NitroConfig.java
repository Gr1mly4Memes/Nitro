package gr1mly4memes.nitro;

import com.destroystokyo.paper.util.SneakyThrow;
import gr1mly4memes.nitro.commands.GlobalConfigManager;
import gr1mly4memes.nitro.commands.NitroCommand;
import gr1mly4memes.nitro.config.ConfigVerify;
import gr1mly4memes.nitro.config.GlobalConfig;
import gr1mly4memes.nitro.region.EnumRegionFileExtension;
import gr1mly4memes.nitro.region.NitroRegionFile;
import gr1mly4memes.nitro.yggdrasil.NitroMinecraftSessionService;
import io.papermc.paper.configuration.GlobalConfiguration;
import net.minecraft.server.MinecraftServer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;

public final class NitroConfig {
    public static final List<String> CONFIG_HEADER = List.of(
            "This is the main configuration file for Nitro.",
            "",
            "Created by Gr1mly4Memes"
    );
    public static final int CURRENT_CONFIG_VERSION = 6;

    private static File configFile;
    public static YamlConfiguration config;
    private static int configVersion;
    public static boolean createWorldSections = true;

    public static void init(final File file) {
        NitroConfig.configFile = file;
        config = new YamlConfiguration();
        config.options().setHeader(CONFIG_HEADER);
        config.options().copyDefaults(true);

        if (!file.exists()) {
            try {
                boolean is = file.createNewFile();
                if (!is) {
                    throw new IOException("Can't create file");
                }
            } catch (final Exception ex) {
                Bukkit.getLogger().log(Level.SEVERE, "Failure to create nitro config", ex);
            }
        } else {
            try {
                config.load(file);
            } catch (final Exception ex) {
                Bukkit.getLogger().log(Level.SEVERE, "Failure to load nitro config", ex);
                SneakyThrow.sneaky(ex);
                throw new RuntimeException(ex);
            }
        }

        NitroConfig.configVersion = NitroConfig.config.getInt("config-version", CURRENT_CONFIG_VERSION);
        NitroConfig.config.set("config-version", CURRENT_CONFIG_VERSION);

        GlobalConfigManager.init();

        registerCommand("nitro", new NitroCommand("nitro"));
    }

    public static void save() {
        try {
            config.save(NitroConfig.configFile);
        } catch (final Exception ex) {
            Bukkit.getLogger().log(Level.SEVERE, "Unable to save nitro config", ex);
        }
    }

    public static void registerCommand(String name, Command command) {
        MinecraftServer.getServer().server.getCommandMap().register(name, "nitro", command);
        MinecraftServer.getServer().server.syncCommands();
    }

    public static void unregisterCommand(String name) {
        name = name.toLowerCase(Locale.ENGLISH).trim();
        MinecraftServer.getServer().server.getCommandMap().getKnownCommands().remove(name);
        MinecraftServer.getServer().server.getCommandMap().getKnownCommands().remove("nitro:" + name);
        MinecraftServer.getServer().server.syncCommands();
    }

    @GlobalConfig(name = "enable-async-mob-spawning", category = {"Optimizations"})
    public static boolean enableAsyncMobSpawning = true;
    // Runtime value (reload-safe)
    public static boolean runtimeAsyncMobSpawning;
    private static boolean initialized = false;
    /** Called after config load (including reload) */
    public static void postLoad() {
        if (!initialized) {
            initialized = true;
            runtimeAsyncMobSpawning = enableAsyncMobSpawning;
        }
    }

    @GlobalConfig(name = "projectile.max-loads-per-tick", category = {"Optimizations"})
    public static int maxProjectileLoadsPerTick = 10;

    @GlobalConfig(name = "projectile.max-loads-per-projectile", category = {"Optimizations"})
    public static int maxProjectileLoadsPerProjectile = 10;

    @GlobalConfig(name = "enable-suffocation-optimization", category = {"Optimizations"})
    public static boolean enableSuffocationOptimization = true;

    @GlobalConfig(name = "enable-books", category = {"features"})
    public static boolean enableBooks = true;

    @GlobalConfig(name = "lava-riptide", category = {"Miscellaneous"})
    public static boolean lavaRiptide = false; /* not even sure if it works */

    @GlobalConfig(name = "disable-method-profiler", category = {"Miscellaneous"})
    public static boolean disableMethodProfiler = true;

    @GlobalConfig(name = "disable-packet-limit", category = {"features"})
    public static boolean disablePacketLimit = false;

    @GlobalConfig(name = "disableMovedWronglyThreshold", category = {"features"})
    public static boolean disableMovedWronglyThreshold = false;

    // Nitro start - region
    @GlobalConfig(name = "format", category = "region", lock = true, verify = RegionFormatVerify.class)
    public static EnumRegionFileExtension regionFormat = EnumRegionFileExtension.MCA;

    private static class RegionFormatVerify extends ConfigVerify.EnumConfigVerify<EnumRegionFileExtension> {
        @Override
        public String check(EnumRegionFileExtension old, EnumRegionFileExtension value) throws IllegalArgumentException {
            if (value == null) {
                throw new RuntimeException("Invalid region format: " + regionFormat);
            }
            if (regionFormat == EnumRegionFileExtension.LINEAR) {
                NitroRegionFile.SAVE_DELAY_MS = linearIoFlushDelayMs;
                NitroRegionFile.SAVE_THREAD_MAX_COUNT = linearIoThreadCount;
                NitroRegionFile.USE_VIRTUAL_THREAD = linearUseVirtualThread;
            }
            return null;
        }
    }

    @GlobalConfig(name = "compression-level", category = {"region", "linear"}, lock = true, verify = LinearCompressVerify.class)
    public static int linearCompressionLevel = 1;

    private static class LinearCompressVerify extends ConfigVerify.IntConfigVerify {
        @Override
        public String check(Integer old, Integer value) throws IllegalArgumentException {
            if (value < 1 || value > 23) {
                MinecraftServer.LOGGER.error("Linear region compression level should be between 1 and 22 in config: {}", linearCompressionLevel);
                MinecraftServer.LOGGER.error("Falling back to compression level 1.");
                linearCompressionLevel = 1;
            }
            return null;
        }
    }

    @GlobalConfig(name = "io-thread-count", category = {"region", "linear"}, lock = true, verify = ConfigVerify.IntConfigVerify.class)
    public static int linearIoThreadCount = 6;

    @GlobalConfig(name = "io-flush-delay-ms", category = {"region", "linear"}, lock = true, verify = ConfigVerify.IntConfigVerify.class)
    public static int linearIoFlushDelayMs = 100;

    @GlobalConfig(name = "use-virtual-thread", category = {"region", "linear"})
    public static boolean linearUseVirtualThread = true;

    @GlobalConfig(name = "flush-max-threads", category = {"region", "linear"}, lock = true, verify = ConfigVerify.IntConfigVerify.class)
    public static int linearFlushThreads = 1;

    public static int getLinearFlushThreads() {
        if (linearFlushThreads < 0) {
            return Math.max(Runtime.getRuntime().availableProcessors() + linearFlushThreads, 1);
        } else {
            return Math.max(linearFlushThreads, 1);
        }
    }
    // Nitro end - region

    @GlobalConfig(name = "extra-yggdrasil-service-enable", category = {"features", "yggdrasil"}, verify = ExtraYggdrasilServiceVerify.class)
    public static boolean extraYggdrasilService = false;

    public static class ExtraYggdrasilServiceVerify extends ConfigVerify.BooleanConfigVerify {
        @Override
        public String check(Boolean old, Boolean value) {
            if (value) {
                NitroLogger.LOGGER.warning("extra-yggdrasil-service is an unofficial support. Enabling it may cause data security problems!");
                GlobalConfiguration.get().unsupportedSettings.performUsernameValidation = true; // always check username
            }
            return null;
        }
    }

    @GlobalConfig(name = "login-protect", category = {"features", "yggdrasil"})
    public static boolean loginProtect = false;

    @GlobalConfig(name = "urls", category = {"features", "yggdrasil"}, lock = true, verify = ExtraYggdrasilUrlsValidator.class)
    public static List<String> serviceList = List.of("https://url.with.authlib-injector-yggdrasil");

    public static class ExtraYggdrasilUrlsValidator extends ConfigVerify.ListConfigVerify {
        @Override
        public String check(List<?> old, List<?> value) {
            NitroMinecraftSessionService.initExtraYggdrasilList(serviceList);
            return null;
        }
    }
}
