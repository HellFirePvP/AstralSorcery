/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.integrations.mods.crafttweaker.network;

import hellfirepvp.astralsorcery.common.crafting.helper.CraftingAccessManager;
import hellfirepvp.astralsorcery.common.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: InfusionRecipeRemove
 * Created by HellFirePvP
 * Date: 27.02.2017 / 02:33
 */
public class InfusionRecipeRemove implements SerializeableRecipe {

    private ItemStack removalOut;

    InfusionRecipeRemove() {}

    public InfusionRecipeRemove(ItemStack out) {
        this.removalOut = out;
    }

    @Override
    public CraftingType getType() {
        return CraftingType.INFUSION_REMOVE;
    }

    @Override
    public void read(ByteBuf buf) {
        this.removalOut = ByteBufUtils.readItemStack(buf);
    }

    @Override
    public void write(ByteBuf buf) {
        ByteBufUtils.writeItemStack(buf, this.removalOut);
    }

    @Override
    public void applyRecipe() {
        CraftingAccessManager.tryRemoveInfusionByOutput(this.removalOut);
    }

}
