/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crafting.recipe;

import hellfirepvp.astralsorcery.common.crafting.helper.RecipeCraftingContext;
import hellfirepvp.astralsorcery.common.tile.TileAltar;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.items.IItemHandler;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: SimpleAltarRecipeContext
 * Created by HellFirePvP
 * Date: 12.08.2019 / 19:32
 */
public class SimpleAltarRecipeContext extends RecipeCraftingContext<SimpleAltarRecipe, IItemHandler> {

    private final TileAltar altar;
    private final PlayerEntity crafter;
    private final LogicalSide side;
    private boolean ignoreStarlightRequirement = false;

    public SimpleAltarRecipeContext(PlayerEntity crafter, LogicalSide side, TileAltar altar) {
        this.altar = altar;
        this.crafter = crafter;
        this.side = side;
    }

    public SimpleAltarRecipeContext setIgnoreStarlightRequirement(boolean ignoreStarlightRequirement) {
        this.ignoreStarlightRequirement = ignoreStarlightRequirement;
        return this;
    }

    public LogicalSide getSide() {
        return side;
    }

    public PlayerEntity getCrafter() {
        return crafter;
    }

    public boolean ignoreStarlightRequirement() {
        return ignoreStarlightRequirement;
    }

    public TileAltar getAltar() {
        return altar;
    }

}
