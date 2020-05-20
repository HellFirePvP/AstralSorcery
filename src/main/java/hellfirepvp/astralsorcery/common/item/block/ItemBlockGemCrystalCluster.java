/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.block;

import hellfirepvp.astralsorcery.common.block.tile.BlockGemCrystalCluster;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemBlockGemCrystalCluster
 * Created by HellFirePvP
 * Date: 17.05.2020 / 09:26
 */
public class ItemBlockGemCrystalCluster extends ItemBlockCustom {

    public ItemBlockGemCrystalCluster(Block block, Properties itemProperties) {
        super(block, itemProperties);
        this.addPropertyOverride(new ResourceLocation("stage"),
                (stack, world, entity) -> ((float) stack.getDamage()) / BlockGemCrystalCluster.STAGE.getAllowedValues().size());
    }

    @Override
    public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
        if (isInGroup(group)) {
            for (BlockGemCrystalCluster.GrowthStageType stage : BlockGemCrystalCluster.STAGE.getAllowedValues()) {
                ItemStack cluster = new ItemStack(this);
                this.setDamage(cluster, stage.ordinal());
                items.add(cluster);
            }
        }
    }

    @Nullable
    @Override
    protected BlockState getStateForPlacement(BlockItemUseContext context) {
        BlockState toPlace = super.getStateForPlacement(context);
        if (toPlace != null) {
            return toPlace.with(BlockGemCrystalCluster.STAGE, this.getGrowthStage(context.getItem()));
        }
        return null;
    }

    @Nonnull
    private BlockGemCrystalCluster.GrowthStageType getGrowthStage(ItemStack stack) {
        if (stack.isEmpty() || !(stack.getItem() instanceof ItemBlockGemCrystalCluster)) {
            return BlockGemCrystalCluster.GrowthStageType.STAGE_0;
        }
        return MiscUtils.getEnumEntry(BlockGemCrystalCluster.GrowthStageType.class, this.getDamage(stack));
    }

    @Override
    public String getTranslationKey(ItemStack stack) {
        BlockGemCrystalCluster.GrowthStageType stage = this.getGrowthStage(stack);
        switch (stage) {
            case STAGE_2_SKY:
                return super.getTranslationKey(stack) + ".sky";
            case STAGE_2_DAY:
                return super.getTranslationKey(stack) + ".day";
            case STAGE_2_NIGHT:
                return super.getTranslationKey(stack) + ".night";
        }
        return super.getTranslationKey(stack);
    }
}
