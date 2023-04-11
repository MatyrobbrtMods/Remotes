package com.matyrobbrt.remotes.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.matyrobbrt.remotes.block.BlockRemoteItem;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({Player.class, ServerPlayer.class})
public abstract class PlayerStillValidMixin {
    @ModifyExpressionValue(method = "tick()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/AbstractContainerMenu;stillValid(Lnet/minecraft/world/entity/player/Player;)Z"))
    private boolean blockremotes$allowContainerValid(boolean original) {
        return BlockRemoteItem.containerValid((Player) (Object) this, original);
    }
}
