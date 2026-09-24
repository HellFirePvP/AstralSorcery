/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.loot;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hellfirepvp.astralsorcery.common.lib.LootAS;
import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.ApplyExplosionDecay;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import java.util.List;
import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: LinearLuckFunction
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class LinearLuckFunction extends LootItemConditionalFunction {

    public static final MapCodec<LinearLuckFunction> CODEC = RecordCodecBuilder.mapCodec(inst ->
            commonFields(inst)
                    .and(Enchantment.CODEC.listOf().fieldOf("enchantments").forGetter(LinearLuckFunction::getEnchantments))
                    .apply(inst, LinearLuckFunction::new));

    private final List<Holder<Enchantment>> enchantments;

    protected LinearLuckFunction(List<LootItemCondition> predicates, List<Holder<Enchantment>> enchantments) {
        super(predicates);
        this.enchantments = enchantments;
    }

    private List<Holder<Enchantment>> getEnchantments() {
        return this.enchantments;
    }

    public static LootItemConditionalFunction.Builder<?> onlyLuck() {
        return simpleBuilder(conditions -> new LinearLuckFunction(conditions, List.of()));
    }

    public static LootItemConditionalFunction.Builder<?> luckAndEnchantments(List<Holder<Enchantment>> enchantments) {
        return simpleBuilder(conditions -> new LinearLuckFunction(conditions, enchantments));
    }

    @Override
    protected ItemStack run(ItemStack stack, LootContext context) {
        ItemStack tool = context.getParamOrNull(LootContextParams.TOOL);
        if (tool != null) {
            int luck = 0;
            Entity entity = context.getParamOrNull(LootContextParams.THIS_ENTITY);
            if (entity instanceof LivingEntity livingEntity && livingEntity.hasEffect(MobEffects.LUCK)) {
                luck = livingEntity.getEffect(MobEffects.LUCK).getAmplifier() + 1;
            }
            for (Holder<Enchantment> enchantmentHolder : this.getEnchantments()) {
                luck += tool.getEnchantmentLevel(enchantmentHolder);
            }

            RandomSource rand = context.getRandom();
            int count = stack.getCount();
            for (int i = 0; i < luck; i++) {
                count += rand.nextInt(3) + 1;
            }
            stack.setCount(count);
        }
        return stack;
    }

    @Override
    public LootItemFunctionType<? extends LootItemConditionalFunction> getType() {
        return LootAS.LINEAR_LUCK_FUNCTION.get();
    }
}
