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
import hellfirepvp.astralsorcery.common.component.ArtifactComponent;
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
 * Class: GenerateRandomArtifactFunction
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class GenerateRandomArtifactFunction extends LootItemConditionalFunction {

    public static final MapCodec<GenerateRandomArtifactFunction> CODEC = RecordCodecBuilder.mapCodec(inst ->
            commonFields(inst).apply(inst, GenerateRandomArtifactFunction::new));

    protected GenerateRandomArtifactFunction(List<LootItemCondition> predicates) {
        super(predicates);
    }

    public static LootItemConditionalFunction.Builder<?> randomArtifact() {
        return simpleBuilder(GenerateRandomArtifactFunction::new);
    }

    @Override
    protected ItemStack run(ItemStack stack, LootContext context) {
        RegistriesAS.REGISTRY_ARTIFACT_TYPES.getRandom(context.getRandom()).ifPresent(type -> {
            stack.set(DataComponentsAS.ARTIFACT, ArtifactComponent.initialize(type.value()));
        });
        return stack;
    }

    @Override
    public LootItemFunctionType<? extends LootItemConditionalFunction> getType() {
        return LootAS.GENERATE_RANDOM_ARTIFACT_FUNCTION.get();
    }
}
