/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.loot;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Effects;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootFunction;
import net.minecraft.world.storage.loot.LootParameter;
import net.minecraft.world.storage.loot.LootParameters;
import net.minecraft.world.storage.loot.conditions.ILootCondition;

import java.util.Random;
import java.util.Set;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: LinearLuckBonus
 * Created by HellFirePvP
 * Date: 20.07.2019 / 22:07
 */
public class LinearLuckBonus extends LootFunction {

    private LinearLuckBonus(ILootCondition[] lootConditions) {
        super(lootConditions);
    }

    @Override
    public Set<LootParameter<?>> getRequiredParameters() {
        return ImmutableSet.of(LootParameters.TOOL);
    }

    @Override
    protected ItemStack doApply(ItemStack itemStack, LootContext lootContext) {
        ItemStack tool = lootContext.get(LootParameters.TOOL);
        if (tool != null) {
            int luck = 0;
            Entity e = lootContext.get(LootParameters.THIS_ENTITY);
            if (e instanceof PlayerEntity && ((PlayerEntity) e).isPotionActive(Effects.LUCK)) {
                luck += ((PlayerEntity) e).getActivePotionEffect(Effects.LUCK).getAmplifier() + 1;
            }
            luck += EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, tool);
            luck += EnchantmentHelper.getEnchantmentLevel(Enchantments.LOOTING, tool);

            Random rand = lootContext.getRandom();
            int size = 0;
            for (int i = 0; i < luck; i++) {
                size += rand.nextInt(3) + 1;
            }
            itemStack.setCount(itemStack.getCount() + size);
        }
        return itemStack;
    }

    public static LootFunction.Builder<?> builder() {
        return builder(LinearLuckBonus::new);
    }

    public static class Serializer extends LootFunction.Serializer<LinearLuckBonus> {

        public Serializer(ResourceLocation name) {
            super(name, LinearLuckBonus.class);
        }

        @Override
        public LinearLuckBonus deserialize(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, ILootCondition[] iLootConditions) {
            return new LinearLuckBonus(iLootConditions);
        }
    }

}
