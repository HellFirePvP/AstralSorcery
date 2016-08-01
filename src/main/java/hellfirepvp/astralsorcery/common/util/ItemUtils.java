package hellfirepvp.astralsorcery.common.util;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemUtils
 * Created by HellFirePvP
 * Date: 31.07.2016 / 17:51
 */
public class ItemUtils {

    private static final Random rand = new Random();

    public static EntityItem dropItem(World world, double x, double y, double z, ItemStack stack) {
        if(world.isRemote) return null;
        EntityItem ei = new EntityItem(world, x, y, z, stack);
        world.spawnEntityInWorld(ei);
        return ei;
    }
    public static EntityItem dropItemNaturally(World world, double x, double y, double z, ItemStack stack) {
        if(world.isRemote) return null;
        EntityItem ei = dropItem(world, x, y, z, stack);
        applyRandomDropOffset(ei);
        return ei;
    }

    private static void applyRandomDropOffset(EntityItem item) {
        item.motionX = rand.nextFloat() * 0.7F - 0.35D;
        item.motionY = rand.nextFloat() * 0.7F - 0.35D;
        item.motionZ = rand.nextFloat() * 0.7F - 0.35D;
    }

}
