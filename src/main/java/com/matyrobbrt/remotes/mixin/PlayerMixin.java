package com.matyrobbrt.remotes.mixin;

import com.matyrobbrt.remotes.block.BlockRemotesPlayer;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Player.class)
public class PlayerMixin implements BlockRemotesPlayer {
    @Unique
    private boolean remotes$remoteOpened;
    @Unique
    private int remotes$remoteSlotIndex;

    @Unique
    @Override
    public void remotes$markRemoteOpened(boolean remoteOpened) {
        this.remotes$remoteOpened = remoteOpened;
    }

    @Unique
    @Override
    public boolean remotes$isRemoteOpened() {
        return this.remotes$remoteOpened;
    }

    @Override
    public int remotes$getRemoteSlotIndex() {
        return remotes$remoteSlotIndex;
    }

    @Override
    public void remotes$setRemoteSlotIndex(int remoteIndex) {
        this.remotes$remoteSlotIndex = remoteIndex;
    }
}
