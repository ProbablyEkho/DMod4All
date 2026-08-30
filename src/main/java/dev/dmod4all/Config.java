package dev.dmod4all;

import net.neoforged.neoforge.common.ModConfigSpec;

public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    public static final ModConfigSpec.BooleanValue VANILLA_TOOLTIPS = BUILDER
        .comment("Whether to use the vanilla tooltips instead of the rename system dependent ones")
        .define("vanillaTooltips", false);
    static final ModConfigSpec SPEC = BUILDER.build();
}
