/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.network.play;

import hellfirepvp.astralsorcery.client.util.structure.StructurePreviewHelper;
import hellfirepvp.astralsorcery.common.lib.ObserversAS;
import hellfirepvp.astralsorcery.common.network.PlayPacketHandler;
import hellfirepvp.astralsorcery.common.structure.observer.CompoundObserverProviderStructure;
import hellfirepvp.astralsorcery.common.tile.base.TileEntityTick;
import hellfirepvp.astralsorcery.common.util.MiscUtil;
import hellfirepvp.astralsorcery.common.util.data.ObserverRegistryObject;
import hellfirepvp.observerlib.api.ObserverProvider;
import hellfirepvp.observerlib.api.structure.MatchableStructure;
import hellfirepvp.observerlib.common.change.ObserverProviderStructure;
import hellfirepvp.observerlib.common.registry.RegistryProviders;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: PktPlayStructurePreview
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class PktPlayStructurePreview extends PlayPacketHandler.ToClient<PktPlayStructurePreview.Request> {

    static final CustomPacketPayload.Type<PktPlayStructurePreview.Request> TYPE = makeType("structure_preview");
    static final StreamCodec<RegistryFriendlyByteBuf, PktPlayStructurePreview.Request> CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC,
            Request::tilePos,
            ResourceKey.streamCodec(RegistryProviders.REGISTRY_KEY),
            PktPlayStructurePreview.Request::observerKey,
            PktPlayStructurePreview.Request::new);

    public static final PktPlayStructurePreview HANDLER = new PktPlayStructurePreview();

    private PktPlayStructurePreview() {
        super(TYPE);
    }

    public static Request showPreview(BlockPos tilePos, ObserverRegistryObject registryObject) {
        return new Request(tilePos, registryObject.observer().getKey());
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, Request> codec() {
        return CODEC;
    }

    @Override
    public void handle(Request payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            ObserversAS.getByName(payload.observerKey()).ifPresent(obj -> {
                Player clientPlayer = context.player();
                Level level = clientPlayer.level();
                TileEntityTick<?> tile = MiscUtil.getTileAt(level, payload.tilePos(), TileEntityTick.class, true).orElse(null);
                if (tile == null) return;

                MatchableStructure neededStructure;
                if (obj.observer().get() instanceof ObserverProviderStructure structureObserver) {
                    neededStructure = structureObserver.getStructure();
                } else if (obj.observer().get() instanceof CompoundObserverProviderStructure compoundStructureObserver) {
                    neededStructure = compoundStructureObserver.getStructures().stream()
                            .filter(structure -> !structure.matches(level, payload.tilePos()))
                            .findFirst()
                            .orElse(null);
                } else {
                    return;
                }
                if (neededStructure == null) return;

                StructurePreviewHelper.newPreview(level, payload.tilePos(), neededStructure)
                        .persistIf((lvl, playerPos) -> {
                            TileEntityTick<?> atTile = MiscUtil.getTileAt(lvl, payload.tilePos(), TileEntityTick.class, true).orElse(null);
                            if (atTile == null) return false;

                            return atTile.getRequiredObserver() != null &&
                                    atTile.getRequiredObserver().equals(obj) &&
                                    !neededStructure.matches(lvl, payload.tilePos());
                        })
                        .showBar(obj.getObserverName())
                        .createAndDisplay();
            });
        });
    }

    public record Request(BlockPos tilePos, ResourceKey<ObserverProvider<?>> observerKey) implements CustomPacketPayload {

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }
}
