package com.matyrobbrt.remotes.block;

import com.matyrobbrt.remotes.RemotesConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class BlockRemoteItem extends Item {
    public static final String LINKED_BLOCK = "LinkedBlock";

    public BlockRemoteItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        if (pContext.getPlayer().isShiftKeyDown()) {
            if (!pContext.getLevel().isClientSide) {
                final BlockState clickedState = pContext.getLevel().getBlockState(pContext.getClickedPos());
                if (check(clickedState, pContext.getPlayer())) {
                    pContext.getItemInHand().getOrCreateTag().put(LINKED_BLOCK, new BlockTarget(
                            pContext.getClickedPos(), pContext.getClickedFace(),
                            pContext.getLevel().dimension(), clickedState
                    ).toNBT());
                }
            }
            return InteractionResult.CONSUME;
        }
        return InteractionResult.PASS;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        final ItemStack stack = pPlayer.getItemInHand(pUsedHand);
        if (!pLevel.isClientSide) {
            final CompoundTag c = stack.getTag();
            if (c == null || !c.contains(LINKED_BLOCK, Tag.TAG_COMPOUND)) return InteractionResultHolder.fail(stack);
            return BlockTarget.fromNBT(c.getCompound(LINKED_BLOCK)).map(blockTarget -> {
                final Level target = Objects.requireNonNull(pLevel.getServer()).getLevel(blockTarget.dimension());
                if (target == null) {
                    pPlayer.displayClientMessage(Component.translatable("cm.remotes.dimension404", Component.literal(blockTarget.dimension().location().toString()).withStyle(ChatFormatting.AQUA)), false);
                    return InteractionResultHolder.fail(stack);
                }

                final AbstractContainerMenu oldInventory = pPlayer.containerMenu;

                final BlockState state = target.getBlockState(blockTarget.pos());
                if (!check(state, pPlayer)) return InteractionResultHolder.fail(stack);

                state.use(target, pPlayer, pUsedHand, new BlockHitResult(Vec3.atCenterOf(blockTarget.pos()), blockTarget.direction(), blockTarget.pos(), false));

                if (oldInventory != pPlayer.containerMenu) { // We've changed inventories
                    final BlockRemotesPlayer brp = (BlockRemotesPlayer) pPlayer;
                    brp.remotes$markRemoteOpened(true);
                    brp.remotes$setRemoteSlotIndex(pUsedHand == InteractionHand.MAIN_HAND ? pPlayer.getInventory().selected : -1);
                }

                c.put(LINKED_BLOCK, blockTarget.withState(state).toNBT());

                return InteractionResultHolder.success(stack);
            }).orElseGet(() -> InteractionResultHolder.fail(stack));
        }
        return InteractionResultHolder.success(stack);
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        if (Optional.ofNullable(pStack.getTag()).flatMap(tag -> BlockTarget.fromNBT(tag.getCompound(LINKED_BLOCK))).isEmpty()) {
            pTooltipComponents.add(Component.translatable("tooltip.remotes.no_target1").withStyle(ChatFormatting.GOLD));
            pTooltipComponents.add(Component.translatable("tooltip.remotes.no_target2").withStyle(ChatFormatting.AQUA));
        }
    }

    @Override
    public Optional<TooltipComponent> getTooltipImage(ItemStack pStack) {
        return Optional.ofNullable(pStack.getTag()).flatMap(tag -> BlockTarget.fromNBT(tag.getCompound(LINKED_BLOCK)));
    }

    public static boolean check(BlockState state, Player player) {
        final String blockId = Objects.requireNonNull(ForgeRegistries.BLOCKS.getKey(state.getBlock())).toString();
        if (RemotesConfig.BLACKLISTED_BLOCKS.get().contains(blockId)) {
            player.displayClientMessage(Component.translatable("cm.remotes.cannot_interact_with_block", Component.literal(blockId).withStyle(ChatFormatting.GOLD)), true);
            return false;
        }
        return true;
    }
}
