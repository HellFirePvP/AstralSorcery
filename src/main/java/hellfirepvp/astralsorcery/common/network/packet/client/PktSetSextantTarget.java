/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.network.packet.client;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import hellfirepvp.astralsorcery.common.network.base.ASPacket;
import hellfirepvp.astralsorcery.common.util.data.ByteBufUtils;
import hellfirepvp.astralsorcery.common.util.sextant.TargetObject;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;

import javax.annotation.Nonnull;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PktSetSextantTarget
 * Created by HellFirePvP
 * Date: 02.06.2019 / 15:04
 */
public class PktSetSextantTarget extends ASPacket<PktSetSextantTarget> {

    private TargetObject target;
    private EnumHand hand;

    public PktSetSextantTarget() {}

    public PktSetSextantTarget(TargetObject target, EnumHand hand) {
        this.target = target;
        this.hand = hand;
    }

    @Nonnull
    @Override
    public Encoder<PktSetSextantTarget> encoder() {
        return (packet, buffer) -> {
            ByteBufUtils.writeRegistryEntry(buffer, packet.target);
            ByteBufUtils.writeEnumValue(buffer, packet.hand);
        };
    }

    @Nonnull
    @Override
    public Decoder<PktSetSextantTarget> decoder() {
        return buffer -> {
            PktSetSextantTarget pkt = new PktSetSextantTarget();

            pkt.target = ByteBufUtils.readRegistryEntry(buffer);
            pkt.hand = ByteBufUtils.readEnumValue(buffer, EnumHand.class);

            return pkt;
        };
    }

    @Nonnull
    @Override
    public Handler<PktSetSextantTarget> handler() {
        return (packet, context, side) -> {
            context.enqueueWork(() -> {
                EntityPlayer player = context.getSender();
                ItemStack held = player.getHeldItem(packet.hand);
                if(held.isEmpty() || !(held.getItem() instanceof ItemSextant)) {
                    return;
                }
                Thread tr = new Thread(() -> {
                    //May be null; In that case, tell that to the client as well so it won't ask the server any longer.
                    BlockPos result = target.searchFor((WorldServer) player.world, player.getPosition());
                    if (result != null) {
                        context.enqueueWork(() -> {
                            if (ResearchManager.useSextantTarget(target, player)) {
                                ItemSextant.setTarget(held, target);
                                ItemSextant.setCurrentTargetInformation(held, result, player.world.getDimension().getType());
                            } else {
                                AstralSorcery.log.warn("Could not set used sextant target for player " + player.getDisplayName() + " - missing progress!");
                            }
                        });
                    }
                });
                tr.setName("SextantTargetFinder-Applying ThreadId=" + tr.getId());
                tr.start();
            });
        };
    }
}
