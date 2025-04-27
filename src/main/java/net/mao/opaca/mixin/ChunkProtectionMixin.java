package net.mao.opaca.mixin;

import net.mao.opaca.OPACAdditions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xaero.pac.common.server.IServerData;
import xaero.pac.common.server.claims.protection.ChunkProtection;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.SectionPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Evoker;
import net.minecraft.world.entity.monster.Vex;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.EvokerFangs;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.*;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.*;
import xaero.pac.common.server.config.ServerConfig;
import xaero.pac.common.server.player.config.api.IPlayerConfigAPI;

import net.mao.opaca.config.IServerConfigAccessor;
import java.util.UUID;

@Mixin(ChunkProtection.class)
public abstract class ChunkProtectionMixin {

    @Unique
    private boolean isWorldProtected(ResourceLocation dimension) {
        if (OPACAdditions.Debug()) OPACAdditions.LOGGER.info("OnlyProtectClaimableDimensions: {}", ((IServerConfigAccessor)ServerConfig.CONFIG).getOnlyProtectClaimableDimensions());
        if (!((IServerConfigAccessor)ServerConfig.CONFIG).getOnlyProtectClaimableDimensions()) return true;

        boolean whitelist = ServerConfig.CONFIG.claimableDimensionsListType.get().toString().equals("ONLY");
        if (ServerConfig.CONFIG.claimableDimensionsList.get().contains(dimension.toString())) {
            if (OPACAdditions.Debug()) OPACAdditions.LOGGER.info("The DimensionsList contains: {}", dimension.toString());
            return whitelist;
        } else return !whitelist;
    }
    @Unique
    private boolean isWorldProtected(ServerLevel world) {
        ResourceLocation dimension = world.dimension().location();
        return isWorldProtected(dimension);
    }

    @Inject(
            method = "hasChunkAccess",
            at = @At("HEAD"),
            cancellable = true,
            remap = false
    )
    private void hasChunkAccessMixin(
            IPlayerConfigAPI claimConfig, Entity accessor, UUID accessorId, CallbackInfoReturnable<Boolean> cir) {
        if (OPACAdditions.Debug()) OPACAdditions.LOGGER.info("hasChunkAccessMixin triggered successfully!"); // will spam
        boolean isAServerPlayer = accessor instanceof ServerPlayer;
        if (isAServerPlayer) {
            ServerPlayer player = ((ServerPlayer) accessor);
            if (!isWorldProtected(player.level().dimension().location())) cir.setReturnValue(true);
        }
    }

    /*

    @Inject(
            method = "hasChunkAccess(Lxaero/pac/common/server/config/IPlayerConfigAPI;Lnet/minecraft/world/entity/Entity;Ljava/util/UUID;)Z",
            at = @At("HEAD"),
            cancellable = true
    )
    private void hasChunkAccessMixin(
            IPlayerConfigAPI claimConfig,
            Entity accessor,
            UUID accessorId,
            CallbackInfoReturnable<Boolean> cir) {
        // if (OPACAdditions.Debug()) OPACAdditions.LOGGER.info("hasChunkAccessMixin triggered successfully!"); // will spam
        boolean isAServerPlayer = accessor instanceof ServerPlayer;
        if (isAServerPlayer) {
            ServerPlayer player = ((ServerPlayer) accessor);
            if (!isWorldProtected(player.level().dimension().location())) cir.setReturnValue(true);
        }
    }

    @Inject(
            method = "onBlockInteraction(Lxaero/pac/common/server/claims/protection/IServerData;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/InteractionHand;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;ZZ)Z",
            at = @At("HEAD"),
            cancellable = true
    )
    private void onBlockInteractionMixin(
            IServerData<?, ?> serverData,
            BlockState blockState,
            Entity entity,
            InteractionHand hand,
            ItemStack heldItem,
            ServerLevel world,
            BlockPos pos,
            Direction direction,
            boolean breaking,
            boolean messages,
            CallbackInfoReturnable<Boolean> cir
    ) {
        if (OPACAdditions.Debug()) OPACAdditions.LOGGER.info("onBlockInteractionMixin triggered successfully!");
        if (!isWorldProtected(world)) cir.setReturnValue(false); // stop the function
    }

    */

}