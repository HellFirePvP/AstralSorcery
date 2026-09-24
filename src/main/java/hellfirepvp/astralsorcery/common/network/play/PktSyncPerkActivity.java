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
import hellfirepvp.astralsorcery.common.perk.tree.AbstractPerk;
import hellfirepvp.astralsorcery.common.perk.tree.PerkDataType;
import hellfirepvp.astralsorcery.common.util.MiscUtil;
import hellfirepvp.astralsorcery.common.util.codec.CodecUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.LogicalSide;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.List;
import java.util.Optional;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: PktSyncPerkActivity
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class PktSyncPerkActivity extends PlayPacketHandler.ToClient<PktSyncPerkActivity.Request> {

    static final CustomPacketPayload.Type<PktSyncPerkActivity.Request> TYPE = makeType("sync_perk_activity");
    static final StreamCodec<RegistryFriendlyByteBuf, PktSyncPerkActivity.Request> CODEC = StreamCodec.composite(
            CodecUtil.enumStreamCodec(Type.class),
            Request::actionType,
            ByteBufCodecs.optional(AbstractPerk.TO_CLIENT_PERKTREE_CODEC),
            Request::perk,
            ByteBufCodecs.optional(PerkDataType.DATA_SYNC_CODEC),
            Request::oldData,
            ByteBufCodecs.optional(PerkDataType.DATA_SYNC_CODEC),
            Request::newData,
            AbstractPerk.TO_CLIENT_PERKTREE_CODEC.apply(ByteBufCodecs.list()),
            Request::perks,
            Request::new
    );

    public static final PktSyncPerkActivity HANDLER = new PktSyncPerkActivity();

    private PktSyncPerkActivity() {
        super(TYPE);
    }

    public static Request applyAll() {
        return new Request(Type.APPLY_ALL, Optional.empty(), Optional.empty(), Optional.empty(), List.of());
    }

    public static Request removePerks(List<AbstractPerk<?>> perks) {
        return new Request(Type.REMOVE_LISTED, Optional.empty(), Optional.empty(), Optional.empty(), List.copyOf(perks));
    }

    public static Request changeData(AbstractPerk<?> perk, AbstractPerk.Data oldData, AbstractPerk.Data newData) {
        return new Request(Type.DATA_CHANGE, Optional.of(perk), Optional.of(oldData), Optional.of(newData), List.of());
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, Request> codec() {
        return CODEC;
    }

    @Override
    public void handle(Request payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            switch (payload.actionType()) {
                case REMOVE_LISTED -> {
                    PerkApplicationManager.modifySources(context.player(), LogicalSide.CLIENT, payload.perks(), PerkManager.Action.REMOVE);
                }
                case APPLY_ALL -> {
                    PerkManager.clientRefreshAllPerks();
                }
                case DATA_CHANGE -> {
                    payload.perk.ifPresent(perk -> {
                        AbstractPerk.Data oldData = payload.oldData.orElseThrow();
                        AbstractPerk.Data newData = payload.newData.orElseThrow();
                        PerkManager.clientChangePerkData(perk, MiscUtil.cast(oldData), MiscUtil.cast(newData));
                    });
                }
            }
        });
    }

    public static record Request(PktSyncPerkActivity.Type actionType,
                                 Optional<AbstractPerk<?>> perk,
                                 Optional<AbstractPerk.Data> oldData,
                                 Optional<AbstractPerk.Data> newData,
                                 List<AbstractPerk<?>> perks) implements CustomPacketPayload {

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    public enum Type {

        REMOVE_LISTED,
        APPLY_ALL,
        DATA_CHANGE

    }
}
