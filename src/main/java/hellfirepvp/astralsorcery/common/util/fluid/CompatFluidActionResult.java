/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util.fluid;

import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CompatFluidActionResult
 * Created by HellFirePvP
 * Date: 19.07.2019 / 18:17
 */
public class CompatFluidActionResult {

    public static final CompatFluidActionResult FAILURE = new CompatFluidActionResult(false, ItemStack.EMPTY);

    public final boolean success;
    @Nonnull
    public final ItemStack result;

    public CompatFluidActionResult(@Nonnull ItemStack result)
    {
        this(true, result);
    }

    private CompatFluidActionResult(boolean success, @Nonnull ItemStack result) {
        this.success = success;
        this.result = result;
    }

    public boolean isSuccess()
    {
        return success;
    }

    @Nonnull
    public ItemStack getResult()
    {
        return result;
    }

}
