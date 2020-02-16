/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block.tile;

import hellfirepvp.astralsorcery.common.block.base.CustomItemBlock;
import hellfirepvp.astralsorcery.common.block.base.LargeBlock;
import hellfirepvp.astralsorcery.common.block.properties.PropertiesMisc;
import hellfirepvp.astralsorcery.common.container.factory.ContainerObservatoryProvider;
import hellfirepvp.astralsorcery.common.tile.TileObservatory;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import net.minecraft.block.BlockState;
import net.minecraft.block.ContainerBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;

import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockObservatory
 * Created by HellFirePvP
 * Date: 16.02.2020 / 08:11
 */
public class BlockObservatory extends ContainerBlock implements LargeBlock, CustomItemBlock {

    private static final AxisAlignedBB PLACEMENT_BOX = new AxisAlignedBB(-1, 0, -1, 1, 3, 1);

    public BlockObservatory() {
        super(PropertiesMisc.defaultGoldMachinery()
                .harvestTool(ToolType.PICKAXE)
                .harvestLevel(1)
                .hardnessAndResistance(3F, 4F));
    }

    @Override
    public AxisAlignedBB getBlockSpace() {
        return PLACEMENT_BOX;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return this.canPlaceAt(context) ? this.getDefaultState() : null;
    }

    @Override
    public boolean onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if (!worldIn.isRemote()) {
            TileObservatory observatory = MiscUtils.getTileAt(worldIn, pos, TileObservatory.class, false);
            if (observatory != null && observatory.isUsable() && !player.isSneaking()) {
                Entity entity = observatory.findRideableObservatoryEntity();
                if (entity != null) {
                    if (player.getRidingEntity() != entity) {
                        player.startRiding(entity);
                    }
                    new ContainerObservatoryProvider(observatory).openFor((ServerPlayerEntity) player);
                }
            }
        }
        return true;
    }

    @Override
    public boolean isSolid(BlockState state) {
        return false;
    }

    @Override
    public boolean hasCustomBreakingProgress(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(IBlockReader worldIn) {
        return new TileObservatory();
    }
}
