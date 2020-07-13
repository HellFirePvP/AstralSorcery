/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.network.play.client;

import hellfirepvp.astralsorcery.client.screen.journal.ScreenJournalPerkTree;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import hellfirepvp.astralsorcery.common.item.useables.ItemPerkSeal;
import hellfirepvp.astralsorcery.common.network.base.ASPacket;
import hellfirepvp.astralsorcery.common.perk.AbstractPerk;
import hellfirepvp.astralsorcery.common.util.data.ByteBufUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent;

import javax.annotation.Nonnull;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PktRequestPerkSealAction
 * Created by HellFirePvP
 * Date: 02.06.2019 / 14:05
 */
public class PktRequestPerkSealAction extends ASPacket<PktRequestPerkSealAction> {

    private AbstractPerk perk;
    private boolean doSealing; //Make/true or break/false the seal

    public PktRequestPerkSealAction() {}

    public PktRequestPerkSealAction(AbstractPerk perk, boolean seal) {
        this.perk = perk;
        this.doSealing = seal;
    }

    @Nonnull
    @Override
    public Encoder<PktRequestPerkSealAction> encoder() {
        return (packet, buffer) -> {
            ByteBufUtils.writeRegistryEntry(buffer, packet.perk);
            buffer.writeBoolean(packet.doSealing);
        };
    }

    @Nonnull
    @Override
    public Decoder<PktRequestPerkSealAction> decoder() {
        return buffer -> {
            PktRequestPerkSealAction pkt = new PktRequestPerkSealAction();

            pkt.perk = ByteBufUtils.readRegistryEntry(buffer);
            pkt.doSealing = buffer.readBoolean();

            return pkt;
        };
    }

    @Nonnull
    @Override
    public Handler<PktRequestPerkSealAction> handler() {
        return new Handler<PktRequestPerkSealAction>() {
            @Override
            @OnlyIn(Dist.CLIENT)
            public void handleClient(PktRequestPerkSealAction packet, NetworkEvent.Context context) {
                Screen current = Minecraft.getInstance().currentScreen;
                if (current instanceof ScreenJournalPerkTree) {
                    if (!packet.doSealing) {
                        Minecraft.getInstance().enqueue(() -> ((ScreenJournalPerkTree) current).playSealBreakAnimation(packet.perk));
                    } else {
                        Minecraft.getInstance().enqueue(() -> ((ScreenJournalPerkTree) current).playSealApplyAnimation(packet.perk));
                    }
                }
            }

            @Override
            public void handle(PktRequestPerkSealAction packet, NetworkEvent.Context context, LogicalSide side) {
                context.enqueueWork(() -> {
                    PlayerEntity player = context.getSender();
                    if (packet.doSealing) {
                        if (ItemPerkSeal.useSeal(player, true) &&
                                ResearchManager.applyPerkSeal(player, packet.perk)) {

                            //Follow-up correction if we can't actually consume the seal item now, but already applied the seal.
                            if (!ItemPerkSeal.useSeal(player, false)) {
                                ResearchManager.breakPerkSeal(player, packet.perk);
                            } else {
                                packet.replyWith(new PktRequestPerkSealAction(packet.perk, true), context);
                            }
                        }
                    } else {
                        if (ResearchManager.breakPerkSeal(player, packet.perk)) {
                            packet.replyWith(new PktRequestPerkSealAction(packet.perk, false), context);
                        }
                    }
                });
            }
        };
    }
}
