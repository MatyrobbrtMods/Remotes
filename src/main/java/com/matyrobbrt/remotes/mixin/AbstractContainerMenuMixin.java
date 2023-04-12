package com.matyrobbrt.remotes.mixin;

import com.matyrobbrt.remotes.block.BlockRemotesPlayer;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractContainerMenu.class)
public class AbstractContainerMenuMixin {
    @Shadow @Final public NonNullList<Slot> slots;

    /**
     * This prevents the remote from being moved around.
     */
    @Inject(at = @At("HEAD"), method = "doClick", cancellable = true)
    private void remotes$preventRemoteSlotInteraction(int pSlotId, int pButton, ClickType pClickType, Player pPlayer, CallbackInfo ci) {
        final BlockRemotesPlayer blockRemotesPlayer = (BlockRemotesPlayer) pPlayer;
        if (pSlotId >= 0 && blockRemotesPlayer.remotes$isRemoteOpened() && blockRemotesPlayer.remotes$getRemoteSlotIndex() >= 0) {
            final Slot slot = this.slots.get(pSlotId);
            if (slot.getSlotIndex() == blockRemotesPlayer.remotes$getRemoteSlotIndex() && slot.container == pPlayer.getInventory()) {
                ci.cancel();
            }
        }
    }
}
