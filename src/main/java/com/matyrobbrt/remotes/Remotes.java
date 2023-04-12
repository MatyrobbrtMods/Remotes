package com.matyrobbrt.remotes;

import com.matyrobbrt.remotes.block.BlockRemoteItem;
import com.matyrobbrt.remotes.block.BlockRemotesPlayer;
import com.matyrobbrt.remotes.block.BlockTarget;
import com.matyrobbrt.remotes.client.BlockTargetTooltip;
import com.mojang.logging.LogUtils;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(Remotes.MOD_ID)
public class Remotes {
    public static final String MOD_ID = "remotes";
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID);

    public static final RegistryObject<Item> BLOCK_REMOTE = ITEMS.register("block_remote", () -> new BlockRemoteItem(new Item.Properties().tab(CreativeModeTab.TAB_MISC).stacksTo(1)));

    public Remotes() {
        final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ITEMS.register(modEventBus);

        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, RemotesConfig.SPEC, MOD_ID + "-server.toml");

        MinecraftForge.EVENT_BUS.addListener((final PlayerContainerEvent.Close event) ->
                ((BlockRemotesPlayer) event.getEntity()).remotes$markRemoteOpened(false));
    }

    @Mod.EventBusSubscriber(modid = Remotes.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        static void clientSetup(final RegisterClientTooltipComponentFactoriesEvent event) {
            event.register(BlockTarget.class, BlockTargetTooltip::new);
        }
    }
}
