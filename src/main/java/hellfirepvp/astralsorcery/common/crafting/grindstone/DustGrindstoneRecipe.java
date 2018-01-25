/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crafting.grindstone;

import hellfirepvp.astralsorcery.common.crafting.ItemHandle;
import hellfirepvp.astralsorcery.common.util.ItemUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;

import javax.annotation.Nonnull;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: DustGrindstoneRecipe
 * Created by HellFirePvP
 * Date: 20.11.2017 / 23:54
 */
public class DustGrindstoneRecipe extends GrindstoneRecipe {

    private final float doubleChance;

    public DustGrindstoneRecipe(ItemHandle input, ItemStack output, int chance, float doubleChance) {
        super(input, output, chance);
        this.doubleChance = doubleChance;
    }

    public float getDoubleChance() {
        return MathHelper.clamp(doubleChance, 0, 1);
    }

    @Nonnull
    @Override
    public GrindResult grind(ItemStack stackIn) {
        if(rand.nextInt(chance) == 0) {
            ItemStack out = ItemUtils.copyStackWithSize(this.output, this.output.getCount());
            if(rand.nextFloat() < doubleChance) {
                out.setCount(out.getCount() * 2);
            }
            return GrindResult.itemChange(out);
        }
        return GrindResult.failNoOp();
    }

}
