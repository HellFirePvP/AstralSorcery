/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.fluid;

import hellfirepvp.astralsorcery.client.effect.function.VFXAlphaFunction;
import hellfirepvp.astralsorcery.client.effect.function.VFXColorFunction;
import hellfirepvp.astralsorcery.client.effect.handler.EffectHelper;
import hellfirepvp.astralsorcery.client.lib.EffectTemplatesAS;
import hellfirepvp.astralsorcery.common.crafting.nojson.LiquidStarlightCraftingRegistry;
import hellfirepvp.astralsorcery.common.data.config.entry.CraftingConfig;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.lib.ColorsAS;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.fluid.EmptyFluid;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.IFluidState;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.ForgeEventFactory;

import java.util.Random;
import java.util.function.Supplier;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockLiquidStarlight
 * Created by HellFirePvP
 * Date: 20.09.2019 / 21:21
 */
public class BlockLiquidStarlight extends FlowingFluidBlock {

    public BlockLiquidStarlight(Supplier<? extends FlowingFluid> fluidSupplier) {
        super(fluidSupplier, Block.Properties.create(Material.WATER)
                .doesNotBlockMovement()
                .lightValue(15)
                .hardnessAndResistance(100.0F)
                .noDrops());
    }

    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        super.onEntityCollision(state, world, pos, entity);

        if (state.get(LEVEL) != 0) {
            return;
        }

        if (entity instanceof LivingEntity) {
            ((LivingEntity) entity).addPotionEffect(new EffectInstance(Effects.NIGHT_VISION, 300, 0, true, true));
        } else if (entity instanceof ItemEntity) {
            LiquidStarlightCraftingRegistry.tryCraft((ItemEntity) entity);

            if (!world.isRemote() &&((ItemEntity) entity).getItem().isEmpty()) {
                entity.remove();
            }
        }
    }

    @Override
    public boolean reactWithNeighbors(World world, BlockPos pos, BlockState state) {
        for (Direction dir : Direction.values()) {
            if (dir == Direction.DOWN) {
                continue;
            }

            IFluidState otherState = world.getFluidState(pos.offset(dir));
            Fluid otherFluid = otherState.getFluid();
            if (otherFluid instanceof FlowingFluid) {
                otherFluid = ((FlowingFluid) otherFluid).getStillFluid();
            }
            if (otherFluid instanceof EmptyFluid || otherFluid.equals(this.getFluid())) {
                continue;
            }

            BlockState generate;
            boolean isHot = otherFluid.getAttributes().getTemperature(world, pos.offset(dir)) > 600;
            if (isHot) {
                if (CraftingConfig.CONFIG.liquidStarlightInteractionSand.get()) {
                    generate = Blocks.SAND.getDefaultState();
                    if (CraftingConfig.CONFIG.liquidStarlightInteractionAquamarine.get() && world.rand.nextInt(800) == 0) {
                        generate = BlocksAS.AQUAMARINE_SAND_ORE.getDefaultState();
                    }
                } else {
                    generate = Blocks.COBBLESTONE.getDefaultState();
                }
            } else {
                if (CraftingConfig.CONFIG.liquidStarlightInteractionIce.get()) {
                    generate = Blocks.PACKED_ICE.getDefaultState();
                } else {
                    generate = Blocks.COBBLESTONE.getDefaultState();
                }
            }

            world.setBlockState(pos, ForgeEventFactory.fireFluidPlaceBlockEvent(world, pos, pos, generate));
        }
        return true;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void animateTick(BlockState state, World world, BlockPos pos, Random rand) {
        Integer level = state.get(LEVEL);
        double percHeight = 1D - (((double) level + 1) / 8D);
        playLiquidStarlightBlockEffect(rand, new Vector3(pos).addY(percHeight * rand.nextFloat()), 1F);
        playLiquidStarlightBlockEffect(rand, new Vector3(pos).addY(percHeight * rand.nextFloat()), 1F);
    }

    @OnlyIn(Dist.CLIENT)
    public static void playLiquidStarlightBlockEffect(Random rand, Vector3 at, float blockSize) {
        if (rand.nextInt(3) == 0) {
            EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                    .spawn(at.clone().add(
                            0.5 + rand.nextFloat() * (blockSize / 2) * (rand.nextBoolean() ? 1 : -1),
                            0,
                            0.5 + rand.nextFloat() * (blockSize / 2) * (rand.nextBoolean() ? 1 : -1)))
                    .setScaleMultiplier(0.1F + rand.nextFloat() * 0.06F)
                    .alpha(VFXAlphaFunction.FADE_OUT)
                    .color(VFXColorFunction.constant(ColorsAS.ROCK_CRYSTAL));
        }
    }
}
