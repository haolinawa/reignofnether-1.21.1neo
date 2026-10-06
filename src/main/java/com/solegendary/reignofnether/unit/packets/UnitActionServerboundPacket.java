package com.solegendary.reignofnether.unit.packets;








import com.solegendary.reignofnether.ReignOfNether;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.FriendlyByteBuf;
import com.solegendary.reignofnether.ReignOfNether;
import com.solegendary.reignofnether.alliance.AlliancesServerEvents;
import com.solegendary.reignofnether.sandbox.SandboxServer;
import com.solegendary.reignofnether.unit.UnitAction;
import com.solegendary.reignofnether.unit.UnitServerEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

public class UnitActionServerboundPacket implements CustomPacketPayload  {
    public static final Type<UnitActionServerboundPacket> TYPE = new Type<>(ResourceLocation.parse("reignofnether:unit_action_serverbound_packet"));
    public static final StreamCodec<FriendlyByteBuf, UnitActionServerboundPacket> STREAM_CODEC =
            StreamCodec.of((buf, payload) -> payload.encode(buf), UnitActionServerboundPacket::new);

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }


    private final String ownerName; // player that is issuing this command
    private final UnitAction action;
    private final int unitId;
    private final int[] unitIds; // units to be controlled
    private final BlockPos preselectedBlockPos;
    private final BlockPos selectedBuildingPos; // for building abilities
    private final boolean shiftQueue; // shift queue actions

    // packet-handler functions
    public UnitActionServerboundPacket(
        String ownerName,
        UnitAction action,
        int unitId,
        int[] unitIds,
        BlockPos preselectedBlockPos,
        BlockPos selectedBuildingPos,
        boolean shiftQueue
    ) {
        this.ownerName = ownerName;
        this.action = action;
        this.unitId = unitId;
        this.unitIds = unitIds;
        this.preselectedBlockPos = preselectedBlockPos;
        this.selectedBuildingPos = selectedBuildingPos;
        this.shiftQueue = shiftQueue;
    }

    public UnitActionServerboundPacket(FriendlyByteBuf buffer) {
        this.ownerName = buffer.readUtf();
        this.action = buffer.readEnum(UnitAction.class);
        this.unitId = buffer.readInt();
        this.unitIds = buffer.readVarIntArray();
        this.preselectedBlockPos = buffer.readBlockPos();
        this.selectedBuildingPos = buffer.readBlockPos();
        this.shiftQueue = buffer.readBoolean();
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeUtf(this.ownerName);
        buffer.writeEnum(this.action);
        buffer.writeInt(this.unitId);
        buffer.writeVarIntArray(this.unitIds);
        buffer.writeBlockPos(this.preselectedBlockPos);
        buffer.writeBlockPos(this.selectedBuildingPos);
        buffer.writeBoolean(this.shiftQueue);
    }

    // server-side packet-consuming functions
    public void handle(IPayloadContext ctx) {
 ctx.enqueueWork(() -> {
            if (this.action == UnitAction.DEBUG1) {
                UnitServerEvents.debug1(this.preselectedBlockPos);
            }
            if (this.action == UnitAction.DEBUG2) {
                UnitServerEvents.debug2();
            }

            ServerPlayer player = (net.minecraft.server.level.ServerPlayer) ctx.player();
            if (player == null) {
                ReignOfNether.LOGGER.warn("Sender for unit action packet was null");
            }
            else if (!player.getName().getString().equals(ownerName) &&
                    !SandboxServer.isSandboxPlayer(ownerName) &&
                    !AlliancesServerEvents.canControlAlly(player.getName().getString(), ownerName)) {
                ReignOfNether.LOGGER.warn("UnitActionServerboundPacket: Tried to process packet from " + player.getName() + " for " + ownerName);
            }
            else {
                //ReignOfNether.LOGGER.info("[UnitAction] {} issued {} (unitIds: {})", player.getName(), this.action, java.util.Arrays.toString(this.unitIds));
                UnitServerEvents.addActionItem(
                        this.ownerName,
                        this.action,
                        this.unitId,
                        this.unitIds,
                        this.preselectedBlockPos,
                        this.selectedBuildingPos,
                        this.shiftQueue
                );
            }
        });
    }
}
