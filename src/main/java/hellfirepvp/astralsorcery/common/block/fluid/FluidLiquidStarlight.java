/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block.fluid;

import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumRarity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: FluidLiquidStarlight
 * Created by HellFirePvP
 * Date: 14.09.2016 / 11:39
 */
public class FluidLiquidStarlight extends Fluid {

    private static final ResourceLocation starlightLiquidStill = new ResourceLocation("astralsorcery:blocks/fluid/starlight_still");
    private static final ResourceLocation starlightLiquidFlow = new ResourceLocation("astralsorcery:blocks/fluid/starlight_flow");

    public FluidLiquidStarlight() {
        super("astralsorcery.liquidStarlight", starlightLiquidStill, starlightLiquidFlow);
        setRarity(EnumRarity.EPIC);
        setLuminosity(15);
        setDensity(1001);
        setViscosity(300);
        setTemperature(10);
        setFillSound(SoundEvents.ITEM_BUCKET_FILL);
    }

}
