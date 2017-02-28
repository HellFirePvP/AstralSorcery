/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.integrations.mods.crafttweaker.network;

import hellfirepvp.astralsorcery.common.crafting.ItemHandle;
import hellfirepvp.astralsorcery.common.crafting.helper.CraftingAccessManager;
import hellfirepvp.astralsorcery.common.tile.TileAltar;
import hellfirepvp.astralsorcery.common.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AltarRecipeDiscovery
 * Created by HellFirePvP
 * Date: 27.02.2017 / 15:15
 */
public class AltarRecipeDiscovery extends BaseAltarRecipe {

    AltarRecipeDiscovery() {
        super(null, null, 0, 0);
    }

    public AltarRecipeDiscovery(ItemHandle[] inputs, ItemStack output, int starlightRequired, int craftingTickTime) {
        super(inputs, output, starlightRequired, craftingTickTime);
    }

    @Override
    public CraftingType getType() {
        return CraftingType.ALTAR_T1_ADD;
    }

    @Override
    public void applyServer() {
        CraftingAccessManager.registerMTAltarRecipe(
                buildRecipeUnsafe(
                        TileAltar.AltarLevel.DISCOVERY,
                        this.starlightRequired,
                        this.craftingTickTime,
                        this.output,
                        this.inputs)
        );
    }

    @Override
    public void applyClient() {
        CraftingAccessManager.registerMTAltarRecipe(
                buildRecipeUnsafe(
                        TileAltar.AltarLevel.DISCOVERY,
                        this.starlightRequired,
                        this.craftingTickTime,
                        this.output,
                        this.inputs)
        );
    }

}
