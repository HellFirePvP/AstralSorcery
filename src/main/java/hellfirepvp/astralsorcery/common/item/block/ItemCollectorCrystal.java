package hellfirepvp.astralsorcery.common.item.block;

import hellfirepvp.astralsorcery.common.constellation.Constellation;
import hellfirepvp.astralsorcery.common.constellation.ConstellationRegistry;
import hellfirepvp.astralsorcery.common.item.base.ItemHighlighted;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.util.ItemNBTHelper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemCollectorCrystal
 * Created by HellFirePvP
 * Date: 01.08.2016 / 13:10
 */
public class ItemCollectorCrystal extends ItemBlockCustomName implements ItemHighlighted {

    public ItemCollectorCrystal() {
        super(BlocksAS.collectorCrystal);
        setMaxStackSize(1);
    }

    public static void setConstellation(ItemStack stack, Constellation constellation) {
        constellation.writeToNBT(ItemNBTHelper.getPersistentData(stack));
    }

    public static Constellation getConstellation(ItemStack stack) {
        return Constellation.readFromNBT(ItemNBTHelper.getPersistentData(stack));
    }

}
