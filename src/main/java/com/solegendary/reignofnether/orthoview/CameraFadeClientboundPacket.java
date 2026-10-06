package com.solegendary.reignofnether.orthoview;










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
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.api.distmarker.Dist;
import com.solegendary.reignofnether.util.DistUtil;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

public class CameraFadeClientboundPacket implements CustomPacketPayload  {
    public static final Type<CameraFadeClientboundPacket> TYPE = new Type<>(ResourceLocation.parse("reignofnether:camera_fade_clientbound_packet"));
    public static final StreamCodec<FriendlyByteBuf, CameraFadeClientboundPacket> STREAM_CODEC =
            StreamCodec.of((buf, payload) -> payload.encode(buf), CameraFadeClientboundPacket::new);

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }


    private final String playerName;
    private final BlockPos pos;
    private final int fadeOutTicks;
    private final int blackoutTicks;
    private final int fadeInTicks;

    public static void fadeMoveCam(ServerPlayer player, BlockPos pos, int fadeOutTicks, int blackoutTicks, int fadeInTicks) {
        if (player == null)
            return;
        PacketDistributor.sendToPlayer(player, new CameraFadeClientboundPacket(player.getName().getString(), pos, fadeOutTicks, blackoutTicks, fadeInTicks)
        );
    }

    public CameraFadeClientboundPacket(String playerName, BlockPos pos, int fadeOutTicks, int blackoutTicks, int fadeInTicks) {
        this.playerName = playerName;
        this.pos = pos;
        this.fadeOutTicks = fadeOutTicks;
        this.blackoutTicks = blackoutTicks;
        this.fadeInTicks = fadeInTicks;
    }

    public CameraFadeClientboundPacket(FriendlyByteBuf buffer) {
        this.playerName = buffer.readUtf();
        this.pos = buffer.readBlockPos();
        this.fadeOutTicks = buffer.readInt();
        this.blackoutTicks = buffer.readInt();
        this.fadeInTicks = buffer.readInt();
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeUtf(this.playerName);
        buffer.writeBlockPos(this.pos);
        buffer.writeInt(this.fadeOutTicks);
        buffer.writeInt(this.blackoutTicks);
        buffer.writeInt(this.fadeInTicks);
    }

    public void handle(IPayloadContext ctx) {

 ctx.enqueueWork(() -> {
            DistUtil.unsafeRunWhenOn(Dist.CLIENT,
                    () -> () -> {
                        CameraFadeClientEvents.fadeMoveCam(this.playerName, this.pos,
                                this.fadeOutTicks, this.blackoutTicks, this.fadeInTicks);
                    });
        });
    }
}