/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block.tile;

import hellfirepvp.astralsorcery.client.effect.function.VFXColorFunction;
import hellfirepvp.astralsorcery.client.effect.handler.EffectHelper;
import hellfirepvp.astralsorcery.client.lib.EffectTemplatesAS;
import hellfirepvp.astralsorcery.common.block.properties.PropertiesMisc;
import hellfirepvp.astralsorcery.common.util.ColorUtils;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.awt.*;
import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockFlareLight
 * Created by HellFirePvP
 * Date: 17.08.2019 / 13:29
 */
public class BlockFlareLight extends Block {

    public static final EnumProperty<DyeColor> COLOR = EnumProperty.create("color", DyeColor.class);
    private static final VoxelShape SHAPE = VoxelShapes.create(6F / 16F, 3F / 16F, 6F / 16F, 10F / 16F, 7F / 16F, 10F / 16F);

    public BlockFlareLight() {
        super(PropertiesMisc.defaultAir()
                .setLightLevel(state -> 15));
        setDefaultState(this.getStateContainer().getBaseState().with(COLOR, DyeColor.YELLOW));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void animateTick(BlockState state, World world, BlockPos pos, Random rand) {
        Color c = ColorUtils.flareColorFromDye(state.get(COLOR));
        for (int i = 0; i < 2; i++) {
            EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                    .spawn(new Vector3(pos)
                            .add(0.5, 0.2, 0.5)
                            .add(rand.nextFloat() * 0.1 * (rand.nextBoolean() ? 1 : -1),
                                    rand.nextFloat() * 0.1 * (rand.nextBoolean() ? 1 : -1),
                                    rand.nextFloat() * 0.1 * (rand.nextBoolean() ? 1 : -1)))
                    .setScaleMultiplier(0.4F + rand.nextFloat() * 0.1F)
                    .setAlphaMultiplier(0.35F)
                    .setMotion(new Vector3(0, rand.nextFloat() * 0.01F, 0))
                    .color(VFXColorFunction.constant(c))
                    .setMaxAge(50 + rand.nextInt(20));
        }
        if (rand.nextBoolean()) {
            EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                    .spawn(new Vector3(pos)
                            .add(0.5, 0.3, 0.5)
                            .add(rand.nextFloat() * 0.02 * (rand.nextBoolean() ? 1 : -1),
                                    rand.nextFloat() * 0.1 * (rand.nextBoolean() ? 1 : -1),
                                    rand.nextFloat() * 0.02 * (rand.nextBoolean() ? 1 : -1)))
                    .setScaleMultiplier(0.15F + rand.nextFloat() * 0.1F)
                    .setMotion(new Vector3(0, rand.nextFloat() * 0.01F, 0))
                    .color(VFXColorFunction.WHITE)
                    .setMaxAge(25 + rand.nextInt(10));
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean addDestroyEffects(BlockState state, World world, BlockPos pos, ParticleManager manager) {
        return true;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean addHitEffects(BlockState state, World worldObj, RayTraceResult target, ParticleManager manager) {
        return true;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean addRunningEffects(BlockState state, World world, BlockPos pos, Entity entity) {
        return true;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean addLandingEffects(BlockState state1, ServerWorld worldserver, BlockPos pos, BlockState state2, LivingEntity entity, int numberOfParticles) {
        return true;
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext ctx) {
        return SHAPE;
    }

    @Override
    public boolean isAir(BlockState state, IBlockReader world, BlockPos pos) {
        return false;
    }

    @Override
    public boolean canBeReplacedByLogs(BlockState state, IWorldReader world, BlockPos pos) {
        return true;
    }

    @Override
    public boolean canBeReplacedByLeaves(BlockState state, IWorldReader world, BlockPos pos) {
        return true;
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> ct) {
        ct.add(COLOR);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.INVISIBLE;
    }
}
