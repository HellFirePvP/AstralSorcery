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
import hellfirepvp.astralsorcery.common.block.tile.crystal.CollectorCrystalType;
import hellfirepvp.astralsorcery.common.constellation.ConstellationItem;
import hellfirepvp.astralsorcery.common.constellation.IMinorConstellation;
import hellfirepvp.astralsorcery.common.constellation.IWeakConstellation;
import hellfirepvp.astralsorcery.common.crystal.CalculationContext;
import hellfirepvp.astralsorcery.common.crystal.CrystalAttributes;
import hellfirepvp.astralsorcery.common.crystal.source.AttunedSourceInstance;
import hellfirepvp.astralsorcery.common.data.research.GatedKnowledge;
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ProgressionTier;
import hellfirepvp.astralsorcery.common.data.research.ResearchHelper;
import hellfirepvp.astralsorcery.common.item.block.ItemBlockCollectorCrystal;
import hellfirepvp.astralsorcery.common.lib.CrystalPropertiesAS;
import hellfirepvp.astralsorcery.common.tile.TileCollectorCrystal;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.observerlib.api.block.BlockStructureObserver;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ToolType;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockCollectorCrystal
 * Created by HellFirePvP
 * Date: 10.08.2019 / 20:32
 */
public abstract class BlockCollectorCrystal extends BlockStarlightNetwork implements BlockStructureObserver, CustomItemBlock {

    private static final VoxelShape SHAPE = Block.makeCuboidShape(4.5, 0, 4.5, 11.5, 16, 11.5);
    private static final float PLAYER_HARVEST_HARDNESS = 4F;

    public BlockCollectorCrystal(CollectorCrystalType type) {
        super(Properties.create(Material.GLASS, type.getMaterialColor())
                .hardnessAndResistance(-1.0F, 3600000.0F)
                .harvestTool(ToolType.PICKAXE)
                .harvestLevel(1)
                .sound(SoundType.GLASS)
                .lightValue(11));
    }

    @Override
    public abstract Class<? extends ItemBlockCollectorCrystal> getItemBlockClass();

    @Override
    @OnlyIn(Dist.CLIENT)
    public void addInformation(ItemStack stack, @Nullable IBlockReader world, List<ITextComponent> toolTip, ITooltipFlag flag) {
        super.addInformation(stack, world, toolTip, flag);

        CrystalAttributes attr = CrystalAttributes.getCrystalAttributes(stack);
        CrystalAttributes.TooltipResult result = null;
        if (attr != null) {
            result = attr.addTooltip(toolTip, CalculationContext.Builder
                    .withSource(new AttunedSourceInstance(CrystalPropertiesAS.Sources.SOURCE_TILE_COLLECTOR_CRYSTAL, ((ConstellationItem) stack.getItem()).getAttunedConstellation(stack)))
                    .addUsage(CrystalPropertiesAS.Usages.USE_COLLECTOR_CRYSTAL)
                    .addUsage(CrystalPropertiesAS.Usages.USE_LENS_TRANSFER)
                    .build());
        }

        if (result != null) {
            PlayerProgress clientProgress = ResearchHelper.getClientProgress();
            ProgressionTier tier = clientProgress.getTierReached();

            boolean addedMissing = result != CrystalAttributes.TooltipResult.ADDED_ALL;
            IWeakConstellation c = ((ConstellationItem) stack.getItem()).getAttunedConstellation(stack);
            if (c != null) {
                if (GatedKnowledge.COLLECTOR_TYPE.canSee(tier) && clientProgress.hasConstellationDiscovered(c)) {
                    toolTip.add(new TranslationTextComponent("crystal.info.astralsorcery.collect.type",
                            c.getConstellationName().applyTextStyle(TextFormatting.BLUE))
                            .setStyle(new Style().setColor(TextFormatting.GRAY)));

                } else if (!addedMissing) {
                    toolTip.add(new TranslationTextComponent("astralsorcery.progress.missing.knowledge").setStyle(new Style().setColor(TextFormatting.GRAY)));
                }
            }

            IMinorConstellation tr = ((ConstellationItem) stack.getItem()).getTraitConstellation(stack);
            if (tr != null) {
                if (GatedKnowledge.CRYSTAL_TRAIT.canSee(tier) && clientProgress.hasConstellationDiscovered(tr)) {
                    toolTip.add(new TranslationTextComponent("crystal.info.astralsorcery.trait",
                            tr.getConstellationName().applyTextStyle(TextFormatting.BLUE))
                            .setStyle(new Style().setColor(TextFormatting.GRAY)));

                } else if (!addedMissing) {
                    toolTip.add(new TranslationTextComponent("astralsorcery.progress.missing.knowledge").setStyle(new Style().setColor(TextFormatting.GRAY)));
                }
            }
        }
    }

    @Override
    public VoxelShape getShape(BlockState p_220053_1_, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
        return SHAPE;
    }

    @Override
    public float getBlockHardness(BlockState state, IBlockReader world, BlockPos pos) {
        TileCollectorCrystal crystal = MiscUtils.getTileAt(world, pos, TileCollectorCrystal.class, false);
        if (crystal != null && crystal.isPlayerMade()) {
            return PLAYER_HARVEST_HARDNESS;
        }
        return super.getBlockHardness(state, world, pos);
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(IBlockReader worldIn) {
        return new TileCollectorCrystal();
    }

    @Override
    public BlockRenderType getRenderType(BlockState p_149645_1_) {
        return BlockRenderType.MODEL;
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, @Nullable LivingEntity entity, ItemStack stack) {
        TileCollectorCrystal tcc = MiscUtils.getTileAt(world, pos, TileCollectorCrystal.class, true);
        Item i = stack.getItem();
        if (tcc != null && i instanceof ItemBlockCollectorCrystal) {
            ItemBlockCollectorCrystal ibcc = (ItemBlockCollectorCrystal) i;
            UUID playerUUID = null;
            if (entity instanceof PlayerEntity) {
                playerUUID = entity.getUniqueID();
            }

            tcc.updateData(playerUUID, ibcc.getCollectorType());
        }

        super.onBlockPlacedBy(world, pos, state, entity, stack);
    }
}
