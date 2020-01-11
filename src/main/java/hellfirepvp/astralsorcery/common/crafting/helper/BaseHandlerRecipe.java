/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crafting.helper;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.items.IItemHandler;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BaseHandlerRecipe
 * Created by HellFirePvP
 * Date: 30.06.2019 / 23:42
 */
public abstract class BaseHandlerRecipe<I extends IItemHandler> implements IHandlerRecipe<I> {

    private final ResourceLocation recipeId;
    private String group = "";

    protected BaseHandlerRecipe(ResourceLocation recipeId) {
        this.recipeId = recipeId;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    @Override
    public String getGroup() {
        return group;
    }

    @Override
    public final ResourceLocation getId() {
        return this.recipeId;
    }
}
