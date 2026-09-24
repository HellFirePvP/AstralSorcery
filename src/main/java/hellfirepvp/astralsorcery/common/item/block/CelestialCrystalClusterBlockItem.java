/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.block;

import hellfirepvp.astralsorcery.common.block.tile.CelestialCrystalClusterBlock;
import hellfirepvp.astralsorcery.common.component.CrystalAttributesComponent;
import hellfirepvp.astralsorcery.common.item.base.BlockItemCustom;
import hellfirepvp.astralsorcery.common.lib.DataComponentsAS;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: CelestialCrystalClusterBlockItem
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class CelestialCrystalClusterBlockItem extends BlockItemCustom {

    public CelestialCrystalClusterBlockItem(Block block) {
        super(block, new Properties()
                .component(DataComponentsAS.CRYSTAL_ATTRIBUTES, CrystalAttributesComponent.defaultEmpty()));
    }

    @Override
    public void fillCreativeTab(Consumer<ItemStack> tabItems) {
        getVariants().forEach(tabItems);
    }

    public static List<ItemStack> getVariants() {
        return CelestialCrystalClusterBlock.STAGE.getPossibleValues().stream().map(stage -> {
            ItemStack stack = ItemsAS.BLOCK_CELESTIAL_CRYSTAL_CLUSTER.toStack();
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
        return CelestialCrystalClusterBlock.STAGE.getPossibleValues().size() - 1;
    }

    public static int getStage(ItemStack stack) {
        return stack.getDamageValue();
    }

    public static void setStage(ItemStack stack, int stage) {
        stack.setDamageValue(stage);
    }
}
