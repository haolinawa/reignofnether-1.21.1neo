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
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.api.distmarker.Dist;
import com.solegendary.reignofnether.util.DistUtil;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

public class CameraClientboundPacket implements CustomPacketPayload  {
    public static final Type<CameraClientboundPacket> TYPE = new Type<>(ResourceLocation.parse("reignofnether:camera_clientbound_packet"));
    public static final StreamCodec<FriendlyByteBuf, CameraClientboundPacket> STREAM_CODEC =
            StreamCodec.of((buf, payload) -> payload.encode(buf), CameraClientboundPacket::new);

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }


    private final String playerName;
    private final BlockPos pos;
    private final int cameraLockTicks;
    private final int forcePanTicks;
    private final int zoomLevel;

    public static void forceMoveCam(ServerPlayer player, BlockPos pos, int cameraLockTicks, int forcePanTicks, int zoomLevel) {
        if (player == null)
            return;
        PacketDistributor.sendToPlayer(player, new CameraClientboundPacket(player.getName().getString(), pos, cameraLockTicks, forcePanTicks, zoomLevel)
        );
    }

    public CameraClientboundPacket(String playerName, BlockPos pos, int cameraLockTicks, int forcePanTicks, int zoomLevel) {
        this.playerName = playerName;
        this.pos = pos;
        this.cameraLockTicks = cameraLockTicks;
        this.forcePanTicks = forcePanTicks;
        this.zoomLevel = zoomLevel;
    }

    public CameraClientboundPacket(FriendlyByteBuf buffer) {
        this.playerName = buffer.readUtf();
        this.pos = buffer.readBlockPos();
        this.cameraLockTicks = buffer.readInt();
        this.forcePanTicks = buffer.readInt();
        this.zoomLevel = buffer.readInt();
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeUtf(this.playerName);
        buffer.writeBlockPos(this.pos);
        buffer.writeInt(this.cameraLockTicks);
        buffer.writeInt(this.forcePanTicks);
        buffer.writeInt(this.zoomLevel);
    }

    public void handle(IPayloadContext ctx) {

 ctx.enqueueWork(() -> {
            DistUtil.unsafeRunWhenOn(Dist.CLIENT,
                    () -> () -> {
                        OrthoviewClientEvents.forceMoveCam(this.playerName, this.pos, cameraLockTicks, forcePanTicks, zoomLevel);
                    });
        });
    }
}