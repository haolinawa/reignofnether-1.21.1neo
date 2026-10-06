package com.solegendary.reignofnether.fogofwar;










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
import com.solegendary.reignofnether.unit.interfaces.RangedAttackerUnit;
import net.minecraft.network.FriendlyByteBuf;
import net.neoforged.api.distmarker.Dist;
import com.solegendary.reignofnether.util.DistUtil;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

public class FogOfWarClientboundPacket implements CustomPacketPayload  {
    public static final Type<FogOfWarClientboundPacket> TYPE = new Type<>(ResourceLocation.parse("reignofnether:fog_of_war_clientbound_packet"));
    public static final StreamCodec<FriendlyByteBuf, FogOfWarClientboundPacket> STREAM_CODEC =
            StreamCodec.of((buf, payload) -> payload.encode(buf), FogOfWarClientboundPacket::new);

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }


    public boolean enable;
    public String playerName;
    public int unitId;

    public static void setEnabled(boolean enable) {
        PacketDistributor.sendToAllPlayers(new FogOfWarClientboundPacket(enable, "", 0));
    }

    public static void revealOrHidePlayer(boolean reveal, String playerName) {
        FogOfWarServerEvents.setPlayerRevealed(playerName, reveal);
    }

    // reveal a ranged unit briefly to the player it's attacking
    public static void revealRangedUnit(String playerBeingAttacked, int unitId) {
        FogOfWarServerEvents.revealRangedUnit(unitId, playerBeingAttacked, RangedAttackerUnit.FOG_REVEAL_TICKS_MAX);
        PacketDistributor.sendToAllPlayers(new FogOfWarClientboundPacket(true, playerBeingAttacked, unitId));
    }

    public FogOfWarClientboundPacket(boolean enable, String playerName, int unitId) {
        this.enable = enable;
        this.playerName = playerName;
        this.unitId = unitId;
    }

    public FogOfWarClientboundPacket(FriendlyByteBuf buffer) {
        this.enable = buffer.readBoolean();
        this.playerName = buffer.readUtf();
        this.unitId = buffer.readInt();
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeBoolean(this.enable);
        buffer.writeUtf(this.playerName);
        buffer.writeInt(this.unitId);
    }

    // server-side packet-consuming functions
    public void handle(IPayloadContext ctx) {

 ctx.enqueueWork(() -> {
            DistUtil.unsafeRunWhenOn(Dist.CLIENT,
                () -> () -> {
                    if (unitId > 0)
                        FogOfWarClientEvents.revealRangedUnit(playerName, unitId);
                    else if (playerName.isEmpty())
                        FogOfWarClientEvents.setEnabled(enable);
                });
        });
    }
}
