package com.matyrobbrt.remotes.block;

import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public interface BlockRemotesPlayer {
    void remotes$markRemoteOpened(boolean remoteOpened);
    boolean remotes$isRemoteOpened();

    void remotes$setRemoteSlotIndex(int remoteIndex);
    int remotes$getRemoteSlotIndex();
}
