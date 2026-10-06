package com.solegendary.reignofnether.util;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;

import java.util.function.Supplier;

/**
 * NeoForge 1.20.5+ removed {@code net.neoforged.fml.DistExecutor}.
 * This shim preserves the original "defer class-loading until the correct side" semantics
 * that the Forge 1.20.1 code relied on.
 */
public final class DistUtil {

    private DistUtil() {}

    public static void runWhenOn(Dist dist, Supplier<Runnable> toRun) {
        if (FMLEnvironment.dist == dist) {
            toRun.get().run();
        }
    }

    public static void unsafeRunWhenOn(Dist dist, Supplier<Runnable> toRun) {
        if (FMLEnvironment.dist == dist) {
            toRun.get().run();
        }
    }

    public static <T> T runForDist(Supplier<Supplier<T>> client, Supplier<Supplier<T>> server) {
        return FMLEnvironment.dist == Dist.CLIENT ? client.get().get() : server.get().get();
    }
}
