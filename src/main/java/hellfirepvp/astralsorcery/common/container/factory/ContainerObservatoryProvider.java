/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.container.factory;

import hellfirepvp.astralsorcery.common.container.ContainerObservatory;
import hellfirepvp.astralsorcery.common.lib.ContainerTypesAS;
import hellfirepvp.astralsorcery.common.tile.TileObservatory;
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
 * Class: ContainerObservatoryProvider
 * Created by HellFirePvP
 * Date: 16.02.2020 / 10:00
 */
public class ContainerObservatoryProvider extends CustomContainerProvider<ContainerObservatory> {

    private final TileObservatory observatory;

    public ContainerObservatoryProvider(TileObservatory observatory) {
        super(ContainerTypesAS.OBSERVATORY);
        this.observatory = observatory;
    }

    @Override
    protected void writeExtraData(PacketBuffer buf) {
        ByteBufUtils.writePos(buf, this.observatory.getPos());
    }

    @Nonnull
    @Override
    public ContainerObservatory createMenu(int windowId, PlayerInventory plInventory, PlayerEntity player) {
        return new ContainerObservatory(this.observatory, windowId);
    }

    private static ContainerObservatory createFromPacket(int windowId, PlayerInventory plInventory, PacketBuffer data) {
        BlockPos at = ByteBufUtils.readPos(data);
        PlayerEntity player = plInventory.player;
        TileObservatory observatory = MiscUtils.getTileAt(player.getEntityWorld(), at, TileObservatory.class, true);
        return new ContainerObservatory(observatory, windowId);
    }

    public static class Factory implements IContainerFactory<ContainerObservatory> {

        @Override
        public ContainerObservatory create(int windowId, PlayerInventory inv, PacketBuffer data) {
            return ContainerObservatoryProvider.createFromPacket(windowId, inv, data);
        }
    }
}
