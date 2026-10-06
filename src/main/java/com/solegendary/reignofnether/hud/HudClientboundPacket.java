package com.solegendary.reignofnether.hud;










import net.neoforged.neoforge.network.PacketDistributor;
import com.solegendary.reignofnether.util.DistUtil;
import com.solegendary.reignofnether.ReignOfNether;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.FriendlyByteBuf;
import com.solegendary.reignofnether.registrars.PacketHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.api.distmarker.Dist;
import com.solegendary.reignofnether.util.DistUtil;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

public class HudClientboundPacket implements CustomPacketPayload  {
    public static final Type<HudClientboundPacket> TYPE = new Type<>(ResourceLocation.parse("reignofnether:hud_clientbound_packet"));
    public static final StreamCodec<FriendlyByteBuf, HudClientboundPacket> STREAM_CODEC =
            StreamCodec.of((buf, payload) -> payload.encode(buf), HudClientboundPacket::new);

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }


    // ticks < 0 means "use HudClientEvents' default duration"
    private static final int DEFAULT_TICKS = -1;

    private final String msgKey;
    private final int ticks;

    public static void showTempMessageI18n(ServerPlayer player, String msgKey) {
        showTempMessageI18n(player, msgKey, DEFAULT_TICKS);
    }

    public static void showTempMessageI18n(ServerPlayer player, String msgKey, int ticks) {
        if (player == null)
            return;
        PacketDistributor.sendToPlayer(player, new HudClientboundPacket(msgKey, ticks)
        );
    }

    public static void showTempMessageI18n(String playerName, String msgKey) {
        showTempMessageI18n(playerName, msgKey, DEFAULT_TICKS);
    }

    public static void showTempMessageI18n(String playerName, String msgKey, int ticks) {
        if (playerName == null || playerName.isBlank())
            return;
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server == null)
            return;
        ServerPlayer sp = server.getPlayerList().getPlayerByName(playerName);
        if (sp != null)
            showTempMessageI18n(sp, msgKey, ticks);
    }

    public HudClientboundPacket(String message, int ticks) {
        this.msgKey = message;
        this.ticks = ticks;
    }

    public HudClientboundPacket(FriendlyByteBuf buffer) {
        this.msgKey = buffer.readUtf();
        this.ticks = buffer.readInt();
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeUtf(this.msgKey);
        buffer.writeInt(this.ticks);
    }

    public void handle(IPayloadContext ctx) {

 ctx.enqueueWork(() -> {
            DistUtil.unsafeRunWhenOn(Dist.CLIENT,
                    () -> () -> {
                        if (this.ticks < 0)
                            HudClientEvents.showTempMessageI18n(this.msgKey);
                        else
                            HudClientEvents.showTempMessageI18n(this.msgKey, this.ticks);
                    });
        });
    }
}