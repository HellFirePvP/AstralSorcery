/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crafting.helper;

import hellfirepvp.astralsorcery.AstralSorcery;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BasePlainRecipe
 * Created by HellFirePvP
 * Date: 18.06.2017 / 12:17
 */
public abstract class BasePlainRecipe implements IRecipe {

    private ResourceLocation registryName;
    private String group = "";

    protected BasePlainRecipe(@Nonnull String recipeName) {
        this(new ResourceLocation(AstralSorcery.MODID, recipeName));
    }

    protected BasePlainRecipe(@Nullable ResourceLocation registryName) {
        this.registryName = registryName;
    }

    public final void setGroup(String group) {
        this.group = group == null ? "" : group;
    }

    @Override
    public IRecipe setRegistryName(ResourceLocation name) {
        this.registryName = name;
        return this;
    }

    @Nullable
    @Override
    public ResourceLocation getRegistryName() {
        return this.registryName;
    }

    @Override
    public Class<IRecipe> getRegistryType() {
        return IRecipe.class;
    }

    @Override
    public String getGroup() {
        return group;
    }

}
