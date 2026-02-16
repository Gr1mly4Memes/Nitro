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

    // Nitro start - features
    @GlobalConfig(name = "disable-packet-limit", category = {"features"})
    public static boolean disablePacketLimit = false;
    @GlobalConfig(name = "disableMovedWronglyThreshold", category = {"features"})
    public static boolean disableMovedWronglyThreshold = false;
    @GlobalConfig(name = "enable-books", category = {"features"})
    public static boolean enableBooks = true;
    @GlobalConfig(name = "use-compact-bit-storage",category = {"features"})
    public static boolean useCompactBitStorage = false;
    @GlobalConfig(name = "enable-amplified-world-generation", category = {"features"})
    public static boolean enableAmplifiedWorldGeneration = false;
    // Nitro start - Adrenaline Rush
    @GlobalConfig(name = "adrenaline-enabled", category = {"features", "Adrenaline"})
    public static boolean adrenalineEnabled = false;
    @GlobalConfig(name = "andrenaline-health-threshold", category = {"features", "Adrenaline"})
    public static double adrenalineHealthThreshold = 6.0; // when player health is <= this value, grant effects
    @GlobalConfig(name = "adrenaline-duration", category = {"features", "Adrenaline"})
    public static int adrenalineDuration = 100; // 5 seconds
    @GlobalConfig(name = "adrenaline-cooldown", category = {"features", "Adrenaline"})
    public static int adrenalineCooldown = 400; // 20 seconds
    @GlobalConfig(name = "adrenaline-block-fall-damage", category = {"features", "Adrenaline"})
    public static boolean adrenalineBlockFallDamage = true;
    // Nitro end - Adrenaline Rush
    // Nitro end - features

    // Nitro start - Optimizations
    @GlobalConfig(name = "create-snapshot-on-retrieving-block-state", category = {"Optimizations"})
    public static boolean createSnapshotOnRetrievingBlockState = true;
    @GlobalConfig(name = "projectile.max-loads-per-tick", category = {"Optimizations"})
    public static int maxProjectileLoadsPerTick = 10;
    @GlobalConfig(name = "projectile.max-loads-per-projectile", category = {"Optimizations"})
    public static int maxProjectileLoadsPerProjectile = 10;
    @GlobalConfig(name = "enable-suffocation-optimization", category = {"Optimizations"})
    public static boolean enableSuffocationOptimization = true;
    @GlobalConfig(name = "enable-async-mob-spawning", category = {"Optimizations", "Async"})
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
    @GlobalConfig(name = "multithreaded-enabled", category = {"Optimizations", "Async"})
    public static boolean multithreadedEnabled = true;
    @GlobalConfig(name = "multithreaded-compact-mode-enabled", category = {"Optimizations", "Async"})
    public static boolean multithreadedCompactModeEnabled = false;
    @GlobalConfig(name = "async-entity-tracker-max-threads", category = {"Optimizations", "Async"})
    public static int asyncEntityTrackerMaxThreads = 1;
    @GlobalConfig(name = "async-entity-tracker-keepalive", category = {"Optimizations", "Async"})
    public static int asyncEntityTrackerKeepalive = 60;
    @GlobalConfig(name = "async-entity-tracker-queue-size", category = {"Optimizations", "Async"})
    public static int asyncEntityTrackerQueueSize = 0;
    @GlobalConfig(name = "max-view-distance", category = {"Optimizations", "Chunk"})
    public static int maxViewDistance = 32;

    // Nitro end - Optimizations

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

    // Nitro end - Miscellaneous
    @GlobalConfig(name = "disable-method-profiler", category = {"Miscellaneous"})
    public static boolean disableMethodProfiler = true;
    @GlobalConfig(name = "allow-end-crystal-respawn", category = {"Miscellaneous"})
    public static boolean allowEndCrystalRespawn = true;
    @GlobalConfig(name = "disable-spawner-on-low-tps", category = {"Miscellaneous"})
    public static int spawnerTpsThreshold = 15;
    @GlobalConfig(name = "force-minecraft-command", category = {"Miscellaneous"})
    public static boolean forceMinecraftCommand = false;
    // Nitro end - Miscellaneous

    // Nitro start - Parallel World Ticking
    @GlobalConfig(name = "enable-parallel-world-ticking", category = {"PWT"})
    public static boolean enableParallelWorldTicking = false;
    @GlobalConfig(name = "parallel-thread-count", category = {"PWT"})
    public static int parallelThreadCount = 4;
    @GlobalConfig(name = "log-container-creation-stacktraces", category = {"PWT"})
    public static boolean logContainerCreationStacktraces = false;
    @GlobalConfig(name = "disable-hard-throw", category = {"PWT"})
    public static boolean disableHardThrow = false;
    @GlobalConfig(name = "use-per-world-tps-bar", category = {"PWT"})
    public static boolean usePerWorldTpsBar = true;
    @GlobalConfig(name = "show-tps-of-server-instead-of-world", category = {"PWT"})
    public static boolean showTPSOfServerInsteadOfWorld = true;
    // Nitro end - Parallel World Ticking

    // Nitro start - Lag compensation
    @GlobalConfig(name = "lag-compensation-enabled", category = {"Lag Compensation"})
    public static boolean lagCompensationEnabled = true;
    @GlobalConfig(name = "block-entity-acceleration", category = {"Lag Compensation"})
    public static boolean blockEntityAcceleration = false;
    @GlobalConfig(name = "block-breaking-acceleration", category = {"Lag Compensation"})
    public static boolean blockBreakingAcceleration = true;
    @GlobalConfig(name = "eating-acceleration", category = {"Lag Compensation"})
    public static boolean eatingAcceleration = true;
    @GlobalConfig(name = "potion-effect-acceleration", category = {"Lag Compensation"})
    public static boolean potionEffectAcceleration = true;
    @GlobalConfig(name = "fluid-acceleration", category = {"Lag Compensation"})
    public static boolean fluidAcceleration = true;
    @GlobalConfig(name = "pickup-acceleration", category = {"Lag Compensation"})
    public static boolean pickupAcceleration = true;
    @GlobalConfig(name = "portal-acceleration", category = {"Lag Compensation"})
    public static boolean portalAcceleration = true;
    @GlobalConfig(name = "time-acceleration", category = {"Lag Compensation"})
    public static boolean timeAcceleration = true;
    // Nitro end - Lag compensation
}
