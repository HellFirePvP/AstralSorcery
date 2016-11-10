package hellfirepvp.astralsorcery.common.item.block;

import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.util.nbt.NBTHelper;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemBlockRitualPedestal
 * Created by HellFirePvP
 * Date: 01.11.2016 / 14:38
 */
public class ItemBlockRitualPedestal extends ItemBlock {

    public ItemBlockRitualPedestal() {
        super(BlocksAS.ritualPedestal);
    }

    /*public static void setBeaconType(ItemStack stack, boolean isPlayerBeacon) {
        NBTHelper.getData(stack).setBoolean("playerBeacon", isPlayerBeacon);
    }

    public static boolean isPlayerBeacon(ItemStack stack) {
        return NBTHelper.getData(stack).getBoolean("playerBeacon");
    }*/

}
