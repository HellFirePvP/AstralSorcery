/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import hellfirepvp.astralsorcery.common.crystal.CrystalAttributeGenItem;
import hellfirepvp.astralsorcery.common.crystal.CrystalAttributes;
import hellfirepvp.astralsorcery.common.crystal.CrystalGenerator;
import hellfirepvp.astralsorcery.common.lib.LootAS;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootFunction;
import net.minecraft.loot.LootFunctionType;
import net.minecraft.loot.conditions.ILootCondition;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RandomCrystalProperty
 * Created by HellFirePvP
 * Date: 21.07.2019 / 08:50
 */
public class RandomCrystalProperty extends LootFunction {

    private RandomCrystalProperty(ILootCondition[] conditions) {
        super(conditions);
    }

    @Override
    public LootFunctionType getFunctionType() {
        return LootAS.Functions.RANDOM_CRYSTAL_PROPERTIES;
    }

    @Override
    protected ItemStack doApply(ItemStack itemStack, LootContext lootContext) {
        if (itemStack.getItem() instanceof CrystalAttributeGenItem) {
            CrystalAttributes attr = CrystalGenerator.generateNewAttributes(itemStack);
            ((CrystalAttributeGenItem) itemStack.getItem()).setAttributes(itemStack, attr);
        }
        return itemStack;
    }

    public static LootFunction.Builder<?> builder() {
        return builder(RandomCrystalProperty::new);
    }

    public static class Serializer extends LootFunction.Serializer<RandomCrystalProperty> {

        @Override
        public RandomCrystalProperty deserialize(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, ILootCondition[] iLootConditions) {
            return new RandomCrystalProperty(iLootConditions);
        }
    }
}
