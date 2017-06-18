package hellfirepvp.astralsorcery.common.crafting.helper;

import hellfirepvp.astralsorcery.common.crafting.ItemHandle;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AbstractRecipeAccessor
 * Created by HellFirePvP
 * Date: 18.06.2017 / 16:17
 */
public abstract class AbstractRecipeAccessor extends AbstractRecipeData {

    public AbstractRecipeAccessor(@Nonnull ItemStack output) {
        super(output);
    }

    @Nullable
    abstract ItemHandle getExpectedStack(int row, int column);

    @Nullable
    abstract ItemHandle getExpectedStack(ShapedRecipeSlot slot);

}
