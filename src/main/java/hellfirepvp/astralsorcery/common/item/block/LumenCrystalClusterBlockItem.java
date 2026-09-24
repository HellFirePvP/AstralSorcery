/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.block;

import hellfirepvp.astralsorcery.common.block.tile.LumenCrystalClusterBlock;
import hellfirepvp.astralsorcery.common.component.LumenComponent;
import hellfirepvp.astralsorcery.common.item.base.BlockItemCustom;
import hellfirepvp.astralsorcery.common.item.base.ItemDynamicColor;
import hellfirepvp.astralsorcery.common.lib.*;
import hellfirepvp.astralsorcery.common.lumen.Lumen;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

import java.util.List;
import java.util.function.Consumer;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: LumenCrystalClusterBlockItem
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class LumenCrystalClusterBlockItem extends BlockItemCustom implements ItemDynamicColor {

    public LumenCrystalClusterBlockItem(Block block) {
        super(block, new Properties()
                .component(DataComponentsAS.LUMEN, LumenComponent.EMPTY));
    }

    @Override
    public CreativeModeTab getCreativeTab() {
        return CreativeTabsAS.CREATIVE_TAB_AS_LUMEN.get();
    }

    @Override
    public void fillCreativeTab(Consumer<ItemStack> tabItems) {
        RegistriesAS.REGISTRY_LUMEN.holders().forEach(lumenRef -> {
            if (lumenRef.is(LumenAS.NONE)) return;
            tabItems.accept(getCluster(lumenRef, 4));
        });
    }

    public static List<ItemStack> getStageVariants() {
        return LumenCrystalClusterBlock.STAGE.getPossibleValues().stream().map(stage -> {
            ItemStack stack = ItemsAS.BLOCK_LUMEN_CRYSTAL_CLUSTER.toStack();
            setStage(stack, stage);
            return stack;
        }).toList();
    }

    public static ItemStack getCluster(Holder<Lumen> lumen, int stage) {
        ItemStack stack = ItemsAS.BLOCK_LUMEN_CRYSTAL_CLUSTER.toStack();
        setStage(stack, stage);
        stack.set(DataComponentsAS.LUMEN, new LumenComponent(lumen));
        return stack;
    }

    @Override
    public Component getName(ItemStack stack) {
        if (stack.has(DataComponentsAS.LUMEN)) {
            Lumen lumen = stack.getOrDefault(DataComponentsAS.LUMEN, LumenComponent.EMPTY).lumen().value();
            if (lumen != LumenAS.NONE.get()) {
                Component lumenName = lumen.getName();
                return Component.translatable("block.astralsorcery.lumen_crystal_cluster.typed", lumenName);
            }
        }
        return super.getName(stack);
    }

    @Override
    public boolean isDamageable(ItemStack stack) {
        return false;
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        return LumenCrystalClusterBlock.STAGE.getPossibleValues().size() - 1;
    }

    public static int getStage(ItemStack stack) {
        return stack.getDamageValue();
    }

    public static void setStage(ItemStack stack, int stage) {
        stack.setDamageValue(stage);
    }

    @Override
    public int getColor(ItemStack stack, long tick, int tintIndex) {
        //tintIndex == 1 &&
        if (stack.has(DataComponentsAS.LUMEN)) {
            return stack.getOrDefault(DataComponentsAS.LUMEN, LumenComponent.EMPTY).lumen().value().getColor(tick).getColor();
        }
        return 0xFFFFFF;
    }
}
