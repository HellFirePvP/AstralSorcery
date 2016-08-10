package hellfirepvp.astralsorcery.common.block;

import hellfirepvp.astralsorcery.common.constellation.Constellation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.awt.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: IBlockRitualComponent
 * Created by HellFirePvP
 * Date: 09.08.2016 / 15:31
 */
public interface IBlockRitualComponent {

    default public boolean isValidComponent(Constellation type, World world, BlockPos at) {
        return true;
    }

    @SideOnly(Side.CLIENT)
    default public Color getStabilizerColor(Constellation type, World world, BlockPos at) {
        return Color.WHITE;
    }

}
