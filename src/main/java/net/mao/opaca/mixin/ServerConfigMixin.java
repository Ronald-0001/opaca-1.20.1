package net.mao.opaca.mixin;

import net.mao.opaca.OPACAdditions;
import net.mao.opaca.config.IServerConfigAccessor;
import net.minecraftforge.common.ForgeConfigSpec;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xaero.pac.common.server.config.ServerConfig;

@Mixin(ServerConfig.class)
public abstract class ServerConfigMixin implements IServerConfigAccessor {

    @Unique
    private ForgeConfigSpec.BooleanValue debug;
    @Unique
    private ForgeConfigSpec.BooleanValue onlyProtectClaimableDimensions;

    @Inject(method = "<init>", at = @At("RETURN"))
    public void ServerConfigMixin(ForgeConfigSpec.Builder builder, CallbackInfo ci) {
        OPACAdditions.LOGGER.info("OPACA ServerConfigMixin attaching configs!");
        builder.push("[OPACA]");
        this.debug = builder
                .comment("Should OPACA spam the console with debug msg?")
                .translation("gui.xaero_pac_config_opaca_debug")
                .worldRestart()
                .define("opacaDebug", false);
        this.onlyProtectClaimableDimensions = builder
                .comment("Should OPAC only protect claimable dimensions?")
                .translation("gui.xaero_pac_config_only_protect_claimable_dimensions")
                .worldRestart()
                .define("onlyProtectClaimableDimensions", true);
        builder.pop();
    }

    @Override
    public boolean getOPACADebug() {
        return debug.get();
    }
    @Override
    public boolean getOnlyProtectClaimableDimensions() {
        return onlyProtectClaimableDimensions.get();
    }
}
