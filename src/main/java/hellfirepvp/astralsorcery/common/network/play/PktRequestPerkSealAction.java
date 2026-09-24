/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.network.play;

import hellfirepvp.astralsorcery.client.screen.tome.TomePerkTreeScreen;
import hellfirepvp.astralsorcery.common.item.PerkSealItem;
import hellfirepvp.astralsorcery.common.network.PlayPacketHandler;
import hellfirepvp.astralsorcery.common.perk.data.PerkTree;
import hellfirepvp.astralsorcery.common.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.research.ResearchHelper;
import hellfirepvp.astralsorcery.common.research.ResearchManager;
import hellfirepvp.astralsorcery.common.research.perk.PerkAllocation;
import hellfirepvp.astralsorcery.common.util.codec.CodecUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.fml.LogicalSide;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: PktRequestPerkSealAction
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class PktRequestPerkSealAction extends PlayPacketHandler.BiDirectional<PktRequestPerkSealAction.Request> {

    static final CustomPacketPayload.Type<Request> TYPE = makeType("request_perk_seal_action");
    static final StreamCodec<RegistryFriendlyByteBuf, Request> CODEC = StreamCodec.composite(
            CodecUtil.enumStreamCodec(Action.class),
            Request::action,
            ResourceLocation.STREAM_CODEC,
            Request::perkKey,
            Request::new
    );

    public static final PktRequestPerkSealAction HANDLER = new PktRequestPerkSealAction();

    private PktRequestPerkSealAction() {
        super(TYPE);
    }

    public static Request unsealPerk(ResourceLocation perkKey) {
        return new Request(Action.UNSEAL, perkKey);
    }

    public static Request sealPerk(ResourceLocation perkKey) {
        return new Request(Action.SEAL, perkKey);
    }

    @Override
    public void handleClient(Request payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            PerkTree.getInstance().getPerk(LogicalSide.CLIENT, payload.perkKey()).ifPresent(perk -> {
                if (Minecraft.getInstance().screen instanceof TomePerkTreeScreen perkTreeScreen) {
                    switch (payload.action()) {
                        case SEAL -> perkTreeScreen.playSealApplyAnimation(perk);
                        case UNSEAL -> perkTreeScreen.playSealBreakAnimation(perk);
                    }
                }
            });
        });
    }

    @Override
    public void handleServer(Request payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer sPlayer) {
                PerkTree.getInstance().getPerk(LogicalSide.SERVER, payload.perkKey()).ifPresent(perk -> {
                    switch (payload.action()) {
                        case SEAL -> {
                            if (PerkSealItem.consumePerkSeal(sPlayer, true) && ResearchHelper.applyPerkSeal(sPlayer, perk)) {

                                if (PerkSealItem.consumePerkSeal(sPlayer, false)) {
                                    context.reply(sealPerk(payload.perkKey()));
                                } else {
                                    // Rollback if seal item is somehow gone
                                    ResearchHelper.breakPerkSeal(sPlayer, perk);
                                }
                            }
                        }
                        case UNSEAL -> {
                            if (ResearchHelper.breakPerkSeal(sPlayer, perk)) {
                                context.reply(unsealPerk(payload.perkKey()));
                            }
                        }
                    }
                });
            }
        });
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, Request> codec() {
        return CODEC;
    }

    public static record Request(Action action, ResourceLocation perkKey) implements CustomPacketPayload {

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    public static enum Action {

        SEAL,
        UNSEAL;

    }

}
