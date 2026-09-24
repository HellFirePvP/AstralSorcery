/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.network.play;

import hellfirepvp.astralsorcery.common.network.PlayPacketHandler;
import hellfirepvp.astralsorcery.common.perk.PerkApplicationManager;
import hellfirepvp.astralsorcery.common.perk.PerkManager;
import hellfirepvp.astralsorcery.common.perk.source.ModifierSource;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.neoforged.fml.LogicalSide;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.Optional;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: PktSyncModifierSource
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class PktSyncModifierSource extends PlayPacketHandler.ToClient<PktSyncModifierSource.Request> {

    static final CustomPacketPayload.Type<PktSyncModifierSource.Request> TYPE = makeType("sync_modifier_source");
    static final StreamCodec<RegistryFriendlyByteBuf, PktSyncModifierSource.Request> CODEC = StreamCodec.composite(
            NeoForgeStreamCodecs.enumCodec(ActionType.class),
            Request::action,
            ByteBufCodecs.optional(ModifierSource.STREAM_CODEC),
            Request::existingSource,
            ByteBufCodecs.optional(ModifierSource.STREAM_CODEC),
            Request::newSource,
            Request::new
    );

    public static final PktSyncModifierSource HANDLER = new PktSyncModifierSource();

    private PktSyncModifierSource() {
        super(TYPE);
    }

    public static Request add(ModifierSource source) {
        return new Request(ActionType.ADD, Optional.empty(), Optional.of(source));
    }

    public static Request remove(ModifierSource source) {
        return new Request(ActionType.REMOVE, Optional.of(source), Optional.empty());
    }

    public static Request update(ModifierSource existing, ModifierSource newSource) {
        return new Request(ActionType.UPDATE, Optional.of(existing), Optional.of(newSource));
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, Request> codec() {
        return CODEC;
    }

    @Override
    public void handle(Request payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();

            switch (payload.action()) {
                case ADD -> {
                    payload.newSource().ifPresent(newSource -> {
                        PerkApplicationManager.modifySource(player, LogicalSide.CLIENT, newSource, PerkManager.Action.ADD);
                    });
                }
                case REMOVE -> {
                    payload.existingSource().ifPresent(existingSource -> {
                        PerkApplicationManager.modifySource(player, LogicalSide.CLIENT, existingSource, PerkManager.Action.REMOVE);
                    });
                }
                case UPDATE -> {
                    if (payload.existingSource().isPresent() && payload.newSource().isPresent()) {
                        PerkApplicationManager.updateSource(player, LogicalSide.CLIENT, payload.existingSource().get(), payload.newSource().get());
                    }
                }
            }
        });
    }

    public static record Request(ActionType action, Optional<ModifierSource> existingSource, Optional<ModifierSource> newSource) implements CustomPacketPayload {

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    private enum ActionType {

        ADD,
        REMOVE,
        UPDATE

    }
}
