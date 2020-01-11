/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.container.factory;

import hellfirepvp.astralsorcery.common.container.ContainerAltarAttunement;
import hellfirepvp.astralsorcery.common.lib.ContainerTypesAS;
import hellfirepvp.astralsorcery.common.tile.TileAltar;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.data.ByteBufUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.IContainerFactory;

import javax.annotation.Nonnull;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ContainerAltarDiscoveryProvider
 * Created by HellFirePvP
 * Date: 15.08.2019 / 16:22
 */
public class ContainerAltarAttunementProvider extends CustomContainerProvider<ContainerAltarAttunement> {

    private final TileAltar ta;

    public ContainerAltarAttunementProvider(TileAltar ta) {
        super(ContainerTypesAS.ALTAR_ATTUNEMENT);
        this.ta = ta;
    }

    @Override
    protected void writeExtraData(PacketBuffer buf) {
        ByteBufUtils.writePos(buf, this.ta.getPos());
    }

    @Nonnull
    @Override
    public ContainerAltarAttunement createMenu(int id, PlayerInventory plInventory, PlayerEntity player) {
        return new ContainerAltarAttunement(ta, plInventory, id);
    }

    private static ContainerAltarAttunement createFromPacket(int id, PlayerInventory plInventory, PacketBuffer data) {
        BlockPos at = ByteBufUtils.readPos(data);
        PlayerEntity player = plInventory.player;
        TileAltar ta = MiscUtils.getTileAt(player.getEntityWorld(), at, TileAltar.class, true);
        return new ContainerAltarAttunement(ta, plInventory, id);
    }

    public static class Factory implements IContainerFactory<ContainerAltarAttunement> {

        @Override
        public ContainerAltarAttunement create(int windowId, PlayerInventory inv, PacketBuffer data) {
            return ContainerAltarAttunementProvider.createFromPacket(windowId, inv, data);
        }
    }
}
