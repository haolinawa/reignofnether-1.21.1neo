package com.solegendary.reignofnether.items;










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
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.neoforged.api.distmarker.Dist;
import com.solegendary.reignofnether.util.DistUtil;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

public class ItemShopClientboundPacket implements CustomPacketPayload  {
    public static final Type<ItemShopClientboundPacket> TYPE = new Type<>(ResourceLocation.parse("reignofnether:item_shop_clientbound_packet"));
    public static final StreamCodec<FriendlyByteBuf, ItemShopClientboundPacket> STREAM_CODEC =
            StreamCodec.of((buf, payload) -> payload.encode(buf), ItemShopClientboundPacket::new);

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }


    private final BlockPos buildingPos;
    private final ArrayList<String> descIds;
    private final ArrayList<Integer> buyCosts;
    private final ArrayList<Integer> maxStocks;
    private final ArrayList<Integer> stocks;
    private final ArrayList<Integer> maxRestockTicks;
    private final ArrayList<Integer> restockTicks;

    public static void syncItemShopStock(BlockPos buildingPos, ArrayList<StockedShopItem> itemsAndStock) {
        PacketDistributor.sendToAllPlayers(new ItemShopClientboundPacket(buildingPos, itemsAndStock)
        );
    }

    public ItemShopClientboundPacket(BlockPos buildingPos, ArrayList<StockedShopItem> itemsAndStock) {
        this.buildingPos = buildingPos;
        this.descIds = new ArrayList<>();
        this.buyCosts = new ArrayList<>();
        this.maxStocks = new ArrayList<>();
        this.stocks = new ArrayList<>();
        this.maxRestockTicks = new ArrayList<>();
        this.restockTicks = new ArrayList<>();

        for (StockedShopItem stock : itemsAndStock) {
            descIds.add(stock.item.descId);
            buyCosts.add(stock.getBuyCost());
            maxStocks.add(stock.maxStock);
            stocks.add(stock.stock);
            maxRestockTicks.add(stock.maxRestockTicks);
            restockTicks.add(stock.getTicksToNextRestock());
        }
    }

    public ItemShopClientboundPacket(FriendlyByteBuf buffer) {
        this.buildingPos = buffer.readBlockPos();
        int size = buffer.readInt();
        this.descIds = new ArrayList<>();
        this.buyCosts = new ArrayList<>();
        this.maxStocks = new ArrayList<>();
        this.stocks = new ArrayList<>();
        this.maxRestockTicks = new ArrayList<>();
        this.restockTicks = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            descIds.add(buffer.readUtf());
            buyCosts.add(buffer.readInt());
            maxStocks.add(buffer.readInt());
            stocks.add(buffer.readInt());
            maxRestockTicks.add(buffer.readInt());
            restockTicks.add(buffer.readInt());
        }
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeBlockPos(buildingPos);
        buffer.writeInt(descIds.size());
        for (int i = 0; i < descIds.size(); i++) {
            buffer.writeUtf(descIds.get(i));
            buffer.writeInt(buyCosts.get(i));
            buffer.writeInt(maxStocks.get(i));
            buffer.writeInt(stocks.get(i));
            buffer.writeInt(maxRestockTicks.get(i));
            buffer.writeInt(restockTicks.get(i));
        }
    }

    // client-side packet-consuming function
    public void handle(IPayloadContext ctx) {

 ctx.enqueueWork(() -> {
            DistUtil.unsafeRunWhenOn(Dist.CLIENT,
                    () -> () -> {
                        ArrayList<StockedShopItem> itemsAndStock = new ArrayList<>();
                        for (int i = 0; i < descIds.size(); i++) {
                            UnitItem unitItem = ItemUtil.getUnitItem(descIds.get(i));
                            if (unitItem == null)
                                continue;
                            itemsAndStock.add(new StockedShopItem(
                                    unitItem,
                                    buyCosts.get(i),
                                    maxStocks.get(i),
                                    stocks.get(i),
                                    maxRestockTicks.get(i),
                                    restockTicks.get(i)
                            ));
                        }
                        ItemClientEvents.setStockedShopItems(buildingPos, itemsAndStock);
                    });
        });
    }
}