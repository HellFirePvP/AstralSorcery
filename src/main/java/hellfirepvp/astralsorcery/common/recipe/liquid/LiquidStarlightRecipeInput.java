/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.recipe.liquid;

import hellfirepvp.astralsorcery.common.recipe.CustomRecipeInput;
import hellfirepvp.astralsorcery.common.recipe.liquid.output.LiquidStarlightRecipeOutputModifier;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: LiquidStarlightRecipeInput
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class LiquidStarlightRecipeInput extends CustomRecipeInput {

    private final ItemEntity triggerEntity;
    private final List<ItemEntity> otherEntities = new ArrayList<>();

    private LiquidStarlightRecipeInput(ItemEntity triggerEntity, List<ItemEntity> otherEntities) {
        this.triggerEntity = triggerEntity;
        this.otherEntities.addAll(otherEntities);
    }

    public static LiquidStarlightRecipeInput of(ItemEntity itemEntity) {
        Level level = itemEntity.level();
        List<ItemEntity> items = level.getEntities(EntityTypeTest.forClass(ItemEntity.class), new AABB(itemEntity.blockPosition()), EntitySelector.ENTITY_STILL_ALIVE);
        items.remove(itemEntity);
        return new LiquidStarlightRecipeInput(itemEntity, items);
    }

    public ItemEntity getTriggerEntity() {
        return this.triggerEntity;
    }

    public List<ItemEntity> getOtherEntities() {
        return Collections.unmodifiableList(this.otherEntities);
    }

    public List<ItemEntity> getFilteredOtherEntitiesByOutput(List<LiquidStarlightRecipeOutputModifier> outputModifiers) {
        List<ItemEntity> filteredOtherEntities = new ArrayList<>(this.otherEntities);
        filteredOtherEntities.removeIf(itemEntity -> {
            return outputModifiers.stream().anyMatch(modifier -> modifier.isOutput(this, itemEntity));
        });
        return filteredOtherEntities;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }
}
