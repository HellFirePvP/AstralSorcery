/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.recipe.lumen;

import com.mojang.datafixers.util.Either;
import hellfirepvp.astralsorcery.common.lumen.Lumen;
import hellfirepvp.astralsorcery.common.recipe.CustomRecipeInput;
import net.minecraft.world.item.ItemStack;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: LumenCrystallizationRecipeInput
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class LumenCrystallizationRecipeInput extends CustomRecipeInput {

    private final Either<ItemStack, Lumen> catalystInput;

    private LumenCrystallizationRecipeInput(Either<ItemStack, Lumen> input) {
        this.catalystInput = input;
    }

    public static LumenCrystallizationRecipeInput of(ItemStack stack) {
        return new LumenCrystallizationRecipeInput(Either.left(stack));
    }

    public static LumenCrystallizationRecipeInput of(Lumen lumen) {
        return new LumenCrystallizationRecipeInput(Either.right(lumen));
    }

    public Either<ItemStack, Lumen> getCatalystInput() {
        return this.catalystInput.mapLeft(ItemStack::copy);
    }

    @Override
    public boolean isEmpty() {
        return false;
    }
}
