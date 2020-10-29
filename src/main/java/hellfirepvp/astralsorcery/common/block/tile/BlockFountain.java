package hellfirepvp.astralsorcery.common.block.tile;

import hellfirepvp.astralsorcery.common.block.base.CustomItemBlock;
import hellfirepvp.astralsorcery.common.block.properties.PropertiesWood;
import hellfirepvp.astralsorcery.common.tile.TileFountain;
import net.minecraft.block.ContainerBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockFountain
 * Created by HellFirePvP
 * Date: 29.10.2020 / 19:54
 */
public class BlockFountain extends ContainerBlock implements CustomItemBlock {

    public BlockFountain() {
        super(PropertiesWood.defaultInfusedWood());
    }

    @Override
    @Nullable
    public TileEntity createNewTileEntity(IBlockReader world) {
        return new TileFountain();
    }
}
