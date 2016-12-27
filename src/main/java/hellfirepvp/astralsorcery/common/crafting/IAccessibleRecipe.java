package hellfirepvp.astralsorcery.common.crafting;

import hellfirepvp.astralsorcery.common.crafting.helper.ShapedRecipeSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: IAccessibleRecipe
 * Created by HellFirePvP
 * Date: 06.10.2016 / 14:18
 */
public interface IAccessibleRecipe extends IRecipe {

    @Nullable
    @SideOnly(Side.CLIENT)
    public List<ItemStack> getExpectedStack(int row, int column);

    @Nullable
    public ItemHandle getExpectedStackHandle(int row, int column);

    @Nullable
    @SideOnly(Side.CLIENT)
    public List<ItemStack> getExpectedStack(ShapedRecipeSlot slot);

    @Nullable
    public ItemHandle getExpectedStackHandle(ShapedRecipeSlot slot);

}
