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
import hellfirepvp.astralsorcery.common.component.DynamicModifiersComponent;
import hellfirepvp.astralsorcery.common.lib.DataComponentsAS;
import hellfirepvp.astralsorcery.common.lib.LootAS;
import hellfirepvp.astralsorcery.common.perk.DynamismGemModifierHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: GenerateDynamismGemRollsFunction
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class GenerateDynamismGemRollsFunction extends LootItemConditionalFunction {

    public static final MapCodec<GenerateDynamismGemRollsFunction> CODEC = RecordCodecBuilder.mapCodec(inst ->
            commonFields(inst).apply(inst, GenerateDynamismGemRollsFunction::new));

    protected GenerateDynamismGemRollsFunction(List<LootItemCondition> predicates) {
        super(predicates);
    }

    public static LootItemConditionalFunction.Builder<?> randomProperties() {
        return simpleBuilder(GenerateDynamismGemRollsFunction::new);
    }

    @Override
    protected ItemStack run(ItemStack stack, LootContext context) {
        if (stack.getOrDefault(DataComponentsAS.DYNAMIC_MODIFIERS, DynamicModifiersComponent.EMPTY).modifiers().isEmpty()) {
            DynamismGemModifierHelper.rollGem(stack);
        }
        return stack;
    }

    @Override
    public LootItemFunctionType<? extends LootItemConditionalFunction> getType() {
        return LootAS.GENERATE_DYNAMISM_GEM_ROLLS_FUNCTION.get();
    }
}
