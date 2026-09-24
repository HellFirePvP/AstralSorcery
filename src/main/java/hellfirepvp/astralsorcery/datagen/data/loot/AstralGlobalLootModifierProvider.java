/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.datagen.data.loot;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.lib.EnchantmentsAS;
import hellfirepvp.astralsorcery.common.loot.global.SmeltLootFunction;
import hellfirepvp.astralsorcery.common.loot.global.TeleportDropsFunction;
import net.minecraft.advancements.critereon.*;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.storage.loot.predicates.MatchTool;
import net.neoforged.neoforge.common.data.GlobalLootModifierProvider;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: AstralGlobalLootModifierProvider
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class AstralGlobalLootModifierProvider extends GlobalLootModifierProvider {

    public AstralGlobalLootModifierProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, AstralSorcery.MODID);
    }

    @Override
    protected void start() {
        HolderLookup.RegistryLookup<Enchantment> enchantments = this.registries.lookupOrThrow(Registries.ENCHANTMENT);

        this.add("teleport_drops", TeleportDropsFunction.of());
        this.add("smelt_loot", SmeltLootFunction.of(
                new MatchTool(Optional.of(ItemPredicate.Builder.item()
                        .withSubPredicate(ItemSubPredicates.ENCHANTMENTS,
                                ItemEnchantmentsPredicate.enchantments(List.of(
                                        new EnchantmentPredicate(enchantments.getOrThrow(EnchantmentsAS.SCORCHING_HEAT.id()), MinMaxBounds.Ints.atLeast(1))
                                )))
                        .build()))
        ));
    }
}
