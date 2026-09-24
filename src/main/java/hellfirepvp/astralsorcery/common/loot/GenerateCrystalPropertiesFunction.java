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
import hellfirepvp.astralsorcery.common.component.CrystalAttributesComponent;
import hellfirepvp.astralsorcery.common.crystal.CrystalPropertyGenerator;
import hellfirepvp.astralsorcery.common.lib.DataComponentsAS;
import hellfirepvp.astralsorcery.common.lib.LootAS;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: GenerateCrystalPropertiesFunction
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class GenerateCrystalPropertiesFunction extends LootItemConditionalFunction {

    public static final MapCodec<GenerateCrystalPropertiesFunction> CODEC = RecordCodecBuilder.mapCodec(inst ->
            commonFields(inst).apply(inst, GenerateCrystalPropertiesFunction::new));

    protected GenerateCrystalPropertiesFunction(List<LootItemCondition> predicates) {
        super(predicates);
    }

    public static LootItemConditionalFunction.Builder<?> randomProperties() {
        return simpleBuilder(GenerateCrystalPropertiesFunction::new);
    }

    @Override
    protected ItemStack run(ItemStack stack, LootContext context) {
        if (stack.has(DataComponentsAS.CRYSTAL_ATTRIBUTES)) {
            CrystalAttributesComponent attributes = stack.get(DataComponentsAS.CRYSTAL_ATTRIBUTES);
            if (attributes != null) {
                attributes = CrystalPropertyGenerator.generateRandomProperties(attributes);
                stack.set(DataComponentsAS.CRYSTAL_ATTRIBUTES, attributes);
            }
        }
        return stack;
    }

    @Override
    public LootItemFunctionType<? extends LootItemConditionalFunction> getType() {
        return LootAS.GENERATE_CRYSTAL_PROPERTIES_FUNCTION.get();
    }
}
