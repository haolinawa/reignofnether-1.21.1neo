package com.solegendary.reignofnether.hud.custombutton;


import net.neoforged.neoforge.network.PacketDistributor;
import com.solegendary.reignofnether.registrars.PacketHandler;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;

public class CustomButtonActionServerboundPacket implements CustomPacketPayload {

	public static final CustomPacketPayload.Type<CustomButtonActionServerboundPacket> TYPE =
		new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(com.solegendary.reignofnether.ReignOfNether.MOD_ID, "custom_button_action_serverbound_packet"));
	public static final StreamCodec<FriendlyByteBuf, CustomButtonActionServerboundPacket> STREAM_CODEC =
		StreamCodec.of((buf, payload) -> payload.encode(buf), CustomButtonActionServerboundPacket::decode);

	@Override
	public CustomPacketPayload.Type<? extends CustomPacketPayload> type() { return TYPE; }
	
	public ResourceLocation button;
	public boolean isLeft;
	
	public CustomButtonActionServerboundPacket(ResourceLocation button, boolean isLeft) {
		this.button = button;
		this.isLeft = isLeft;
	}
	
	public static CustomButtonActionServerboundPacket decode(FriendlyByteBuf buf) {
		return new CustomButtonActionServerboundPacket(buf.readResourceLocation(), buf.readBoolean());
	}
	
	public static void runLeftClickCommand(ResourceLocation button) {
		PacketDistributor.sendToServer(new CustomButtonActionServerboundPacket(button, true));
	}
	
	public static void runRightClickCommand(ResourceLocation button) {
		PacketDistributor.sendToServer(new CustomButtonActionServerboundPacket(button, false));
	}
	
	public void encode(FriendlyByteBuf buf) {
		buf.writeResourceLocation(button);
		buf.writeBoolean(isLeft);
	}
	
	public void handle(net.neoforged.neoforge.network.handling.IPayloadContext ctx) {
		ctx.enqueueWork(() -> {
			ServerPlayer player = (ServerPlayer) ctx.player();
			if (player == null)
				return;
			
			CustomButton button = CustomButtonServerEvents.customButtons.get(this.button);
			if (button == null)
				return;
			List<CustomButtonActions.CustomButtonAction> actions = button.leftClickActions;
			if (actions == null || actions.isEmpty())
				return;
			for (CustomButtonActions.CustomButtonAction action : actions) {
				action.execute(player);
			}
			
		});
	}
}