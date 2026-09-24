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
import hellfirepvp.astralsorcery.common.component.ArtifactTypeComponent;
import hellfirepvp.astralsorcery.common.lib.DataComponentsAS;
import hellfirepvp.astralsorcery.common.lib.LootAS;
import hellfirepvp.astralsorcery.common.lib.RegistriesAS;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: GenerateRandomArtifactTypeFunction
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class GenerateRandomArtifactTypeFunction extends LootItemConditionalFunction {

    public static final MapCodec<GenerateRandomArtifactTypeFunction> CODEC = RecordCodecBuilder.mapCodec(inst ->
            commonFields(inst).apply(inst, GenerateRandomArtifactTypeFunction::new));

    protected GenerateRandomArtifactTypeFunction(List<LootItemCondition> predicates) {
        super(predicates);
    }

    public static LootItemConditionalFunction.Builder<?> randomArtifactType() {
        return simpleBuilder(GenerateRandomArtifactTypeFunction::new);
    }

    @Override
    protected ItemStack run(ItemStack stack, LootContext context) {
        RegistriesAS.REGISTRY_ARTIFACT_TYPES.getRandom(context.getRandom()).ifPresent(type -> {
            stack.set(DataComponentsAS.ARTIFACT_TYPE, new ArtifactTypeComponent(type.value()));
        });
        return stack;
    }

    @Override
    public LootItemFunctionType<? extends LootItemConditionalFunction> getType() {
        return LootAS.GENERATE_RANDOM_ARTIFACT_TYPE_FUNCTION.get();
    }
}
