/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.crystal;

import hellfirepvp.astralsorcery.common.component.CrystalAttributesComponent;
import hellfirepvp.astralsorcery.common.crystal.CrystalPropertyGenerator;
import hellfirepvp.astralsorcery.common.entity.ItemEntityReplacement;
import hellfirepvp.astralsorcery.common.item.base.AttuneableItem;
import hellfirepvp.astralsorcery.common.item.base.ItemCustom;
import hellfirepvp.astralsorcery.common.lib.DataComponentsAS;
import hellfirepvp.astralsorcery.common.lib.EntitiesAS;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import hellfirepvp.astralsorcery.common.lib.constants.ColorsAS;
import hellfirepvp.astralsorcery.common.util.data.ColorWrapper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: RockCrystalItem
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class RockCrystalItem extends ItemCustom implements AttuneableItem {

    public RockCrystalItem() {
        this(CrystalAttributesComponent.empty(4, 9));
    }

    protected RockCrystalItem(CrystalAttributesComponent emptyComponent) {
        super(new Properties().stacksTo(1).component(DataComponentsAS.CRYSTAL_ATTRIBUTES, emptyComponent));
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

    @Override
    public boolean hasCustomEntity(ItemStack stack) {
        return true;
    }

    @Override
    @Nullable
    public Entity createEntity(Level level, Entity location, ItemStack stack) {
        if (location instanceof ItemEntity itemEntity) {
            return ItemEntityReplacement.replace(EntitiesAS.ITEM_CRYSTAL.get(), itemEntity)
                    .setColor(this.getItemEntityColor(stack));
        }
        return super.createEntity(level, location, stack);
    }

    @Nullable
    public ColorWrapper getItemEntityColor(ItemStack stack) {
        return ColorsAS.ROCK_CRYSTAL;
    }

    @Nonnull
    public RockCrystalItem getCrystalSplitItem() {
        return this;
    }

    @Override
    public Item getAttunedItem() {
        return ItemsAS.ATTUNED_ROCK_CRYSTAL.get();
    }
}
