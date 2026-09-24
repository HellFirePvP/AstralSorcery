/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.block;

import hellfirepvp.astralsorcery.common.block.tile.CelestialCrystalClusterBlock;
import hellfirepvp.astralsorcery.common.block.tile.GemCrystalClusterBlock;
import hellfirepvp.astralsorcery.common.item.base.BlockItemCustom;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import hellfirepvp.astralsorcery.common.util.MiscUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

import java.util.List;
import java.util.function.Consumer;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: GemCrystalClusterBlockItem
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class GemCrystalClusterBlockItem extends BlockItemCustom {

    public GemCrystalClusterBlockItem(Block block) {
        super(block, new Properties());
    }

    @Override
    public void fillCreativeTab(Consumer<ItemStack> tabItems) {
        getVariants().forEach(tabItems);
    }

    public static List<ItemStack> getVariants() {
        return GemCrystalClusterBlock.STAGE.getPossibleValues().stream().map(stage -> {
            ItemStack stack = ItemsAS.BLOCK_GEM_CRYSTAL_CLUSTER.toStack();
            setStage(stack, stage);
            return stack;
        }).toList();
    }

    @Override
    public boolean isDamageable(ItemStack stack) {
        return false;
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        return GemCrystalClusterBlock.STAGE.getPossibleValues().size() - 1;
    }

    public static GemCrystalClusterBlock.GrowthStageType getStage(ItemStack stack) {
        return MiscUtil.getEnumEntry(GemCrystalClusterBlock.GrowthStageType.class, stack.getDamageValue());
    }

    public static void setStage(ItemStack stack, GemCrystalClusterBlock.GrowthStageType stage) {
        stack.setDamageValue(stage.ordinal());
    }
}
