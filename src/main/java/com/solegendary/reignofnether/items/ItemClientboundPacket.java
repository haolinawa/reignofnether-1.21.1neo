package com.solegendary.reignofnether.items;

import com.solegendary.reignofnether.util.MiscUtil;










import net.neoforged.neoforge.network.PacketDistributor;
import com.solegendary.reignofnether.util.DistUtil;
import com.solegendary.reignofnether.ReignOfNether;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.FriendlyByteBuf;
import com.solegendary.reignofnether.ReignOfNether;
import com.solegendary.reignofnether.registrars.PacketHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import com.solegendary.reignofnether.util.DistUtil;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

// syncs the full contents of a unit's inventory to clients
// stacks are serialised via ItemStack.save/of, so tags, enchantments and damage all survive the trip
public class ItemClientboundPacket implements CustomPacketPayload  {
    public static final Type<ItemClientboundPacket> TYPE = new Type<>(ResourceLocation.parse("reignofnether:item_clientbound_packet"));
    public static final StreamCodec<FriendlyByteBuf, ItemClientboundPacket> STREAM_CODEC =
            StreamCodec.of((buf, payload) -> payload.encode(buf), ItemClientboundPacket::new);

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }


    private final int unitId;
    private final List<ItemStack> items;
    private final BlockPos shopPos;

    public static void syncInventory(int unitId, List<ItemStack> items) {
        PacketDistributor.sendToAllPlayers(new ItemClientboundPacket(unitId, items));
    }

    public static void setShopServedUnit(int unitId, BlockPos shopPos) {
        PacketDistributor.sendToAllPlayers(new ItemClientboundPacket(unitId, shopPos));
    }

    public ItemClientboundPacket(int unitId, List<ItemStack> items) {
        this.unitId = unitId;
        this.items = new ArrayList<>(items.size());
        for (ItemStack stack : items) // copy so later server-side mutation can't race the encode
            this.items.add(stack.copy());
        this.shopPos = new BlockPos(0,0,0);
    }

    public ItemClientboundPacket(int unitId, BlockPos shopPos) {
        this.unitId = unitId;
        this.items = List.of();
        this.shopPos = shopPos;
    }

    public ItemClientboundPacket(FriendlyByteBuf buffer) {
        this.unitId = buffer.readInt();
        this.items = buffer.readList(ItemClientboundPacket::readStack);
        this.shopPos = buffer.readBlockPos();
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeInt(this.unitId);
        buffer.writeCollection(this.items, ItemClientboundPacket::writeStack);
        buffer.writeBlockPos(this.shopPos);
    }

    // full-fidelity ItemStack (de)serialisation: item id, count and the entire tag compound
    public static void writeStack(FriendlyByteBuf buffer, ItemStack stack) {
        // 1.21.1: ItemStack#save throws "Cannot encode empty ItemStack" for empty stacks,
        // so write a null tag; readStack() turns that back into ItemStack.EMPTY.
        buffer.writeNbt(stack.isEmpty() ? null : stack.save(net.minecraft.core.RegistryAccess.EMPTY, new CompoundTag()));
    }

    public static ItemStack readStack(FriendlyByteBuf buffer) {
        CompoundTag tag = buffer.readNbt();
        if (tag == null) return ItemStack.EMPTY;
        ItemStack stack = MiscUtil.itemStackFromTag(tag);
        return stack.isEmpty() ? ItemStack.EMPTY : stack; // normalise to the singleton
    }

    // client-side packet-consuming functions
    public void handle(IPayloadContext ctx) {
 ctx.enqueueWork(() -> DistUtil.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            if (this.items == null) {
                ReignOfNether.LOGGER.warn("ItemClientboundPacket: no items for unitId " + this.unitId);
            }
            else if (!this.items.isEmpty()) {
                ItemClientEvents.syncInventory(this.unitId, this.items);
            } else {
                ItemClientEvents.setShopServedUnit(this.unitId, this.shopPos);
            }
        }));
    }
}