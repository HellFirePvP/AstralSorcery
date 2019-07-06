/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crafting.helper;

import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CustomRecipeSerializer
 * Created by HellFirePvP
 * Date: 06.07.2019 / 20:38
 */
public abstract class CustomRecipeSerializer<T extends IRecipe<?>> implements IRecipeSerializer<T> {

    private final ResourceLocation name;
    private final Class<IRecipeSerializer<?>> thisClass;

    public CustomRecipeSerializer(ResourceLocation name) {
        this.name = name;
        this.thisClass = (Class<IRecipeSerializer<?>>) this.getClass();
    }

    @Override
    public IRecipeSerializer<T> setRegistryName(ResourceLocation name) {
        return this;
    }

    @Nullable
    @Override
    public ResourceLocation getRegistryName() {
        return this.name;
    }

    @Override
    public Class<IRecipeSerializer<?>> getRegistryType() {
        return this.thisClass;
    }
}
