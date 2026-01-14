package gr1mly4memes.nitro.yggdrasil;

import com.destroystokyo.paper.profile.PaperAuthenticationService;
import com.mojang.authlib.minecraft.MinecraftSessionService;

import java.net.Proxy;

public class NitroAuthenticationService extends PaperAuthenticationService {

    public NitroAuthenticationService(Proxy proxy) {
        super(proxy);
    }

    @Override
    public MinecraftSessionService createMinecraftSessionService() {
        return new NitroMinecraftSessionService(this.getServicesKeySet(), this.getProxy(), this.environment);
    }
}
