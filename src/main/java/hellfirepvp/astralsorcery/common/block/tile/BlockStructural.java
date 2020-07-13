/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 *  All rights reserved.
 *  The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 *  For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block.tile;

import com.google.common.collect.Lists;
import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.GuiType;
import hellfirepvp.astralsorcery.common.event.EventFlags;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.*;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ToolType;

import javax.annotation.Nullable;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockStructural
 * Created by HellFirePvP
 * Date: 15.01.2020 / 16:22
 */
public class BlockStructural extends Block {

    public static EnumProperty<BlockType> BLOCK_TYPE = EnumProperty.create("blocktype", BlockType.class);

    private static final VoxelShape STRUCT_TELESCOPE = VoxelShapes.create(1D / 16D, -16D / 16D, 1D / 16D, 15D / 16D, 16D / 16D, 15D / 16D);

    public BlockStructural() {
        super(Block.Properties.create(Material.BARRIER, MaterialColor.AIR)
                .sound(SoundType.GLASS));

        this.setDefaultState(this.getDefaultState().with(BLOCK_TYPE, BlockType.TELESCOPE));
    }

    @Override
    public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {}

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(BLOCK_TYPE);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        switch (state.get(BLOCK_TYPE)) {
            case TELESCOPE:
                return STRUCT_TELESCOPE;
        }
        return super.getShape(state, worldIn, pos, context);
    }

    @Nullable
    @Override
    public ToolType getHarvestTool(BlockState state) {
        return state.get(BLOCK_TYPE).getSupportedState().getHarvestTool();
    }

    @Override
    public int getHarvestLevel(BlockState state) {
        return state.get(BLOCK_TYPE).getSupportedState().getHarvestLevel();
    }

    @Override
    public SoundType getSoundType(BlockState state, IWorldReader world, BlockPos pos, @Nullable Entity entity) {
        switch (state.get(BLOCK_TYPE)) {
            case TELESCOPE:
                return SoundType.WOOD;
        }
        return super.getSoundType(state, world, pos, entity);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean addDestroyEffects(BlockState state, World world, BlockPos pos, ParticleManager manager) {
        EventFlags.PLAY_BLOCK_BREAK_EFFECTS.executeWithFlag(() -> {
            switch (state.get(BLOCK_TYPE)) {
                case TELESCOPE:
                    manager.addBlockDestroyEffects(pos.down(), BlocksAS.TELESCOPE.getDefaultState());
                    break;
            }
        });
        return true;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean addHitEffects(BlockState state, World world, RayTraceResult target, ParticleManager manager) {
        if (target instanceof BlockRayTraceResult) {
            EventFlags.PLAY_BLOCK_BREAK_EFFECTS.executeWithFlag(() -> {
                switch (state.get(BLOCK_TYPE)) {
                    case TELESCOPE:
                        manager.addBlockDestroyEffects(((BlockRayTraceResult) target).getPos().down(), BlocksAS.TELESCOPE.getDefaultState());
                        break;
                }
            });
        }
        return true;
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity entity, Hand hand, BlockRayTraceResult rayTraceResult) {
        switch (state.get(BLOCK_TYPE)) {
            case TELESCOPE:
                if (world.isRemote()) {
                    AstralSorcery.getProxy().openGui(entity, GuiType.TELESCOPE, pos.down());
                }
                return ActionResultType.SUCCESS;
        }
        return super.onBlockActivated(state, world, pos, entity, hand, rayTraceResult);
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
        List<ItemStack> drops = Lists.newArrayList();
        switch (state.get(BLOCK_TYPE)) {
            case TELESCOPE:
                return BlockType.TELESCOPE.getSupportedState().getDrops(builder);
        }
        return drops;
    }

    @Override
    public float getBlockHardness(BlockState state, IBlockReader world, BlockPos pos) {
        switch (state.get(BLOCK_TYPE)) {
            case TELESCOPE:
                return BlockType.TELESCOPE.getSupportedState().getBlockHardness(world, pos.down());
        }
        return super.getBlockHardness(state, world, pos);
    }

    @Override
    public float getExplosionResistance(BlockState state, IWorldReader world, BlockPos pos, @Nullable Entity exploder, Explosion explosion) {
        switch (state.get(BLOCK_TYPE)) {
            case TELESCOPE:
                return BlockType.TELESCOPE.getSupportedState().getExplosionResistance(world, pos.down(), exploder, explosion);
        }
        return super.getExplosionResistance(state, world, pos, exploder, explosion);
    }

    @Override
    public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader world, BlockPos pos, PlayerEntity player) {
        switch (state.get(BLOCK_TYPE)) {
            case TELESCOPE:
                return BlockType.TELESCOPE.getSupportedState().getPickBlock(target, world, pos.down(), player);
        }
        return super.getPickBlock(state, target, world, pos, player);
    }

    @Override
    public void neighborChanged(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
        switch (state.get(BLOCK_TYPE)) {
            case TELESCOPE:
                if (world.isAirBlock(pos.down())) {
                    world.removeBlock(pos, isMoving);
                }
                return;
        }
        super.neighborChanged(state, world, pos, block, fromPos, isMoving);
    }

    @Override
    public void onNeighborChange(BlockState state, IWorldReader world, BlockPos pos, BlockPos neighbor) {
        if (!(world instanceof IWorldWriter)) {
            return;
        }
        switch (state.get(BLOCK_TYPE)) {
            case TELESCOPE:
                if (world.isAirBlock(pos.down())) {
                    ((IWorldWriter) world).removeBlock(pos, false);
                }
        }
    }

    @Override
    public boolean isNormalCube(BlockState state, IBlockReader world, BlockPos pos) {
        switch (state.get(BLOCK_TYPE)) {
            case TELESCOPE:
                return BlockType.TELESCOPE.getSupportedState().isNormalCube(world, pos.down());
        }
        return super.isNormalCube(state, world, pos);
    }

    @Override
    public BlockRenderType getRenderType(BlockState p_149645_1_) {
        return BlockRenderType.INVISIBLE;
    }

    public static enum BlockType implements IStringSerializable {

        DUMMY(Blocks.AIR.getDefaultState()),
        TELESCOPE(BlocksAS.TELESCOPE.getDefaultState());

        private final BlockState supportedState;

        private BlockType(BlockState supportedState) {
            this.supportedState = supportedState;
        }

        public BlockState getSupportedState() {
            return supportedState;
        }

        @Override
        public String getName() {
            return name().toLowerCase();
        }

        @Override
        public String toString() {
            return this.getName();
        }
    }
}
