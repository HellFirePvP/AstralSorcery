/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block.base;

import com.google.common.collect.Lists;
import hellfirepvp.astralsorcery.client.effect.function.VFXAlphaFunction;
import hellfirepvp.astralsorcery.client.effect.function.VFXColorFunction;
import hellfirepvp.astralsorcery.client.effect.handler.EffectHelper;
import hellfirepvp.astralsorcery.client.lib.EffectTemplatesAS;
import hellfirepvp.astralsorcery.client.util.RenderingUtils;
import hellfirepvp.astralsorcery.common.tile.base.TileFakedState;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.block.*;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockFakedState
 * Created by HellFirePvP
 * Date: 04.09.2020 / 19:19
 */
public abstract class BlockFakedState extends ContainerBlock {

    protected BlockFakedState(Properties builder) {
        super(builder);
    }

    @OnlyIn(Dist.CLIENT)
    protected void playParticles(World world, BlockPos pos, Random rand) {
        if (rand.nextInt(8) == 0) {
            VFXColorFunction<?> colorFn = VFXColorFunction.WHITE;
            TileFakedState fakedState = MiscUtils.getTileAt(world, pos, TileFakedState.class, false);
            if (fakedState != null) {
                colorFn = VFXColorFunction.constant(fakedState.getOverlayColor());
            }
            EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                    .spawn(Vector3.random().abs().add(pos))
                    .alpha(VFXAlphaFunction.FADE_OUT)
                    .color(colorFn)
                    .setScaleMultiplier(0.2F + rand.nextFloat() * 0.05F)
                    .setMaxAge(25 + rand.nextInt(5));
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean addDestroyEffects(BlockState state, World world, BlockPos pos, ParticleManager manager) {
        BlockState fakeState = this.getFakedState(world, pos);
        RenderingUtils.playBlockBreakParticles(pos, state, fakeState);
        return true;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean addHitEffects(BlockState state, World worldObj, RayTraceResult target, ParticleManager manager) {
        return true;
    }

    @Override
    public boolean addLandingEffects(BlockState state1, ServerWorld worldserver, BlockPos pos, BlockState state2, LivingEntity entity, int numberOfParticles) {
        return true;
    }

    @Override
    public boolean addRunningEffects(BlockState state, World world, BlockPos pos, Entity entity) {
        return true;
    }

    @Override
    public SoundType getSoundType(BlockState state, IWorldReader world, BlockPos pos, @Nullable Entity entity) {
        BlockState fakeState = this.getFakedState(world, pos);
        return fakeState.getSoundType(world, pos, entity);
    }

    @Override
    public boolean canEntityDestroy(BlockState state, IBlockReader world, BlockPos pos, Entity entity) {
        return false;
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
        BlockState fakeState = this.getFakedState(world, pos);
        return fakeState.getShape(world, pos, context);
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
        return Lists.newArrayList();
    }

    @Override
    public Vec3d getOffset(BlockState state, IBlockReader worldIn, BlockPos pos) {
        BlockState fakeState = this.getFakedState(worldIn, pos);
        try {
            //if (fakeState.getBlock().getOffsetType())
            return fakeState.getOffset(worldIn, pos);
        } catch (Exception exc) {
            //Ignore the result if this happens to be more complex than expected
        }
        return Vec3d.ZERO;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        BlockState fakeState = this.getFakedState(worldIn, pos);
        try {
            return fakeState.getCollisionShape(worldIn, pos, context);
        } catch (Exception exc) {
            //Ignore the result if this happens to be more complex than expected
        }
        return super.getCollisionShape(state, worldIn, pos, context);
    }

    @Override
    public VoxelShape getRenderShape(BlockState state, IBlockReader worldIn, BlockPos pos) {
        BlockState fakeState = this.getFakedState(worldIn, pos);
        try {
            return fakeState.getRenderShape(worldIn, pos);
        } catch (Exception exc) {
            //Ignore the result if this happens to be more complex than expected
        }
        return super.getRenderShape(state, worldIn, pos);
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        BlockState fakeState = this.getFakedState(world, pos);
        try {
            return fakeState.onBlockActivated(world, player, handIn, hit);
        } catch (Exception exc) {
            //Ignore the result if we can't interact
        }
        return super.onBlockActivated(state, world, pos, player, handIn, hit);
    }

    @Override
    public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader world, BlockPos pos, PlayerEntity player) {
        BlockState fakeState = this.getFakedState(world, pos);
        try {
            return fakeState.getPickBlock(target, world, pos, player);
        } catch (Exception exc) {
            //Ignore the result. If we can't pick that stuff here, well.. guess we can't at all.
        }
        return ItemStack.EMPTY;
    }

    @Nonnull
    private BlockState getFakedState(IBlockReader world, BlockPos pos) {
        TileFakedState tb = MiscUtils.getTileAt(world, pos, TileFakedState.class, true);
        return tb != null ? tb.getFakedState() : Blocks.AIR.getDefaultState();
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.INVISIBLE;
    }
}
