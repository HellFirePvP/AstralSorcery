/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import hellfirepvp.astralsorcery.common.util.crystal.CrystalProperties;
import net.minecraft.item.ItemStack;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootFunction;
import net.minecraft.world.storage.loot.conditions.ILootCondition;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RandomCrystalProperty
 * Created by HellFirePvP
 * Date: 21.07.2019 / 08:50
 */
public class RandomCrystalProperty extends LootFunction {

    private final boolean celestial;

    private RandomCrystalProperty(ILootCondition[] conditions, boolean celestial) {
        super(conditions);
        this.celestial = celestial;
    }

    @Override
    protected ItemStack doApply(ItemStack itemStack, LootContext lootContext) {
        CrystalProperties prop;
        if (celestial) {
            prop = CrystalProperties.createRandomCelestial();
        } else {
            prop = CrystalProperties.createRandomRock();
        }
        CrystalProperties.applyCrystalProperties(itemStack, prop);
        return itemStack;
    }

    public static class Serializer extends LootFunction.Serializer<RandomCrystalProperty> {

        public Serializer(ResourceLocation name) {
            super(name, RandomCrystalProperty.class);
        }

        @Override
        public RandomCrystalProperty deserialize(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, ILootCondition[] iLootConditions) {
            boolean celestial = JSONUtils.getBoolean(jsonObject, "celestial");
            return new RandomCrystalProperty(iLootConditions, celestial);
        }
    }
}
