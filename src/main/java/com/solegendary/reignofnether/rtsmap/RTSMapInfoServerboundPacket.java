package com.solegendary.reignofnether.rtsmap;









import net.neoforged.neoforge.network.PacketDistributor;
import com.solegendary.reignofnether.ReignOfNether;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.FriendlyByteBuf;
import com.solegendary.reignofnether.ReignOfNether;
import com.solegendary.reignofnether.alliance.AlliancesServerEvents;
import com.solegendary.reignofnether.registrars.PacketHandler;
import com.solegendary.reignofnether.sounds.SoundAction;
import com.solegendary.reignofnether.sounds.SoundClientboundPacket;
import com.solegendary.reignofnether.startpos.StartPosServerEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

public class RTSMapInfoServerboundPacket implements CustomPacketPayload  {
    public static final Type<RTSMapInfoServerboundPacket> TYPE = new Type<>(ResourceLocation.parse("reignofnether:r_t_s_map_info_serverbound_packet"));
    public static final StreamCodec<FriendlyByteBuf, RTSMapInfoServerboundPacket> STREAM_CODEC =
            StreamCodec.of((buf, payload) -> payload.encode(buf), RTSMapInfoServerboundPacket::new);

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }

    private final String mode;

    public static void setStartingMode(String mode) {
        PacketDistributor.sendToServer(new RTSMapInfoServerboundPacket(mode));
    }

    public RTSMapInfoServerboundPacket(String mode) {
        this.mode = mode;
    }

    public RTSMapInfoServerboundPacket(FriendlyByteBuf buffer) {
        this.mode = buffer.readUtf();
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeUtf(this.mode);
    }

    public void handle(IPayloadContext ctx) {
 ctx.enqueueWork(() -> {

            ServerPlayer player = (net.minecraft.server.level.ServerPlayer) ctx.player();
            if (player == null) {
                ReignOfNether.LOGGER.warn("RTSMapInfoServerboundPacket: Sender was null");
                return;
            } else if (!player.hasPermissions(2)) {
                ReignOfNether.LOGGER.warn("RTSMapInfoServerboundPacket: Tried to process packet from " + player.getName() + " with insufficient permissions");
                return;
            }
            if (RTSMapInfoServerEvents.rtsMapInfo != null &&
                RTSMapInfoServerEvents.rtsMapInfo.supportsMode(mode) &&
                !StartPosServerEvents.isStartingGame()) {
                RTSMapInfoServerEvents.rtsMapInfo.setDefaultMode(mode);
                RTSMapInfoClientboundPacket.sendValue(RTSMapInfoAction.SET_MODE, mode);
                StartPosServerEvents.loadPositionsFromMapInfo();
            }
        });
    }
}
