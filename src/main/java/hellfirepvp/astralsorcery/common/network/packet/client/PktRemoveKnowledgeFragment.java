/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.network.packet.client;

import hellfirepvp.astralsorcery.common.network.base.ASPacket;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PktRemoveKnowledgeFragment
 * Created by HellFirePvP
 * Date: 02.06.2019 / 14:03
 */
public class PktRemoveKnowledgeFragment extends ASPacket<PktRemoveKnowledgeFragment> {

    private int invIndex = 0;

    public PktRemoveKnowledgeFragment() {}

    public PktRemoveKnowledgeFragment(int invIndex) {
        this.invIndex = invIndex;
    }

    @Nonnull
    @Override
    public Encoder<PktRemoveKnowledgeFragment> encoder() {
        return (packet, buffer) -> buffer.writeInt(packet.invIndex);
    }

    @Nonnull
    @Override
    public Decoder<PktRemoveKnowledgeFragment> decoder() {
        return buffer -> new PktRemoveKnowledgeFragment(buffer.readInt());
    }

    @Nonnull
    @Override
    public Handler<PktRemoveKnowledgeFragment> handler() {
        return (packet, context, side) -> {
            context.enqueueWork(() -> {
                EntityPlayer player = context.getSender();
                ItemStack inInventory = player.inventory.getStackInSlot(packet.invIndex);
                if (!inInventory.isEmpty() && inInventory.getItem() instanceof ItemKnowledgeFragment) {
                    player.inventory.setInventorySlotContents(packet.invIndex, ItemStack.EMPTY);
                }
            });
        };
    }
}
