/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.block;

import hellfirepvp.astralsorcery.common.component.CrystalAttributesComponent;
import hellfirepvp.astralsorcery.common.item.base.BlockItemCustom;
import hellfirepvp.astralsorcery.common.lib.CrystalPropertiesAS;
import hellfirepvp.astralsorcery.common.lib.DataComponentsAS;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

import java.util.function.Consumer;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: LensBlockItem
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class LensBlockItem extends BlockItemCustom {

    public LensBlockItem(Block block) {
        super(block, new Properties()
                .component(DataComponentsAS.CRYSTAL_ATTRIBUTES, CrystalAttributesComponent.defaultEmpty()));
    }

    @Override
    public void fillCreativeTab(Consumer<ItemStack> tabItems) {
        ItemStack stack = new ItemStack(this);
        stack.set(DataComponentsAS.CRYSTAL_ATTRIBUTES, CrystalAttributesComponent.defaultEmpty()
                .setAttributeTier(CrystalPropertiesAS.PURITY, CrystalPropertiesAS.PURITY.get().getMaxTier()));
        tabItems.accept(stack);
    }
}
