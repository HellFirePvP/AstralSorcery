/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block.tile;

import hellfirepvp.astralsorcery.common.block.base.BlockStarlightNetwork;
import hellfirepvp.astralsorcery.common.block.base.CustomItemBlock;
import hellfirepvp.astralsorcery.common.block.properties.PropertiesMarble;
import hellfirepvp.astralsorcery.common.block.tile.altar.AltarType;
import hellfirepvp.astralsorcery.common.container.factory.*;
import hellfirepvp.astralsorcery.common.data.research.ProgressionTier;
import hellfirepvp.astralsorcery.common.data.research.ResearchHelper;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.tile.TileAltar;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.item.ItemUtils;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.fml.LogicalSide;

import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockAltar
 * Created by HellFirePvP
 * Date: 12.08.2019 / 20:00
 */
public abstract class BlockAltar extends BlockStarlightNetwork implements CustomItemBlock {

    private final AltarType type;

    public BlockAltar(AltarType type) {
        super(PropertiesMarble.defaultMarble()
                .harvestLevel(1)
                .harvestTool(ToolType.PICKAXE));

        this.type = type;
    }

    public AltarType getAltarType() {
        return type;
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        if (!world.isRemote() && player instanceof ServerPlayerEntity) {
            TileAltar altar = MiscUtils.getTileAt(world, pos, TileAltar.class, true);
            if (altar != null) {
                CustomContainerProvider<?> provider;
                switch (altar.getAltarType()) {
                    case DISCOVERY:
                        provider = new ContainerAltarDiscoveryProvider(altar);

                        if (!ResearchHelper.getProgress(player, LogicalSide.SERVER)
                                .getTierReached().isThisLaterOrEqual(ProgressionTier.BASIC_CRAFT)) {
                            ResearchManager.informCrafted(player, new ItemStack(BlocksAS.ALTAR_DISCOVERY));
                        }
                        break;
                    case ATTUNEMENT:
                        provider = new ContainerAltarAttunementProvider(altar);
                        break;
                    case CONSTELLATION:
                        provider = new ContainerAltarConstellationProvider(altar);
                        break;
                    case RADIANCE:
                        provider = new ContainerAltarRadianceProvider(altar);
                        break;
                    default:
                        provider = null;
                        break;
                }

                if (provider != null) {
                    provider.openFor((ServerPlayerEntity) player);
                }
            }
        }
        return ActionResultType.SUCCESS;
    }

    @Override
    public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!(newState.getBlock() instanceof BlockAltar)) {
            TileAltar ta = MiscUtils.getTileAt(worldIn, pos, TileAltar.class, true);
            if (ta != null && !worldIn.isRemote) {
                ItemUtils.dropInventory(ta.getInventory(), worldIn, pos);
            }
            super.onReplaced(state, worldIn, pos, newState, isMoving);
        } else {
            AltarType thisType = ((BlockAltar)    state.getBlock()).type;
            AltarType thatType = ((BlockAltar) newState.getBlock()).type;
            if (thisType != thatType) {
                TileAltar ta = MiscUtils.getTileAt(worldIn, pos, TileAltar.class, true);
                if (ta != null) {
                    ta.updateType(thatType, false);
                }
            }
        }
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(IBlockReader world) {
        return new TileAltar().updateType(this.type, true);
    }
}
