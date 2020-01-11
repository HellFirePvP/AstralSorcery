/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.fluid;

import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.Rarity;
import net.minecraft.state.StateContainer;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.ForgeFlowingFluid;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: FluidLiquidStarlight
 * Created by HellFirePvP
 * Date: 20.09.2019 / 21:32
 */
public abstract class FluidLiquidStarlight extends ForgeFlowingFluid {

    private FluidLiquidStarlight(Properties properties) {
        super(properties);
    }

    public static FluidAttributes.Builder addAttributes(FluidAttributes.Builder attributeBuilder) {
        return attributeBuilder
                .rarity(Rarity.EPIC)
                .density(1001)
                .viscosity(300)
                .temperature(40);
    }

    public static class Flowing extends FluidLiquidStarlight {

        public Flowing(Properties properties) {
            super(properties);
            setDefaultState(getStateContainer().getBaseState().with(LEVEL_1_8, 7));
        }

        protected void fillStateContainer(StateContainer.Builder<Fluid, IFluidState> builder) {
            super.fillStateContainer(builder);
            builder.add(LEVEL_1_8);
        }

        public int getLevel(IFluidState state) {
            return state.get(LEVEL_1_8);
        }

        public boolean isSource(IFluidState state) {
            return false;
        }
    }

    public static class Source extends FluidLiquidStarlight {

        public Source(Properties properties) {
            super(properties);
        }

        public int getLevel(IFluidState state) {
            return 8;
        }

        public boolean isSource(IFluidState state) {
            return true;
        }

    }

}
