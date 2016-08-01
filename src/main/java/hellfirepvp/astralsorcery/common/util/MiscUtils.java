package hellfirepvp.astralsorcery.common.util;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: MiscUtils
 * Created by HellFirePvP
 * Date: 01.08.2016 / 13:38
 */
public class MiscUtils {

    public static <T extends TileEntity> T getTileAt(IBlockAccess world, BlockPos pos, Class<T> tileClass) {
        TileEntity te = world.getTileEntity(pos);
        if(te == null) return null;
        if(tileClass.isInstance(te)) return (T) te;
        return null;
    }

}
