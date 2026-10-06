package com.solegendary.reignofnether.tutorial;










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
import net.neoforged.api.distmarker.Dist;
import com.solegendary.reignofnether.util.DistUtil;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

public class TutorialClientboundPacket implements CustomPacketPayload  {
    public static final Type<TutorialClientboundPacket> TYPE = new Type<>(ResourceLocation.parse("reignofnether:tutorial_clientbound_packet"));
    public static final StreamCodec<FriendlyByteBuf, TutorialClientboundPacket> STREAM_CODEC =
            StreamCodec.of((buf, payload) -> payload.encode(buf), TutorialClientboundPacket::new);

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }


    private final TutorialAction action;
    private final TutorialStage stage;

    public static void enableTutorial() {
        PacketDistributor.sendToAllPlayers(new TutorialClientboundPacket(TutorialAction.ENABLE, TutorialStage.INTRO));
    }
    public static void disableTutorial() {
        PacketDistributor.sendToAllPlayers(new TutorialClientboundPacket(TutorialAction.DISABLE, TutorialStage.INTRO));
    }
    public static void loadTutorialStage(TutorialStage stage) {
        PacketDistributor.sendToAllPlayers(new TutorialClientboundPacket(TutorialAction.LOAD_STAGE, stage));
    }

    public TutorialClientboundPacket(TutorialAction action, TutorialStage stage) {
        this.action = action;
        this.stage = stage;
    }

    public TutorialClientboundPacket(FriendlyByteBuf buffer) {
        this.action = buffer.readEnum(TutorialAction.class);
        this.stage = buffer.readEnum(TutorialStage.class);
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeEnum(this.action);
        buffer.writeEnum(this.stage);
    }

    // client-side packet-consuming functions
    public void handle(IPayloadContext ctx) {

 ctx.enqueueWork(() -> {
            DistUtil.unsafeRunWhenOn(Dist.CLIENT,
                () -> () -> {
                    switch (action) {
                        case ENABLE -> TutorialClientEvents.setEnabled(true);
                        case DISABLE -> TutorialClientEvents.setEnabled(false);
                        case LOAD_STAGE -> TutorialClientEvents.loadStage(stage);
                    }
                });
        });
    }
}
