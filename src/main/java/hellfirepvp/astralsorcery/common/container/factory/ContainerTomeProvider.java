/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.container.factory;

import hellfirepvp.astralsorcery.common.container.ContainerTome;
import hellfirepvp.astralsorcery.common.lib.ContainerTypesAS;
import hellfirepvp.astralsorcery.common.util.data.ByteBufUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.IContainerFactory;

import javax.annotation.Nonnull;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ContainerTomeProvider
 * Created by HellFirePvP
 * Date: 10.08.2019 / 09:15
 */
public class ContainerTomeProvider extends CustomContainerProvider<ContainerTome> {

    private ItemStack stackTome;
    private int slotTome;

    public ContainerTomeProvider(ItemStack stackTome, int slotTome) {
        super(ContainerTypesAS.TOME);
        this.stackTome = stackTome;
        this.slotTome = slotTome;
    }

    @Override
    protected void writeExtraData(PacketBuffer buf) {
        ByteBufUtils.writeItemStack(buf, this.stackTome);
        buf.writeInt(this.slotTome);
    }

    @Nonnull
    @Override
    public ContainerTome createMenu(int id, PlayerInventory plInventory, PlayerEntity player) {
        return new ContainerTome(id, plInventory, this.stackTome, this.slotTome);
    }

    private static ContainerTome createFromPacket(int id, PlayerInventory plInventory, PacketBuffer data) {
        ItemStack tome = ByteBufUtils.readItemStack(data);
        int slot = data.readInt();
        return new ContainerTome(id, plInventory, tome, slot);
    }

    public static class Factory implements IContainerFactory<ContainerTome> {

        @Override
        public ContainerTome create(int windowId, PlayerInventory inv, PacketBuffer data) {
            return ContainerTomeProvider.createFromPacket(windowId, inv, data);
        }
    }

}
