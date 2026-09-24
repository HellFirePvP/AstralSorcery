/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.network.play;

import hellfirepvp.astralsorcery.common.network.PlayPacketHandler;
import hellfirepvp.astralsorcery.common.perk.data.PerkTree;
import hellfirepvp.astralsorcery.common.perk.tree.AbstractPerk;
import hellfirepvp.astralsorcery.common.perk.tree.perk.socket.GemSocketPerk;
import hellfirepvp.astralsorcery.common.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.research.ResearchManager;
import hellfirepvp.astralsorcery.common.util.codec.CodecUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.LogicalSide;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: PktRequestSocketPerkItem
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class PktRequestSocketPerkItem extends PlayPacketHandler.ToServer<PktRequestSocketPerkItem.Request> {

    static final CustomPacketPayload.Type<Request> TYPE = makeType("request_socket_perk_item");
    static final StreamCodec<RegistryFriendlyByteBuf, Request> CODEC = StreamCodec.composite(
            CodecUtil.enumStreamCodec(Action.class),
            Request::action,
            ResourceLocation.STREAM_CODEC,
            Request::perkKey,
            ByteBufCodecs.INT,
            Request::inventorySlot,
            Request::new
    );

    public static final PktRequestSocketPerkItem HANDLER = new PktRequestSocketPerkItem();

    private PktRequestSocketPerkItem() {
        super(TYPE);
    }

    public static Request insertItem(GemSocketPerk perk, int playerSlotId) {
        return new Request(Action.INSERT_ITEM, perk.getKey(), playerSlotId);
    }

    public static Request dropItem(GemSocketPerk perk) {
        return new Request(Action.REMOVE_ITEM, perk.getKey(), -1);
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, Request> codec() {
        return CODEC;
    }

    @Override
    public void handle(Request payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer sPlayer) {
                PerkTree.getInstance().getPerk(LogicalSide.SERVER, payload.perkKey()).ifPresent(perk -> {
                    if (perk instanceof GemSocketPerk gemSocketPerk) {
                        PlayerProgress progress = ResearchManager.getProgress(sPlayer, LogicalSide.SERVER);
                        switch (payload.action()) {
                            case INSERT_ITEM -> {
                                if (!gemSocketPerk.hasGemStack(progress)) {
                                    ItemStack stack = sPlayer.getInventory().getItem(payload.inventorySlot());
                                    if (!stack.isEmpty()) {
                                        ItemStack toInsert = stack.copyWithCount(1);
                                        if (gemSocketPerk.setGemStack(sPlayer, toInsert)) {
                                            stack.shrink(1);
                                            sPlayer.getInventory().setItem(payload.inventorySlot(), stack);
                                        }
                                    }
                                }
                            }
                            case REMOVE_ITEM -> {
                                if (gemSocketPerk.hasGemStack(progress)) {
                                    gemSocketPerk.dropGemStack(sPlayer);
                                }
                            }
                        }
                    }
                });
            }
        });
    }

    public record Request(Action action, ResourceLocation perkKey, int inventorySlot) implements CustomPacketPayload {

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    public enum Action {

        REMOVE_ITEM,
        INSERT_ITEM

    }
}
