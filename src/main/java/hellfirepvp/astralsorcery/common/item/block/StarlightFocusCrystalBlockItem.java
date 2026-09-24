/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.block;

import hellfirepvp.astralsorcery.common.block.tile.StarlightFocusCrystalBlock;
import hellfirepvp.astralsorcery.common.component.AttunedConstellationComponent;
import hellfirepvp.astralsorcery.common.component.CrystalAttributesComponent;
import hellfirepvp.astralsorcery.common.crystal.CrystalPropertyGenerator;
import hellfirepvp.astralsorcery.common.item.base.BlockItemCustom;
import hellfirepvp.astralsorcery.common.lib.*;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.function.Consumer;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: StarlightFocusCrystalBlockItem
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class StarlightFocusCrystalBlockItem extends BlockItemCustom {

    public StarlightFocusCrystalBlockItem(StarlightFocusCrystalBlock block) {
        super(block, new Properties().stacksTo(1)
                .component(DataComponentsAS.ATTUNED_CONSTELLATION, AttunedConstellationComponent.EMPTY)
                .component(DataComponentsAS.CRYSTAL_ATTRIBUTES, generateEmpty(block.getType())));
    }

    private static CrystalAttributesComponent generateEmpty(StarlightFocusCrystalBlock.Type type) {
        return switch (type) {
            case ROCK_CRYSTAL -> ItemsAS.ROCK_CRYSTAL.asItem().components().get(DataComponentsAS.CRYSTAL_ATTRIBUTES.get());
            case CELESTIAL_CRYSTAL -> ItemsAS.CELESTIAL_CRYSTAL.asItem().components().get(DataComponentsAS.CRYSTAL_ATTRIBUTES.get());
        };
    }

    @Override
    public void fillCreativeTab(Consumer<ItemStack> tabItems) {
        RegistriesAS.REGISTRY_CONSTELLATIONS.forEach(cst -> {
            ItemStack stack = new ItemStack(this);
            stack.set(DataComponentsAS.ATTUNED_CONSTELLATION, new AttunedConstellationComponent(cst));
            stack.set(DataComponentsAS.CRYSTAL_ATTRIBUTES, CrystalAttributesComponent.defaultEmpty()
                    .setAttributeTier(CrystalPropertiesAS.SIZE, CrystalPropertiesAS.SIZE.get().getMaxTier())
                    .setAttributeTier(CrystalPropertiesAS.CUT, CrystalPropertiesAS.CUT.get().getMaxTier()));
            tabItems.accept(stack);
        });
    }

    @Override
    public CreativeModeTab getCreativeTab() {
        return CreativeTabsAS.CREATIVE_TAB_AS_ATTUNED_CRYSTALS.get();
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (!level.isClientSide()) {
            CrystalAttributesComponent cmp = stack.getOrDefault(DataComponentsAS.CRYSTAL_ATTRIBUTES, CrystalAttributesComponent.defaultEmpty());
            if (cmp.isEmpty()) {
                stack.set(DataComponentsAS.CRYSTAL_ATTRIBUTES, CrystalPropertyGenerator.generateRandomProperties(cmp));
            }
        }
    }
}
