/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.network.play.client;

import hellfirepvp.astralsorcery.common.item.gem.ItemPerkGem;
import hellfirepvp.astralsorcery.common.network.base.ASPacket;
import hellfirepvp.astralsorcery.common.perk.AbstractPerk;
import hellfirepvp.astralsorcery.common.perk.DynamicModifierHelper;
import hellfirepvp.astralsorcery.common.perk.PerkTree;
import hellfirepvp.astralsorcery.common.perk.node.GemSlotPerk;
import hellfirepvp.astralsorcery.common.util.data.ByteBufUtils;
import hellfirepvp.astralsorcery.common.util.item.ItemUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.LogicalSide;

import javax.annotation.Nonnull;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PktPerkGemModification
 * Created by HellFirePvP
 * Date: 02.06.2019 / 13:55
 */
public class PktPerkGemModification extends ASPacket<PktPerkGemModification> {

    private int action = 0;

    private ResourceLocation perkKey = null;
    private int slotId = -1;

    public PktPerkGemModification() {}

    public static PktPerkGemModification insertItem(AbstractPerk perk, int slotId) {
        PktPerkGemModification pkt = new PktPerkGemModification();
        pkt.action = 0;
        pkt.perkKey = perk.getRegistryName();
        pkt.slotId = slotId;
        return pkt;
    }

    public static PktPerkGemModification dropItem(AbstractPerk perk) {
        PktPerkGemModification pkt = new PktPerkGemModification();
        pkt.action = 1;
        pkt.perkKey = perk.getRegistryName();
        return pkt;
    }

    @Nonnull
    @Override
    public Encoder<PktPerkGemModification> encoder() {
        return (packet, buffer) -> {
            buffer.writeInt(packet.action);
            ByteBufUtils.writeOptional(buffer, packet.perkKey, ByteBufUtils::writeResourceLocation);
            buffer.writeInt(packet.slotId);
        };
    }

    @Nonnull
    @Override
    public Decoder<PktPerkGemModification> decoder() {
        return buffer -> {
            PktPerkGemModification pkt = new PktPerkGemModification();

            pkt.action = buffer.readInt();
            pkt.perkKey = ByteBufUtils.readOptional(buffer, ByteBufUtils::readResourceLocation);
            pkt.slotId = buffer.readInt();

            return pkt;
        };
    }

    @Nonnull
    @Override
    public Handler<PktPerkGemModification> handler() {
        return (packet, context, side) -> {
            context.enqueueWork(() -> {
                PerkTree.PERK_TREE.getPerk(side, packet.perkKey).ifPresent(perk -> {
                    PlayerEntity player = context.getSender();
                    if (!(perk instanceof GemSlotPerk)) { //Exclusively for socketable gem perks.
                        return;
                    }

                    switch (packet.action) {
                        case 0:
                            ItemStack stack = player.inventory.getStackInSlot(packet.slotId);
                            ItemStack toInsert = ItemUtils.copyStackWithSize(stack, 1);
                            if (!toInsert.isEmpty() &&
                                    toInsert.getItem() instanceof ItemPerkGem &&
                                    !DynamicModifierHelper.getStaticModifiers(toInsert).isEmpty() &&
                                    !((GemSlotPerk) perk).hasItem(player, LogicalSide.SERVER) &&
                                    ((GemSlotPerk) perk).setContainedItem(player, LogicalSide.SERVER, toInsert)) {
                                player.inventory.setInventorySlotContents(packet.slotId, ItemUtils.copyStackWithSize(stack, stack.getCount() - 1));
                            }
                            break;
                        case 1:
                            if (((GemSlotPerk) perk).hasItem(player, LogicalSide.SERVER)) {
                                ((GemSlotPerk) perk).dropItemToPlayer(player);
                            }
                            break;
                        default:
                            break;
                    }
                });
            });
        };
    }
}
