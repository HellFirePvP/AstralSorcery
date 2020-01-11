/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crafting.recipe;

import hellfirepvp.astralsorcery.common.crafting.helper.RecipeCraftingContext;
import hellfirepvp.astralsorcery.common.tile.TileInfuser;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.items.IItemHandler;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: LiquidInfusionContext
 * Created by HellFirePvP
 * Date: 26.07.2019 / 21:29
 */
public class LiquidInfusionContext extends RecipeCraftingContext<LiquidInfusion, IItemHandler> {

    private final TileInfuser infuser;
    private final PlayerEntity crafter;
    private final LogicalSide side;

    public LiquidInfusionContext(TileInfuser infuser, PlayerEntity crafter, LogicalSide side) {
        this.infuser = infuser;
        this.crafter = crafter;
        this.side = side;
    }

    public TileInfuser getInfuser() {
        return infuser;
    }

    public PlayerEntity getCrafter() {
        return crafter;
    }

    public LogicalSide getSide() {
        return side;
    }
}
