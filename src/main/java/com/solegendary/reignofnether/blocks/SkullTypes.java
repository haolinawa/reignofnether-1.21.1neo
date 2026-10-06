package com.solegendary.reignofnether.blocks;

import net.minecraft.world.level.block.SkullBlock;

/**
 * 1.21 port: SkullBlock.Type is now an interface extending StringRepresentable
 * (vanilla uses the nested enum SkullBlock.Types). We mirror that shape.
 */
public enum SkullTypes implements SkullBlock.Type {
    STRAY("stray"),
    BOGGED("bogged"),
    DROWNED("drowned"),
    HUSK("husk");

    private final String name;

    SkullTypes(String name) {
        this.name = name;
        TYPES.put(name, this);
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }
}
