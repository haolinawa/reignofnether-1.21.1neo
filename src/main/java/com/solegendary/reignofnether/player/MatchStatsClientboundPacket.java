package com.solegendary.reignofnether.player;










import net.neoforged.neoforge.network.PacketDistributor;
import com.solegendary.reignofnether.util.DistUtil;
import com.solegendary.reignofnether.ReignOfNether;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.FriendlyByteBuf;
import com.solegendary.reignofnether.faction.Faction;
import com.solegendary.reignofnether.faction.Factions;
import com.solegendary.reignofnether.matchstart.MatchEndClientEvents;
import com.solegendary.reignofnether.registrars.PacketHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.neoforged.api.distmarker.Dist;
import com.solegendary.reignofnether.util.DistUtil;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

// Sent to all clients once a match ends, carrying the final scoreboard so the
// end-of-match stats screen (MatchEndScreen) can be rendered. Scores otherwise
// only exist server-side, so this is the only way the client learns them.
public class MatchStatsClientboundPacket implements CustomPacketPayload  {
    public static final Type<MatchStatsClientboundPacket> TYPE = new Type<>(ResourceLocation.parse("reignofnether:match_stats_clientbound_packet"));
    public static final StreamCodec<FriendlyByteBuf, MatchStatsClientboundPacket> STREAM_CODEC =
            StreamCodec.of((buf, payload) -> payload.encode(buf), MatchStatsClientboundPacket::new);

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }


    // one results-table row per player that took part in the match
    public static class MatchStatRow {
        public final String name;
        public final Faction faction;
        public final boolean winner;
        public final int teamId; // startPosColorId - players sharing it are on the same team
        public final int[] scores; // ordered as RTSPlayerScoresEnum.values()

        public MatchStatRow(String name, Faction faction, boolean winner, int teamId, int[] scores) {
            this.name = name;
            this.faction = faction;
            this.winner = winner;
            this.teamId = teamId;
            this.scores = scores;
        }
    }

    private final long gameDurationTicks;
    private final List<MatchStatRow> rows;

    public static void broadcast(long gameDurationTicks, List<MatchStatRow> rows) {
        PacketDistributor.sendToAllPlayers(new MatchStatsClientboundPacket(gameDurationTicks, rows));
    }

    public MatchStatsClientboundPacket(long gameDurationTicks, List<MatchStatRow> rows) {
        this.gameDurationTicks = gameDurationTicks;
        this.rows = rows;
    }

    public MatchStatsClientboundPacket(FriendlyByteBuf buffer) {
        this.gameDurationTicks = buffer.readLong();
        int n = buffer.readInt();
        this.rows = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            String name = buffer.readUtf();
            Faction faction = Factions.getFaction(buffer.readResourceLocation());
            boolean winner = buffer.readBoolean();
            int teamId = buffer.readVarInt();
            int[] scores = buffer.readVarIntArray();
            this.rows.add(new MatchStatRow(name, faction, winner, teamId, scores));
        }
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeLong(gameDurationTicks);
        buffer.writeInt(rows.size());
        for (MatchStatRow row : rows) {
            buffer.writeUtf(row.name);
            buffer.writeResourceLocation(row.faction.key);
            buffer.writeBoolean(row.winner);
            buffer.writeVarInt(row.teamId);
            buffer.writeVarIntArray(row.scores);
        }
    }

    public void handle(IPayloadContext ctx) {
 ctx.enqueueWork(() -> {
            DistUtil.unsafeRunWhenOn(Dist.CLIENT,
                    () -> () -> {
                        MatchEndClientEvents.receive(gameDurationTicks, rows);
                    });
        });
    }
}
