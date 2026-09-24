/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.datagen.data.perks;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.lib.RegistriesAS;
import hellfirepvp.astralsorcery.common.lib.types.PerkTypesAS;
import hellfirepvp.astralsorcery.common.perk.data.builder.PerkDataBuilder;
import hellfirepvp.astralsorcery.common.perk.data.builder.PerkDataProvider;
import hellfirepvp.astralsorcery.common.perk.type.base.ModifierType;
import hellfirepvp.astralsorcery.common.perk.type.base.PerkAttributeType;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: DebugPerkTreeProvider
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class DebugPerkTreeProvider extends PerkDataProvider {

    public DebugPerkTreeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    public void registerPerks(HolderLookup.Provider registries, Consumer<BuiltPerk> registrar) {
        int count = 1;
        for (PerkAttributeType type : RegistriesAS.REGISTRY_PERK_ATTRIBUTE_TYPES) {
            count += 2;

            if (!type.isMultiplicative()) {
                PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                        .create(AstralSorcery.key("attribute_perk_test_add_" + count), count, 1)
                        .addModifier(1, ModifierType.ADDITION, type)
                        .build(registrar);
            }
            PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                    .create(AstralSorcery.key("attribute_perk_test_add_mult_" + count), count, 3)
                    .addModifier(0.2F, ModifierType.ADDED_MULTIPLY, type)
                    .build(registrar);
            PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                    .create(AstralSorcery.key("attribute_perk_test_stack_mult_" + count), count, 5)
                    .addModifier(0.2F, ModifierType.STACKING_MULTIPLY, type)
                    .build(registrar);
        }
    }

    @Override
    public String getName() {
        return "Debug-Perks";
    }
}
