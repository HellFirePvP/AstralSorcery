package hellfirepvp.astralsorcery.common.crafting;

import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerWorkbench;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

import javax.annotation.Nullable;
import java.lang.reflect.Field;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: LightProximityCraftingTableRecipe
 * Created by HellFirePvP
 * Date: 02.08.2016 / 22:57
 */
public class LightProximityCraftingTableRecipe implements IRecipe {

    private static final Field fieldContainer, fieldPos;

    @Override
    public boolean matches(InventoryCrafting inv, World worldIn) {
        Container c;
        try {
            c = (Container) fieldContainer.get(inv);
        } catch (IllegalAccessException e) {
            return false;
        }
        if(!(c instanceof ContainerWorkbench)) return false;
        BlockPos pos;
        try {
            pos = (BlockPos) fieldPos.get(c);
        } catch (IllegalAccessException e) {
            return false;
        }
        return false;
    }

    @Nullable
    @Override
    public ItemStack getCraftingResult(InventoryCrafting inv) {
        return null;
    }

    @Override
    public int getRecipeSize() {
        return 0;
    }

    @Nullable
    @Override
    public ItemStack getRecipeOutput() {
        return null;
    }

    @Override
    public ItemStack[] getRemainingItems(InventoryCrafting inv) {
        return new ItemStack[0];
    }

    static {
        fieldContainer = ReflectionHelper.findField(InventoryCrafting.class, "eventHandler", "field_70465_c");
        fieldContainer.setAccessible(true);
        fieldPos = ReflectionHelper.findField(ContainerWorkbench.class, "pos", "field_178145_h");
        fieldPos.setAccessible(true);
    }

}
