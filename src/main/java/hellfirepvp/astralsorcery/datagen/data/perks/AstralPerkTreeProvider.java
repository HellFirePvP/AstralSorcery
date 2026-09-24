/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.datagen.data.perks;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.enchantment.EnchantmentModifier;
import hellfirepvp.astralsorcery.common.lib.ConstellationsAS;
import hellfirepvp.astralsorcery.common.lib.PerksAS;
import hellfirepvp.astralsorcery.common.lib.types.PerkTypesAS;
import hellfirepvp.astralsorcery.common.perk.data.builder.PerkDataBuilder;
import hellfirepvp.astralsorcery.common.perk.data.builder.PerkDataProvider;
import hellfirepvp.astralsorcery.common.perk.tree.AttributeModifierPerk;
import hellfirepvp.astralsorcery.common.perk.tree.requirement.PerkRequirementConstellation;
import hellfirepvp.astralsorcery.common.perk.tree.requirement.PerkRequirementProgress;
import hellfirepvp.astralsorcery.common.perk.type.base.ModifierType;
import hellfirepvp.astralsorcery.common.research.ResearchTier;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import static hellfirepvp.astralsorcery.AstralSorcery.key;
import static hellfirepvp.astralsorcery.common.lib.PerksAS.AttributeTypes.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: AstralPerkTreeProvider
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class AstralPerkTreeProvider extends PerkDataProvider {

    public AstralPerkTreeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    // whenever importing a new data set:
    // - replace PerkRequirementProgress.of("luminance") with PerkRequirementProgress.of(ResearchTier.LUMINANCE)
    // - add references to epiphany travel nodes
    // - add enchantments to dynamic enchantment nodes

    @Override
    public void registerPerks(HolderLookup.Provider registries, Consumer<BuiltPerk> registrar) {
        var enchLookup = registries.lookupOrThrow(Registries.ENCHANTMENT);
        Holder<Enchantment> fortune = enchLookup.getOrThrow(Enchantments.FORTUNE);
        Holder<Enchantment> unbreaking = enchLookup.getOrThrow(Enchantments.UNBREAKING);
        Holder<Enchantment> infinity = enchLookup.getOrThrow(Enchantments.INFINITY);

// Paste inside AstralPerkTreeProvider.registerPerks(Registrar registrar)

        var rootAevitas = PerkDataBuilder.builder(PerkTypesAS.ROOT_PERK_AEVITAS)
                .create(AstralSorcery.key("root_aevitas"), -30f, 11f)
                .setNameKey("perk.astralsorcery.root_perk_aevitas")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.AEVITAS))
                .addModifier(1.1f, ModifierType.STACKING_MULTIPLY, PerksAS.AttributeTypes.MAX_HEALTH)
                .build(registrar);

        var rootVicio = PerkDataBuilder.builder(PerkTypesAS.ROOT_PERK_VICIO)
                .create(AstralSorcery.key("root_vicio"), 0f, 30f)
                .setNameKey("perk.astralsorcery.root_perk_vicio")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.VICIO))
                .addModifier(1.1f, ModifierType.STACKING_MULTIPLY, PerksAS.AttributeTypes.MOVEMENT_SPEED)
                .build(registrar);

        var rootArmara = PerkDataBuilder.builder(PerkTypesAS.ROOT_PERK_ARMARA)
                .create(AstralSorcery.key("root_armara"), 30f, 11f)
                .setNameKey("perk.astralsorcery.root_perk_armara")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.ARMARA))
                .addModifier(1.15f, ModifierType.STACKING_MULTIPLY, PerksAS.AttributeTypes.ARMOR)
                .build(registrar);

        var rootEvorsio = PerkDataBuilder.builder(PerkTypesAS.ROOT_PERK_EVORSIO)
                .create(AstralSorcery.key("root_evorsio"), -20f, -26f)
                .setNameKey("perk.astralsorcery.root_perk_evorsio")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.EVORSIO))
                .addModifier(2f, ModifierType.ADDITION, PerksAS.AttributeTypes.BLOCK_REACH)
                .build(registrar);

        var rootDiscidia = PerkDataBuilder.builder(PerkTypesAS.ROOT_PERK_DISCIDIA)
                .create(AstralSorcery.key("root_discidia"), 19f, -26f)
                .setNameKey("perk.astralsorcery.root_perk_discidia")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.DISCIDIA))
                .addModifier(0.1f, ModifierType.ADDITION, PerksAS.AttributeTypes.CRITICAL_HIT_CHANCE)
                .build(registrar);

        var travel0 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("travel_0"), 0f, -11f)
                .setNameKey("perk.name.astralsorcery.generic.inc.perk_effect")
                .addModifier(0.02f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.PERK_EFFECT)
                .build(registrar);

        var travel1 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("travel_1"), 4f, -10f)
                .setNameKey("perk.name.astralsorcery.generic.inc.perk_effect")
                .addModifier(0.02f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.PERK_EFFECT)
                .connect(travel0)
                .build(registrar);

        var travel2 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("travel_2"), 7f, -8f)
                .setNameKey("perk.name.astralsorcery.generic.inc.perk_effect")
                .addModifier(0.02f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.PERK_EFFECT)
                .connect(travel1)
                .build(registrar);

        var travel3 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("travel_3"), 10f, -4f)
                .setNameKey("perk.name.astralsorcery.generic.inc.perk_effect")
                .addModifier(0.02f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.PERK_EFFECT)
                .connect(travel2)
                .build(registrar);

        var travel4 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("travel_4"), 10f, 0f)
                .setNameKey("perk.name.astralsorcery.generic.inc.perk_effect")
                .addModifier(0.02f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.PERK_EFFECT)
                .connect(travel3)
                .build(registrar);

        var travel5 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("travel_5"), 9f, 5f)
                .setNameKey("perk.name.astralsorcery.generic.inc.perk_effect")
                .addModifier(0.02f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.PERK_EFFECT)
                .connect(travel4)
                .build(registrar);

        var travel6 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("travel_6"), 6f, 9f)
                .setNameKey("perk.name.astralsorcery.generic.inc.perk_effect")
                .addModifier(0.02f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.PERK_EFFECT)
                .connect(travel5)
                .build(registrar);

        var travel7 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("travel_7"), 3f, 10f)
                .setNameKey("perk.name.astralsorcery.generic.inc.perk_effect")
                .addModifier(0.02f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.PERK_EFFECT)
                .connect(travel6)
                .build(registrar);

        var travel8 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("travel_8"), -2f, 10f)
                .setNameKey("perk.name.astralsorcery.generic.inc.perk_effect")
                .addModifier(0.02f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.PERK_EFFECT)
                .connect(travel7)
                .build(registrar);

        var travel9 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("travel_9"), -6f, 9f)
                .setNameKey("perk.name.astralsorcery.generic.inc.perk_effect")
                .addModifier(0.02f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.PERK_EFFECT)
                .connect(travel8)
                .build(registrar);

        var travel10 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("travel_10"), -9f, 5f)
                .setNameKey("perk.name.astralsorcery.generic.inc.perk_effect")
                .addModifier(0.02f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.PERK_EFFECT)
                .connect(travel9)
                .build(registrar);

        var travel11 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("travel_11"), -10f, 0f)
                .setNameKey("perk.name.astralsorcery.generic.inc.perk_effect")
                .addModifier(0.02f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.PERK_EFFECT)
                .connect(travel10)
                .build(registrar);

        var travel12 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("travel_12"), -10f, -4f)
                .setNameKey("perk.name.astralsorcery.generic.inc.perk_effect")
                .addModifier(0.02f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.PERK_EFFECT)
                .connect(travel11)
                .build(registrar);

        var travel13 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("travel_13"), -8f, -7f)
                .setNameKey("perk.name.astralsorcery.generic.inc.perk_effect")
                .addModifier(0.02f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.PERK_EFFECT)
                .connect(travel12)
                .build(registrar);

        var travel14 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("travel_14"), -4f, -10f)
                .setNameKey("perk.name.astralsorcery.generic.inc.perk_effect")
                .addModifier(0.02f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.PERK_EFFECT)
                .connect(travel13)
                .connect(travel0)
                .build(registrar);

        var travel15 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("travel_15"), 0f, -30f)
                .setNameKey("perk.name.astralsorcery.generic.inc.perk_effect")
                .addModifier(0.02f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.PERK_EFFECT)
                .build(registrar);

        var travel16 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("travel_16"), 7f, -31f)
                .setNameKey("perk.name.astralsorcery.generic.inc.perk_effect")
                .addModifier(0.02f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.PERK_EFFECT)
                .connect(travel15)
                .build(registrar);

        var travel17 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("travel_17"), 14f, -30f)
                .setNameKey("perk.name.astralsorcery.generic.inc.perk_effect")
                .addModifier(0.02f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.PERK_EFFECT)
                .connect(travel16)
                .build(registrar);

        var travel18 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("travel_18"), 22f, -29f)
                .setNameKey("perk.name.astralsorcery.generic.inc.perk_effect")
                .addModifier(0.02f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.PERK_EFFECT)
                .connect(travel17)
                .build(registrar);

        var travel19 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("travel_19"), 25f, -24f)
                .setNameKey("perk.name.astralsorcery.generic.inc.perk_effect")
                .addModifier(0.02f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.PERK_EFFECT)
                .connect(travel18)
                .build(registrar);

        var travel20 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("travel_20"), 29f, -18f)
                .setNameKey("perk.name.astralsorcery.generic.inc.perk_effect")
                .addModifier(0.02f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.PERK_EFFECT)
                .connect(travel19)
                .build(registrar);

        var travel21 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("travel_21"), 31f, -12f)
                .setNameKey("perk.name.astralsorcery.generic.inc.perk_effect")
                .addModifier(0.02f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.PERK_EFFECT)
                .connect(travel20)
                .build(registrar);

        var travel22 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("travel_22"), 34f, -5f)
                .setNameKey("perk.name.astralsorcery.generic.inc.perk_effect")
                .addModifier(0.02f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.PERK_EFFECT)
                .connect(travel21)
                .build(registrar);

        var travel23 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("travel_23"), 33f, 4f)
                .setNameKey("perk.name.astralsorcery.generic.inc.perk_effect")
                .addModifier(0.02f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.PERK_EFFECT)
                .connect(travel22)
                .build(registrar);

        var travel24 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("travel_24"), 34f, 13f)
                .setNameKey("perk.name.astralsorcery.generic.inc.perk_effect")
                .addModifier(0.02f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.PERK_EFFECT)
                .connect(travel23)
                .build(registrar);

        var travel25 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("travel_25"), 29f, 19f)
                .setNameKey("perk.name.astralsorcery.generic.inc.perk_effect")
                .addModifier(0.02f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.PERK_EFFECT)
                .connect(travel24)
                .build(registrar);

        var travel26 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("travel_26"), 21f, 23f)
                .setNameKey("perk.name.astralsorcery.generic.inc.perk_effect")
                .addModifier(0.02f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.PERK_EFFECT)
                .connect(travel25)
                .build(registrar);

        var travel27 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("travel_27"), 15f, 28f)
                .setNameKey("perk.name.astralsorcery.generic.inc.perk_effect")
                .addModifier(0.02f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.PERK_EFFECT)
                .connect(travel26)
                .build(registrar);

        var travel28 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("travel_28"), 12f, 31f)
                .setNameKey("perk.name.astralsorcery.generic.inc.perk_effect")
                .addModifier(0.02f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.PERK_EFFECT)
                .connect(travel27)
                .build(registrar);

        var travel29 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("travel_29"), 6f, 33f)
                .setNameKey("perk.name.astralsorcery.generic.inc.perk_effect")
                .addModifier(0.02f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.PERK_EFFECT)
                .connect(travel28)
                .build(registrar);

        var travel30 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("travel_30"), 1f, 34f)
                .setNameKey("perk.name.astralsorcery.generic.inc.perk_effect")
                .addModifier(0.02f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.PERK_EFFECT)
                .connect(travel29)
                .build(registrar);

        var travel31 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("travel_31"), -5f, 33f)
                .setNameKey("perk.name.astralsorcery.generic.inc.perk_effect")
                .addModifier(0.02f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.PERK_EFFECT)
                .connect(travel30)
                .build(registrar);

        var travel32 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("travel_32"), -10f, 31f)
                .setNameKey("perk.name.astralsorcery.generic.inc.perk_effect")
                .addModifier(0.02f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.PERK_EFFECT)
                .connect(travel31)
                .build(registrar);

        var travel33 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("travel_33"), -15f, 28f)
                .setNameKey("perk.name.astralsorcery.generic.inc.perk_effect")
                .addModifier(0.02f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.PERK_EFFECT)
                .connect(travel32)
                .build(registrar);

        var travel34 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("travel_34"), -20f, 24f)
                .setNameKey("perk.name.astralsorcery.generic.inc.perk_effect")
                .addModifier(0.02f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.PERK_EFFECT)
                .connect(travel33)
                .build(registrar);

        var travel35 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("travel_35"), -27f, 19f)
                .setNameKey("perk.name.astralsorcery.generic.inc.perk_effect")
                .addModifier(0.02f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.PERK_EFFECT)
                .connect(travel34)
                .build(registrar);

        var travel36 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("travel_36"), -34f, 13f)
                .setNameKey("perk.name.astralsorcery.generic.inc.perk_effect")
                .addModifier(0.02f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.PERK_EFFECT)
                .connect(travel35)
                .build(registrar);

        var travel37 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("travel_37"), -35f, 4f)
                .setNameKey("perk.name.astralsorcery.generic.inc.perk_effect")
                .addModifier(0.02f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.PERK_EFFECT)
                .connect(travel36)
                .build(registrar);

        var travel38 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("travel_38"), -34f, -6f)
                .setNameKey("perk.name.astralsorcery.generic.inc.perk_effect")
                .addModifier(0.02f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.PERK_EFFECT)
                .connect(travel37)
                .build(registrar);

        var travel39 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("travel_39"), -31f, -12f)
                .setNameKey("perk.name.astralsorcery.generic.inc.perk_effect")
                .addModifier(0.02f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.PERK_EFFECT)
                .connect(travel38)
                .build(registrar);

        var travel40 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("travel_40"), -28f, -17f)
                .setNameKey("perk.name.astralsorcery.generic.inc.perk_effect")
                .addModifier(0.02f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.PERK_EFFECT)
                .connect(travel39)
                .build(registrar);

        var travel41 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("travel_41"), -25f, -24f)
                .setNameKey("perk.name.astralsorcery.generic.inc.perk_effect")
                .addModifier(0.02f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.PERK_EFFECT)
                .connect(travel40)
                .build(registrar);

        var travel42 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("travel_42"), -21f, -29f)
                .setNameKey("perk.name.astralsorcery.generic.inc.perk_effect")
                .addModifier(0.02f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.PERK_EFFECT)
                .connect(travel41)
                .build(registrar);

        var travel43 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("travel_43"), -14f, -30f)
                .setNameKey("perk.name.astralsorcery.generic.inc.perk_effect")
                .addModifier(0.02f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.PERK_EFFECT)
                .connect(travel42)
                .build(registrar);

        var travel44 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("travel_44"), -7f, -29f)
                .setNameKey("perk.name.astralsorcery.generic.inc.perk_effect")
                .addModifier(0.02f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.PERK_EFFECT)
                .connect(travel43)
                .connect(travel15)
                .build(registrar);

        var travelBranch0 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("travel_branch_0"), -1f, -24f)
                .setNameKey("perk.name.astralsorcery.generic.inc.perk_experience")
                .addModifier(0.04f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.PERK_EXPERIENCE)
                .connect(travel15)
                .build(registrar);

        var travelBranch3 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("travel_branch_3"), -1f, -17f)
                .setNameKey("perk.name.astralsorcery.generic.inc.perk_experience")
                .addModifier(0.04f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.PERK_EXPERIENCE)
                .connect(travel0)
                .build(registrar);

        var travelBranch1 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("travel_branch_1"), 1f, -26f)
                .setNameKey("perk.name.astralsorcery.generic.inc.perk_effect")
                .addModifier(0.03f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.PERK_EFFECT)
                .connect(travel15)
                .build(registrar);

        var travelBranch2 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("travel_branch_2"), 0f, -20f)
                .setNameKey("perk.name.astralsorcery.generic.inc.perk_effect")
                .addModifier(0.02f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.PERK_EFFECT)
                .connect(travelBranch0)
                .connect(travelBranch1)
                .connect(travelBranch3)
                .build(registrar);

        var travelBranch4 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("travel_branch_4"), 1f, -15f)
                .setNameKey("perk.name.astralsorcery.generic.inc.perk_effect")
                .addModifier(0.03f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.PERK_EFFECT)
                .connect(travelBranch2)
                .connect(travel0)
                .build(registrar);

        var travelBranch5 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("travel_branch_5"), 24f, -11f)
                .setNameKey("perk.name.astralsorcery.generic.inc.perk_experience")
                .addModifier(0.04f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.PERK_EXPERIENCE)
                .connect(travel21)
                .build(registrar);

        var travelBranch6 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("travel_branch_6"), 26f, -9f)
                .setNameKey("perk.name.astralsorcery.generic.inc.perk_effect")
                .addModifier(0.03f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.PERK_EFFECT)
                .connect(travel21)
                .build(registrar);

        var travelBranch8 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("travel_branch_8"), 13f, -7f)
                .setNameKey("perk.name.astralsorcery.generic.inc.perk_experience")
                .addModifier(0.04f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.PERK_EXPERIENCE)
                .connect(travel3)
                .build(registrar);

        var travelBranch7 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("travel_branch_7"), 19f, -8f)
                .setNameKey("perk.name.astralsorcery.generic.inc.perk_effect")
                .addModifier(0.02f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.PERK_EFFECT)
                .connect(travelBranch5)
                .connect(travelBranch6)
                .connect(travelBranch8)
                .build(registrar);

        var travelBranch9 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("travel_branch_9"), 15f, -5f)
                .setNameKey("perk.name.astralsorcery.generic.inc.perk_effect")
                .addModifier(0.03f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.PERK_EFFECT)
                .connect(travel3)
                .connect(travelBranch7)
                .build(registrar);

        var travelBranch10 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("travel_branch_10"), 14f, 22f)
                .setNameKey("perk.name.astralsorcery.generic.inc.perk_experience")
                .addModifier(0.04f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.PERK_EXPERIENCE)
                .connect(travel27)
                .build(registrar);

        var travelBranch11 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("travel_branch_11"), 12f, 24f)
                .setNameKey("perk.name.astralsorcery.generic.inc.perk_effect")
                .addModifier(0.03f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.PERK_EFFECT)
                .connect(travel27)
                .build(registrar);

        var travelBranch12 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("travel_branch_12"), 10f, 18f)
                .setNameKey("perk.name.astralsorcery.generic.inc.perk_effect")
                .addModifier(0.02f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.PERK_EFFECT)
                .connect(travelBranch10)
                .connect(travelBranch11)
                .build(registrar);

        var travelBranch13 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("travel_branch_13"), 9f, 12f)
                .setNameKey("perk.name.astralsorcery.generic.inc.perk_experience")
                .addModifier(0.04f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.PERK_EXPERIENCE)
                .connect(travelBranch12)
                .connect(travel6)
                .build(registrar);

        var travelBranch14 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("travel_branch_14"), 7f, 14f)
                .setNameKey("perk.name.astralsorcery.generic.inc.perk_effect")
                .addModifier(0.03f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.PERK_EFFECT)
                .connect(travelBranch12)
                .connect(travel6)
                .build(registrar);

        var travelBranch15 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("travel_branch_15"), -12f, 24f)
                .setNameKey("perk.name.astralsorcery.generic.inc.perk_experience")
                .addModifier(0.04f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.PERK_EXPERIENCE)
                .connect(travel33)
                .build(registrar);

        var travelBranch16 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("travel_branch_16"), -14f, 22f)
                .setNameKey("perk.name.astralsorcery.generic.inc.perk_effect")
                .addModifier(0.03f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.PERK_EFFECT)
                .connect(travel33)
                .build(registrar);

        var travelBranch17 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("travel_branch_17"), -10f, 18f)
                .setNameKey("perk.name.astralsorcery.generic.inc.perk_effect")
                .addModifier(0.02f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.PERK_EFFECT)
                .connect(travelBranch16)
                .connect(travelBranch15)
                .build(registrar);

        var travelBranch18 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("travel_branch_18"), -7f, 14f)
                .setNameKey("perk.name.astralsorcery.generic.inc.perk_experience")
                .addModifier(0.04f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.PERK_EXPERIENCE)
                .connect(travelBranch17)
                .connect(travel9)
                .build(registrar);

        var travelBranch19 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("travel_branch_19"), -9f, 12f)
                .setNameKey("perk.name.astralsorcery.generic.inc.perk_effect")
                .addModifier(0.03f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.PERK_EFFECT)
                .connect(travelBranch17)
                .connect(travel9)
                .build(registrar);

        var travelBranch20 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("travel_branch_20"), -26f, -9f)
                .setNameKey("perk.name.astralsorcery.generic.inc.perk_experience")
                .addModifier(0.04f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.PERK_EXPERIENCE)
                .connect(travel39)
                .build(registrar);

        var travelBranch21 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("travel_branch_21"), -24f, -11f)
                .setNameKey("perk.name.astralsorcery.generic.inc.perk_effect")
                .addModifier(0.03f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.PERK_EFFECT)
                .connect(travel39)
                .build(registrar);

        var travelBranch22 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("travel_branch_22"), -19f, -8f)
                .setNameKey("perk.name.astralsorcery.generic.inc.perk_effect")
                .addModifier(0.02f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.PERK_EFFECT)
                .connect(travelBranch20)
                .connect(travelBranch21)
                .build(registrar);

        var travelBranch23 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("travel_branch_23"), -15f, -5f)
                .setNameKey("perk.name.astralsorcery.generic.inc.perk_experience")
                .addModifier(0.04f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.PERK_EXPERIENCE)
                .connect(travel12)
                .connect(travelBranch22)
                .build(registrar);

        var travelBranch24 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("travel_branch_24"), -13f, -7f)
                .setNameKey("perk.name.astralsorcery.generic.inc.perk_effect")
                .addModifier(0.03f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.PERK_EFFECT)
                .connect(travel12)
                .connect(travelBranch22)
                .build(registrar);

        var connectorN0 = PerkDataBuilder.builder(PerkTypesAS.KEY_TREE_CONNECTOR_DELEGATE)
                .create(AstralSorcery.key("connector_n_0"), -1f, -33f)
                .setNameKey("perk.name.astralsorcery.named.connector_n.delegate")
                .addModifier(0.02f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.PERK_EFFECT)
                .addModifier(0.02f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.PERK_EXPERIENCE)
                .modify(perk -> perk.setDelegateKey(AstralSorcery.key("tree_connector_n")))
                .connect(travel15)
                .build(registrar);

        var connectorN1 = PerkDataBuilder.builder(PerkTypesAS.KEY_TREE_CONNECTOR_DELEGATE)
                .create(AstralSorcery.key("connector_n_1"), -4f, -38f)
                .setNameKey("perk.name.astralsorcery.named.connector_n.delegate")
                .addModifier(0.02f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.PERK_EFFECT)
                .addModifier(0.02f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.PERK_EXPERIENCE)
                .modify(perk -> perk.setDelegateKey(AstralSorcery.key("tree_connector_n")))
                .connect(connectorN0)
                .build(registrar);

        var connectorN2 = PerkDataBuilder.builder(PerkTypesAS.KEY_TREE_CONNECTOR_DELEGATE)
                .create(AstralSorcery.key("connector_n_2"), 2f, -38f)
                .setNameKey("perk.name.astralsorcery.named.connector_n.delegate")
                .addModifier(0.02f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.PERK_EFFECT)
                .addModifier(0.02f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.PERK_EXPERIENCE)
                .modify(perk -> perk.setDelegateKey(AstralSorcery.key("tree_connector_n")))
                .connect(connectorN1)
                .connect(connectorN0)
                .build(registrar);

        var treeConnectorN = PerkDataBuilder.builder(PerkTypesAS.KEY_TREE_CONNECTOR)
                .create(AstralSorcery.key("tree_connector_n"), -1f, -36f)
                .setNameKey("perk.name.astralsorcery.named.connector_n")
                .connect(connectorN0)
                .connect(connectorN1)
                .connect(connectorN2)
                .build(registrar);

        var connectorNe0 = PerkDataBuilder.builder(PerkTypesAS.KEY_TREE_CONNECTOR_DELEGATE)
                .create(AstralSorcery.key("connector_ne_0"), 34f, -14f)
                .setNameKey("perk.name.astralsorcery.named.connector_ne.delegate")
                .addModifier(0.02f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.PERK_EFFECT)
                .addModifier(0.02f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.PERK_EXPERIENCE)
                .modify(perk -> perk.setDelegateKey(AstralSorcery.key("tree_connector_ne")))
                .connect(travel21)
                .build(registrar);

        var connectorNe1 = PerkDataBuilder.builder(PerkTypesAS.KEY_TREE_CONNECTOR_DELEGATE)
                .create(AstralSorcery.key("connector_ne_1"), 35f, -19f)
                .setNameKey("perk.name.astralsorcery.named.connector_ne.delegate")
                .addModifier(0.02f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.PERK_EFFECT)
                .addModifier(0.02f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.PERK_EXPERIENCE)
                .modify(perk -> perk.setDelegateKey(AstralSorcery.key("tree_connector_ne")))
                .connect(connectorNe0)
                .build(registrar);

        var connectorNe2 = PerkDataBuilder.builder(PerkTypesAS.KEY_TREE_CONNECTOR_DELEGATE)
                .create(AstralSorcery.key("connector_ne_2"), 39f, -15f)
                .setNameKey("perk.name.astralsorcery.named.connector_ne.delegate")
                .addModifier(0.02f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.PERK_EFFECT)
                .addModifier(0.02f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.PERK_EXPERIENCE)
                .modify(perk -> perk.setDelegateKey(AstralSorcery.key("tree_connector_ne")))
                .connect(connectorNe1)
                .connect(connectorNe0)
                .build(registrar);

        var treeConnectorNe = PerkDataBuilder.builder(PerkTypesAS.KEY_TREE_CONNECTOR)
                .create(AstralSorcery.key("tree_connector_ne"), 36f, -16f)
                .setNameKey("perk.name.astralsorcery.named.connector_ne")
                .connect(connectorNe0)
                .connect(connectorNe1)
                .connect(connectorNe2)
                .build(registrar);

        var connectorSe0 = PerkDataBuilder.builder(PerkTypesAS.KEY_TREE_CONNECTOR_DELEGATE)
                .create(AstralSorcery.key("connector_se_0"), 19f, 29f)
                .setNameKey("perk.name.astralsorcery.named.connector_se.delegate")
                .addModifier(0.02f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.PERK_EFFECT)
                .addModifier(0.02f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.PERK_EXPERIENCE)
                .modify(perk -> perk.setDelegateKey(AstralSorcery.key("tree_connector_se")))
                .connect(travel27)
                .build(registrar);

        var connectorSe1 = PerkDataBuilder.builder(PerkTypesAS.KEY_TREE_CONNECTOR_DELEGATE)
                .create(AstralSorcery.key("connector_se_1"), 24f, 30f)
                .setNameKey("perk.name.astralsorcery.named.connector_se.delegate")
                .addModifier(0.02f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.PERK_EFFECT)
                .addModifier(0.02f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.PERK_EXPERIENCE)
                .modify(perk -> perk.setDelegateKey(AstralSorcery.key("tree_connector_se")))
                .connect(connectorSe0)
                .build(registrar);

        var connectorSe2 = PerkDataBuilder.builder(PerkTypesAS.KEY_TREE_CONNECTOR_DELEGATE)
                .create(AstralSorcery.key("connector_se_2"), 20f, 34f)
                .setNameKey("perk.name.astralsorcery.named.connector_se.delegate")
                .addModifier(0.02f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.PERK_EFFECT)
                .addModifier(0.02f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.PERK_EXPERIENCE)
                .modify(perk -> perk.setDelegateKey(AstralSorcery.key("tree_connector_se")))
                .connect(connectorSe0)
                .connect(connectorSe1)
                .build(registrar);

        var treeConnectorSe = PerkDataBuilder.builder(PerkTypesAS.KEY_TREE_CONNECTOR)
                .create(AstralSorcery.key("tree_connector_se"), 21f, 31f)
                .setNameKey("perk.name.astralsorcery.named.connector_se")
                .connect(connectorSe0)
                .connect(connectorSe2)
                .connect(connectorSe1)
                .build(registrar);

        var connectorSw0 = PerkDataBuilder.builder(PerkTypesAS.KEY_TREE_CONNECTOR_DELEGATE)
                .create(AstralSorcery.key("connector_sw_0"), -18f, 29f)
                .setNameKey("perk.name.astralsorcery.named.connector_sw.delegate")
                .addModifier(0.02f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.PERK_EFFECT)
                .addModifier(0.02f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.PERK_EXPERIENCE)
                .modify(perk -> perk.setDelegateKey(AstralSorcery.key("tree_connector_sw")))
                .connect(travel33)
                .build(registrar);

        var connectorSw1 = PerkDataBuilder.builder(PerkTypesAS.KEY_TREE_CONNECTOR_DELEGATE)
                .create(AstralSorcery.key("connector_sw_1"), -23f, 30f)
                .setNameKey("perk.name.astralsorcery.named.connector_sw.delegate")
                .addModifier(0.02f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.PERK_EFFECT)
                .addModifier(0.02f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.PERK_EXPERIENCE)
                .modify(perk -> perk.setDelegateKey(AstralSorcery.key("tree_connector_sw")))
                .connect(connectorSw0)
                .build(registrar);

        var connectorSw2 = PerkDataBuilder.builder(PerkTypesAS.KEY_TREE_CONNECTOR_DELEGATE)
                .create(AstralSorcery.key("connector_sw_2"), -19f, 34f)
                .setNameKey("perk.name.astralsorcery.named.connector_sw.delegate")
                .addModifier(0.02f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.PERK_EFFECT)
                .addModifier(0.02f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.PERK_EXPERIENCE)
                .modify(perk -> perk.setDelegateKey(AstralSorcery.key("tree_connector_sw")))
                .connect(connectorSw1)
                .connect(connectorSw0)
                .build(registrar);

        var treeConnectorSw = PerkDataBuilder.builder(PerkTypesAS.KEY_TREE_CONNECTOR)
                .create(AstralSorcery.key("tree_connector_sw"), -20f, 31f)
                .setNameKey("perk.name.astralsorcery.named.connector_sw")
                .connect(connectorSw0)
                .connect(connectorSw1)
                .connect(connectorSw2)
                .build(registrar);

        var connectorNw0 = PerkDataBuilder.builder(PerkTypesAS.KEY_TREE_CONNECTOR_DELEGATE)
                .create(AstralSorcery.key("connector_nw_0"), -34f, -14f)
                .setNameKey("perk.name.astralsorcery.named.connector_nw.delegate")
                .addModifier(0.02f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.PERK_EFFECT)
                .addModifier(0.02f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.PERK_EXPERIENCE)
                .modify(perk -> perk.setDelegateKey(AstralSorcery.key("tree_connector_nw")))
                .connect(travel39)
                .build(registrar);

        var connectorNw1 = PerkDataBuilder.builder(PerkTypesAS.KEY_TREE_CONNECTOR_DELEGATE)
                .create(AstralSorcery.key("connector_nw_1"), -39f, -15f)
                .setNameKey("perk.name.astralsorcery.named.connector_nw.delegate")
                .addModifier(0.02f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.PERK_EFFECT)
                .addModifier(0.02f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.PERK_EXPERIENCE)
                .modify(perk -> perk.setDelegateKey(AstralSorcery.key("tree_connector_nw")))
                .connect(connectorNw0)
                .build(registrar);

        var connectorNw2 = PerkDataBuilder.builder(PerkTypesAS.KEY_TREE_CONNECTOR_DELEGATE)
                .create(AstralSorcery.key("connector_nw_2"), -35f, -19f)
                .setNameKey("perk.name.astralsorcery.named.connector_nw.delegate")
                .addModifier(0.02f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.PERK_EFFECT)
                .addModifier(0.02f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.PERK_EXPERIENCE)
                .modify(perk -> perk.setDelegateKey(AstralSorcery.key("tree_connector_nw")))
                .connect(connectorNw1)
                .connect(connectorNw0)
                .build(registrar);

        var treeConnectorNw = PerkDataBuilder.builder(PerkTypesAS.KEY_TREE_CONNECTOR)
                .create(AstralSorcery.key("tree_connector_nw"), -36f, -16f)
                .setNameKey("perk.name.astralsorcery.named.connector_nw")
                .connect(connectorNw0)
                .connect(connectorNw1)
                .connect(connectorNw2)
                .build(registrar);

        var gemSocketCore = PerkDataBuilder.builder(PerkTypesAS.GEM_SOCKET_PERK)
                .create(AstralSorcery.key("gem_socket_core"), 0f, 0f)
                .setNameKey("perk.name.astralsorcery.generic.gem_socket")
                .addRequirement(PerkRequirementProgress.of(ResearchTier.LUMINANCE))
                .build(registrar);

        var travelCore0 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("travel_core_0"), -5f, 1f)
                .setNameKey("perk.name.astralsorcery.generic.inc.perk_effect")
                .addRequirement(PerkRequirementProgress.of(ResearchTier.LUMINANCE))
                .addModifier(0.03f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.PERK_EFFECT)
                .connect(travel11)
                .connect(gemSocketCore)
                .build(registrar);

        var travelCore2 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("travel_core_2"), 4f, -4f)
                .setNameKey("perk.name.astralsorcery.generic.inc.perk_effect")
                .addRequirement(PerkRequirementProgress.of(ResearchTier.LUMINANCE))
                .addModifier(0.03f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.PERK_EFFECT)
                .connect(travel2)
                .connect(gemSocketCore)
                .build(registrar);

        var travelCore3 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("travel_core_3"), 4f, 3f)
                .setNameKey("perk.name.astralsorcery.generic.inc.perk_effect")
                .addRequirement(PerkRequirementProgress.of(ResearchTier.LUMINANCE))
                .addModifier(0.03f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.PERK_EFFECT)
                .connect(travel5)
                .connect(gemSocketCore)
                .build(registrar);

        var travelCore4 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("travel_core_4"), -1f, 6f)
                .setNameKey("perk.name.astralsorcery.generic.inc.perk_effect")
                .addRequirement(PerkRequirementProgress.of(ResearchTier.LUMINANCE))
                .addModifier(0.03f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.PERK_EFFECT)
                .connect(travel8)
                .connect(gemSocketCore)
                .build(registrar);

        var aevitasInnerArmor2 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("aevitas_inner_armor_2"), -25f, 7f)
                .setNameKey("perk.name.astralsorcery.hybrid.life_armor")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.AEVITAS))
                .addModifier(1f, ModifierType.ADDITION, PerksAS.AttributeTypes.MAX_HEALTH)
                .addModifier(0.05f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.ARMOR)
                .build(registrar);

        var aevitasInnerReach1 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("aevitas_inner_reach_1"), -26f, 12f)
                .setNameKey("perk.name.astralsorcery.hybrid.life_block_reach")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.AEVITAS))
                .addModifier(1f, ModifierType.ADDITION, PerksAS.AttributeTypes.MAX_HEALTH)
                .addModifier(0.05f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.BLOCK_REACH)
                .connect(rootAevitas)
                .build(registrar);

        var aevitasInnerReach2 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("aevitas_inner_reach_2"), -23f, 11f)
                .setNameKey("perk.name.astralsorcery.hybrid.life_block_reach")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.AEVITAS))
                .addModifier(1f, ModifierType.ADDITION, PerksAS.AttributeTypes.MAX_HEALTH)
                .addModifier(0.05f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.BLOCK_REACH)
                .connect(aevitasInnerReach1)
                .build(registrar);

        var aevitasGemSocket = PerkDataBuilder.builder(PerkTypesAS.GEM_SOCKET_PERK)
                .create(AstralSorcery.key("aevitas_gem_socket"), -22f, 8f)
                .setNameKey("perk.name.astralsorcery.generic.gem_socket")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.AEVITAS))
                .connect(aevitasInnerArmor2)
                .connect(aevitasInnerReach2)
                .build(registrar);

        var aevitasInnerM1 = PerkDataBuilder.builder(PerkTypesAS.MAJOR_PERK)
                .create(AstralSorcery.key("aevitas_inner_m_1"), -24f, 3f)
                .setNameKey("perk.name.astralsorcery.named.thick_skin")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.AEVITAS))
                .addModifier(2f, ModifierType.ADDITION, PerksAS.AttributeTypes.MAX_HEALTH)
                .addModifier(0.1f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.ARMOR)
                .connect(aevitasInnerArmor2)
                .build(registrar);

        var aevitasBridge1 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("aevitas_bridge_1"), -21f, 5f)
                .setNameKey("perk.name.astralsorcery.generic.add.life")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.AEVITAS))
                .addModifier(1f, ModifierType.ADDITION, PerksAS.AttributeTypes.MAX_HEALTH)
                .connect(aevitasInnerM1)
                .connect(aevitasGemSocket)
                .build(registrar);

        var aevitasBridge4 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("aevitas_bridge_4"), -19f, 9f)
                .setNameKey("perk.name.astralsorcery.generic.add.life")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.AEVITAS))
                .addModifier(1f, ModifierType.ADDITION, PerksAS.AttributeTypes.MAX_HEALTH)
                .connect(aevitasGemSocket)
                .build(registrar);

        var aevitasInnerM2 = PerkDataBuilder.builder(PerkTypesAS.MAJOR_PERK)
                .create(AstralSorcery.key("aevitas_inner_m_2"), -19f, 13f)
                .setNameKey("perk.name.astralsorcery.named.melding")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.AEVITAS))
                .addModifier(2f, ModifierType.ADDITION, PerksAS.AttributeTypes.MAX_HEALTH)
                .addModifier(0.1f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.ELEMENTAL_RESISTANCE)
                .connect(aevitasInnerReach2)
                .connect(aevitasBridge4)
                .build(registrar);

        var aevitasBridge2 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("aevitas_bridge_2"), -18f, 3f)
                .setNameKey("perk.name.astralsorcery.generic.add.life")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.AEVITAS))
                .addModifier(1f, ModifierType.ADDITION, PerksAS.AttributeTypes.MAX_HEALTH)
                .connect(aevitasBridge1)
                .build(registrar);

        var aevitasBridge5 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("aevitas_bridge_5"), -15f, 8f)
                .setNameKey("perk.name.astralsorcery.generic.add.life")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.AEVITAS))
                .addModifier(1f, ModifierType.ADDITION, PerksAS.AttributeTypes.MAX_HEALTH)
                .connect(aevitasBridge4)
                .build(registrar);

        var aevitasBridge3 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("aevitas_bridge_3"), -14f, 2f)
                .setNameKey("perk.name.astralsorcery.generic.add.life")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.AEVITAS))
                .addModifier(1f, ModifierType.ADDITION, PerksAS.AttributeTypes.MAX_HEALTH)
                .connect(aevitasBridge2)
                .connect(travel11)
                .build(registrar);

        var aevitasBridge6 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("aevitas_bridge_6"), -12f, 6f)
                .setNameKey("perk.name.astralsorcery.generic.add.life")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.AEVITAS))
                .addModifier(1f, ModifierType.ADDITION, PerksAS.AttributeTypes.MAX_HEALTH)
                .connect(travel10)
                .connect(aevitasBridge5)
                .build(registrar);

        var aevitasBridgeConnect1 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("aevitas_bridge_connect_1"), -16f, 5f)
                .setNameKey("perk.name.astralsorcery.hybrid.life_perk_effect")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.AEVITAS))
                .addModifier(1f, ModifierType.ADDITION, PerksAS.AttributeTypes.MAX_HEALTH)
                .addModifier(0.02f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.PERK_EFFECT)
                .connect(aevitasBridge2)
                .build(registrar);

        var aevitasBridgeConnect2 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("aevitas_bridge_connect_2"), -14f, 4f)
                .setNameKey("perk.name.astralsorcery.hybrid.life_perk_effect")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.AEVITAS))
                .addModifier(1f, ModifierType.ADDITION, PerksAS.AttributeTypes.MAX_HEALTH)
                .addModifier(0.02f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.PERK_EFFECT)
                .connect(aevitasBridge6)
                .connect(aevitasBridgeConnect1)
                .build(registrar);

        var aevitasConnectOut3 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("aevitas_connect_out_3"), -22f, 16f)
                .setNameKey("perk.name.astralsorcery.hybrid.life_perk_effect")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.AEVITAS))
                .addModifier(1f, ModifierType.ADDITION, PerksAS.AttributeTypes.MAX_HEALTH)
                .addModifier(0.02f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.PERK_EFFECT)
                .connect(aevitasInnerM2)
                .build(registrar);

        var aevitasConnectOut4 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("aevitas_connect_out_4"), -25f, 15f)
                .setNameKey("perk.name.astralsorcery.hybrid.life_perk_effect")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.AEVITAS))
                .addModifier(1f, ModifierType.ADDITION, PerksAS.AttributeTypes.MAX_HEALTH)
                .addModifier(0.02f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.PERK_EFFECT)
                .connect(travel35)
                .connect(aevitasConnectOut3)
                .build(registrar);

        var aevitasConnectOut1 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("aevitas_connect_out_1"), -29f, 2f)
                .setNameKey("perk.name.astralsorcery.hybrid.life_perk_effect")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.AEVITAS))
                .addModifier(1f, ModifierType.ADDITION, PerksAS.AttributeTypes.MAX_HEALTH)
                .addModifier(0.02f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.PERK_EFFECT)
                .connect(aevitasInnerM1)
                .build(registrar);

        var aevitasConnectOut2 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("aevitas_connect_out_2"), -31f, 5f)
                .setNameKey("perk.name.astralsorcery.hybrid.life_perk_effect")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.AEVITAS))
                .addModifier(1f, ModifierType.ADDITION, PerksAS.AttributeTypes.MAX_HEALTH)
                .addModifier(0.02f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.PERK_EFFECT)
                .connect(travel37)
                .connect(aevitasConnectOut1)
                .build(registrar);

        var aevitasConnectIn1 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("aevitas_connect_in_1"), -23f, -1f)
                .setNameKey("perk.name.astralsorcery.generic.inc.armor")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.AEVITAS))
                .addModifier(0.03f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.ARMOR)
                .connect(aevitasInnerM1)
                .build(registrar);

        var aevitasConnectIn2 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("aevitas_connect_in_2"), -20f, -4f)
                .setNameKey("perk.name.astralsorcery.generic.inc.armor")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.AEVITAS))
                .addModifier(0.03f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.ARMOR)
                .connect(aevitasConnectIn1)
                .connect(travelBranch22)
                .build(registrar);

        var aevitasConnectIn3 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("aevitas_connect_in_3"), -16f, 16f)
                .setNameKey("perk.name.astralsorcery.generic.inc.block_reach")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.AEVITAS))
                .addModifier(0.03f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.BLOCK_REACH)
                .connect(aevitasInnerM2)
                .build(registrar);

        var aevitasConnectIn4 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("aevitas_connect_in_4"), -13f, 15f)
                .setNameKey("perk.name.astralsorcery.generic.inc.block_reach")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.AEVITAS))
                .addModifier(0.03f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.BLOCK_REACH)
                .connect(aevitasConnectIn3)
                .connect(travelBranch17)
                .build(registrar);

        var armaraInnerArmor1 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("armara_inner_armor_1"), 26f, 12f)
                .setNameKey("perk.name.astralsorcery.generic.add.armor")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.ARMARA))
                .addModifier(2f, ModifierType.ADDITION, PerksAS.AttributeTypes.ARMOR)
                .connect(rootArmara)
                .build(registrar);

        var armaraInnerArmor2 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("armara_inner_armor_2"), 23f, 11f)
                .setNameKey("perk.name.astralsorcery.generic.add.armor")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.ARMARA))
                .addModifier(2f, ModifierType.ADDITION, PerksAS.AttributeTypes.ARMOR)
                .connect(armaraInnerArmor1)
                .build(registrar);

        var armaraInnerResist1 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("armara_inner_resist_1"), 28f, 8f)
                .setNameKey("perk.name.astralsorcery.generic.inc.elemental_resistance")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.ARMARA))
                .addModifier(0.05f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.ELEMENTAL_RESISTANCE)
                .connect(rootArmara)
                .build(registrar);

        var armaraInnerResist2 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("armara_inner_resist_2"), 25f, 7f)
                .setNameKey("perk.name.astralsorcery.generic.inc.elemental_resistance")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.ARMARA))
                .addModifier(0.05f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.ELEMENTAL_RESISTANCE)
                .connect(armaraInnerResist1)
                .build(registrar);

        var armaraGemSocket = PerkDataBuilder.builder(PerkTypesAS.GEM_SOCKET_PERK)
                .create(AstralSorcery.key("armara_gem_socket"), 22f, 8f)
                .setNameKey("perk.name.astralsorcery.generic.gem_socket")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.ARMARA))
                .connect(armaraInnerArmor2)
                .connect(armaraInnerResist2)
                .build(registrar);

        var armaraInnerM2 = PerkDataBuilder.builder(PerkTypesAS.MAJOR_PERK)
                .create(AstralSorcery.key("armara_inner_m_2"), 19f, 13f)
                .setNameKey("perk.name.astralsorcery.named.tough")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.ARMARA))
                .addModifier(2f, ModifierType.ADDITION, PerksAS.AttributeTypes.ARMOR)
                .addModifier(4f, ModifierType.ADDITION, PerksAS.AttributeTypes.ARMOR_TOUGHNESS)
                .connect(armaraInnerArmor2)
                .build(registrar);

        var armaraInnerM1 = PerkDataBuilder.builder(PerkTypesAS.MAJOR_PERK)
                .create(AstralSorcery.key("armara_inner_m_1"), 24f, 3f)
                .setNameKey("perk.name.astralsorcery.named.bulwark")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.ARMARA))
                .addModifier(2f, ModifierType.ADDITION, PerksAS.AttributeTypes.ARMOR)
                .addModifier(0.1f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.ELEMENTAL_RESISTANCE)
                .connect(armaraInnerResist2)
                .build(registrar);

        var armaraBridge1 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("armara_bridge_1"), 19f, 9f)
                .setNameKey("perk.name.astralsorcery.generic.add.armor")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.ARMARA))
                .addModifier(1f, ModifierType.ADDITION, PerksAS.AttributeTypes.ARMOR)
                .connect(armaraGemSocket)
                .connect(armaraInnerM2)
                .build(registrar);

        var armaraBridge4 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("armara_bridge_4"), 21f, 5f)
                .setNameKey("perk.name.astralsorcery.generic.add.armor")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.ARMARA))
                .addModifier(1f, ModifierType.ADDITION, PerksAS.AttributeTypes.ARMOR)
                .connect(armaraInnerM1)
                .connect(armaraGemSocket)
                .build(registrar);

        var armaraBridge2 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("armara_bridge_2"), 15f, 7f)
                .setNameKey("perk.name.astralsorcery.generic.add.armor")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.ARMARA))
                .addModifier(1f, ModifierType.ADDITION, PerksAS.AttributeTypes.ARMOR)
                .connect(armaraBridge1)
                .build(registrar);

        var armaraBridge3 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("armara_bridge_3"), 12f, 6f)
                .setNameKey("perk.name.astralsorcery.generic.add.armor")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.ARMARA))
                .addModifier(1f, ModifierType.ADDITION, PerksAS.AttributeTypes.ARMOR)
                .connect(travel5)
                .connect(armaraBridge2)
                .build(registrar);

        var armaraBridge5 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("armara_bridge_5"), 18f, 3f)
                .setNameKey("perk.name.astralsorcery.generic.add.armor")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.ARMARA))
                .addModifier(1f, ModifierType.ADDITION, PerksAS.AttributeTypes.ARMOR)
                .connect(armaraBridge4)
                .build(registrar);

        var armaraBridge6 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("armara_bridge_6"), 15f, 1f)
                .setNameKey("perk.name.astralsorcery.generic.add.armor")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.ARMARA))
                .addModifier(1f, ModifierType.ADDITION, PerksAS.AttributeTypes.ARMOR)
                .connect(travel4)
                .connect(armaraBridge5)
                .build(registrar);

        var armaraBridgeConnect2 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("armara_bridge_connect_2"), 14f, 3f)
                .setNameKey("perk.name.astralsorcery.hybrid.armor_perk_effect")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.ARMARA))
                .addModifier(1f, ModifierType.ADDITION, PerksAS.AttributeTypes.ARMOR)
                .addModifier(0.02f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.PERK_EFFECT)
                .connect(armaraBridge6)
                .build(registrar);

        var armaraBridgeConnect1 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("armara_bridge_connect_1"), 16f, 5f)
                .setNameKey("perk.name.astralsorcery.hybrid.armor_perk_effect")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.ARMARA))
                .addModifier(1f, ModifierType.ADDITION, PerksAS.AttributeTypes.ARMOR)
                .addModifier(0.02f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.PERK_EFFECT)
                .connect(armaraBridge2)
                .connect(armaraBridgeConnect2)
                .build(registrar);

        var armaraConnectOut1 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("armara_connect_out_1"), 22f, 16f)
                .setNameKey("perk.name.astralsorcery.hybrid.armor_perk_effect")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.ARMARA))
                .addModifier(1f, ModifierType.ADDITION, PerksAS.AttributeTypes.ARMOR)
                .addModifier(0.02f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.PERK_EFFECT)
                .connect(armaraInnerM2)
                .build(registrar);

        var armaraConnectOut2 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("armara_connect_out_2"), 26f, 15f)
                .setNameKey("perk.name.astralsorcery.hybrid.armor_perk_effect")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.ARMARA))
                .addModifier(1f, ModifierType.ADDITION, PerksAS.AttributeTypes.ARMOR)
                .addModifier(0.02f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.PERK_EFFECT)
                .connect(armaraConnectOut1)
                .connect(travel25)
                .build(registrar);

        var armaraConnectOut3 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("armara_connect_out_3"), 28f, 2f)
                .setNameKey("perk.name.astralsorcery.hybrid.armor_perk_effect")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.ARMARA))
                .addModifier(1f, ModifierType.ADDITION, PerksAS.AttributeTypes.ARMOR)
                .addModifier(0.02f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.PERK_EFFECT)
                .connect(armaraInnerM1)
                .build(registrar);

        var armaraConnectOut4 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("armara_connect_out_4"), 29f, 5f)
                .setNameKey("perk.name.astralsorcery.hybrid.armor_perk_effect")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.ARMARA))
                .addModifier(1f, ModifierType.ADDITION, PerksAS.AttributeTypes.ARMOR)
                .addModifier(0.02f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.PERK_EFFECT)
                .connect(armaraConnectOut3)
                .connect(travel23)
                .build(registrar);

        var armaraConnectIn1 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("armara_connect_in_1"), 16f, 14f)
                .setNameKey("perk.name.astralsorcery.generic.add.armor")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.ARMARA))
                .addModifier(1f, ModifierType.ADDITION, PerksAS.AttributeTypes.ARMOR)
                .connect(armaraInnerM2)
                .build(registrar);

        var armaraConnectIn3 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("armara_connect_in_3"), 23f, -1f)
                .setNameKey("perk.name.astralsorcery.generic.inc.elemental_resistance")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.ARMARA))
                .addModifier(0.03f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.ELEMENTAL_RESISTANCE)
                .connect(armaraInnerM1)
                .build(registrar);

        var armaraConnectIn4 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("armara_connect_in_4"), 20f, -4f)
                .setNameKey("perk.name.astralsorcery.generic.inc.elemental_resistance")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.ARMARA))
                .addModifier(0.03f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.ELEMENTAL_RESISTANCE)
                .connect(armaraConnectIn3)
                .connect(travelBranch7)
                .build(registrar);

        var vicioInnerReach1 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("vicio_inner_reach_1"), -2f, 27f)
                .setNameKey("perk.name.astralsorcery.hybrid.movespeed_block_reach")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.VICIO))
                .addModifier(0.02f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.MOVEMENT_SPEED)
                .addModifier(0.05f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.BLOCK_REACH)
                .connect(rootVicio)
                .build(registrar);

        var vicioInnerReach2 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("vicio_inner_reach_2"), -4f, 25f)
                .setNameKey("perk.name.astralsorcery.hybrid.movespeed_block_reach")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.VICIO))
                .addModifier(0.02f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.MOVEMENT_SPEED)
                .addModifier(0.05f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.BLOCK_REACH)
                .connect(vicioInnerReach1)
                .build(registrar);

        var vicioInnerSwim1 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("vicio_inner_swim_1"), 2f, 27f)
                .setNameKey("perk.name.astralsorcery.hybrid.movespeed_swimspeed")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.VICIO))
                .addModifier(0.02f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.MOVEMENT_SPEED)
                .addModifier(0.03f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.SWIM_SPEED)
                .connect(rootVicio)
                .build(registrar);

        var vicioInnerSwim2 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("vicio_inner_swim_2"), 4f, 25f)
                .setNameKey("perk.name.astralsorcery.hybrid.movespeed_swimspeed")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.VICIO))
                .addModifier(0.02f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.MOVEMENT_SPEED)
                .addModifier(0.03f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.SWIM_SPEED)
                .connect(vicioInnerSwim1)
                .build(registrar);

        var vicioGemSocket = PerkDataBuilder.builder(PerkTypesAS.GEM_SOCKET_PERK)
                .create(AstralSorcery.key("vicio_gem_socket"), 0f, 23f)
                .setNameKey("perk.name.astralsorcery.generic.gem_socket")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.VICIO))
                .connect(vicioInnerSwim2)
                .connect(vicioInnerReach2)
                .build(registrar);

        var vicioConnectIn1 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("vicio_connect_in_1"), -7f, 19f)
                .setNameKey("perk.name.astralsorcery.generic.inc.block_reach")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.VICIO))
                .addModifier(0.04f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.BLOCK_REACH)
                .build(registrar);

        var vicioConnectIn2 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("vicio_connect_in_2"), -9f, 21f)
                .setNameKey("perk.name.astralsorcery.generic.inc.block_reach")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.VICIO))
                .addModifier(0.04f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.BLOCK_REACH)
                .connect(vicioConnectIn1)
                .connect(travelBranch17)
                .build(registrar);

        var vicioConnectOut1 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("vicio_connect_out_1"), -8f, 25f)
                .setNameKey("perk.name.astralsorcery.hybrid.movespeed_perk_effect")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.VICIO))
                .addModifier(0.02f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.MOVEMENT_SPEED)
                .addModifier(0.02f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.PERK_EFFECT)
                .build(registrar);

        var vicioConnectOut2 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("vicio_connect_out_2"), -6f, 28f)
                .setNameKey("perk.name.astralsorcery.hybrid.movespeed_perk_effect")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.VICIO))
                .addModifier(0.02f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.MOVEMENT_SPEED)
                .addModifier(0.02f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.PERK_EFFECT)
                .connect(vicioConnectOut1)
                .connect(travel31)
                .build(registrar);

        var vicioConnectIn3 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("vicio_connect_in_3"), 8f, 21f)
                .setNameKey("perk.name.astralsorcery.generic.inc.swim_speed")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.VICIO))
                .addModifier(0.04f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.SWIM_SPEED)
                .build(registrar);

        var vicioConnectIn4 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("vicio_connect_in_4"), 7f, 19f)
                .setNameKey("perk.name.astralsorcery.generic.inc.swim_speed")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.VICIO))
                .addModifier(0.04f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.SWIM_SPEED)
                .connect(vicioConnectIn3)
                .connect(travelBranch12)
                .build(registrar);

        var vicioConnectOut3 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("vicio_connect_out_3"), 7f, 26f)
                .setNameKey("perk.name.astralsorcery.hybrid.movespeed_perk_effect")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.VICIO))
                .addModifier(0.02f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.MOVEMENT_SPEED)
                .addModifier(0.02f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.PERK_EFFECT)
                .build(registrar);

        var vicioConnectOut4 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("vicio_connect_out_4"), 5f, 30f)
                .setNameKey("perk.name.astralsorcery.hybrid.movespeed_perk_effect")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.VICIO))
                .addModifier(0.02f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.MOVEMENT_SPEED)
                .addModifier(0.02f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.PERK_EFFECT)
                .connect(vicioConnectOut3)
                .connect(travel29)
                .build(registrar);

        var vicioBridge2 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("vicio_bridge_2"), -2f, 19f)
                .setNameKey("perk.name.astralsorcery.generic.inc.move_speed")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.VICIO))
                .addModifier(0.02f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.MOVEMENT_SPEED)
                .build(registrar);

        var vicioBridge3 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("vicio_bridge_3"), -3f, 14f)
                .setNameKey("perk.name.astralsorcery.generic.inc.move_speed")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.VICIO))
                .addModifier(0.02f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.MOVEMENT_SPEED)
                .connect(travel8)
                .connect(vicioBridge2)
                .build(registrar);

        var vicioBridge4 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("vicio_bridge_4"), 2f, 22f)
                .setNameKey("perk.name.astralsorcery.generic.inc.move_speed")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.VICIO))
                .addModifier(0.02f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.MOVEMENT_SPEED)
                .connect(vicioGemSocket)
                .build(registrar);

        var vicioBridge5 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("vicio_bridge_5"), 3f, 18f)
                .setNameKey("perk.name.astralsorcery.generic.inc.move_speed")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.VICIO))
                .addModifier(0.02f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.MOVEMENT_SPEED)
                .connect(vicioBridge4)
                .build(registrar);

        var vicioBridge6 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("vicio_bridge_6"), 2f, 14f)
                .setNameKey("perk.name.astralsorcery.generic.inc.move_speed")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.VICIO))
                .addModifier(0.02f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.MOVEMENT_SPEED)
                .connect(travel7)
                .connect(vicioBridge5)
                .build(registrar);

        var vicioBridgeConnect1 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("vicio_bridge_connect_1"), 1f, 17f)
                .setNameKey("perk.name.astralsorcery.hybrid.movespeed_perk_effect")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.VICIO))
                .addModifier(0.02f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.MOVEMENT_SPEED)
                .addModifier(0.02f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.PERK_EFFECT)
                .connect(vicioBridge2)
                .build(registrar);

        var vicioBridgeConnect2 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("vicio_bridge_connect_2"), -1f, 16f)
                .setNameKey("perk.name.astralsorcery.hybrid.movespeed_perk_effect")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.VICIO))
                .addModifier(0.02f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.MOVEMENT_SPEED)
                .addModifier(0.02f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.PERK_EFFECT)
                .connect(vicioBridgeConnect1)
                .connect(vicioBridge6)
                .build(registrar);

        var vicioInnerM2 = PerkDataBuilder.builder(PerkTypesAS.MAJOR_PERK)
                .create(AstralSorcery.key("vicio_inner_m_2"), 6f, 23f)
                .setNameKey("perk.name.astralsorcery.named.nimble")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.VICIO))
                .addModifier(0.04f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.MOVEMENT_SPEED)
                .addModifier(6f, ModifierType.ADDITION, PerksAS.AttributeTypes.SAFE_FALL_DISTANCE)
                .connect(vicioBridge4)
                .connect(vicioInnerSwim2)
                .connect(vicioConnectOut3)
                .connect(vicioConnectIn3)
                .build(registrar);

        var vicioInnerM1 = PerkDataBuilder.builder(PerkTypesAS.MAJOR_PERK)
                .create(AstralSorcery.key("vicio_inner_m_1"), -6f, 23f)
                .setNameKey("perk.name.astralsorcery.named.swiftness")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.VICIO))
                .addModifier(0.04f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.MOVEMENT_SPEED)
                .addModifier(0.1f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.BLOCK_REACH)
                .connect(vicioConnectIn1)
                .connect(vicioConnectOut1)
                .connect(vicioInnerReach2)
                .build(registrar);

        var discidiaInnerMelee1 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("discidia_inner_melee_1"), 18f, -22f)
                .setNameKey("perk.name.astralsorcery.hybrid.attack_damage_critical_hit_chance")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.DISCIDIA))
                .addModifier(0.05f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.ATTACK_DAMAGE)
                .addModifier(0.05f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.CRITICAL_HIT_CHANCE)
                .connect(rootDiscidia)
                .build(registrar);

        var discidiaInnerMelee2 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("discidia_inner_melee_2"), 17f, -20f)
                .setNameKey("perk.name.astralsorcery.hybrid.attack_damage_critical_hit_chance")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.DISCIDIA))
                .addModifier(0.05f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.ATTACK_DAMAGE)
                .addModifier(0.05f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.CRITICAL_HIT_CHANCE)
                .connect(discidiaInnerMelee1)
                .build(registrar);

        var discidiaInnerProj1 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("discidia_inner_proj_1"), 13f, -24f)
                .setNameKey("perk.name.astralsorcery.hybrid.projectile_damage_critical_hit_chance")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.DISCIDIA))
                .addModifier(0.07f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.PROJECTILE_DAMAGE)
                .addModifier(0.05f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.CRITICAL_HIT_CHANCE)
                .build(registrar);

        var discidiaInnerProj2 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("discidia_inner_proj_2"), 15f, -25f)
                .setNameKey("perk.name.astralsorcery.hybrid.projectile_damage_critical_hit_chance")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.DISCIDIA))
                .addModifier(0.07f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.PROJECTILE_DAMAGE)
                .addModifier(0.05f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.CRITICAL_HIT_CHANCE)
                .connect(rootDiscidia)
                .connect(discidiaInnerProj1)
                .build(registrar);

        var discidiaGemSocket = PerkDataBuilder.builder(PerkTypesAS.GEM_SOCKET_PERK)
                .create(AstralSorcery.key("discidia_gem_socket"), 14f, -21f)
                .setNameKey("perk.name.astralsorcery.generic.gem_socket")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.DISCIDIA))
                .connect(discidiaInnerProj1)
                .connect(discidiaInnerMelee2)
                .build(registrar);

        var discidiaBridge1 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("discidia_bridge_1"), 15f, -18f)
                .setNameKey("perk.name.astralsorcery.hybrid.attack_damage_projectile_damage")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.DISCIDIA))
                .addModifier(0.04f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.ATTACK_DAMAGE)
                .addModifier(0.04f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.PROJECTILE_DAMAGE)
                .connect(discidiaGemSocket)
                .build(registrar);

        var discidiaInnerM_ = PerkDataBuilder.builder(PerkTypesAS.MAJOR_PERK)
                .create(AstralSorcery.key("discidia_inner_m_"), 18f, -17f)
                .setNameKey("perk.name.astralsorcery.named.strong_arms")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.DISCIDIA))
                .addModifier(0.1f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.ATTACK_DAMAGE)
                .addModifier(0.1f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.ATTACK_REACH)
                .connect(discidiaInnerMelee2)
                .connect(discidiaBridge1)
                .build(registrar);

        var discidiaInnerM1 = PerkDataBuilder.builder(PerkTypesAS.MAJOR_PERK)
                .create(AstralSorcery.key("discidia_inner_m_1"), 10f, -23f)
                .setNameKey("perk.name.astralsorcery.named.deadly_draw")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.DISCIDIA))
                .addModifier(0.1f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.PROJECTILE_DAMAGE)
                .addModifier(0.15f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.PROJECTILE_SPEED)
                .connect(discidiaInnerProj1)
                .build(registrar);

        var discidiaBridgeConnect1 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("discidia_bridge_connect_1"), 10f, -16f)
                .setNameKey("perk.name.astralsorcery.hybrid.attack_damage_perk_effect")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.DISCIDIA))
                .addModifier(0.08f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.ATTACK_DAMAGE)
                .addModifier(0.02f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.PERK_EFFECT)
                .build(registrar);

        var discidiaBridgeConnect2 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("discidia_bridge_connect_2"), 9f, -14f)
                .setNameKey("perk.name.astralsorcery.hybrid.projectile_damage_perk_effect")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.DISCIDIA))
                .addModifier(0.08f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.PROJECTILE_DAMAGE)
                .addModifier(0.02f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.PERK_EFFECT)
                .connect(discidiaBridgeConnect1)
                .build(registrar);

        var discidiaConnectIn1 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("discidia_connect_in_1"), 7f, -21f)
                .setNameKey("perk.name.astralsorcery.generic.inc.critical_hit_chance")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.DISCIDIA))
                .addModifier(0.04f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.CRITICAL_HIT_CHANCE)
                .connect(discidiaInnerM1)
                .build(registrar);

        var discidiaConnectIn2 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("discidia_connect_in_2"), 4f, -22f)
                .setNameKey("perk.name.astralsorcery.generic.inc.critical_hit_chance")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.DISCIDIA))
                .addModifier(0.04f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.CRITICAL_HIT_CHANCE)
                .connect(travelBranch2)
                .connect(discidiaConnectIn1)
                .build(registrar);

        var discidiaConnectIn3 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("discidia_connect_in_3"), 20f, -14f)
                .setNameKey("perk.name.astralsorcery.generic.inc.attack_reach")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.DISCIDIA))
                .addModifier(0.05f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.ATTACK_REACH)
                .connect(discidiaInnerM_)
                .build(registrar);

        var discidiaConnectIn4 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("discidia_connect_in_4"), 18f, -11f)
                .setNameKey("perk.name.astralsorcery.generic.inc.attack_reach")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.DISCIDIA))
                .addModifier(0.05f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.ATTACK_REACH)
                .connect(discidiaConnectIn3)
                .connect(travelBranch7)
                .build(registrar);

        var evorsioInnerDamage1 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("evorsio_inner_damage_1"), -16f, -25f)
                .setNameKey("perk.name.astralsorcery.generic.inc.attack_damage")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.EVORSIO))
                .addModifier(0.04f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.ATTACK_DAMAGE)
                .connect(rootEvorsio)
                .build(registrar);

        var evorsioInnerMining1 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("evorsio_inner_mining_1"), -19f, -22f)
                .setNameKey("perk.name.astralsorcery.generic.inc.block_break_speed")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.EVORSIO))
                .addModifier(0.06f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.BLOCK_BREAK_SPEED)
                .connect(rootEvorsio)
                .build(registrar);

        var evorsioInnerDamage2 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("evorsio_inner_damage_2"), -14f, -24f)
                .setNameKey("perk.name.astralsorcery.generic.inc.attack_damage")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.EVORSIO))
                .addModifier(0.04f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.ATTACK_DAMAGE)
                .connect(evorsioInnerDamage1)
                .build(registrar);

        var evorsioInnerMining2 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("evorsio_inner_mining_2"), -18f, -20f)
                .setNameKey("perk.name.astralsorcery.generic.inc.block_break_speed")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.EVORSIO))
                .addModifier(0.06f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.BLOCK_BREAK_SPEED)
                .connect(evorsioInnerMining1)
                .build(registrar);

        var evorsioGemSocket = PerkDataBuilder.builder(PerkTypesAS.GEM_SOCKET_PERK)
                .create(AstralSorcery.key("evorsio_gem_socket"), -15f, -21f)
                .setNameKey("perk.name.astralsorcery.generic.gem_socket")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.EVORSIO))
                .connect(evorsioInnerDamage2)
                .connect(evorsioInnerMining2)
                .build(registrar);

        var evorsioInnerM1 = PerkDataBuilder.builder(PerkTypesAS.MAJOR_PERK)
                .create(AstralSorcery.key("evorsio_inner_m_1"), -11f, -23f)
                .setNameKey("perk.name.astralsorcery.named.finesse")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.EVORSIO))
                .addModifier(0.1f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.ATTACK_DAMAGE)
                .addModifier(0.06f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.ATTACK_SPEED)
                .connect(evorsioInnerDamage2)
                .build(registrar);

        var evorsioInnerM2 = PerkDataBuilder.builder(PerkTypesAS.MAJOR_PERK)
                .create(AstralSorcery.key("evorsio_inner_m_2"), -19f, -17f)
                .setNameKey("perk.name.astralsorcery.named.tunneling")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.EVORSIO))
                .addModifier(0.1f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.BLOCK_BREAK_SPEED)
                .addModifier(0.1f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.BLOCK_REACH)
                .connect(evorsioInnerMining2)
                .build(registrar);

        var evorsioBridge1 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("evorsio_bridge_1"), -16f, -18f)
                .setNameKey("perk.name.astralsorcery.generic.inc.block_break_speed")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.EVORSIO))
                .addModifier(0.03f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.BLOCK_BREAK_SPEED)
                .connect(evorsioGemSocket)
                .connect(evorsioInnerM2)
                .build(registrar);

        var evorsioBridge4 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("evorsio_bridge_4"), -12f, -20f)
                .setNameKey("perk.name.astralsorcery.generic.inc.block_break_speed")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.EVORSIO))
                .addModifier(0.03f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.BLOCK_BREAK_SPEED)
                .connect(evorsioInnerM1)
                .connect(evorsioGemSocket)
                .build(registrar);

        var evorsioBridge2 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("evorsio_bridge_2"), -14f, -15f)
                .setNameKey("perk.name.astralsorcery.generic.inc.block_break_speed")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.EVORSIO))
                .addModifier(0.03f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.BLOCK_BREAK_SPEED)
                .connect(evorsioBridge1)
                .build(registrar);

        var evorsioBridge3 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("evorsio_bridge_3"), -10f, -11f)
                .setNameKey("perk.name.astralsorcery.generic.inc.block_break_speed")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.EVORSIO))
                .addModifier(0.03f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.BLOCK_BREAK_SPEED)
                .connect(evorsioBridge2)
                .connect(travel13)
                .build(registrar);

        var evorsioBridge5 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("evorsio_bridge_5"), -10f, -17f)
                .setNameKey("perk.name.astralsorcery.generic.inc.block_break_speed")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.EVORSIO))
                .addModifier(0.03f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.BLOCK_BREAK_SPEED)
                .connect(evorsioBridge4)
                .build(registrar);

        var evorsioBridge6 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("evorsio_bridge_6"), -7f, -14f)
                .setNameKey("perk.name.astralsorcery.generic.inc.block_break_speed")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.EVORSIO))
                .addModifier(0.03f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.BLOCK_BREAK_SPEED)
                .connect(travel14)
                .connect(evorsioBridge5)
                .build(registrar);

        var evorsioBridgeConnect1 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("evorsio_bridge_connect_1"), -11f, -15f)
                .setNameKey("perk.name.astralsorcery.hybrid.block_reach_perk_effect")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.EVORSIO))
                .addModifier(0.07f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.BLOCK_REACH)
                .addModifier(0.02f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.PERK_EFFECT)
                .connect(evorsioBridge5)
                .build(registrar);

        var evorsioBridgeConnect2 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("evorsio_bridge_connect_2"), -9f, -13f)
                .setNameKey("perk.name.astralsorcery.hybrid.block_reach_perk_effect")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.EVORSIO))
                .addModifier(0.07f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.BLOCK_REACH)
                .addModifier(0.02f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.PERK_EFFECT)
                .connect(evorsioBridgeConnect1)
                .connect(evorsioBridge3)
                .build(registrar);

        var evorsioConnectIn1 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("evorsio_connect_in_1"), -7f, -24f)
                .setNameKey("perk.name.astralsorcery.generic.inc.attack_speed")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.EVORSIO))
                .addModifier(0.03f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.ATTACK_SPEED)
                .connect(evorsioInnerM1)
                .build(registrar);

        var evorsioConnectIn2 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("evorsio_connect_in_2"), -4f, -21f)
                .setNameKey("perk.name.astralsorcery.generic.inc.attack_speed")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.EVORSIO))
                .addModifier(0.03f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.ATTACK_SPEED)
                .connect(evorsioConnectIn1)
                .connect(travelBranch2)
                .build(registrar);

        var evorsioConnectIn3 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("evorsio_connect_in_3"), -20f, -14f)
                .setNameKey("perk.name.astralsorcery.generic.inc.block_break_speed")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.EVORSIO))
                .addModifier(0.03f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.BLOCK_BREAK_SPEED)
                .connect(evorsioInnerM2)
                .build(registrar);

        var evorsioConnectIn4 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("evorsio_connect_in_4"), -18f, -11f)
                .setNameKey("perk.name.astralsorcery.generic.inc.block_break_speed")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.EVORSIO))
                .addModifier(0.03f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.BLOCK_BREAK_SPEED)
                .connect(evorsioConnectIn3)
                .connect(travelBranch22)
                .build(registrar);

        var discidiaConnectOut1 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("discidia_connect_out_1"), 21f, -19f)
                .setNameKey("perk.name.astralsorcery.hybrid.attack_damage_perk_effect")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.DISCIDIA))
                .addModifier(0.04f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.ATTACK_DAMAGE)
                .addModifier(0.02f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.PERK_EFFECT)
                .connect(discidiaInnerM_)
                .build(registrar);

        var discidiaConnectOut2 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("discidia_connect_out_2"), 22f, -22f)
                .setNameKey("perk.name.astralsorcery.hybrid.attack_damage_perk_effect")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.DISCIDIA))
                .addModifier(0.04f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.ATTACK_DAMAGE)
                .addModifier(0.02f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.PERK_EFFECT)
                .connect(discidiaConnectOut1)
                .connect(travel19)
                .build(registrar);

        var discidiaConnectOut3 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("discidia_connect_out_3"), 9f, -26f)
                .setNameKey("perk.name.astralsorcery.hybrid.projectile_damage_perk_effect")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.DISCIDIA))
                .addModifier(0.04f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.PROJECTILE_DAMAGE)
                .addModifier(0.02f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.PERK_EFFECT)
                .connect(discidiaInnerM1)
                .build(registrar);

        var discidiaConnectOut4 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("discidia_connect_out_4"), 12f, -28f)
                .setNameKey("perk.name.astralsorcery.hybrid.projectile_damage_perk_effect")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.DISCIDIA))
                .addModifier(0.04f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.PROJECTILE_DAMAGE)
                .addModifier(0.02f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.PERK_EFFECT)
                .connect(discidiaConnectOut3)
                .connect(travel17)
                .build(registrar);

        var evorsioConnectOut1 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("evorsio_connect_out_1"), -12f, -26f)
                .setNameKey("perk.name.astralsorcery.hybrid.attack_damage_perk_effect")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.EVORSIO))
                .addModifier(0.03f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.ATTACK_DAMAGE)
                .addModifier(0.02f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.PERK_EFFECT)
                .connect(evorsioInnerM1)
                .build(registrar);

        var evorsioConnectOut2 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("evorsio_connect_out_2"), -15f, -28f)
                .setNameKey("perk.name.astralsorcery.hybrid.attack_damage_perk_effect")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.EVORSIO))
                .addModifier(0.03f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.ATTACK_DAMAGE)
                .addModifier(0.02f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.ATTACK_SPEED)
                .connect(travel43)
                .connect(evorsioConnectOut1)
                .build(registrar);

        var evorsioConnectOut3 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("evorsio_connect_out_3"), -22f, -19f)
                .setNameKey("perk.name.astralsorcery.hybrid.block_break_speed_perk_effect")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.EVORSIO))
                .addModifier(0.05f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.BLOCK_BREAK_SPEED)
                .addModifier(0.02f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.PERK_EFFECT)
                .connect(evorsioInnerM2)
                .build(registrar);

        var evorsioConnectOut4 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("evorsio_connect_out_4"), -24f, -21f)
                .setNameKey("perk.name.astralsorcery.hybrid.block_break_speed_perk_effect")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.EVORSIO))
                .addModifier(0.05f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.BLOCK_BREAK_SPEED)
                .addModifier(0.02f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.PERK_EFFECT)
                .connect(travel41)
                .connect(evorsioConnectOut3)
                .build(registrar);

        var aevitasInnerArmor1 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("aevitas_inner_armor_1"), -28f, 8f)
                .setNameKey("perk.name.astralsorcery.hybrid.life_armor")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.AEVITAS))
                .addModifier(1f, ModifierType.ADDITION, PerksAS.AttributeTypes.MAX_HEALTH)
                .addModifier(0.05f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.ARMOR)
                .connect(rootAevitas)
                .connect(aevitasInnerArmor2)
                .build(registrar);

        var vicioBridge1 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("vicio_bridge_1"), -3f, 22f)
                .setNameKey("perk.name.astralsorcery.generic.inc.move_speed")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.VICIO))
                .addModifier(0.02f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.MOVEMENT_SPEED)
                .connect(vicioInnerM1)
                .connect(vicioBridge2)
                .connect(vicioGemSocket)
                .build(registrar);

        var armaraConnectIn2 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("armara_connect_in_2"), 14f, 17f)
                .setNameKey("perk.name.astralsorcery.generic.add.armor")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.ARMARA))
                .addModifier(1f, ModifierType.ADDITION, PerksAS.AttributeTypes.ARMOR)
                .connect(travelBranch12)
                .connect(armaraConnectIn1)
                .build(registrar);

        var discidiaBridge2 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("discidia_bridge_2"), 13f, -15f)
                .setNameKey("perk.name.astralsorcery.hybrid.attack_damage_projectile_damage")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.DISCIDIA))
                .addModifier(0.04f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.ATTACK_DAMAGE)
                .addModifier(0.04f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.PROJECTILE_DAMAGE)
                .connect(discidiaBridge1)
                .connect(discidiaBridgeConnect1)
                .build(registrar);

        var discidiaBridge3 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("discidia_bridge_3"), 10f, -12f)
                .setNameKey("perk.name.astralsorcery.hybrid.attack_damage_projectile_damage")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.DISCIDIA))
                .addModifier(0.04f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.ATTACK_DAMAGE)
                .addModifier(0.04f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.PROJECTILE_DAMAGE)
                .connect(travel2)
                .connect(discidiaBridge2)
                .build(registrar);

        var discidiaBridge4 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("discidia_bridge_4"), 11f, -20f)
                .setNameKey("perk.name.astralsorcery.hybrid.attack_damage_projectile_damage")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.DISCIDIA))
                .addModifier(0.04f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.ATTACK_DAMAGE)
                .addModifier(0.04f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.PROJECTILE_DAMAGE)
                .connect(discidiaInnerM1)
                .connect(discidiaGemSocket)
                .build(registrar);

        var discidiaBridge5 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("discidia_bridge_5"), 8f, -17f)
                .setNameKey("perk.name.astralsorcery.hybrid.attack_damage_projectile_damage")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.DISCIDIA))
                .addModifier(0.04f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.ATTACK_DAMAGE)
                .addModifier(0.04f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.PROJECTILE_DAMAGE)
                .connect(discidiaBridge4)
                .build(registrar);

        var discidiaBridge6 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("discidia_bridge_6"), 6f, -13f)
                .setNameKey("perk.name.astralsorcery.hybrid.attack_damage_projectile_damage")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.DISCIDIA))
                .addModifier(0.04f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.ATTACK_DAMAGE)
                .addModifier(0.04f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.PROJECTILE_DAMAGE)
                .connect(travel1)
                .connect(discidiaBridgeConnect2)
                .connect(discidiaBridge5)
                .build(registrar);

        var travelCore1 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("travel_core_1"), -1f, -5f)
                .setNameKey("perk.name.astralsorcery.generic.inc.perk_effect")
                .addRequirement(PerkRequirementProgress.of(ResearchTier.LUMINANCE))
                .addModifier(0.03f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.PERK_EFFECT)
                .connect(gemSocketCore)
                .connect(travel14)
                .build(registrar);

        var keyLastBreath = PerkDataBuilder.builder(PerkTypesAS.KEY_LAST_BREATH)
                .create(AstralSorcery.key("key_last_breath"), -8f, -34f)
                .setNameKey("perk.name.astralsorcery.named.last_breath")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.EVORSIO))
                .build(registrar);

        var lastBreath2 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("last_breath_2"), -7f, -33f)
                .setNameKey("perk.name.astralsorcery.generic.inc.block_break_speed")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.EVORSIO))
                .addModifier(0.03f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.BLOCK_BREAK_SPEED)
                .connect(keyLastBreath)
                .build(registrar);

        var geologicProwess1 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("geologic_prowess_1"), -24f, -31f)
                .setNameKey("perk.name.astralsorcery.generic.inc.perk_effect")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.EVORSIO))
                .addModifier(0.02f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.PERK_EFFECT)
                .connect(travel42)
                .build(registrar);

        var geologicProwess2 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("geologic_prowess_2"), -25f, -30f)
                .setNameKey("perk.name.astralsorcery.generic.inc.perk_effect")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.EVORSIO))
                .addModifier(0.02f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.PERK_EFFECT)
                .connect(geologicProwess1)
                .build(registrar);

        var keyGeologicProwess = PerkDataBuilder.builder(PerkTypesAS.MAJOR_PERK)
                .create(AstralSorcery.key("key_geologic_prowess"), -24f, -29f)
                .setNameKey("perk.name.astralsorcery.named.geologic_prowess")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.EVORSIO))
                .addModifier(1f, ModifierType.ADDITION, PerksAS.AttributeTypes.MINING_SIZE)
                .connect(geologicProwess2)
                .build(registrar);

        var consistentLuck1 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("consistent_luck_1"), -26f, -15f)
                .setNameKey("perk.name.astralsorcery.generic.add.luck")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.EVORSIO))
                .addModifier(0.5f, ModifierType.ADDITION, PerksAS.AttributeTypes.LUCK)
                .connect(travel40)
                .build(registrar);

        var consistentLuck2 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("consistent_luck_2"), -25f, -16f)
                .setNameKey("perk.name.astralsorcery.generic.add.luck")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.EVORSIO))
                .addModifier(0.5f, ModifierType.ADDITION, PerksAS.AttributeTypes.LUCK)
                .connect(consistentLuck1)
                .build(registrar);

        var keyConsistentLuck = PerkDataBuilder.builder(PerkTypesAS.KEY_ADD_ENCHANTMENTS)
                .create(AstralSorcery.key("key_consistent_luck"), -26f, -17f)
                .setNameKey("perk.name.astralsorcery.named.consistent_luck")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.EVORSIO))
                .modify(perk -> perk.addEnchantment(EnchantmentModifier.addLevel(fortune, 1)))
                .connect(consistentLuck2)
                .build(registrar);

        var honedInfluence1 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("honed_influence_1"), -29f, -20f)
                .setNameKey("perk.name.astralsorcery.generic.inc.block_reach")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.EVORSIO))
                .addModifier(0.04f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.BLOCK_REACH)
                .connect(travel40)
                .build(registrar);

        var honedInfluence2 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("honed_influence_2"), -30f, -21f)
                .setNameKey("perk.name.astralsorcery.generic.inc.block_reach")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.EVORSIO))
                .addModifier(0.04f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.BLOCK_REACH)
                .connect(honedInfluence1)
                .build(registrar);

        var keyHonedInfluence = PerkDataBuilder.builder(PerkTypesAS.MAJOR_PERK)
                .create(AstralSorcery.key("key_honed_influence"), -31f, -20f)
                .setNameKey("perk.name.astralsorcery.named.honed_influence")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.EVORSIO))
                .addModifier(1.5f, ModifierType.ADDITION, PerksAS.AttributeTypes.ATTACK_REACH)
                .connect(honedInfluence2)
                .build(registrar);

        var illusoryHammer1 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("illusory_hammer_1"), -11f, -9f)
                .setNameKey("perk.name.astralsorcery.generic.inc.block_break_speed")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.EVORSIO))
                .addModifier(0.03f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.BLOCK_BREAK_SPEED)
                .connect(evorsioBridge3)
                .build(registrar);

        var illusoryHammer2 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("illusory_hammer_2"), -12f, -10f)
                .setNameKey("perk.name.astralsorcery.generic.inc.block_break_speed")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.EVORSIO))
                .addModifier(0.03f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.BLOCK_BREAK_SPEED)
                .connect(illusoryHammer1)
                .build(registrar);

        var keyIllusoryHammer = PerkDataBuilder.builder(PerkTypesAS.MAJOR_PERK)
                .create(AstralSorcery.key("key_illusory_hammer"), -13f, -9f)
                .setNameKey("perk.name.astralsorcery.named.illusory_hammer")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.EVORSIO))
                .addModifier(1f, ModifierType.ADDITION, PerksAS.AttributeTypes.MINING_SIZE)
                .connect(illusoryHammer2)
                .build(registrar);

        var breakingAim1 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("breaking_aim_1"), -15f, -13f)
                .setNameKey("perk.name.astralsorcery.generic.add.critical_hit_chance")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.EVORSIO))
                .addModifier(0.02f, ModifierType.ADDITION, PerksAS.AttributeTypes.CRITICAL_HIT_CHANCE)
                .connect(evorsioBridge2)
                .build(registrar);

        var breakingAim2 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("breaking_aim_2"), -16f, -12f)
                .setNameKey("perk.name.astralsorcery.generic.add.critical_hit_chance")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.EVORSIO))
                .addModifier(0.02f, ModifierType.ADDITION, PerksAS.AttributeTypes.CRITICAL_HIT_CHANCE)
                .connect(breakingAim1)
                .build(registrar);

        var keyBreakingAim = PerkDataBuilder.builder(PerkTypesAS.KEY_DISARM)
                .create(AstralSorcery.key("key_breaking_aim"), -15f, -11f)
                .setNameKey("perk.name.astralsorcery.named.breaking_aim")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.EVORSIO))
                .connect(breakingAim2)
                .build(registrar);

        var differentAngles1 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("different_angles_1"), -4f, -15f)
                .setNameKey("perk.name.astralsorcery.generic.inc.block_reach")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.EVORSIO))
                .addModifier(0.04f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.BLOCK_REACH)
                .connect(evorsioBridge6)
                .build(registrar);

        var differentAngles2 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("different_angles_2"), -3f, -16f)
                .setNameKey("perk.name.astralsorcery.generic.inc.block_reach")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.EVORSIO))
                .addModifier(0.04f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.BLOCK_REACH)
                .connect(differentAngles1)
                .build(registrar);

        var keyDifferentAngles = PerkDataBuilder.builder(PerkTypesAS.KEY_ALL_TOOL_TYPES)
                .create(AstralSorcery.key("key_different_angles"), -4f, -17f)
                .setNameKey("perk.name.astralsorcery.named.different_angles")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.EVORSIO))
                .connect(differentAngles2)
                .build(registrar);

        var bluntForce1 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("blunt_force_1"), -8f, -19f)
                .setNameKey("perk.name.astralsorcery.generic.inc.attack_damage")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.EVORSIO))
                .addModifier(0.04f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.ATTACK_DAMAGE)
                .connect(evorsioBridge5)
                .build(registrar);

        var bluntForce2 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("blunt_force_2"), -7f, -20f)
                .setNameKey("perk.name.astralsorcery.generic.inc.attack_damage")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.EVORSIO))
                .addModifier(0.04f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.ATTACK_DAMAGE)
                .connect(bluntForce1)
                .build(registrar);

        var keyBluntForce = PerkDataBuilder.builder(PerkTypesAS.MAJOR_PERK)
                .create(AstralSorcery.key("key_blunt_force"), -6f, -19f)
                .setNameKey("perk.name.astralsorcery.named.blunt_force")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.EVORSIO))
                .addModifier(0.08f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.ATTACK_DAMAGE)
                .addModifier(0.16f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.CRITICAL_HIT_DAMAGE)
                .connect(bluntForce2)
                .build(registrar);

        var cullingStrike1 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("culling_strike_1"), 23f, -32f)
                .setNameKey("perk.name.astralsorcery.generic.inc.critical_hit_chance")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.DISCIDIA))
                .addModifier(0.04f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.CRITICAL_HIT_CHANCE)
                .connect(travel18)
                .build(registrar);

        var cullingStrike2 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("culling_strike_2"), 24f, -33f)
                .setNameKey("perk.name.astralsorcery.generic.inc.critical_hit_chance")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.DISCIDIA))
                .addModifier(0.04f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.CRITICAL_HIT_CHANCE)
                .connect(cullingStrike1)
                .build(registrar);

        var cullingStrike3 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("culling_strike_3"), 23f, -34f)
                .setNameKey("perk.name.astralsorcery.generic.inc.critical_hit_chance")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.DISCIDIA))
                .addModifier(0.04f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.CRITICAL_HIT_CHANCE)
                .connect(cullingStrike2)
                .build(registrar);

        var keyCullingStrike = PerkDataBuilder.builder(PerkTypesAS.KEY_CULLING_ATTACK)
                .create(AstralSorcery.key("key_culling_strike"), 22f, -33f)
                .setNameKey("perk.name.astralsorcery.named.culling_strike")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.DISCIDIA))
                .connect(cullingStrike3)
                .build(registrar);

        var lethality1 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("lethality_1"), 8f, -33f)
                .setNameKey("perk.name.astralsorcery.generic.inc.critical_hit_damage")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.DISCIDIA))
                .addModifier(0.1f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.CRITICAL_HIT_DAMAGE)
                .connect(travel16)
                .build(registrar);

        var lethality2 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("lethality_2"), 9f, -34f)
                .setNameKey("perk.name.astralsorcery.generic.inc.critical_hit_damage")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.DISCIDIA))
                .addModifier(0.1f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.CRITICAL_HIT_DAMAGE)
                .connect(lethality1)
                .build(registrar);

        var keyLethality = PerkDataBuilder.builder(PerkTypesAS.MAJOR_PERK)
                .create(AstralSorcery.key("key_lethality"), 10f, -33f)
                .setNameKey("perk.name.astralsorcery.named.lethality")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.DISCIDIA))
                .addModifier(0.2f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.CRITICAL_HIT_DAMAGE)
                .connect(lethality2)
                .build(registrar);

        var cursedTouch1 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("cursed_touch_1"), 30f, -21f)
                .setNameKey("perk.name.astralsorcery.generic.inc.attack_speed")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.DISCIDIA))
                .addModifier(0.02f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.ATTACK_SPEED)
                .connect(travel20)
                .build(registrar);

        var cursedTouch2 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("cursed_touch_2"), 31f, -22f)
                .setNameKey("perk.name.astralsorcery.generic.inc.attack_speed")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.DISCIDIA))
                .addModifier(0.04f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.ATTACK_SPEED)
                .connect(cursedTouch1)
                .build(registrar);

        var cursedTouch3 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("cursed_touch_3"), 30f, -23f)
                .setNameKey("perk.name.astralsorcery.generic.inc.attack_speed")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.DISCIDIA))
                .addModifier(0.06f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.ATTACK_SPEED)
                .connect(cursedTouch2)
                .build(registrar);

        var keyCursedTouch = PerkDataBuilder.builder(PerkTypesAS.KEY_DAMAGE_EFFECTS)
                .create(AstralSorcery.key("key_cursed_touch"), 31f, -24f)
                .setNameKey("perk.name.astralsorcery.named.cursed_touch")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.DISCIDIA))
                .connect(cursedTouch3)
                .build(registrar);

        var dextralDraw1 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("dextral_draw_1"), 26f, -17f)
                .setNameKey("perk.name.astralsorcery.generic.inc.projectile_speed")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.DISCIDIA))
                .addModifier(0.04f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.PROJECTILE_SPEED)
                .connect(travel20)
                .build(registrar);

        var dextralDraw2 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("dextral_draw_2"), 25f, -16f)
                .setNameKey("perk.name.astralsorcery.generic.inc.projectile_speed")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.DISCIDIA))
                .addModifier(0.04f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.PROJECTILE_SPEED)
                .connect(dextralDraw1)
                .build(registrar);

        var keyDextralDraw = PerkDataBuilder.builder(PerkTypesAS.MAJOR_PERK)
                .create(AstralSorcery.key("key_dextral_draw"), 26f, -15f)
                .setNameKey("perk.name.astralsorcery.named.dextral_draw")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.DISCIDIA))
                .addModifier(0.06f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.PROJECTILE_SPEED)
                .addModifier(0.06f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.PROJECTILE_DAMAGE)
                .connect(dextralDraw2)
                .build(registrar);

        var longShot1 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("long_shot_1"), 15f, -14f)
                .setNameKey("perk.name.astralsorcery.generic.inc.projectile_damage")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.DISCIDIA))
                .addModifier(0.05f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.PROJECTILE_DAMAGE)
                .connect(discidiaBridge2)
                .build(registrar);

        var longShot2 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("long_shot_2"), 16f, -13f)
                .setNameKey("perk.name.astralsorcery.generic.inc.projectile_damage")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.DISCIDIA))
                .addModifier(0.05f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.PROJECTILE_DAMAGE)
                .connect(longShot1)
                .build(registrar);

        var keyLongShot = PerkDataBuilder.builder(PerkTypesAS.KEY_PROJECTILE_DISTANCE)
                .create(AstralSorcery.key("key_long_shot"), 15f, -12f)
                .setNameKey("perk.name.astralsorcery.named.long_shot")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.DISCIDIA))
                .connect(longShot2)
                .build(registrar);

        var bluntBolts1 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("blunt_bolts_1"), 5f, -18f)
                .setNameKey("perk.name.astralsorcery.generic.inc.projectile_damage")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.DISCIDIA))
                .addModifier(0.05f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.PROJECTILE_DAMAGE)
                .connect(discidiaBridge5)
                .build(registrar);

        var bluntBolts2 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("blunt_bolts_2"), 4f, -19f)
                .setNameKey("perk.name.astralsorcery.generic.inc.projectile_damage")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.DISCIDIA))
                .addModifier(0.05f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.PROJECTILE_DAMAGE)
                .connect(bluntBolts1)
                .build(registrar);

        var keyBluntBolts = PerkDataBuilder.builder(PerkTypesAS.KEY_PROJECTILE_PROXIMITY)
                .create(AstralSorcery.key("key_blunt_bolts"), 5f, -20f)
                .setNameKey("perk.name.astralsorcery.named.blunt_bolts")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.DISCIDIA))
                .connect(bluntBolts2)
                .build(registrar);

        var rampage1 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("rampage_1"), 4f, -14f)
                .setNameKey("perk.name.astralsorcery.generic.inc.move_speed")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.DISCIDIA))
                .addModifier(0.02f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.MOVEMENT_SPEED)
                .connect(discidiaBridge6)
                .build(registrar);

        var rampage2 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("rampage_2"), 3f, -15f)
                .setNameKey("perk.name.astralsorcery.generic.inc.move_speed")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.DISCIDIA))
                .addModifier(0.02f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.MOVEMENT_SPEED)
                .connect(rampage1)
                .build(registrar);

        var keyRampage = PerkDataBuilder.builder(PerkTypesAS.KEY_RAMPAGE)
                .create(AstralSorcery.key("key_rampage"), 4f, -16f)
                .setNameKey("perk.name.astralsorcery.named.rampage")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.DISCIDIA))
                .connect(rampage2)
                .build(registrar);

        var combatFocus1 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("combat_focus_1"), 11f, -10f)
                .setNameKey("perk.name.astralsorcery.generic.inc.critical_hit_damage")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.DISCIDIA))
                .addModifier(0.06f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.CRITICAL_HIT_DAMAGE)
                .connect(discidiaBridge3)
                .build(registrar);

        var combatFocus2 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("combat_focus_2"), 12f, -9f)
                .setNameKey("perk.name.astralsorcery.generic.inc.critical_hit_damage")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.DISCIDIA))
                .addModifier(0.06f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.CRITICAL_HIT_DAMAGE)
                .connect(combatFocus1)
                .build(registrar);

        var keyCombatFocus = PerkDataBuilder.builder(PerkTypesAS.MAJOR_PERK)
                .create(AstralSorcery.key("key_combat_focus"), 13f, -10f)
                .setNameKey("perk.name.astralsorcery.named.combat_focus")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.DISCIDIA))
                .addModifier(0.14f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.CRITICAL_HIT_DAMAGE)
                .addModifier(0.04f, ModifierType.ADDITION, PerksAS.AttributeTypes.CRITICAL_HIT_CHANCE)
                .connect(combatFocus2)
                .build(registrar);

        var keyUnwavering = PerkDataBuilder.builder(PerkTypesAS.KEY_NO_KNOCKBACK)
                .create(AstralSorcery.key("key_unwavering"), 15f, 10f)
                .setNameKey("perk.name.astralsorcery.named.unwavering")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.ARMARA))
                .build(registrar);

        var unwavering1 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("unwavering_1"), 14f, 9f)
                .setNameKey("perk.name.astralsorcery.generic.add.life")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.ARMARA))
                .addModifier(1f, ModifierType.ADDITION, PerksAS.AttributeTypes.MAX_HEALTH)
                .connect(armaraBridge2)
                .connect(keyUnwavering)
                .build(registrar);

        var bodyBlocking1 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("body_blocking_1"), 11f, 8f)
                .setNameKey("perk.name.astralsorcery.generic.inc.armor")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.ARMARA))
                .addModifier(0.05f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.ARMOR)
                .connect(armaraBridge3)
                .build(registrar);

        var bodyBlocking2 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("body_blocking_2"), 10f, 9f)
                .setNameKey("perk.name.astralsorcery.generic.inc.armor")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.ARMARA))
                .addModifier(0.05f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.ARMOR)
                .connect(bodyBlocking1)
                .build(registrar);

        var keyBodyBlocking = PerkDataBuilder.builder(PerkTypesAS.KEY_DAMAGE_ARMOR)
                .create(AstralSorcery.key("key_body_blocking"), 9f, 8f)
                .setNameKey("perk.name.astralsorcery.named.body_blocking")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.ARMARA))
                .connect(bodyBlocking2)
                .build(registrar);

        var firmness1 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("firmness_1"), 19f, 1f)
                .setNameKey("perk.name.astralsorcery.hybrid.armor_armor_toughness")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.ARMARA))
                .addModifier(1f, ModifierType.ADDITION, PerksAS.AttributeTypes.ARMOR)
                .addModifier(0.5f, ModifierType.ADDITION, PerksAS.AttributeTypes.ARMOR_TOUGHNESS)
                .connect(armaraBridge5)
                .build(registrar);

        var firmness2 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("firmness_2"), 20f, 0f)
                .setNameKey("perk.name.astralsorcery.hybrid.armor_armor_toughness")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.ARMARA))
                .addModifier(1f, ModifierType.ADDITION, PerksAS.AttributeTypes.ARMOR)
                .addModifier(0.5f, ModifierType.ADDITION, PerksAS.AttributeTypes.ARMOR_TOUGHNESS)
                .connect(firmness1)
                .build(registrar);

        var keyFirmness = PerkDataBuilder.builder(PerkTypesAS.MAJOR_PERK)
                .create(AstralSorcery.key("key_firmness"), 19f, -1f)
                .setNameKey("perk.name.astralsorcery.named.firmness")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.ARMARA))
                .addModifier(4f, ModifierType.ADDITION, PerksAS.AttributeTypes.ARMOR)
                .addModifier(1.5f, ModifierType.ADDITION, PerksAS.AttributeTypes.ARMOR_TOUGHNESS)
                .connect(firmness2)
                .build(registrar);

        var tenacity1 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("tenacity_1"), 14f, -1f)
                .setNameKey("perk.name.astralsorcery.generic.inc.life_recovery")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.ARMARA))
                .addModifier(0.15f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.LIFE_RECOVERY)
                .connect(armaraBridge6)
                .build(registrar);

        var tenacity2 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("tenacity_2"), 15f, -2f)
                .setNameKey("perk.name.astralsorcery.generic.inc.life_recovery")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.ARMARA))
                .addModifier(0.15f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.LIFE_RECOVERY)
                .connect(tenacity1)
                .build(registrar);

        var keyTenacity = PerkDataBuilder.builder(PerkTypesAS.MAJOR_PERK)
                .create(AstralSorcery.key("key_tenacity"), 16f, -1f)
                .setNameKey("perk.name.astralsorcery.named.tenacity")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.ARMARA))
                .addModifier(2f, ModifierType.ADDITION, PerksAS.AttributeTypes.MAX_HEALTH)
                .addModifier(0.1f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.LIFE_RECOVERY)
                .connect(tenacity2)
                .build(registrar);

        var phoenixBlessing1 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("phoenix_blessing_1"), 32f, -4f)
                .setNameKey("perk.name.astralsorcery.hybrid.life_armor")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.ARMARA))
                .addModifier(1f, ModifierType.ADDITION, PerksAS.AttributeTypes.MAX_HEALTH)
                .addModifier(0.5f, ModifierType.ADDITION, PerksAS.AttributeTypes.ARMOR)
                .connect(travel22)
                .build(registrar);

        var phoenixBlessing2 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("phoenix_blessing_2"), 31f, -5f)
                .setNameKey("perk.name.astralsorcery.hybrid.life_armor")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.ARMARA))
                .addModifier(0.5f, ModifierType.ADDITION, PerksAS.AttributeTypes.MAX_HEALTH)
                .addModifier(1f, ModifierType.ADDITION, PerksAS.AttributeTypes.ARMOR)
                .connect(phoenixBlessing1)
                .build(registrar);

        var keyPhoenixBlessing = PerkDataBuilder.builder(PerkTypesAS.KEY_CHEAT_DEATH)
                .create(AstralSorcery.key("key_phoenix_blessing"), 29f, -5f)
                .setNameKey("perk.name.astralsorcery.named.phoenix_blessing")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.ARMARA))
                .build(registrar);

        var phoenixBlessing3 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("phoenix_blessing_3"), 30f, -4f)
                .setNameKey("perk.name.astralsorcery.hybrid.life_armor")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.ARMARA))
                .addModifier(1f, ModifierType.ADDITION, PerksAS.AttributeTypes.MAX_HEALTH)
                .addModifier(1f, ModifierType.ADDITION, PerksAS.AttributeTypes.ARMOR)
                .connect(keyPhoenixBlessing)
                .connect(phoenixBlessing2)
                .build(registrar);

        var dislocatedReflection1 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("dislocated_reflection_1"), 36f, 3f)
                .setNameKey("perk.name.astralsorcery.generic.inc.damage_reflect")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.ARMARA))
                .addModifier(0.03f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.DAMAGE_REFLECT)
                .connect(travel23)
                .build(registrar);

        var dislocatedReflection2 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("dislocated_reflection_2"), 37f, 4f)
                .setNameKey("perk.name.astralsorcery.generic.inc.damage_reflect")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.ARMARA))
                .addModifier(0.03f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.DAMAGE_REFLECT)
                .connect(dislocatedReflection1)
                .build(registrar);

        var keydislocatedReflection = PerkDataBuilder.builder(PerkTypesAS.MAJOR_PERK)
                .create(AstralSorcery.key("keydislocated_reflection"), 36f, 5f)
                .setNameKey("perk.name.astralsorcery.named.dislocated_reflection")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.ARMARA))
                .addModifier(0.14f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.DAMAGE_REFLECT)
                .connect(dislocatedReflection2)
                .build(registrar);

        var diamondSkin1 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("diamond_skin_1"), 36f, 15f)
                .setNameKey("perk.name.astralsorcery.generic.add.armor")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.ARMARA))
                .addModifier(1f, ModifierType.ADDITION, PerksAS.AttributeTypes.ARMOR)
                .connect(travel24)
                .build(registrar);

        var diamondSkin2 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("diamond_skin_2"), 37f, 14f)
                .setNameKey("perk.name.astralsorcery.generic.add.armor")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.ARMARA))
                .addModifier(1f, ModifierType.ADDITION, PerksAS.AttributeTypes.ARMOR)
                .connect(diamondSkin1)
                .build(registrar);

        var diamondSkin3 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("diamond_skin_3"), 38f, 15f)
                .setNameKey("perk.name.astralsorcery.generic.add.armor")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.ARMARA))
                .addModifier(1f, ModifierType.ADDITION, PerksAS.AttributeTypes.ARMOR)
                .connect(diamondSkin2)
                .build(registrar);

        var keyDiamondSkin = PerkDataBuilder.builder(PerkTypesAS.KEY_NO_ARMOR)
                .create(AstralSorcery.key("key_diamond_skin"), 39f, 14f)
                .setNameKey("perk.name.astralsorcery.named.diamond_skin")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.ARMARA))
                .connect(diamondSkin3)
                .build(registrar);

        var clarity1 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("clarity_1"), 24f, 24f)
                .setNameKey("perk.name.astralsorcery.generic.inc.perk_experience")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.ARMARA))
                .addModifier(0.04f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.PERK_EXPERIENCE)
                .connect(travel26)
                .build(registrar);

        var clarity2 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("clarity_2"), 25f, 25f)
                .setNameKey("perk.name.astralsorcery.generic.inc.perk_experience")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.ARMARA))
                .addModifier(0.04f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.PERK_EXPERIENCE)
                .connect(clarity1)
                .build(registrar);

        var keyClarity = PerkDataBuilder.builder(PerkTypesAS.MAJOR_PERK)
                .create(AstralSorcery.key("key_clarity"), 26f, 24f)
                .setNameKey("perk.name.astralsorcery.named.clarity")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.ARMARA))
                .addModifier(0.14f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.PERK_EXPERIENCE)
                .connect(clarity2)
                .build(registrar);

        var osmosis1 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("osmosis_1"), 22f, 20f)
                .setNameKey("perk.name.astralsorcery.hybrid.life_cooldown_reduction")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.ARMARA))
                .addModifier(0.05f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.COOLDOWN_REDUCTION)
                .addModifier(0.05f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.LIFE_RECOVERY)
                .connect(travel26)
                .build(registrar);

        var osmosis2 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("osmosis_2"), 21f, 19f)
                .setNameKey("perk.name.astralsorcery.hybrid.life_cooldown_reduction")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.ARMARA))
                .addModifier(0.05f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.COOLDOWN_REDUCTION)
                .addModifier(0.05f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.LIFE_RECOVERY)
                .connect(osmosis1)
                .build(registrar);

        var keyOsmosis = PerkDataBuilder.builder(PerkTypesAS.MAJOR_PERK)
                .create(AstralSorcery.key("key_osmosis"), 20f, 20f)
                .setNameKey("perk.name.astralsorcery.named.osmosis")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.ARMARA))
                .addModifier(0.08f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.COOLDOWN_REDUCTION)
                .addModifier(0.1f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.LIFE_RECOVERY)
                .connect(osmosis2)
                .build(registrar);

        var arrowSlits1 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("arrow_slits_1"), 36f, -4f)
                .setNameKey("perk.name.astralsorcery.generic.inc.projectile_damage")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.ARMARA))
                .addModifier(0.04f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.PROJECTILE_DAMAGE)
                .connect(travel22)
                .build(registrar);

        var arrowSlits2 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("arrow_slits_2"), 37f, -5f)
                .setNameKey("perk.name.astralsorcery.generic.inc.projectile_damage")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.ARMARA))
                .addModifier(0.04f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.PROJECTILE_DAMAGE)
                .connect(arrowSlits1)
                .build(registrar);

        var keyArrowSlits = PerkDataBuilder.builder(PerkTypesAS.MAJOR_PERK)
                .create(AstralSorcery.key("key_arrow_slits"), 36f, -6f)
                .setNameKey("perk.name.astralsorcery.named.arrow_slits")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.ARMARA))
                .addModifier(2f, ModifierType.ADDITION, PerksAS.AttributeTypes.ARMOR)
                .addModifier(2f, ModifierType.ADDITION, PerksAS.AttributeTypes.PROJECTILE_DAMAGE)
                .connect(arrowSlits2)
                .build(registrar);

        var zeal1 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("zeal_1"), -4f, 18f)
                .setNameKey("perk.name.astralsorcery.generic.inc.attack_speed")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.VICIO))
                .addModifier(0.04f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.ATTACK_SPEED)
                .connect(vicioBridge2)
                .build(registrar);

        var keyZeal = PerkDataBuilder.builder(PerkTypesAS.MAJOR_PERK)
                .create(AstralSorcery.key("key_zeal"), -5f, 17f)
                .setNameKey("perk.name.astralsorcery.named.zeal")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.VICIO))
                .addModifier(0.12f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.ATTACK_SPEED)
                .connect(zeal1)
                .build(registrar);

        var biggerStomach1 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("bigger_stomach_1"), 5f, 17f)
                .setNameKey("perk.name.astralsorcery.generic.inc.move_speed")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.VICIO))
                .addModifier(0.02f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.MOVEMENT_SPEED)
                .connect(vicioBridge5)
                .build(registrar);

        var biggerStomach2 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("bigger_stomach_2"), 6f, 16f)
                .setNameKey("perk.name.astralsorcery.generic.inc.move_speed")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.VICIO))
                .addModifier(0.02f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.MOVEMENT_SPEED)
                .connect(biggerStomach1)
                .build(registrar);

        var keyBiggerStomach = PerkDataBuilder.builder(PerkTypesAS.KEY_REDUCED_FOOD)
                .create(AstralSorcery.key("key_bigger_stomach"), 5f, 15f)
                .setNameKey("perk.name.astralsorcery.named.bigger_stomach")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.VICIO))
                .connect(biggerStomach2)
                .build(registrar);

        var haste1 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("haste_1"), 4f, 12f)
                .setNameKey("perk.name.astralsorcery.generic.inc.block_break_speed")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.VICIO))
                .addModifier(0.05f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.BLOCK_BREAK_SPEED)
                .connect(vicioBridge6)
                .build(registrar);

        var keyHaste = PerkDataBuilder.builder(PerkTypesAS.MAJOR_PERK)
                .create(AstralSorcery.key("key_haste"), 5f, 13f)
                .setNameKey("perk.name.astralsorcery.named.haste")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.VICIO))
                .addModifier(0.12f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.BLOCK_BREAK_SPEED)
                .addModifier(0.03f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.MOVEMENT_SPEED)
                .connect(haste1)
                .build(registrar);

        var fins1 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("fins_1"), -4f, 12f)
                .setNameKey("perk.name.astralsorcery.generic.inc.swim_speed")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.VICIO))
                .addModifier(0.08f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.SWIM_SPEED)
                .connect(vicioBridge3)
                .build(registrar);

        var keyFins = PerkDataBuilder.builder(PerkTypesAS.MAJOR_PERK)
                .create(AstralSorcery.key("key_fins"), -5f, 13f)
                .setNameKey("perk.name.astralsorcery.named.fins")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.VICIO))
                .addModifier(0.16f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.SWIM_SPEED)
                .connect(fins1)
                .build(registrar);

        var hikingBoots1 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("hiking_boots_1"), 10f, 30f)
                .setNameKey("perk.name.astralsorcery.generic.inc.move_speed")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.VICIO))
                .addModifier(0.02f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.MOVEMENT_SPEED)
                .connect(travel28)
                .build(registrar);

        var hikingBoots2 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("hiking_boots_2"), 11f, 29f)
                .setNameKey("perk.name.astralsorcery.generic.inc.move_speed")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.VICIO))
                .addModifier(0.02f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.MOVEMENT_SPEED)
                .connect(hikingBoots1)
                .build(registrar);

        var keyHikingBoots = PerkDataBuilder.builder(PerkTypesAS.MAJOR_PERK)
                .create(AstralSorcery.key("key_hiking_boots"), 10f, 28f)
                .setNameKey("perk.name.astralsorcery.named.hiking_boots")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.VICIO))
                .addModifier(1f, ModifierType.ADDITION, PerksAS.AttributeTypes.STEP_HEIGHT)
                .addModifier(0.04f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.MOVEMENT_SPEED)
                .connect(hikingBoots2)
                .build(registrar);

        var dervish1 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("dervish_1"), 8f, 34f)
                .setNameKey("perk.name.astralsorcery.generic.inc.attack_speed")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.VICIO))
                .addModifier(0.03f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.ATTACK_SPEED)
                .connect(travel29)
                .build(registrar);

        var dervish2 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("dervish_2"), 9f, 35f)
                .setNameKey("perk.name.astralsorcery.generic.inc.attack_speed")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.VICIO))
                .addModifier(0.03f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.ATTACK_SPEED)
                .connect(dervish1)
                .build(registrar);

        var keyDervish = PerkDataBuilder.builder(PerkTypesAS.MAJOR_PERK)
                .create(AstralSorcery.key("key_dervish"), 8f, 36f)
                .setNameKey("perk.name.astralsorcery.named.dervish")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.VICIO))
                .addModifier(0.08f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.ATTACK_SPEED)
                .addModifier(1f, ModifierType.ADDITION, PerksAS.AttributeTypes.ATTACK_DAMAGE)
                .connect(dervish2)
                .build(registrar);

        var fleetFooted1 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("fleet_footed_1"), -4f, 35f)
                .setNameKey("perk.name.astralsorcery.generic.inc.cooldown_reduction")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.VICIO))
                .addModifier(0.04f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.COOLDOWN_REDUCTION)
                .connect(travel31)
                .build(registrar);

        var fleetFooted2 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("fleet_footed_2"), -5f, 36f)
                .setNameKey("perk.name.astralsorcery.generic.inc.cooldown_reduction")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.VICIO))
                .addModifier(0.04f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.COOLDOWN_REDUCTION)
                .connect(fleetFooted1)
                .build(registrar);

        var keyFleetFooted = PerkDataBuilder.builder(PerkTypesAS.MAJOR_PERK)
                .create(AstralSorcery.key("key_fleet_footed"), -4f, 37f)
                .setNameKey("perk.name.astralsorcery.named.fleet_footed")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.VICIO))
                .addModifier(0.08f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.COOLDOWN_REDUCTION)
                .addModifier(0.04f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.MOVEMENT_SPEED)
                .connect(fleetFooted2)
                .build(registrar);

        var spatialManipulation1 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("spatial_manipulation_1"), -9f, 29f)
                .setNameKey("perk.name.astralsorcery.generic.inc.attack_reach")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.VICIO))
                .addModifier(0.04f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.ATTACK_REACH)
                .connect(travel32)
                .build(registrar);

        var spatialManipulation2 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("spatial_manipulation_2"), -10f, 28f)
                .setNameKey("perk.name.astralsorcery.generic.inc.attack_reach")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.VICIO))
                .addModifier(0.04f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.ATTACK_REACH)
                .connect(spatialManipulation1)
                .build(registrar);

        var keySpatialManipulation = PerkDataBuilder.builder(PerkTypesAS.KEY_TELEPORT_DROPS)
                .create(AstralSorcery.key("key_spatial_manipulation"), -9f, 27f)
                .setNameKey("perk.name.astralsorcery.named.spatial_manipulation")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.VICIO))
                .connect(spatialManipulation2)
                .build(registrar);

        var keyVividGrowth = PerkDataBuilder.builder(PerkTypesAS.KEY_GROW_PLANTS)
                .create(AstralSorcery.key("key_vivid_growth"), -14f, 12f)
                .setNameKey("perk.name.astralsorcery.named.vivid_growth")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.AEVITAS))
                .build(registrar);

        var vividGrowth1 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("vivid_growth_1"), -14f, 10f)
                .setNameKey("perk.name.astralsorcery.hybrid.life_block_reach")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.AEVITAS))
                .addModifier(0.5f, ModifierType.ADDITION, PerksAS.AttributeTypes.MAX_HEALTH)
                .addModifier(0.04f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.BLOCK_REACH)
                .connect(aevitasBridge5)
                .build(registrar);

        var vividGrowth2 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("vivid_growth_2"), -15f, 11f)
                .setNameKey("perk.name.astralsorcery.hybrid.life_block_reach")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.AEVITAS))
                .addModifier(0.5f, ModifierType.ADDITION, PerksAS.AttributeTypes.MAX_HEALTH)
                .addModifier(0.04f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.BLOCK_REACH)
                .connect(vividGrowth1)
                .connect(keyVividGrowth)
                .build(registrar);

        var cleansing1 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("cleansing_1"), -13f, -1f)
                .setNameKey("perk.name.astralsorcery.generic.inc.life_recovery")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.AEVITAS))
                .addModifier(0.1f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.LIFE_RECOVERY)
                .connect(aevitasBridge3)
                .build(registrar);

        var keyCleansing = PerkDataBuilder.builder(PerkTypesAS.KEY_CLEANSE_NEGATIVE_EFFECTS)
                .create(AstralSorcery.key("key_cleansing"), -14f, -2f)
                .setNameKey("perk.name.astralsorcery.named.cleansing")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.AEVITAS))
                .connect(cleansing1)
                .build(registrar);

        var ironHeart1 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("iron_heart_1"), -17f, 1f)
                .setNameKey("perk.name.astralsorcery.hybrid.life_movespeed")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.AEVITAS))
                .addModifier(2f, ModifierType.ADDITION, PerksAS.AttributeTypes.MAX_HEALTH)
                .addModifier(0.95f, ModifierType.STACKING_MULTIPLY, PerksAS.AttributeTypes.MOVEMENT_SPEED)
                .connect(aevitasBridge2)
                .build(registrar);

        var ironHeart2 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("iron_heart_2"), -18f, 0f)
                .setNameKey("perk.name.astralsorcery.hybrid.life_movespeed")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.AEVITAS))
                .addModifier(2f, ModifierType.ADDITION, PerksAS.AttributeTypes.MAX_HEALTH)
                .addModifier(0.95f, ModifierType.STACKING_MULTIPLY, PerksAS.AttributeTypes.MOVEMENT_SPEED)
                .connect(ironHeart1)
                .build(registrar);

        var keyIronHeart2 = PerkDataBuilder.builder(PerkTypesAS.MAJOR_PERK)
                .create(AstralSorcery.key("key_iron_heart_2"), -17f, -1f)
                .setNameKey("perk.name.astralsorcery.named.iron_heart")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.AEVITAS))
                .addModifier(0.1f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.MAX_HEALTH)
                .addModifier(0.91f, ModifierType.STACKING_MULTIPLY, PerksAS.AttributeTypes.MOVEMENT_SPEED)
                .addModifier(4f, ModifierType.ADDITION, PerksAS.AttributeTypes.ARMOR)
                .connect(ironHeart2)
                .build(registrar);

        var adaptive1 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("adaptive_1"), -10f, 7f)
                .setNameKey("perk.name.astralsorcery.generic.inc.elemental_resistance")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.AEVITAS))
                .addModifier(0.04f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.ELEMENTAL_RESISTANCE)
                .connect(aevitasBridge6)
                .build(registrar);

        var adaptive2 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("adaptive_2"), -9f, 8f)
                .setNameKey("perk.name.astralsorcery.generic.inc.elemental_resistance")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.AEVITAS))
                .addModifier(0.04f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.ELEMENTAL_RESISTANCE)
                .connect(adaptive1)
                .build(registrar);

        var keyAdaptive = PerkDataBuilder.builder(PerkTypesAS.MAJOR_PERK)
                .create(AstralSorcery.key("key_adaptive"), -10f, 9f)
                .setNameKey("perk.name.astralsorcery.named.adaptive")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.AEVITAS))
                .addModifier(0.08f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.ELEMENTAL_RESISTANCE)
                .addModifier(0.05f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.DAMAGE_REDUCTION)
                .connect(adaptive2)
                .build(registrar);

        var keyMending = PerkDataBuilder.builder(PerkTypesAS.KEY_MEND_ARMOR)
                .create(AstralSorcery.key("key_mending"), -24f, 25f)
                .setNameKey("perk.name.astralsorcery.named.mending")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.AEVITAS))
                .build(registrar);

        var mending1 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("mending_1"), -22f, 25f)
                .setNameKey("perk.name.astralsorcery.generic.add.life")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.AEVITAS))
                .addModifier(0.5f, ModifierType.ADDITION, PerksAS.AttributeTypes.MAX_HEALTH)
                .connect(travel34)
                .build(registrar);

        var mending2 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("mending_2"), -23f, 26f)
                .setNameKey("perk.name.astralsorcery.generic.add.life")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.AEVITAS))
                .addModifier(0.5f, ModifierType.ADDITION, PerksAS.AttributeTypes.MAX_HEALTH)
                .connect(keyMending)
                .connect(mending1)
                .build(registrar);

        var vitality1 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("vitality_1"), -32f, -5f)
                .setNameKey("perk.name.astralsorcery.generic.add.life")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.AEVITAS))
                .addModifier(1f, ModifierType.ADDITION, PerksAS.AttributeTypes.MAX_HEALTH)
                .connect(travel38)
                .build(registrar);

        var vitality2 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("vitality_2"), -31f, -6f)
                .setNameKey("perk.name.astralsorcery.generic.add.life")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.AEVITAS))
                .addModifier(1f, ModifierType.ADDITION, PerksAS.AttributeTypes.MAX_HEALTH)
                .connect(vitality1)
                .build(registrar);

        var keyVitality = PerkDataBuilder.builder(PerkTypesAS.MAJOR_PERK)
                .create(AstralSorcery.key("key_vitality"), -30f, -5f)
                .setNameKey("perk.name.astralsorcery.named.vitality")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.AEVITAS))
                .addModifier(0.03f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.MAX_HEALTH)
                .addModifier(0.12f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.LIFE_RECOVERY)
                .connect(vitality2)
                .build(registrar);

        var sage1 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("sage_1"), -36f, 1f)
                .setNameKey("perk.name.astralsorcery.generic.inc.perk_experience")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.AEVITAS))
                .addModifier(0.05f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.PERK_EXPERIENCE)
                .connect(travel37)
                .build(registrar);

        var keySage = PerkDataBuilder.builder(PerkTypesAS.MAJOR_PERK)
                .create(AstralSorcery.key("key_sage"), -37f, 0f)
                .setNameKey("perk.name.astralsorcery.named.sage")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.AEVITAS))
                .addModifier(0.1f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.PERK_EXPERIENCE)
                .addModifier(0.04f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.PERK_EFFECT)
                .connect(sage1)
                .build(registrar);

        var nourishment1 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("nourishment_1"), -30f, 20f)
                .setNameKey("perk.name.astralsorcery.generic.inc.cooldown_reduction")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.AEVITAS))
                .addModifier(0.04f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.COOLDOWN_REDUCTION)
                .connect(travel35)
                .build(registrar);

        var nourishment2 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("nourishment_2"), -31f, 21f)
                .setNameKey("perk.name.astralsorcery.generic.inc.cooldown_reduction")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.AEVITAS))
                .addModifier(0.04f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.COOLDOWN_REDUCTION)
                .connect(nourishment1)
                .build(registrar);

        var leyNourishment = PerkDataBuilder.builder(PerkTypesAS.MAJOR_PERK)
                .create(AstralSorcery.key("ley_nourishment"), -30f, 22f)
                .setNameKey("perk.name.astralsorcery.named.nourishment")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.AEVITAS))
                .addModifier(0.04f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.COOLDOWN_REDUCTION)
                .addModifier(4f, ModifierType.ADDITION, PerksAS.AttributeTypes.MAX_HEALTH)
                .addModifier(1f, ModifierType.ADDITION, PerksAS.AttributeTypes.LUCK)
                .connect(nourishment2)
                .build(registrar);

        var cunning1 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("cunning_1"), 3f, -7f)
                .setNameKey("perk.name.astralsorcery.generic.add.luck")
                .addRequirement(PerkRequirementProgress.of(ResearchTier.LUMINANCE))
                .addModifier(0.5f, ModifierType.ADDITION, PerksAS.AttributeTypes.LUCK)
                .connect(travelCore2)
                .build(registrar);

        var keyCunning = PerkDataBuilder.builder(PerkTypesAS.MAJOR_PERK)
                .create(AstralSorcery.key("key_cunning"), 4f, -8f)
                .setNameKey("perk.name.astralsorcery.named.cunning")
                .addRequirement(PerkRequirementProgress.of(ResearchTier.LUMINANCE))
                .addModifier(1.5f, ModifierType.ADDITION, PerksAS.AttributeTypes.LUCK)
                .connect(cunning1)
                .build(registrar);

        var focused1 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("focused_1"), -4f, -2f)
                .setNameKey("perk.name.astralsorcery.generic.inc.perk_experience")
                .addRequirement(PerkRequirementProgress.of(ResearchTier.LUMINANCE))
                .addModifier(0.05f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.PERK_EXPERIENCE)
                .connect(travelCore0)
                .build(registrar);

        var keyFocused = PerkDataBuilder.builder(PerkTypesAS.MAJOR_PERK)
                .create(AstralSorcery.key("key_focused"), -5f, -3f)
                .setNameKey("perk.name.astralsorcery.named.focused")
                .addRequirement(PerkRequirementProgress.of(ResearchTier.LUMINANCE))
                .addModifier(0.12f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.PERK_EXPERIENCE)
                .connect(focused1)
                .build(registrar);

        var alchGenius1 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("alch_genius_1"), 1f, 4f)
                .setNameKey("perk.name.astralsorcery.generic.inc.potion_duration")
                .addRequirement(PerkRequirementProgress.of(ResearchTier.LUMINANCE))
                .addModifier(0.06f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.POTION_DURATION)
                .connect(travelCore4)
                .build(registrar);

        var keyAlchGenius = PerkDataBuilder.builder(PerkTypesAS.MAJOR_PERK)
                .create(AstralSorcery.key("key_alch_genius"), 2f, 5f)
                .setNameKey("perk.name.astralsorcery.named.alchemists_genius")
                .addRequirement(PerkRequirementProgress.of(ResearchTier.LUMINANCE))
                .addModifier(0.1f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.POTION_DURATION)
                .connect(alchGenius1)
                .build(registrar);

        var profaneChemistry1 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("profane_chemistry_1"), 0f, -8f)
                .setNameKey("perk.name.astralsorcery.generic.inc.potion_duration")
                .addRequirement(PerkRequirementProgress.of(ResearchTier.LUMINANCE))
                .addModifier(0.08f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.POTION_DURATION)
                .connect(travelCore1)
                .build(registrar);

        var keyProfaneChemistry = PerkDataBuilder.builder(PerkTypesAS.MAJOR_PERK)
                .create(AstralSorcery.key("key_profane_chemistry"), -1f, -9f)
                .setNameKey("perk.name.astralsorcery.named.profane_chemistry")
                .addRequirement(PerkRequirementProgress.of(ResearchTier.LUMINANCE))
                .addModifier(1.25f, ModifierType.STACKING_MULTIPLY, PerksAS.AttributeTypes.POTION_DURATION)
                .addModifier(0.7f, ModifierType.STACKING_MULTIPLY, PerksAS.AttributeTypes.MAX_HEALTH)
                .connect(profaneChemistry1)
                .build(registrar);

        var tiltedPendulum1 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("tilted_pendulum_1"), 7f, 2f)
                .setNameKey("perk.name.astralsorcery.generic.inc.cooldown_reduction")
                .addRequirement(PerkRequirementProgress.of(ResearchTier.LUMINANCE))
                .addModifier(0.08f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.COOLDOWN_REDUCTION)
                .connect(travelCore3)
                .build(registrar);

        var keyTiltedPendulum = PerkDataBuilder.builder(PerkTypesAS.MAJOR_PERK)
                .create(AstralSorcery.key("key_tilted_pendulum"), 8f, 1f)
                .setNameKey("perk.name.astralsorcery.named.tilted_pendulum")
                .addRequirement(PerkRequirementProgress.of(ResearchTier.LUMINANCE))
                .addModifier(0.25f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.COOLDOWN_REDUCTION)
                .addModifier(0.8f, ModifierType.STACKING_MULTIPLY, PerksAS.AttributeTypes.ARMOR)
                .connect(tiltedPendulum1)
                .build(registrar);

        var enduring1 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("enduring_1"), -3f, 2f)
                .setNameKey("perk.name.astralsorcery.generic.inc.damage_reduction")
                .addRequirement(PerkRequirementProgress.of(ResearchTier.LUMINANCE))
                .addModifier(0.03f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.DAMAGE_REDUCTION)
                .connect(travelCore0)
                .build(registrar);

        var keyEnduring = PerkDataBuilder.builder(PerkTypesAS.KEY_ADD_ENCHANTMENTS)
                .create(AstralSorcery.key("key_enduring"), -4f, 3f)
                .setNameKey("perk.name.astralsorcery.named.enduring")
                .addRequirement(PerkRequirementProgress.of(ResearchTier.LUMINANCE))
                .modify(perk -> perk.addEnchantment(EnchantmentModifier.addLevel(unbreaking, 1)))
                .connect(enduring1)
                .build(registrar);

        var endlessMunitions1 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("endless_munitions_1"), 3f, 0f)
                .setNameKey("perk.name.astralsorcery.generic.inc.projectile_damage")
                .addRequirement(PerkRequirementProgress.of(ResearchTier.LUMINANCE))
                .addModifier(0.05f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.PROJECTILE_DAMAGE)
                .connect(travelCore3)
                .build(registrar);

        var keyEndlessMunitions = PerkDataBuilder.builder(PerkTypesAS.KEY_ADD_ENCHANTMENTS)
                .create(AstralSorcery.key("key_endless_munitions"), 4f, -1f)
                .setNameKey("perk.name.astralsorcery.named.endless_munitions")
                .addRequirement(PerkRequirementProgress.of(ResearchTier.LUMINANCE))
                .modify(perk -> perk.addEnchantment(EnchantmentModifier.addLevel(infinity, 1)))
                .connect(endlessMunitions1)
                .build(registrar);

        var precision1 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("precision_1"), -3f, -6f)
                .setNameKey("perk.name.astralsorcery.generic.inc.critical_hit_chance")
                .addRequirement(PerkRequirementProgress.of(ResearchTier.LUMINANCE))
                .addModifier(0.05f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.CRITICAL_HIT_CHANCE)
                .connect(travelCore1)
                .build(registrar);

        var precision2 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("precision_2"), -4f, -5f)
                .setNameKey("perk.name.astralsorcery.generic.inc.critical_hit_chance")
                .addRequirement(PerkRequirementProgress.of(ResearchTier.LUMINANCE))
                .addModifier(0.05f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.CRITICAL_HIT_CHANCE)
                .connect(precision1)
                .build(registrar);

        var keyPrecision = PerkDataBuilder.builder(PerkTypesAS.MAJOR_PERK)
                .create(AstralSorcery.key("key_precision"), -5f, -6f)
                .setNameKey("perk.name.astralsorcery.named.precision")
                .addRequirement(PerkRequirementProgress.of(ResearchTier.LUMINANCE))
                .addModifier(0.08f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.CRITICAL_HIT_CHANCE)
                .addModifier(0.1f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.PERK_EFFECT)
                .connect(precision2)
                .build(registrar);

        var vampirism1 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("vampirism_1"), 6f, -3f)
                .setNameKey("perk.name.astralsorcery.generic.add.life_leech")
                .addRequirement(PerkRequirementProgress.of(ResearchTier.LUMINANCE))
                .addModifier(0.03f, ModifierType.ADDITION, PerksAS.AttributeTypes.LIFE_LEECH)
                .connect(travelCore2)
                .build(registrar);

        var keyVampirism = PerkDataBuilder.builder(PerkTypesAS.MAJOR_PERK)
                .create(AstralSorcery.key("key_vampirism"), 7f, -4f)
                .setNameKey("perk.name.astralsorcery.named.vampirism")
                .addRequirement(PerkRequirementProgress.of(ResearchTier.LUMINANCE))
                .addModifier(0.07f, ModifierType.ADDITION, PerksAS.AttributeTypes.LIFE_LEECH)
                .connect(vampirism1)
                .build(registrar);

        var prismaticShimmer1 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("prismatic_shimmer_1"), -7f, 2f)
                .setNameKey("perk.name.astralsorcery.generic.inc.dynamic_enchantment_effect")
                .addRequirement(PerkRequirementProgress.of(ResearchTier.LUMINANCE))
                .addModifier(0.05f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.ENCHANTMENT_EFFECT)
                .connect(travelCore0)
                .build(registrar);

        var prismaticShimmer2 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("prismatic_shimmer_2"), -8f, 3f)
                .setNameKey("perk.name.astralsorcery.generic.inc.dynamic_enchantment_effect")
                .addRequirement(PerkRequirementProgress.of(ResearchTier.LUMINANCE))
                .addModifier(0.05f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.ENCHANTMENT_EFFECT)
                .connect(prismaticShimmer1)
                .build(registrar);

        var keyPrismaticShimmer = PerkDataBuilder.builder(PerkTypesAS.MAJOR_PERK)
                .create(AstralSorcery.key("key_prismatic_shimmer"), -7f, 4f)
                .setNameKey("perk.name.astralsorcery.named.prismatic_shimmer")
                .addRequirement(PerkRequirementProgress.of(ResearchTier.LUMINANCE))
                .addModifier(0.15f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.ENCHANTMENT_EFFECT)
                .connect(prismaticShimmer2)
                .build(registrar);

        var compact1 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("compact_1"), -3f, 7f)
                .setNameKey("perk.name.astralsorcery.generic.less.scale")
                .addRequirement(PerkRequirementProgress.of(ResearchTier.LUMINANCE))
                .addModifier(0.95f, ModifierType.STACKING_MULTIPLY, PerksAS.AttributeTypes.SCALE)
                .connect(travelCore4)
                .build(registrar);

        var keyCompact = PerkDataBuilder.builder(PerkTypesAS.MAJOR_PERK)
                .create(AstralSorcery.key("key_compact"), -4f, 6f)
                .setNameKey("perk.name.astralsorcery.named.compact")
                .addRequirement(PerkRequirementProgress.of(ResearchTier.LUMINANCE))
                .addModifier(0.85f, ModifierType.STACKING_MULTIPLY, PerksAS.AttributeTypes.SCALE)
                .connect(compact1)
                .build(registrar);

        var lastBreath1 = PerkDataBuilder.builder(PerkTypesAS.MODIFIER_PERK)
                .create(AstralSorcery.key("last_breath_1"), -8f, -32f)
                .setNameKey("perk.name.astralsorcery.generic.inc.block_break_speed")
                .addRequirement(PerkRequirementConstellation.of(ConstellationsAS.EVORSIO))
                .addModifier(0.03f, ModifierType.ADDED_MULTIPLY, PerksAS.AttributeTypes.BLOCK_BREAK_SPEED)
                .connect(lastBreath2)
                .connect(travel44)
                .build(registrar);

    }
}
