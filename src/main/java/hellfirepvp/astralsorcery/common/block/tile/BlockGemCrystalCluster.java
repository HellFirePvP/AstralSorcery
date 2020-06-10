/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block.tile;

import hellfirepvp.astralsorcery.common.block.base.CustomItemBlock;
import hellfirepvp.astralsorcery.common.block.tile.crystal.CollectorCrystalType;
import hellfirepvp.astralsorcery.common.constellation.world.DayTimeHelper;
import hellfirepvp.astralsorcery.common.item.block.ItemBlockGemCrystalCluster;
import hellfirepvp.astralsorcery.common.lib.ColorsAS;
import hellfirepvp.astralsorcery.common.network.PacketChannel;
import hellfirepvp.astralsorcery.common.network.play.server.PktPlayEffect;
import hellfirepvp.astralsorcery.common.tile.TileGemCrystals;
import hellfirepvp.astralsorcery.common.util.data.ByteBufUtils;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;

import javax.annotation.Nullable;
import java.awt.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockGemCrystalCluster
 * Created by HellFirePvP
 * Date: 16.11.2019 / 10:06
 */
public class BlockGemCrystalCluster extends ContainerBlock implements CustomItemBlock {

    private static final VoxelShape STAGE_0       = Block.makeCuboidShape(4, 0, 4, 12,  6, 12);
    private static final VoxelShape STAGE_1       = Block.makeCuboidShape(4, 0, 4, 12,  8, 12);
    private static final VoxelShape STAGE_2_SKY   = Block.makeCuboidShape(5, 0, 5, 11, 10, 11);
    private static final VoxelShape STAGE_2_DAY   = Block.makeCuboidShape(4, 0, 4, 12, 10, 12);
    private static final VoxelShape STAGE_2_NIGHT = Block.makeCuboidShape(5, 0, 5, 11,  8, 11);

    public static final EnumProperty<GrowthStageType> STAGE = EnumProperty.create("stage", GrowthStageType.class);

    public BlockGemCrystalCluster() {
        super(Properties.create(Material.GLASS, CollectorCrystalType.ROCK_CRYSTAL.getMaterialColor())
                .hardnessAndResistance(3, 3)
                .harvestTool(ToolType.PICKAXE)
                .harvestLevel(1)
                .sound(SoundType.GLASS)
                .lightValue(6));
    }

    @Override
    public Class<? extends BlockItem> getItemBlockClass() {
        return ItemBlockGemCrystalCluster.class;
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(STAGE);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
        Vec3d offset = state.getOffset(world, pos);
        VoxelShape shape = VoxelShapes.fullCube();
        switch (state.get(STAGE)) {
            case STAGE_0:
                shape = STAGE_0;
                break;
            case STAGE_1:
                shape = STAGE_1;
                break;
            case STAGE_2_SKY:
                shape = STAGE_2_SKY;
                break;
            case STAGE_2_DAY:
                shape = STAGE_2_DAY;
                break;
            case STAGE_2_NIGHT:
                shape = STAGE_2_NIGHT;
                break;
        }
        return shape.withOffset(offset.x, offset.y, offset.z);
    }

    @Override
    public OffsetType getOffsetType() {
        return OffsetType.XZ;
    }

    @Override
    public Vec3d getOffset(BlockState state, IBlockReader world, BlockPos pos) {
        return super.getOffset(state, world, pos).mul(0.7, 0.7, 0.7);
    }

    @Override
    public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos) {
        BlockPos blockpos = pos.down();
        return hasSolidSideOnTop(worldIn, blockpos) || hasEnoughSolidSide(worldIn, blockpos, Direction.UP);
    }

    @Override
    public BlockState updatePostPlacement(BlockState state, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
        return facing == Direction.DOWN && !state.isValidPosition(worldIn, currentPos) ? Blocks.AIR.getDefaultState() :
                super.updatePostPlacement(state, facing, facingState, worldIn, currentPos, facingPos);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public void onReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) {
            super.onReplaced(state, world, pos, newState, isMoving);

            PktPlayEffect effect = new PktPlayEffect(PktPlayEffect.Type.GEM_CRYSTAL_BREAK)
                    .addData(buf -> {
                        ByteBufUtils.writeVector(buf, new Vector3(pos).add(state.getOffset(world, pos)));
                        buf.writeInt(state.get(STAGE).ordinal());
                    });
            PacketChannel.CHANNEL.sendToAllAround(effect, PacketChannel.pointFromPos(world, pos, 32));
        }
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(IBlockReader world) {
        return new TileGemCrystals();
    }


    public static enum GrowthStageType implements IStringSerializable {

        STAGE_0      (0, Color.WHITE),
        STAGE_1      (1, Color.WHITE),
        STAGE_2_SKY  (2, ColorsAS.GEM_SKY),
        STAGE_2_DAY  (2, ColorsAS.GEM_DAY),
        STAGE_2_NIGHT(2, ColorsAS.GEM_NIGHT);

        private final int growthStage;
        private final Color displayColor;

        GrowthStageType(int growthStage, Color displayColor) {
            this.growthStage = growthStage;
            this.displayColor = displayColor;
        }

        public Color getDisplayColor() {
            return displayColor;
        }

        public int getGrowthStage() {
            return growthStage;
        }

        public GrowthStageType grow(World world) {
            if (this == STAGE_0) {
                return STAGE_1;
            }
            if (this == STAGE_1) {
                if (DayTimeHelper.isDay(world)) {
                    return STAGE_2_DAY;
                }
                if (DayTimeHelper.isNight(world)) {
                    return STAGE_2_NIGHT;
                }
                return STAGE_2_SKY;
            }
            return this;
        }

        public GrowthStageType shrink() {
            int stage = getGrowthStage();
            if (stage == 2) {
                return STAGE_1;
            }
            return STAGE_0;
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
