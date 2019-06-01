/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
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
 * Date: 30.05.2019 / 18:01
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
    public String getGroup() {
        return group;
    }

    @Override
    public ResourceLocation getId() {
        return this.registryName;
    }

}