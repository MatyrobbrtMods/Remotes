package com.matyrobbrt.remotes.block;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;

public record BlockTarget(BlockPos pos, Direction direction, ResourceKey<Level> dimension, BlockState rememberedState) implements TooltipComponent {
    public static final Codec<BlockTarget> CODEC = RecordCodecBuilder.create(in -> in.group(
            BlockPos.CODEC.fieldOf("pos").forGetter(BlockTarget::pos),
            Direction.CODEC.fieldOf("direction").forGetter(BlockTarget::direction),
            ResourceKey.codec(Registries.DIMENSION).fieldOf("dimension").forGetter(BlockTarget::dimension),
            BlockState.CODEC.fieldOf("rememberedState").forGetter(BlockTarget::rememberedState)
    ).apply(in, BlockTarget::new));

    public BlockTarget withState(BlockState state) {
        return new BlockTarget(this.pos, this.direction, this.dimension, state);
    }

    public static Optional<BlockTarget> fromNBT(CompoundTag tag) {
        return CODEC.decode(NbtOps.INSTANCE, tag).get().left().map(Pair::getFirst);
    }

    public Tag toNBT() {
        return CODEC.encodeStart(NbtOps.INSTANCE, this).getOrThrow(false, e -> {});
    }
}
