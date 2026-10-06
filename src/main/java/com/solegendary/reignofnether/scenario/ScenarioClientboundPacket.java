package com.solegendary.reignofnether.scenario;









import com.solegendary.reignofnether.util.DistUtil;
import com.solegendary.reignofnether.ReignOfNether;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.neoforged.api.distmarker.Dist;
import com.solegendary.reignofnether.util.DistUtil;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

public class ScenarioClientboundPacket implements CustomPacketPayload  {
    public static final Type<ScenarioClientboundPacket> TYPE = new Type<>(ResourceLocation.parse("reignofnether:scenario_clientbound_packet"));
    public static final StreamCodec<FriendlyByteBuf, ScenarioClientboundPacket> STREAM_CODEC =
            StreamCodec.of((buf, payload) -> payload.encode(buf), ScenarioClientboundPacket::new);

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }


    public ScenarioAction action;
    public CompoundTag roleNbt;

    public ScenarioClientboundPacket(ScenarioAction action, CompoundTag roleNbt) {
        this.action = action;
        this.roleNbt = roleNbt;
    }

    public ScenarioClientboundPacket(FriendlyByteBuf buffer) {
        this.action = buffer.readEnum(ScenarioAction.class);
        this.roleNbt = buffer.readNbt();
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeEnum(this.action);
        buffer.writeNbt(this.roleNbt);
    }

    // server-side packet-consuming functions
    public void handle(IPayloadContext ctx) {
 ctx.enqueueWork(() -> {
            DistUtil.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {

                switch (this.action) {
                    case LOAD_SCENARIO_ROLE -> {
                        int index = roleNbt.getInt("index");
                        for (int i = 0; i < ScenarioClientEvents.scenarioRoles.size(); i++) {
                            if (ScenarioClientEvents.scenarioRoles.get(i).index == index) {
                                ScenarioClientEvents.scenarioRoles.get(i).nbt = roleNbt;
                                ScenarioClientEvents.scenarioRoles.get(i).unpackNbt();
                                break;
                            }
                        }
                    }
                }
            });
        });
    }
}
