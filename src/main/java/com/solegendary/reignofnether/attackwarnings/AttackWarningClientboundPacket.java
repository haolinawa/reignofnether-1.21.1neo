
package com.solegendary.reignofnether.attackwarnings;









import net.neoforged.neoforge.network.PacketDistributor;
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
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

public class AttackWarningClientboundPacket implements CustomPacketPayload  {
    public static final Type<AttackWarningClientboundPacket> TYPE = new Type<>(ResourceLocation.parse("reignofnether:attack_warning_clientbound_packet"));
    public static final StreamCodec<FriendlyByteBuf, AttackWarningClientboundPacket> STREAM_CODEC =
            StreamCodec.of((buf, payload) -> payload.encode(buf), AttackWarningClientboundPacket::new);

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }


    private final String attackedPlayerName;
    private final BlockPos attackPos;

    public static void sendWarning(String attackedPlayerName, BlockPos attackPos) {
        PacketDistributor.sendToAllPlayers(new AttackWarningClientboundPacket(
                attackedPlayerName,
                attackPos
            ));
    }

    // packet-handler functions
    public AttackWarningClientboundPacket(
        String attackedPlayerName,
        BlockPos attackPos
    ) {
        this.attackedPlayerName = attackedPlayerName;
        this.attackPos = attackPos;
    }

    public AttackWarningClientboundPacket(FriendlyByteBuf buffer) {
        this.attackedPlayerName = buffer.readUtf();
        this.attackPos = buffer.readBlockPos();
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeUtf(this.attackedPlayerName);
        buffer.writeBlockPos(this.attackPos);
    }

    // server-side packet-consuming functions
    public void handle(IPayloadContext ctx) {
 ctx.enqueueWork(() -> {
            AttackWarningClientEvents.checkAndTriggerAttackWarning(attackedPlayerName, attackPos);
        });
    }
}
