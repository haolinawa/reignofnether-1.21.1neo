package com.solegendary.reignofnether.config;









import com.solegendary.reignofnether.util.DistUtil;
import com.solegendary.reignofnether.ReignOfNether;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.FriendlyByteBuf;
import com.solegendary.reignofnether.resources.ResourceCost;
import net.minecraft.network.FriendlyByteBuf;
import net.neoforged.api.distmarker.Dist;
import com.solegendary.reignofnether.util.DistUtil;

import java.util.concurrent.atomic.AtomicBoolean;

/*
    Clientbound packet to synchronize serverside config options with the client
    so that the GUI and other elements can properly reflect the values present on the server.
 */
public class ClientboundSyncResourceCostPacket implements CustomPacketPayload  {
    public static final Type<ClientboundSyncResourceCostPacket> TYPE = new Type<>(ResourceLocation.parse("reignofnether:clientbound_sync_resource_cost_packet"));
    public static final StreamCodec<FriendlyByteBuf, ClientboundSyncResourceCostPacket> STREAM_CODEC =
            StreamCodec.of((buf, payload) -> payload.encode(buf), ClientboundSyncResourceCostPacket::new);

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }

    private final int food;
    private final int wood;
    private final int ore;
    private final int ticks;
    private final int population;
    private final String id;

    public ClientboundSyncResourceCostPacket(ResourceCost entry) {
        this.food = entry.food;
        this.wood = entry.wood;
        this.ore = entry.ore;
        this.ticks = entry.ticks;
        this.population = entry.population;
        this.id = entry.id;
    }
    public ClientboundSyncResourceCostPacket(FriendlyByteBuf buf) {
        this.food = buf.readInt();
        this.wood = buf.readInt();
        this.ore = buf.readInt();
        this.ticks = buf.readInt();
        this.population = buf.readInt();
        this.id = buf.readUtf();
    }
    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(this.getFood());
        buf.writeInt(this.getWood());
        buf.writeInt(this.getOre());
        buf.writeInt(this.getTicks());
        buf.writeInt(this.getPopulation());
        buf.writeUtf(this.getId());
    }
    public static ClientboundSyncResourceCostPacket decode(FriendlyByteBuf buf) {
        return new ClientboundSyncResourceCostPacket(buf);
    }

    public void handle(net.neoforged.neoforge.network.handling.IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            DistUtil.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ConfigClientEvents.loadConfigData(this));
        });
    }

    public int getFood() {
        return food;
    }

    public int getWood() {
        return wood;
    }

    public int getOre() {
        return ore;
    }

    public int getTicks() {
        return ticks;
    }

    public int getPopulation() {
        return population;
    }

    public String getId() {
        return id;
    }
}
