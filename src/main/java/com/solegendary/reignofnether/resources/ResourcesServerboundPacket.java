package com.solegendary.reignofnether.resources;









import net.neoforged.neoforge.network.PacketDistributor;
import com.solegendary.reignofnether.ReignOfNether;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.FriendlyByteBuf;
import com.solegendary.reignofnether.ReignOfNether;
import com.solegendary.reignofnether.registrars.PacketHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

public class ResourcesServerboundPacket implements CustomPacketPayload  {
    public static final Type<ResourcesServerboundPacket> TYPE = new Type<>(ResourceLocation.parse("reignofnether:resources_serverbound_packet"));
    public static final StreamCodec<FriendlyByteBuf, ResourcesServerboundPacket> STREAM_CODEC =
            StreamCodec.of((buf, payload) -> payload.encode(buf), ResourcesServerboundPacket::new);

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }


    ResourcesAction action;
    public String senderName;
    public String receiverName;
    public int food;
    public int wood;
    public int ore;
    public int emerald;

    public static void sendResources(Resources resources, String senderName) {
        PacketDistributor.sendToServer(new ResourcesServerboundPacket(
                ResourcesAction.SEND_RESOURCES,
                senderName,
                resources.ownerName,
                resources.food,
                resources.wood,
                resources.ore,
                resources.emerald
        ));
    }

    public ResourcesServerboundPacket(ResourcesAction action, String senderName, String receiverName, int food, int wood, int ore, int emerald) {
        this.action = action;
        this.senderName = senderName;
        this.receiverName = receiverName;
        this.food = food;
        this.wood = wood;
        this.ore = ore;
        this.emerald = emerald;
    }

    public ResourcesServerboundPacket(FriendlyByteBuf buffer) {
        this.action = buffer.readEnum(ResourcesAction.class);
        this.senderName = buffer.readUtf();
        this.receiverName = buffer.readUtf();
        this.food = buffer.readInt();
        this.wood = buffer.readInt();
        this.ore = buffer.readInt();
        this.emerald = buffer.readInt();
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeEnum(this.action);
        buffer.writeUtf(this.senderName);
        buffer.writeUtf(this.receiverName);
        buffer.writeInt(this.food);
        buffer.writeInt(this.wood);
        buffer.writeInt(this.ore);
        buffer.writeInt(this.emerald);
    }

    // server-side packet-consuming functions
    public void handle(IPayloadContext ctx) {
 ctx.enqueueWork(() -> {

            ServerPlayer player = (net.minecraft.server.level.ServerPlayer) ctx.player();
            if (player == null) {
                ReignOfNether.LOGGER.warn("ResourcesServerboundPacket: Sender was null");
                return;
            }
            if (!player.getName().getString().equals(senderName)) {
                ReignOfNether.LOGGER.warn("ResourcesServerboundPacket: Tried to process packet from " + player.getName() + " for: " + senderName);
                return;
            }
            if (action == ResourcesAction.SEND_RESOURCES) {
                ReignOfNether.LOGGER.info("[Resources] {} sent resources to {} (food: {}, wood: {}, ore: {}, emerald: {})",
                        senderName, this.receiverName, this.food, this.wood, this.ore, this.emerald);
                ResourcesServerEvents.trySendingAnyResources(this.receiverName, new Resources(this.senderName, this.food, this.wood, this.ore, this.emerald));
            }
        });
    }
}
