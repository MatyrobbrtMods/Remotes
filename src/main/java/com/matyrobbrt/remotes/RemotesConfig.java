package com.matyrobbrt.remotes;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;

public class RemotesConfig {
    public static final ForgeConfigSpec SPEC;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> BLACKLISTED_BLOCKS;

    static {
        final ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        BLACKLISTED_BLOCKS = builder.comment("A list of block IDs which cannot be interacted with remotely")
                .defineListAllowEmpty(List.of("blacklisted_blocks"), List::of, o -> ForgeRegistries.BLOCKS.containsKey(new ResourceLocation((String) o)));

        SPEC = builder.build();
    }
}
