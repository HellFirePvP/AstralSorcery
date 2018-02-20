/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.base;

import hellfirepvp.astralsorcery.common.block.BlockCustomSandOre;
import hellfirepvp.astralsorcery.common.item.crystal.base.ItemRockCrystalBase;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import hellfirepvp.astralsorcery.common.tile.TileChalice;
import hellfirepvp.astralsorcery.common.util.ItemUtils;
import hellfirepvp.astralsorcery.common.util.WRItemObject;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.WeightedRandom;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: LiquidInteraction
 * Created by HellFirePvP
 * Date: 22.10.2017 / 01:43
 */
public class LiquidInteraction {

    private static final Random rand = new Random();
    private static List<LiquidInteraction> ALL_INTERACTIONS = new LinkedList<>();

    public static final LiquidInteraction WATER_LAVA_COBBLESTONE =
            new LiquidInteraction(6,
                    new FluidStack(FluidRegistry.WATER, 10),
                    new FluidStack(FluidRegistry.LAVA, 10),
                    createItemDropAction(0, 0, new ItemStack(Blocks.COBBLESTONE)));

    public static final LiquidInteraction WATER_LAVA_STONE =
            new LiquidInteraction(3,
                    new FluidStack(FluidRegistry.WATER, 10),
                    new FluidStack(FluidRegistry.LAVA, 10),
                    createItemDropAction(1F, 0.4F, new ItemStack(Blocks.STONE)));

    public static final LiquidInteraction WATER_LAVA_OBSIDIAN =
            new LiquidInteraction(1,
                    new FluidStack(FluidRegistry.WATER, 10),
                    new FluidStack(FluidRegistry.LAVA, 10),
                    createItemDropAction(0.7F, 1F, new ItemStack(Blocks.OBSIDIAN)));

    public static final LiquidInteraction STARLIGHT_WATER_ICE =
            new LiquidInteraction(1,
                    new FluidStack(BlocksAS.fluidLiquidStarlight, 30),
                    new FluidStack(FluidRegistry.WATER, 10),
                    createItemDropAction(1F, 1F, new ItemStack(Blocks.ICE)));

    public static final LiquidInteraction STARLIGHT_LAVA_SAND =
            new LiquidInteraction(1200,
                    new FluidStack(BlocksAS.fluidLiquidStarlight, 10),
                    new FluidStack(FluidRegistry.LAVA, 10),
                    createItemDropAction(0.8F, 0.8F, new ItemStack(Blocks.SAND)));

    public static final LiquidInteraction STARLIGHT_LAVA_AQUAMARINE =
            new LiquidInteraction(30,
                    new FluidStack(BlocksAS.fluidLiquidStarlight, 70),
                    new FluidStack(FluidRegistry.LAVA, 70),
                    createItemDropAction(1F, 1F, BlockCustomSandOre.OreType.AQUAMARINE.asStack()));

    public static final LiquidInteraction STARLIGHT_LAVA_CRYSTAL =
            new LiquidInteraction(1,
                    new FluidStack(BlocksAS.fluidLiquidStarlight, 500),
                    new FluidStack(FluidRegistry.LAVA, 500),
                    createCrystalDropAction(1F, 1F));

    private final FluidStack component1, component2;
    private final int probability;
    private final FluidInteractionAction action;

    public LiquidInteraction(int probability, FluidStack component1, FluidStack component2, FluidInteractionAction action) {
        this.probability = probability;
        this.component1 = component1;
        this.component2 = component2;
        this.action = action;
        ALL_INTERACTIONS.add(this);
    }

    public FluidStack getComponent1() {
        return component1;
    }

    public FluidStack getComponent2() {
        return component2;
    }

    public static InteractionFormBlock createItemDropAction(float chanceConsumption1, float chanceConsumption2, ItemStack resultBlockStack) {
        return new InteractionFormBlock(chanceConsumption1, chanceConsumption2, resultBlockStack);
    }

    public static InteractionFormBlock createCrystalDropAction(float chanceConsumption1, float chanceConsumption2) {
        return new InteractionFormBlock(chanceConsumption1, chanceConsumption2, new ItemStack(ItemsAS.rockCrystal)) {
            @Override
            public void doInteraction(World world, Vector3 position) {
                ItemStack out = ItemRockCrystalBase.createRandomBaseCrystal();
                ItemUtils.dropItemNaturally(world, position.getX(), position.getY(), position.getZ(), out);
            }
        };
    }

    public void drainComponents(@Nonnull TileChalice tc1, @Nonnull TileChalice tc2) {
        drainComponents(tc1.getTank(), tc2.getTank());
    }

    public <T extends IFluidTank & IFluidHandler> void drainComponents(@Nonnull T tank1, @Nonnull T tank2) {
        this.action.drainComponent1(this.component1.copy(), tank1);
        this.action.drainComponent2(this.component2.copy(), tank2);
    }

    public void triggerInteraction(World world, Vector3 position) {
        this.action.doInteraction(world, position);
    }

    @Nullable
    public static LiquidInteraction tryFindInteraction(@Nonnull TileChalice tc1, @Nonnull TileChalice tc2) {
        return tryFindInteraction(tc1.getTank(), tc2.getTank());
    }

    @Nullable
    public static LiquidInteraction tryFindInteraction(@Nonnull IFluidTank tank1, @Nonnull IFluidTank tank2) {
        return tryFindInteraction(tank1.getFluid(), tank2.getFluid());
    }

    @Nullable
    public static LiquidInteraction tryFindInteraction(@Nullable FluidStack fluid1, @Nullable FluidStack fluid2) {
        if(fluid1 == null || fluid2 == null) return null;
        List<WRItemObject<LiquidInteraction>> test = new LinkedList<>();

        for (LiquidInteraction li : ALL_INTERACTIONS) {
            if(fluid1.containsFluid(li.component1) && fluid2.containsFluid(li.component2)) {
                test.add(new WRItemObject<>(li.probability, li));
            }
        }
        if(test.isEmpty()) {
            return null;
        }
        return WeightedRandom.getRandomItem(rand, test).getValue();
    }

    public static List<LiquidInteraction> getPossibleInteractions(@Nullable FluidStack comp1) {
        List<LiquidInteraction> out = new LinkedList<>();
        if(comp1 == null) return out;
        for (LiquidInteraction li : ALL_INTERACTIONS) {
            if(li.component1.isFluidEqual(comp1)) {
                out.add(li);
            }
        }

        return out;
    }

    @Nullable
    public static LiquidInteraction getMatchingInteraction(List<LiquidInteraction> interactions, @Nullable FluidStack comp2) {
        if(comp2 == null) return null;
        List<WRItemObject<LiquidInteraction>> test = new LinkedList<>();

        for (LiquidInteraction li : interactions) {
            if(testForInteraction(li, comp2)) {
                test.add(new WRItemObject<>(li.probability, li));
            }
        }
        if(test.isEmpty()) {
            return null;
        }
        return WeightedRandom.getRandomItem(rand, test).getValue();
    }

    public static boolean testForInteraction(LiquidInteraction li, @Nullable FluidStack comp2) {
        return comp2 != null && li.component2.isFluidEqual(comp2);
    }

    public static abstract class FluidInteractionAction {

        public abstract void drainComponent1(FluidStack component, IFluidHandler tank);

        public abstract void drainComponent2(FluidStack component, IFluidHandler tank);

        public abstract void doInteraction(World world, Vector3 position);

    }

    public static class InteractionFormBlock extends FluidInteractionAction {

        private final float c1, c2;
        private final ItemStack result;

        private InteractionFormBlock(float chanceConsumption1, float chanceConsumption2, ItemStack resultBlockStack) {
            this.c1 = chanceConsumption1;
            this.c2 = chanceConsumption2;
            this.result = resultBlockStack.copy();
        }

        @Override
        public void drainComponent1(FluidStack component, IFluidHandler tank) {
            if(rand.nextFloat() < c1) {
                tank.drain(component, true);
            }
        }

        @Override
        public void drainComponent2(FluidStack component, IFluidHandler tank) {
            if(rand.nextFloat() < c2) {
                tank.drain(component, true);
            }
        }

        @Override
        public void doInteraction(World world, Vector3 position) {
            EntityItem ei = ItemUtils.dropItemNaturally(world, position.getX(), position.getY(), position.getZ(), result.copy());
            if(ei != null) {
                ei.age = ei.lifespan / 2;
            }
        }

    }

}
