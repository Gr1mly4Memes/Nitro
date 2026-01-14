package gr1mly4memes.nitro.config;


import gr1mly4memes.nitro.NitroConfig;
import gr1mly4memes.nitro.commands.GlobalConfigManager;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class GlobalConfigCreator {

    public static void main(String[] args) {
        YamlConfiguration config = new YamlConfiguration();
        config.options().setHeader(NitroConfig.CONFIG_HEADER);

        config.set("config-version", NitroConfig.CURRENT_CONFIG_VERSION);

        Class<NitroConfig> clazz = NitroConfig.class;

        for (Field field : clazz.getDeclaredFields()) {
            if (Modifier.isStatic(field.getModifiers())) {
                field.setAccessible(true);

                GlobalConfig globalConfig = field.getAnnotation(GlobalConfig.class);
                if (globalConfig != null) {
                    try {
                        GlobalConfigManager.VerifiedConfig verifiedConfig = GlobalConfigManager.VerifiedConfig.build(globalConfig, field);

                        ConfigVerify<? super Object> verify = verifiedConfig.verify();
                        boolean isEnumConfig = verify instanceof ConfigVerify.EnumConfigVerify;

                        Object defValue = isEnumConfig ? field.get(null).toString() : field.get(null);
                        config.set(verifiedConfig.path(), defValue);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        try {
            File file = new File("nitro.yml");
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
