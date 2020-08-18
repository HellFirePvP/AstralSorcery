package hellfirepvp.astralsorcery.datagen.data.perks;

import hellfirepvp.astralsorcery.common.lib.ConstellationsAS;
import hellfirepvp.astralsorcery.common.lib.PerkConvertersAS;
import hellfirepvp.astralsorcery.common.lib.PerkCustomModifiersAS;
import hellfirepvp.astralsorcery.common.perk.data.PerkTypeHandler;
import hellfirepvp.astralsorcery.common.perk.data.builder.PerkDataBuilder;
import hellfirepvp.astralsorcery.common.perk.data.builder.PerkDataProvider;
import hellfirepvp.astralsorcery.common.perk.modifier.AttributeModifierPerk;
import hellfirepvp.astralsorcery.common.perk.type.ModifierType;
import net.minecraft.data.DataGenerator;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.util.ResourceLocation;

import java.util.function.Consumer;

import static hellfirepvp.astralsorcery.AstralSorcery.key;
import static hellfirepvp.astralsorcery.common.lib.PerkAttributeTypesAS.*;
import static hellfirepvp.astralsorcery.common.lib.PerkNamesAS.*;
import static hellfirepvp.astralsorcery.common.perk.data.PerkTypeHandler.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AstralPerkTreeProvider
 * Created by HellFirePvP
 * Date: 14.08.2020 / 19:13
 */
public class AstralPerkTreeProvider extends PerkDataProvider {

    public AstralPerkTreeProvider(DataGenerator generator) {
        super(generator);
    }

    @Override
    public void registerPerks(Consumer<FinishedPerk> registrar) {
        registerRoots(registrar);
        registerTravel(registrar);
        registerInnerRoots(registrar);
        registerCore(registrar);
        registerOuterTravel(registrar);
        registerRootConnectors(registrar);
        registerMetaPerks(registrar);
        registerOuterRoots(registrar);
    }

    private void registerOuterRoots(Consumer<FinishedPerk> registrar) {
        registerAevitasOuter(registrar);
        registerVicioOuter(registrar);
        registerArmaraOuter(registrar);
        registerDiscidiaOuter(registrar);
        registerEvorsioOuter(registrar);
    }

    private void registerEvorsioOuter(Consumer<FinishedPerk> registrar) {
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("evorsio_outer_lowlife_1"), 28, 8)
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_HARVEST_SPEED)
                .setName(NAME_INC_MINING_SPEED)
                .connect(key("travel_53"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("evorsio_outer_lowlife_2"), 29, 7)
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_HARVEST_SPEED)
                .setName(NAME_INC_MINING_SPEED)
                .connect(key("evorsio_outer_lowlife_1"))
                .build(registrar);
        PerkDataBuilder.ofType(KEY_LOW_LIFE)
                .create(key("evorsio_outer_lowlife_3"), 30, 8)
                .setName(name("key.low_life"))
                .connect(key("evorsio_outer_lowlife_2"))
                .build(registrar);

        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("evorsio_outer_size_1"), 17, 9)
                .addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT)
                .setName(NAME_INC_PERK_EFFECT)
                .connect(key("travel_52"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("evorsio_outer_size_2"), 18, 8)
                .addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT)
                .setName(NAME_INC_PERK_EFFECT)
                .connect(key("evorsio_outer_size_1"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("evorsio_outer_size_3"), 17, 7)
                .addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT)
                .setName(NAME_INC_PERK_EFFECT)
                .connect(key("evorsio_outer_size_2"))
                .build(registrar);
        PerkDataBuilder.ofType(MAJOR_PERK)
                .create(key("evorsio_outer_size_4"), 16, 8)
                .addModifier(1F, ModifierType.ADDITION, ATTR_TYPE_MINING_SIZE)
                .setName(name("named.geologic_prowess"))
                .connect(key("evorsio_outer_size_3"))
                .build(registrar);

        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("evorsio_outer_luck_1"), 10, 21)
                .addModifier(0.5F, ModifierType.ADDITION, ATTR_TYPE_LUCK)
                .setName(NAME_ADD_LUCK)
                .connect(key("travel_50"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("evorsio_outer_luck_2"), 9, 20)
                .addModifier(0.5F, ModifierType.ADDITION, ATTR_TYPE_LUCK)
                .setName(NAME_ADD_LUCK)
                .connect(key("evorsio_outer_luck_1"))
                .build(registrar);
        PerkDataBuilder.ofType(KEY_ADD_ENCHANTMENT)
                .create(key("evorsio_outer_luck_3"), 10, 19)
                .modify(perk -> perk.addEnchantment(Enchantments.FORTUNE, 1))
                .setName(name("key.luck"))
                .connect(key("evorsio_outer_luck_2"))
                .build(registrar);
    }

    private void registerDiscidiaOuter(Consumer<FinishedPerk> registrar) {
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("discidia_outer_potionhit_1"), 67, 24)
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ATTACK_SPEED)
                .setName(NAME_INC_ATTACK_SPEED)
                .connect(key("travel_34"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("discidia_outer_potionhit_2"), 66, 25)
                .addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ATTACK_SPEED)
                .setName(NAME_INC_ATTACK_SPEED)
                .connect(key("discidia_outer_potionhit_1"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("discidia_outer_potionhit_3"), 65, 24)
                .addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ATTACK_SPEED)
                .setName(NAME_INC_ATTACK_SPEED)
                .connect(key("discidia_outer_potionhit_2"))
                .build(registrar);
        PerkDataBuilder.ofType(KEY_ON_HIT_POTIONS)
                .create(key("discidia_outer_potionhit_4"), 66, 23)
                .setName(name("key.damage_types"))
                .connect(key("discidia_outer_potionhit_3"))
                .build(registrar);

        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("discidia_outer_cull_1"), 64, 9)
                .addModifier(0.05F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_CRIT_CHANCE)
                .setName(NAME_INC_CRIT_CHANCE)
                .connect(key("travel_32"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("discidia_outer_cull_2"), 65, 8)
                .addModifier(0.05F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_CRIT_CHANCE)
                .setName(NAME_INC_CRIT_CHANCE)
                .connect(key("discidia_outer_cull_1"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("discidia_outer_cull_3"), 64, 7)
                .addModifier(0.05F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_CRIT_CHANCE)
                .setName(NAME_INC_CRIT_CHANCE)
                .connect(key("discidia_outer_cull_2"))
                .build(registrar);
        PerkDataBuilder.ofType(KEY_CULLING)
                .create(key("discidia_outer_cull_4"), 63, 8)
                .setName(name("key.cull_attack"))
                .connect(key("discidia_outer_cull_3"))
                .build(registrar);

        PerkDataBuilder.ofType(KEY_BLEED)
                .create(key("discidia_outer_bleed_1"), 56, 8)
                .setName(name("key.bleed"))
                .connect(key("travel_31"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("discidia_outer_bleed_2"), 58, 7)
                .addModifier(0.25F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_BLEED_CHANCE)
                .setName(NAME_INC_BLEED_CHANCE)
                .connect(key("discidia_outer_bleed_1"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("discidia_outer_bleed_3"), 59, 8)
                .addModifier(0.25F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_BLEED_DURATION)
                .setName(NAME_INC_BLEED_DURATION)
                .connect(key("discidia_outer_bleed_2"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("discidia_outer_bleed_4"), 55, 6)
                .addModifier(0.25F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_BLEED_CHANCE)
                .setName(NAME_INC_BLEED_CHANCE)
                .connect(key("discidia_outer_bleed_1"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("discidia_outer_bleed_5"), 54, 7)
                .addModifier(2F, ModifierType.ADDITION, ATTR_TYPE_BLEED_STACKS)
                .setName(NAME_ADD_BLEED_STACKS)
                .connect(key("discidia_outer_bleed_4"))
                .build(registrar);

        PerkDataBuilder.ofType(KEY_LIGHTNING_ARC)
                .create(key("discidia_outer_arc_1"), 49, 7)
                .setName(name("key.arc"))
                .connect(key("travel_30"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("discidia_outer_arc_2"), 50, 5)
                .addModifier(1F, ModifierType.ADDITION, ATTR_TYPE_ARC_CHAINS)
                .setName(name("special.arc_chain_1"))
                .connect(key("discidia_outer_arc_1"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("discidia_outer_arc_3"), 51, 6)
                .addModifier(1F, ModifierType.ADDITION, ATTR_TYPE_ARC_CHAINS)
                .setName(name("special.arc_chain_1"))
                .connect(key("discidia_outer_arc_2"))
                .build(registrar);
        PerkDataBuilder.ofType(MAJOR_PERK)
                .create(key("discidia_outer_arc_4"), 52, 4)
                .addModifier(2F, ModifierType.ADDITION, ATTR_TYPE_ARC_CHAINS)
                .setName(name("special.arc_chain_2"))
                .connect(key("discidia_outer_arc_3"))
                .build(registrar);

        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("discidia_outer_exp_1"), 70, 20)
                .addModifier(0.05F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_PROJ_SPEED)
                .setName(NAME_INC_PROJ_SPEED)
                .connect(key("travel_34"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("discidia_outer_exp_2"), 71, 19)
                .addModifier(0.05F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_PROJ_SPEED)
                .setName(NAME_INC_PROJ_SPEED)
                .connect(key("discidia_outer_exp_1"))
                .build(registrar);
        PerkDataBuilder.ofType(MAJOR_PERK)
                .create(key("discidia_outer_exp_3"), 70, 18)
                .addModifier(0.14F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EXP)
                .addModifier(0.08F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_PROJ_SPEED)
                .setName(name("named.finesse"))
                .connect(key("discidia_outer_exp_2"))
                .build(registrar);

        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("discidia_outer_crit_1"), 46, 11)
                .addModifier(0.1F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_CRIT_MULTIPLIER)
                .setName(NAME_INC_CRIT_MULTIPLIER)
                .connect(key("travel_30"))
                .build(registrar);
        PerkDataBuilder.ofType(MAJOR_PERK)
                .create(key("discidia_outer_crit_2"), 47, 12)
                .addModifier(0.15F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_CRIT_MULTIPLIER)
                .setName(name("named.lethality"))
                .connect(key("discidia_outer_crit_1"))
                .build(registrar);
    }

    private void registerArmaraOuter(Consumer<FinishedPerk> registrar) {
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("armara_outer_reflect_1"), 75, 46)
                .addModifier(5F, ModifierType.ADDITION, ATTR_TYPE_INC_THORNS)
                .setName(NAME_INC_THORNS)
                .connect(key("travel_36"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("armara_outer_reflect_2"), 76, 47)
                .addModifier(5F, ModifierType.ADDITION, ATTR_TYPE_INC_THORNS)
                .setName(NAME_INC_THORNS)
                .connect(key("armara_outer_reflect_1"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("armara_outer_reflect_3"), 77, 46)
                .addModifier(5F, ModifierType.ADDITION, ATTR_TYPE_INC_THORNS)
                .setName(NAME_INC_THORNS)
                .connect(key("armara_outer_reflect_2"))
                .build(registrar);
        PerkDataBuilder.ofType(MAJOR_PERK)
                .create(key("armara_outer_reflect_4"), 76, 45)
                .addModifier(5F, ModifierType.ADDITION, ATTR_TYPE_INC_THORNS)
                .addModifier(1F, ModifierType.ADDITION, ATTR_TYPE_INC_THORNS_RANGED)
                .setName(name("key.thorns_ranged"))
                .connect(key("armara_outer_reflect_3"))
                .build(registrar);

        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("armara_outer_revive_1"), 72, 36)
                .addModifier(0.5F, ModifierType.ADDITION, ATTR_TYPE_HEALTH)
                .setName(NAME_ADD_LIFE)
                .connect(key("travel_35"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("armara_outer_revive_2"), 71, 37)
                .addModifier(0.5F, ModifierType.ADDITION, ATTR_TYPE_HEALTH)
                .setName(NAME_ADD_LIFE)
                .connect(key("armara_outer_revive_1"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("armara_outer_revive_3"), 70, 36)
                .addModifier(0.5F, ModifierType.ADDITION, ATTR_TYPE_HEALTH)
                .setName(NAME_ADD_LIFE)
                .connect(key("armara_outer_revive_2"))
                .build(registrar);
        PerkDataBuilder.ofType(KEY_CHEATDEATH)
                .create(key("armara_outer_revive_4"), 69, 37)
                .setName(name("key.revive"))
                .connect(key("armara_outer_revive_3"))
                .build(registrar);

        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("armara_outer_worn_1"), 70, 62)
                .addModifier(1F, ModifierType.ADDITION, ATTR_TYPE_ARMOR)
                .addModifier(0.5F, ModifierType.ADDITION, ATTR_TYPE_ARMOR_TOUGHNESS)
                .setName(name("hybrid.armor_armor_toughness"))
                .connect(key("travel_38"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("armara_outer_worn_2"), 71, 63)
                .addModifier(1F, ModifierType.ADDITION, ATTR_TYPE_ARMOR)
                .addModifier(0.5F, ModifierType.ADDITION, ATTR_TYPE_ARMOR_TOUGHNESS)
                .setName(name("hybrid.armor_armor_toughness"))
                .connect(key("armara_outer_worn_1"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("armara_outer_worn_3"), 72, 62)
                .addModifier(1F, ModifierType.ADDITION, ATTR_TYPE_ARMOR)
                .addModifier(0.5F, ModifierType.ADDITION, ATTR_TYPE_ARMOR_TOUGHNESS)
                .setName(name("hybrid.armor_armor_toughness"))
                .connect(key("armara_outer_worn_2"))
                .build(registrar);
        PerkDataBuilder.ofType(KEY_NO_ARMOR)
                .create(key("armara_outer_worn_4"), 71, 61)
                .setName(name("key.no_armor"))
                .connect(key("armara_outer_worn_3"))
                .build(registrar);

        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("armara_outer_exp_1"), 64, 65)
                .addModifier(0.04F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EXP)
                .setName(NAME_INC_PERK_EXP)
                .connect(key("travel_39"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("armara_outer_exp_2"), 65, 66)
                .addModifier(0.04F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EXP)
                .setName(NAME_INC_PERK_EXP)
                .connect(key("armara_outer_exp_1"))
                .build(registrar);
        PerkDataBuilder.ofType(MAJOR_PERK)
                .create(key("armara_outer_exp_3"), 66, 65)
                .addModifier(0.12F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EXP)
                .setName(name("named.clarity"))
                .connect(key("armara_outer_exp_2"))
                .build(registrar);

        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("armara_outer_recovery_1"), 62, 60)
                .addModifier(0.08F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_LIFE_RECOVERY)
                .addModifier(0.04F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_COOLDOWN_REDUCTION)
                .setName(name("hybrid.life_recovery_cooldown_reduction"))
                .connect(key("travel_39"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("armara_outer_recovery_2"), 61, 59)
                .addModifier(0.08F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_LIFE_RECOVERY)
                .addModifier(0.04F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_COOLDOWN_REDUCTION)
                .setName(name("hybrid.life_recovery_cooldown_reduction"))
                .connect(key("armara_outer_recovery_1"))
                .build(registrar);
        PerkDataBuilder.ofType(MAJOR_PERK)
                .create(key("armara_outer_recovery_3"), 60, 60)
                .addModifier(0.10F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_LIFE_RECOVERY)
                .addModifier(0.08F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_COOLDOWN_REDUCTION)
                .setName(name("named.osmosis"))
                .connect(key("armara_outer_recovery_2"))
                .build(registrar);

        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("armara_outer_proj_1"), 75, 32)
                .addModifier(0.04F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_PROJ_DAMAGE)
                .setName(NAME_INC_PROJ_DAMAGE)
                .connect(key("travel_35"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("armara_outer_proj_2"), 76, 31)
                .addModifier(0.04F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_PROJ_DAMAGE)
                .setName(NAME_INC_PROJ_DAMAGE)
                .connect(key("armara_outer_proj_1"))
                .build(registrar);
        PerkDataBuilder.ofType(MAJOR_PERK)
                .create(key("armara_outer_proj_3"), 77, 32)
                .addModifier(0.06F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_PROJ_DAMAGE)
                .addModifier(1F, ModifierType.ADDITION, ATTR_TYPE_ARMOR)
                .setName(name("named.arrow_slits"))
                .connect(key("armara_outer_proj_2"))
                .build(registrar);
    }

    private void registerVicioOuter(Consumer<FinishedPerk> registrar) {
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("vicio_outer_step_1"), 51, 69)
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_MOVESPEED)
                .setName(NAME_INC_MOVESPEED)
                .connect(key("travel_40"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("vicio_outer_step_2"), 50, 68)
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_MOVESPEED)
                .setName(NAME_INC_MOVESPEED)
                .connect(key("vicio_outer_step_1"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("vicio_outer_step_3"), 51, 67)
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_MOVESPEED)
                .setName(NAME_INC_MOVESPEED)
                .connect(key("vicio_outer_step_2"))
                .build(registrar);
        PerkDataBuilder.ofType(KEY_STEP_ASSIST)
                .create(key("vicio_outer_step_4"), 50, 66)
                .setName(name("key.step_assist"))
                .connect(key("vicio_outer_step_3"))
                .build(registrar);

        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("vicio_outer_magnet_1"), 29, 73)
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_REACH)
                .setName(NAME_INC_REACH)
                .connect(key("travel_44"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("vicio_outer_magnet_2"), 30, 74)
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_REACH)
                .setName(NAME_INC_REACH)
                .connect(key("vicio_outer_magnet_1"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("vicio_outer_magnet_3"), 29, 75)
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_REACH)
                .setName(NAME_INC_REACH)
                .connect(key("vicio_outer_magnet_2"))
                .build(registrar);
        PerkDataBuilder.ofType(KEY_MAGNET_DROPS)
                .create(key("vicio_outer_magnet_4"), 28, 74)
                .setName(name("key.magnet_drops"))
                .connect(key("vicio_outer_magnet_3"))
                .build(registrar);

        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("vicio_outer_flight_1"), 42, 77)
                .addModifier(0.04F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_ALL_ELEMENTAL_RESIST)
                .setName(NAME_INC_ALL_RES)
                .connect(key("travel_42"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("vicio_outer_flight_2"), 43, 78)
                .addModifier(0.04F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_ALL_ELEMENTAL_RESIST)
                .setName(NAME_INC_ALL_RES)
                .connect(key("vicio_outer_flight_1"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("vicio_outer_flight_3"), 42, 79)
                .addModifier(0.04F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_ALL_ELEMENTAL_RESIST)
                .setName(NAME_INC_ALL_RES)
                .connect(key("vicio_outer_flight_2"))
                .build(registrar);
        PerkDataBuilder.ofType(KEY_MANTLE_CREATIVE_FLIGHT)
                .create(key("vicio_outer_flight_4"), 43, 80)
                .setName(name("key.mantle_flight"))
                .connect(key("vicio_outer_flight_3"))
                .build(registrar);

        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("vicio_outer_cdr_1"), 47, 76)
                .addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_COOLDOWN_REDUCTION)
                .setName(NAME_INC_COOLDOWN_RECOVERY)
                .connect(key("travel_41"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("vicio_outer_cdr_2"), 46, 77)
                .addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_COOLDOWN_REDUCTION)
                .setName(NAME_INC_COOLDOWN_RECOVERY)
                .connect(key("vicio_outer_cdr_1"))
                .build(registrar);
        PerkDataBuilder.ofType(MAJOR_PERK)
                .create(key("vicio_outer_cdr_3"), 45, 76)
                .addModifier(0.06F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_COOLDOWN_REDUCTION)
                .addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_MOVESPEED)
                .setName(name("named.fleet_footed"))
                .connect(key("vicio_outer_cdr_2"))
                .build(registrar);

        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("vicio_outer_charge_resist_1"), 36, 75)
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_ALL_ELEMENTAL_RESIST)
                .addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ALIGNMENT_CHARGE_MAXIMUM)
                .setName(name("hybrid.charge_all_resist"))
                .connect(key("travel_43"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("vicio_outer_charge_resist_2"), 37, 76)
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_ALL_ELEMENTAL_RESIST)
                .addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ALIGNMENT_CHARGE_MAXIMUM)
                .setName(name("hybrid.charge_all_resist"))
                .connect(key("vicio_outer_charge_resist_1"))
                .build(registrar);
        PerkDataBuilder.ofType(MAJOR_PERK)
                .create(key("vicio_outer_charge_resist_3"), 36, 77)
                .addModifier(0.06F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_ALL_ELEMENTAL_RESIST)
                .addModifier(0.1F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ALIGNMENT_CHARGE_MAXIMUM)
                .setName(name("named.dazzling_barrier"))
                .connect(key("vicio_outer_charge_resist_2"))
                .build(registrar);

        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("vicio_outer_movespeed_1"), 31, 68)
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_MOVESPEED)
                .setName(NAME_INC_MOVESPEED)
                .connect(key("travel_44"))
                .build(registrar);
        PerkDataBuilder.ofType(MAJOR_PERK)
                .create(key("vicio_outer_movespeed_2"), 30, 67)
                .addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_MOVESPEED)
                .addModifier(2F, ModifierType.ADDITION, ATTR_TYPE_INC_DODGE)
                .setName(name("named.hushed_steps"))
                .connect(key("vicio_outer_movespeed_1"))
                .build(registrar);

        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("vicio_outer_meleedmg_1"), 54, 73)
                .addModifier(0.04F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_MELEE_DAMAGE)
                .setName(NAME_INC_MELEE_DAMAGE)
                .connect(key("travel_40"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("vicio_outer_meleedmg_2"), 55, 74)
                .addModifier(0.04F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_MELEE_DAMAGE)
                .setName(NAME_INC_MELEE_DAMAGE)
                .connect(key("vicio_outer_meleedmg_1"))
                .build(registrar);
        PerkDataBuilder.ofType(MAJOR_PERK)
                .create(key("vicio_outer_meleedmg_3"), 54, 75)
                .addModifier(0.07F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_MELEE_DAMAGE)
                .addModifier(0.05F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ATTACK_SPEED)
                .setName(name("named.dervish"))
                .connect(key("vicio_outer_meleedmg_2"))
                .build(registrar);
    }

    private void registerAevitasOuter(Consumer<FinishedPerk> registrar) {
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("aevitas_outer_ttt_1"), 21, 61)
                .addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_HARVEST_SPEED)
                .setName(NAME_INC_MINING_SPEED)
                .connect(key("travel_45"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("aevitas_outer_ttt_2"), 22, 60)
                .addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_HARVEST_SPEED)
                .setName(NAME_INC_MINING_SPEED)
                .connect(key("aevitas_outer_ttt_1"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("aevitas_outer_ttt_3"), 23, 61)
                .addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_HARVEST_SPEED)
                .setName(NAME_INC_MINING_SPEED)
                .connect(key("aevitas_outer_ttt_2"))
                .build(registrar);
        PerkDataBuilder.ofType(KEY_VOID_TRASH)
                .create(key("aevitas_outer_ttt_4"), 22, 62)
                .setName(name("key.void_trash"))
                .connect(key("aevitas_outer_ttt_3"))
                .build(registrar);

        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("aevitas_outer_enrich_1"), 12, 62)
                .addModifier(0.5F, ModifierType.ADDITION, ATTR_TYPE_HEALTH)
                .setName(NAME_ADD_LIFE)
                .connect(key("travel_46"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("aevitas_outer_enrich_2"), 11, 63)
                .addModifier(0.5F, ModifierType.ADDITION, ATTR_TYPE_HEALTH)
                .setName(NAME_ADD_LIFE)
                .connect(key("aevitas_outer_enrich_1"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("aevitas_outer_enrich_3"), 12, 64)
                .addModifier(0.5F, ModifierType.ADDITION, ATTR_TYPE_HEALTH)
                .setName(NAME_ADD_LIFE)
                .connect(key("aevitas_outer_enrich_2"))
                .build(registrar);
        PerkDataBuilder.ofType(KEY_STONE_ENRICHMENT)
                .create(key("aevitas_outer_enrich_4"), 13, 63)
                .setName(name("key.stone_enrichment"))
                .connect(key("aevitas_outer_enrich_3"))
                .build(registrar);

        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("aevitas_outer_mending_1"), 9, 33)
                .addModifier(0.07F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_ALL_ELEMENTAL_RESIST)
                .setName(NAME_INC_ALL_RES)
                .connect(key("travel_49"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("aevitas_outer_mending_2"), 10, 32)
                .addModifier(0.07F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_ALL_ELEMENTAL_RESIST)
                .setName(NAME_ADD_ARMOR)
                .connect(key("aevitas_outer_mending_1"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("aevitas_outer_mending_3"), 11, 33)
                .addModifier(0.07F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_ALL_ELEMENTAL_RESIST)
                .setName(NAME_ADD_ARMOR)
                .connect(key("aevitas_outer_mending_2"))
                .build(registrar);
        PerkDataBuilder.ofType(KEY_ARMOR_MENDING)
                .create(key("aevitas_outer_mending_4"), 10, 34)
                .setName(name("key.mending"))
                .connect(key("aevitas_outer_mending_3"))
                .build(registrar);

        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("aevitas_outer_vit_1"), 3, 42)
                .addModifier(0.12F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_LIFE_RECOVERY)
                .setName(NAME_INC_LIFE_RECOVERY)
                .connect(key("travel_48"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("aevitas_outer_vit_2"), 2, 41)
                .addModifier(0.12F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_LIFE_RECOVERY)
                .setName(NAME_INC_LIFE_RECOVERY)
                .connect(key("aevitas_outer_vit_1"))
                .build(registrar);
        PerkDataBuilder.ofType(MAJOR_PERK)
                .create(key("aevitas_outer_vit_3"), 3, 40)
                .addModifier(0.1F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_LIFE_RECOVERY)
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_HEALTH)
                .setName(name("named.vitality"))
                .connect(key("aevitas_outer_vit_2"))
                .build(registrar);

        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("aevitas_outer_perkexp_1"), 3, 46)
                .addModifier(0.05F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EXP)
                .setName(NAME_INC_PERK_EXP)
                .connect(key("travel_48"))
                .build(registrar);
        PerkDataBuilder.ofType(MAJOR_PERK)
                .create(key("aevitas_outer_perkexp_2"), 4, 47)
                .addModifier(0.1F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EXP)
                .setName(name("named.sage"))
                .connect(key("aevitas_outer_perkexp_1"))
                .build(registrar);

        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("aevitas_outer_cdr_1"), 18, 65)
                .addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_COOLDOWN_REDUCTION)
                .setName(NAME_INC_COOLDOWN_RECOVERY)
                .connect(key("travel_45"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("aevitas_outer_cdr_2"), 17, 66)
                .addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_COOLDOWN_REDUCTION)
                .setName(NAME_INC_COOLDOWN_RECOVERY)
                .connect(key("aevitas_outer_cdr_1"))
                .build(registrar);
        PerkDataBuilder.ofType(MAJOR_PERK)
                .create(key("aevitas_outer_cdr_3"), 16, 65)
                .addModifier(0.08F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_COOLDOWN_REDUCTION)
                .addModifier(0.5F, ModifierType.ADDITION, ATTR_TYPE_HEALTH)
                .setName(name("named.vivid_growth"))
                .connect(key("aevitas_outer_cdr_2"))
                .build(registrar);

        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("aevitas_outer_dodge_1"), 8, 36)
                .addModifier(1F, ModifierType.ADDITION, ATTR_TYPE_INC_DODGE)
                .setName(NAME_ADD_DODGE)
                .connect(key("travel_49"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("aevitas_outer_dodge_2"), 9, 37)
                .addModifier(1F, ModifierType.ADDITION, ATTR_TYPE_INC_DODGE)
                .setName(NAME_ADD_DODGE)
                .connect(key("aevitas_outer_dodge_1"))
                .build(registrar);
        PerkDataBuilder.ofType(MAJOR_PERK)
                .create(key("aevitas_outer_dodge_3"), 8, 38)
                .addModifier(1F, ModifierType.ADDITION, ATTR_TYPE_INC_DODGE)
                .addModifier(2F, ModifierType.ADDITION, ATTR_TYPE_ARMOR)
                .setName(name("named.adaptive"))
                .connect(key("aevitas_outer_dodge_2"))
                .build(registrar);
    }

    private void registerMetaPerks(Consumer<FinishedPerk> registrar) {
        registerFocusPerks(registrar);
        registerConnectorPerks(registrar);
    }

    private void registerConnectorPerks(Consumer<FinishedPerk> registrar) {
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("aevitas_connector_1"), 22, 70)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.aevitas))
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT)
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EXP)
                .setName(name("special.connector.aevitas"))
                .connect(key("travel_22"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("aevitas_connector_2"), 21, 75)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.aevitas))
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT)
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EXP)
                .setName(name("special.connector.aevitas"))
                .connect(key("aevitas_connector_1"))
                .connect(key("aevitas_connector_3"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("aevitas_connector_3"), 17, 71)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.aevitas))
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT)
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EXP)
                .setName(name("special.connector.aevitas"))
                .connect(key("aevitas_connector_1"))
                .connect(key("aevitas_connector_2"))
                .build(registrar);
        PerkDataBuilder.ofType(EPIPHANY_PERK)
                .create(key("aevitas_connector_4"), 20, 72)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.aevitas))
                .setName(name("special.connector.key.aevitas"))
                .connect(key("aevitas_connector_1"))
                .connect(key("aevitas_connector_2"))
                .connect(key("aevitas_connector_3"))
                .build(registrar);

        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("vicio_connector_1"), 59, 69)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.vicio))
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT)
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EXP)
                .setName(name("special.connector.vicio"))
                .connect(key("travel_20"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("vicio_connector_2"), 64, 70)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.vicio))
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT)
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EXP)
                .setName(name("special.connector.vicio"))
                .connect(key("vicio_connector_1"))
                .connect(key("vicio_connector_3"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("vicio_connector_3"), 60, 74)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.vicio))
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT)
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EXP)
                .setName(name("special.connector.vicio"))
                .connect(key("vicio_connector_1"))
                .connect(key("vicio_connector_2"))
                .build(registrar);
        PerkDataBuilder.ofType(EPIPHANY_PERK)
                .create(key("vicio_connector_4"), 61, 71)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.vicio))
                .setName(name("special.connector.key.vicio"))
                .connect(key("vicio_connector_1"))
                .connect(key("vicio_connector_2"))
                .connect(key("vicio_connector_3"))
                .build(registrar);

        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("armara_connector_1"), 74, 26)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.armara))
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT)
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EXP)
                .setName(name("special.connector.armara"))
                .connect(key("travel_18"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("armara_connector_2"), 75, 21)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.armara))
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT)
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EXP)
                .setName(name("special.connector.armara"))
                .connect(key("armara_connector_1"))
                .connect(key("armara_connector_3"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("armara_connector_3"), 79, 25)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.armara))
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT)
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EXP)
                .setName(name("special.connector.armara"))
                .connect(key("armara_connector_1"))
                .connect(key("armara_connector_2"))
                .build(registrar);
        PerkDataBuilder.ofType(EPIPHANY_PERK)
                .create(key("armara_connector_4"), 76, 24)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.armara))
                .setName(name("special.connector.key.armara"))
                .connect(key("armara_connector_1"))
                .connect(key("armara_connector_2"))
                .connect(key("armara_connector_3"))
                .build(registrar);

        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("discidia_connector_1"), 39, 7)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.discidia))
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT)
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EXP)
                .setName(name("special.connector.discidia"))
                .connect(key("travel_16"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("discidia_connector_2"), 36, 2)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.discidia))
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT)
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EXP)
                .setName(name("special.connector.discidia"))
                .connect(key("discidia_connector_1"))
                .connect(key("discidia_connector_3"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("discidia_connector_3"), 42, 2)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.discidia))
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT)
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EXP)
                .setName(name("special.connector.discidia"))
                .connect(key("discidia_connector_1"))
                .connect(key("discidia_connector_2"))
                .build(registrar);
        PerkDataBuilder.ofType(EPIPHANY_PERK)
                .create(key("discidia_connector_4"), 39, 4)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.discidia))
                .setName(name("special.connector.key.discidia"))
                .connect(key("discidia_connector_1"))
                .connect(key("discidia_connector_2"))
                .connect(key("discidia_connector_3"))
                .build(registrar);

        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("evorsio_connector_1"), 6, 26)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.evorsio))
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT)
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EXP)
                .setName(name("special.connector.evorsio"))
                .connect(key("travel_24"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("evorsio_connector_2"), 1, 25)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.evorsio))
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT)
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EXP)
                .setName(name("special.connector.evorsio"))
                .connect(key("evorsio_connector_1"))
                .connect(key("evorsio_connector_3"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("evorsio_connector_3"), 5, 21)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.evorsio))
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT)
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EXP)
                .setName(name("special.connector.evorsio"))
                .connect(key("evorsio_connector_1"))
                .connect(key("evorsio_connector_2"))
                .build(registrar);
        PerkDataBuilder.ofType(EPIPHANY_PERK)
                .create(key("evorsio_connector_4"), 4, 24)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.evorsio))
                .setName(name("special.connector.key.evorsio"))
                .connect(key("evorsio_connector_1"))
                .connect(key("evorsio_connector_2"))
                .connect(key("evorsio_connector_3"))
                .build(registrar);
    }

    private void registerFocusPerks(Consumer<FinishedPerk> registrar) {
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("vorux_effect"), 3, 54)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.vorux))
                .addModifier(0.04F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT)
                .setName(NAME_INC_PERK_EFFECT)
                .connect(key("travel_47"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("vorux_exp"), 5, 56)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.vorux))
                .addModifier(0.04F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EXP)
                .setName(NAME_INC_PERK_EFFECT)
                .connect(key("travel_47"))
                .build(registrar);
        PerkDataBuilder.ofType(FOCUS_VORUX)
                .create(key("focus_vorux"), 1, 58)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.vorux))
                .addModifier(PerkCustomModifiersAS.FOCUS_VORUX)
                .setName(name("special.focus.vorux"))
                .connect(key("vorux_effect"))
                .connect(key("vorux_exp"))
                .build(registrar);

        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("alcara_effect"), 75, 56)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.alcara))
                .addModifier(0.04F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT)
                .setName(NAME_INC_PERK_EFFECT)
                .connect(key("travel_37"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("alcara_exp"), 77, 54)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.alcara))
                .addModifier(0.04F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EXP)
                .setName(NAME_INC_PERK_EFFECT)
                .connect(key("travel_37"))
                .build(registrar);
        PerkDataBuilder.ofType(FOCUS_ALCARA)
                .create(key("focus_alcara"), 79, 58)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.alcara))
                .addConverter(PerkConvertersAS.FOCUS_ALCARA)
                .setName(name("special.focus.alcara"))
                .connect(key("alcara_effect"))
                .connect(key("alcara_exp"))
                .build(registrar);

        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("ulteria_effect"), 68, 15)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.ulteria))
                .addModifier(0.04F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT)
                .setName(NAME_INC_PERK_EFFECT)
                .connect(key("travel_33"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("ulteria_exp"), 66, 13)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.ulteria))
                .addModifier(0.04F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EXP)
                .setName(NAME_INC_PERK_EFFECT)
                .connect(key("travel_33"))
                .build(registrar);
        PerkDataBuilder.ofType(FOCUS_ULTERIA)
                .create(key("focus_ulteria"), 70, 11)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.ulteria))
                .addModifier(PerkCustomModifiersAS.FOCUS_ULTERIA)
                .setName(name("special.focus.ulteria"))
                .connect(key("ulteria_effect"))
                .connect(key("ulteria_exp"))
                .build(registrar);

        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("gelu_effect"), 14, 13)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.gelu))
                .addModifier(0.04F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT)
                .setName(NAME_INC_PERK_EFFECT)
                .connect(key("travel_51"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("gelu_exp"), 12, 15)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.gelu))
                .addModifier(0.04F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EXP)
                .setName(NAME_INC_PERK_EFFECT)
                .connect(key("travel_51"))
                .build(registrar);
        PerkDataBuilder.ofType(FOCUS_GELU)
                .create(key("focus_gelu"), 10, 11)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.gelu))
                .addModifier(PerkCustomModifiersAS.FOCUS_GELU)
                .addConverter(PerkConvertersAS.FOCUS_GELU)
                .setName(name("special.focus.gelu"))
                .connect(key("gelu_effect"))
                .connect(key("gelu_exp"))
                .build(registrar);
    }

    private void registerRootConnectors(Consumer<FinishedPerk> registrar) {
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("aevitas_co_armor_1"), 12, 43)
                .addModifier(1F, ModifierType.ADDITION, ATTR_TYPE_ARMOR)
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT)
                .setName(NAME_ADD_ARMOR)
                .connect(key("aevitas_m_life_armor"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("aevitas_co_armor_2"), 9, 45)
                .addModifier(1F, ModifierType.ADDITION, ATTR_TYPE_ARMOR)
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT)
                .setName(NAME_ADD_ARMOR)
                .connect(key("aevitas_co_armor_1"))
                .connect(key("travel_48"))
                .build(registrar);

        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("aevitas_co_ats_1"), 18, 55)
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ATTACK_SPEED)
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT)
                .setName(NAME_INC_ATTACK_SPEED)
                .connect(key("aevitas_m_life_resist"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("aevitas_co_ats_2"), 15, 56)
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ATTACK_SPEED)
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT)
                .setName(NAME_INC_ATTACK_SPEED)
                .connect(key("aevitas_co_ats_1"))
                .connect(key("travel_46"))
                .build(registrar);

        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("vicio_co_ms_1"), 33, 66)
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_MOVESPEED)
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT)
                .setName(NAME_INC_MOVESPEED)
                .connect(key("vicio_m_reach_movespeed"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("vicio_co_ms_2"), 34, 70)
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_MOVESPEED)
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT)
                .setName(NAME_INC_MOVESPEED)
                .connect(key("vicio_co_ms_1"))
                .connect(key("travel_43"))
                .build(registrar);

        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("vicio_co_dodge_1"), 47, 66)
                .addModifier(0.1F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_DODGE)
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT)
                .setName(NAME_INC_DODGE)
                .connect(key("vicio_m_dodge_movespeed"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("vicio_co_dodge_2"), 45, 69)
                .addModifier(0.1F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_DODGE)
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT)
                .setName(NAME_INC_DODGE)
                .connect(key("vicio_co_dodge_1"))
                .connect(key("travel_41"))
                .build(registrar);

        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("armara_co_life_1"), 63, 55)
                .addModifier(0.5F, ModifierType.ADDITION, ATTR_TYPE_HEALTH)
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT)
                .setName(NAME_ADD_LIFE)
                .connect(key("armara_m_life_armor"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("armara_co_life_2"), 67, 56)
                .addModifier(0.5F, ModifierType.ADDITION, ATTR_TYPE_HEALTH)
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT)
                .setName(NAME_ADD_LIFE)
                .connect(key("armara_co_life_1"))
                .connect(key("travel_38"))
                .build(registrar);

        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("armara_co_mining_1"), 67, 43)
                .addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_HARVEST_SPEED)
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT)
                .setName(NAME_INC_MINING_SPEED)
                .connect(key("armara_m_resist_armor"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("armara_co_mining_2"), 70, 45)
                .addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_HARVEST_SPEED)
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT)
                .setName(NAME_INC_MINING_SPEED)
                .connect(key("armara_co_mining_1"))
                .connect(key("travel_36"))
                .build(registrar);

        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("discidia_co_projectiles_1"), 59, 21)
                .addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_PROJ_DAMAGE)
                .addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_PROJ_SPEED)
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT)
                .setName(name("hybrid.proj_dmg_speed"))
                .connect(key("discidia_m_proj_dmg_speed"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("discidia_co_projectiles_2"), 62, 18)
                .addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_PROJ_DAMAGE)
                .addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_PROJ_SPEED)
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT)
                .setName(name("hybrid.proj_dmg_speed"))
                .connect(key("discidia_co_projectiles_1"))
                .connect(key("travel_33"))
                .build(registrar);

        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("discidia_co_melee_1"), 49, 15)
                .addModifier(0.10F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_CRIT_CHANCE)
                .addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_MELEE_DAMAGE)
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT)
                .setName(name("hybrid.melee_crit_chance"))
                .connect(key("discidia_m_melee_reach"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("discidia_co_melee_2"), 51, 12)
                .addModifier(0.10F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_CRIT_CHANCE)
                .addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_MELEE_DAMAGE)
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT)
                .setName(name("hybrid.melee_crit_chance"))
                .connect(key("discidia_co_melee_1"))
                .connect(key("travel_31"))
                .build(registrar);

        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("evorsio_co_cdr_1"), 31, 15)
                .addModifier(1F, ModifierType.ADDITION, ATTR_TYPE_HEALTH)
                .addModifier(0.08F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_COOLDOWN_REDUCTION)
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT)
                .setName(name("hybrid.life_cooldown_reduction"))
                .connect(key("evorsio_m_dmg_ats"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("evorsio_co_cdr_2"), 28, 13)
                .addModifier(1F, ModifierType.ADDITION, ATTR_TYPE_HEALTH)
                .addModifier(0.08F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_COOLDOWN_REDUCTION)
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT)
                .setName(name("hybrid.life_cooldown_reduction"))
                .connect(key("evorsio_co_cdr_1"))
                .connect(key("travel_53"))
                .build(registrar);

        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("evorsio_co_reach_1"), 21, 22)
                .addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_REACH)
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_MOVESPEED)
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT)
                .setName(name("hybrid.reach_movespeed"))
                .connect(key("evorsio_m_mining_reach"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("evorsio_co_reach_2"), 17, 20)
                .addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_REACH)
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_MOVESPEED)
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT)
                .setName(name("hybrid.reach_movespeed"))
                .connect(key("evorsio_co_reach_1"))
                .connect(key("travel_51"))
                .build(registrar);
    }

    private void registerOuterTravel(Consumer<FinishedPerk> registrar) {
        this.makeTravelNode(47, 9, key("travel_30"))
                .connect(key("travel_16"))
                .build(registrar);
        this.makeTravelNode(54, 10, key("travel_31"))
                .connect(key("travel_30"))
                .build(registrar);
        this.makeTravelNode(62, 11, key("travel_32"))
                .connect(key("travel_31"))
                .build(registrar);
        this.makeTravelNode(65, 16, key("travel_33"))
                .connect(key("travel_32"))
                .build(registrar);
        this.makeTravelNode(69, 22, key("travel_34"))
                .connect(key("travel_33"))
                .connect(key("travel_18"))
                .build(registrar);
        this.makeTravelNode(73, 35, key("travel_35"))
                .connect(key("travel_18"))
                .build(registrar);
        this.makeTravelNode(73, 44, key("travel_36"))
                .connect(key("travel_35"))
                .build(registrar);
        this.makeTravelNode(74, 53, key("travel_37"))
                .connect(key("travel_36"))
                .build(registrar);
        this.makeTravelNode(69, 59, key("travel_38"))
                .connect(key("travel_37"))
                .build(registrar);
        this.makeTravelNode(61, 63, key("travel_39"))
                .connect(key("travel_38"))
                .connect(key("travel_20"))
                .build(registrar);
        this.makeTravelNode(52, 71, key("travel_40"))
                .connect(key("travel_20"))
                .build(registrar);
        this.makeTravelNode(46, 73, key("travel_41"))
                .connect(key("travel_40"))
                .build(registrar);
        this.makeTravelNode(41, 74, key("travel_42"))
                .connect(key("travel_41"))
                .build(registrar);
        this.makeTravelNode(35, 73, key("travel_43"))
                .connect(key("travel_42"))
                .build(registrar);
        this.makeTravelNode(30, 71, key("travel_44"))
                .connect(key("travel_43"))
                .connect(key("travel_22"))
                .build(registrar);
        this.makeTravelNode(20, 64, key("travel_45"))
                .connect(key("travel_22"))
                .build(registrar);
        this.makeTravelNode(13, 59, key("travel_46"))
                .connect(key("travel_45"))
                .build(registrar);
        this.makeTravelNode(6, 53, key("travel_47"))
                .connect(key("travel_46"))
                .build(registrar);
        this.makeTravelNode(5, 44, key("travel_48"))
                .connect(key("travel_47"))
                .build(registrar);
        this.makeTravelNode(6, 34, key("travel_49"))
                .connect(key("travel_48"))
                .connect(key("travel_24"))
                .build(registrar);
        this.makeTravelNode(12, 23, key("travel_50"))
                .connect(key("travel_24"))
                .build(registrar);
        this.makeTravelNode(15, 16, key("travel_51"))
                .connect(key("travel_50"))
                .build(registrar);
        this.makeTravelNode(19, 11, key("travel_52"))
                .connect(key("travel_51"))
                .build(registrar);
        this.makeTravelNode(26, 10, key("travel_53"))
                .connect(key("travel_52"))
                .build(registrar);
        this.makeTravelNode(33, 11, key("travel_54"))
                .connect(key("travel_53"))
                .connect(key("travel_16"))
                .build(registrar);
    }

    private void registerCore(Consumer<FinishedPerk> registrar) {
        PerkDataBuilder.ofType(GEM_SLOT_PERK)
                .create(key("core_m_gem"), 40, 40)
                .build(registrar);
        this.makeTravelNode(41, 34, key("travel_25"))
                .connect(key("core_m_gem"))
                .connect(key("travel_0"))
                .build(registrar);
        this.makeTravelNode(45, 39, key("travel_26"))
                .connect(key("core_m_gem"))
                .connect(key("travel_3"))
                .build(registrar);
        this.makeTravelNode(43, 45, key("travel_27"))
                .connect(key("core_m_gem"))
                .connect(key("travel_6"))
                .build(registrar);
        this.makeTravelNode(37, 46, key("travel_28"))
                .connect(key("core_m_gem"))
                .connect(key("travel_9"))
                .build(registrar);
        this.makeTravelNode(35, 38, key("travel_29"))
                .connect(key("core_m_gem"))
                .connect(key("travel_12"))
                .build(registrar);

        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("core_charge_cave_1"), 44, 33)
                .addModifier(0.05F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ALIGNMENT_CHARGE_REGENERATION)
                .setName(NAME_INC_CHARGE_REGEN)
                .connect(key("travel_25"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("core_charge_cave_2"), 45, 34)
                .addModifier(0.05F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ALIGNMENT_CHARGE_REGENERATION)
                .setName(NAME_INC_CHARGE_REGEN)
                .connect(key("core_charge_cave_1"))
                .build(registrar);
        PerkDataBuilder.ofType(KEY_CHARGE_BALANCING)
                .create(key("core_charge_cave_3"), 44, 35)
                .addModifier(0.7F, ModifierType.STACKING_MULTIPLY, ATTR_TYPE_ALIGNMENT_CHARGE_MAXIMUM)
                .addModifier(0.15F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ALIGNMENT_CHARGE_REGENERATION)
                .setName(name("key.charge_regen"))
                .connect(key("core_charge_cave_2"))
                .build(registrar);

        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("core_aoe_reach_1"), 34, 41)
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_REACH)
                .setName(NAME_INC_REACH)
                .connect(key("travel_29"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("core_aoe_reach_2"), 33, 42)
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_REACH)
                .setName(NAME_INC_REACH)
                .connect(key("core_aoe_reach_1"))
                .build(registrar);
        PerkDataBuilder.ofType(KEY_AOE)
                .create(key("core_aoe_reach_3"), 32, 41)
                .setName(name("key.aoe_effect"))
                .connect(key("core_aoe_reach_2"))
                .build(registrar);

        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("core_potion_dur_1"), 43, 37)
                .addModifier(0.1F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_POTION_DURATION)
                .setName(NAME_INC_POTION_DURATION)
                .connect(key("travel_26"))
                .build(registrar);
        PerkDataBuilder.ofType(MAJOR_PERK)
                .create(key("core_potion_dur_2"), 42, 38)
                .addModifier(0.3F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_POTION_DURATION)
                .addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_MOVESPEED)
                .setName(name("named.alchemists_flasks"))
                .connect(key("core_potion_dur_1"))
                .build(registrar);

        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("core_potion_dur_3"), 39, 45)
                .addModifier(0.1F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_POTION_DURATION)
                .setName(NAME_INC_POTION_DURATION)
                .connect(key("travel_28"))
                .build(registrar);
        PerkDataBuilder.ofType(MAJOR_PERK)
                .create(key("core_potion_dur_4"), 40, 44)
                .addModifier(0.4F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_POTION_DURATION)
                .addModifier(0.85F, ModifierType.STACKING_MULTIPLY, ATTR_TYPE_HEALTH)
                .setName(name("named.profane_chemistry"))
                .connect(key("core_potion_dur_3"))
                .build(registrar);

        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("core_unbreaking_1"), 34, 44)
                .addModifier(0.04F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_ALL_ELEMENTAL_RESIST)
                .setName(NAME_INC_ALL_RES)
                .connect(key("travel_28"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("core_unbreaking_2"), 33, 45)
                .addModifier(0.04F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_ALL_ELEMENTAL_RESIST)
                .setName(NAME_INC_ALL_RES)
                .connect(key("core_unbreaking_1"))
                .build(registrar);
        PerkDataBuilder.ofType(KEY_ADD_ENCHANTMENT)
                .create(key("core_unbreaking_3"), 34, 46)
                .modify(perk -> perk.addEnchantment(Enchantments.UNBREAKING, 1))
                .setName(name("key.enduring"))
                .connect(key("core_unbreaking_2"))
                .build(registrar);

        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("core_ench_effect_1"), 34, 36)
                .addModifier(0.05F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_ENCH_EFFECT)
                .setName(NAME_INC_ENCH_EFFECT)
                .connect(key("travel_29"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("core_ench_effect_2"), 33, 35)
                .addModifier(0.05F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_ENCH_EFFECT)
                .setName(NAME_INC_ENCH_EFFECT)
                .connect(key("core_ench_effect_1"))
                .build(registrar);
        PerkDataBuilder.ofType(MAJOR_PERK)
                .create(key("core_ench_effect_3"), 34, 34)
                .addModifier(0.15F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_ENCH_EFFECT)
                .setName(name("named.prismatic_shimmer"))
                .connect(key("core_ench_effect_2"))
                .build(registrar);

        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("core_cdr_1"), 42, 47)
                .addModifier(0.07F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_COOLDOWN_REDUCTION)
                .setName(NAME_INC_COOLDOWN_RECOVERY)
                .connect(key("travel_27"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("core_cdr_2"), 41, 48)
                .addModifier(0.07F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_COOLDOWN_REDUCTION)
                .setName(NAME_INC_COOLDOWN_RECOVERY)
                .connect(key("core_cdr_1"))
                .build(registrar);
        PerkDataBuilder.ofType(MAJOR_PERK)
                .create(key("core_cdr_3"), 40, 47)
                .addModifier(0.20F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_COOLDOWN_REDUCTION)
                .setName(name("named.tilted_pendulum"))
                .connect(key("core_cdr_2"))
                .build(registrar);

        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("core_lifeleech_1"), 47, 40)
                .addModifier(0.5F, ModifierType.ADDITION, ATTR_TYPE_ATTACK_LIFE_LEECH)
                .setName(NAME_ADD_LIFE_LEECH)
                .connect(key("travel_26"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("core_lifeleech_2"), 48, 41)
                .addModifier(0.5F, ModifierType.ADDITION, ATTR_TYPE_ATTACK_LIFE_LEECH)
                .setName(NAME_ADD_LIFE_LEECH)
                .connect(key("core_lifeleech_1"))
                .build(registrar);
        PerkDataBuilder.ofType(MAJOR_PERK)
                .create(key("core_lifeleech_3"), 47, 42)
                .addModifier(1.5F, ModifierType.ADDITION, ATTR_TYPE_ATTACK_LIFE_LEECH)
                .addModifier(1F, ModifierType.ADDITION, ATTR_TYPE_HEALTH)
                .setName(name("named.vampirism"))
                .connect(key("core_lifeleech_2"))
                .build(registrar);

        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("core_perk_exp_1"), 39, 32)
                .addModifier(0.04F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_CRIT_CHANCE)
                .setName(NAME_INC_CRIT_CHANCE)
                .connect(key("travel_25"))
                .build(registrar);
        PerkDataBuilder.ofType(MAJOR_PERK)
                .create(key("core_perk_exp_2"), 38, 33)
                .addModifier(0.15F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EXP)
                .addModifier(0.04F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_CRIT_CHANCE)
                .setName(name("named.precision"))
                .connect(key("core_perk_exp_1"))
                .build(registrar);

        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("core_perk_exp_3"), 45, 44)
                .addModifier(0.05F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EXP)
                .setName(NAME_INC_PERK_EXP)
                .connect(key("travel_27"))
                .build(registrar);
        PerkDataBuilder.ofType(MAJOR_PERK)
                .create(key("core_perk_exp_4"), 46, 45)
                .addModifier(0.15F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EXP)
                .setName(name("named.focused_mind"))
                .connect(key("core_perk_exp_3"))
                .build(registrar);

        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("core_luck_1"), 38, 36)
                .addModifier(0.5F, ModifierType.ADDITION, ATTR_TYPE_LUCK)
                .setName(NAME_ADD_LUCK)
                .connect(key("travel_25"))
                .build(registrar);
        PerkDataBuilder.ofType(MAJOR_PERK)
                .create(key("core_luck_2"), 39, 37)
                .addModifier(1F, ModifierType.ADDITION, ATTR_TYPE_LUCK)
                .setName(name("named.cunning"))
                .connect(key("core_luck_1"))
                .build(registrar);

        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("core_smite_1"), 36, 42)
                .addModifier(0.04F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_MELEE_DAMAGE)
                .setName(NAME_INC_MELEE_DAMAGE)
                .connect(key("travel_28"))
                .build(registrar);
        PerkDataBuilder.ofType(KEY_ADD_ENCHANTMENT)
                .create(key("core_smite_2"), 37, 41)
                .modify(perk -> perk.addEnchantment(Enchantments.SMITE, 1))
                .setName(name("key.undead_bane"))
                .connect(key("core_smite_1"))
                .build(registrar);

        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("core_infinity_1"), 44, 42)
                .addModifier(0.04F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_PROJ_DAMAGE)
                .setName(NAME_INC_PROJ_DAMAGE)
                .connect(key("travel_26"))
                .build(registrar);
        PerkDataBuilder.ofType(KEY_ADD_ENCHANTMENT)
                .create(key("core_infinity_2"), 43, 41)
                .modify(perk -> perk.addEnchantment(Enchantments.INFINITY, 1))
                .setName(name("key.endless_munitions"))
                .connect(key("core_infinity_1"))
                .build(registrar);
    }

    private void registerInnerRoots(Consumer<FinishedPerk> registrar) {
        registerAevitasInner(registrar);
        registerVicioInner(registrar);
        registerArmaraInner(registrar);
        registerDiscidiaInner(registrar);
        registerEvorsioInner(registrar);
    }

    private void registerEvorsioInner(Consumer<FinishedPerk> registrar) {
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("evorsio_inner_1"), 29, 27)
                .addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_HARVEST_SPEED)
                .setName(NAME_INC_MINING_SPEED)
                .connect(key("evorsio_mining_bridge_3"))
                .connect(key("evorsio_inner_2"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("evorsio_inner_2"), 31, 29)
                .addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_HARVEST_SPEED)
                .setName(NAME_INC_MINING_SPEED)
                .connect(key("travel_13"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("evorsio_inner_3"), 32, 25)
                .addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_HARVEST_SPEED)
                .setName(NAME_INC_MINING_SPEED)
                .connect(key("evorsio_mining_bridge_1"))
                .connect(key("evorsio_inner_4"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("evorsio_inner_4"), 35, 27)
                .addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_HARVEST_SPEED)
                .setName(NAME_INC_MINING_SPEED)
                .connect(key("travel_14"))
                .build(registrar);

        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("evorsio_inner_crit1"), 30, 31)
                .addModifier(1F, ModifierType.ADDITION, ATTR_TYPE_INC_CRIT_CHANCE)
                .setName(NAME_INC_CRIT_CHANCE)
                .connect(key("evorsio_inner_2"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("evorsio_inner_crit2"), 29, 32)
                .addModifier(1F, ModifierType.ADDITION, ATTR_TYPE_INC_CRIT_CHANCE)
                .setName(NAME_INC_CRIT_CHANCE)
                .connect(key("evorsio_inner_crit1"))
                .build(registrar);
        PerkDataBuilder.ofType(KEY_DISARM)
                .create(key("evorsio_inner_crit3"), 28, 31)
                .setName(name("key.disarm"))
                .connect(key("evorsio_inner_crit2"))
                .build(registrar);

        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("evorsio_digtypes_1"), 34, 23)
                .addModifier(0.05F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_REACH)
                .setName(NAME_INC_REACH)
                .connect(key("evorsio_inner_3"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("evorsio_digtypes_2"), 35, 22)
                .addModifier(0.05F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_REACH)
                .setName(NAME_INC_REACH)
                .connect(key("evorsio_digtypes_1"))
                .build(registrar);
        PerkDataBuilder.ofType(KEY_DIG_TYPES)
                .create(key("evorsio_digtypes_3"), 34, 21)
                .setName(name("key.dig_types"))
                .connect(key("evorsio_digtypes_2"))
                .build(registrar);

        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("evorsio_charge_inner_1"), 37, 26)
                .addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ALIGNMENT_CHARGE_REGENERATION)
                .addModifier(0.1F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ALIGNMENT_CHARGE_MAXIMUM)
                .setName(name("hybrid.charge_max_regen"))
                .connect(key("evorsio_inner_4"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("evorsio_charge_inner_2"), 38, 25)
                .addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ALIGNMENT_CHARGE_REGENERATION)
                .addModifier(0.1F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ALIGNMENT_CHARGE_MAXIMUM)
                .setName(name("hybrid.charge_max_regen"))
                .connect(key("evorsio_charge_inner_1"))
                .build(registrar);
        PerkDataBuilder.ofType(MAJOR_PERK)
                .create(key("evorsio_charge_inner_3"), 37, 24)
                .addModifier(0.2F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ALIGNMENT_CHARGE_MAXIMUM)
                .setName(name("named.bloom"))
                .connect(key("evorsio_charge_inner_2"))
                .build(registrar);

        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("evorsio_mining_size_1"), 26, 29)
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_MOVESPEED)
                .setName(NAME_INC_MOVESPEED)
                .connect(key("evorsio_inner_1"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("evorsio_mining_size_2"), 25, 28)
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_MOVESPEED)
                .setName(NAME_INC_MOVESPEED)
                .connect(key("evorsio_mining_size_1"))
                .build(registrar);
        PerkDataBuilder.ofType(MAJOR_PERK)
                .create(key("evorsio_mining_size_3"), 26, 27)
                .addModifier(1F, ModifierType.ADDITION, ATTR_TYPE_MINING_SIZE)
                .setName(name("named.illusory_hammer"))
                .connect(key("evorsio_mining_size_2"))
                .build(registrar);
    }

    private void registerDiscidiaInner(Consumer<FinishedPerk> registrar) {
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("discidia_inner_1"), 47, 24)
                .addModifier(0.15F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_CRIT_CHANCE)
                .setName(NAME_INC_CRIT_CHANCE)
                .connect(key("discidia_crit_bridge_3"))
                .connect(key("discidia_inner_2"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("discidia_inner_2"), 46, 27)
                .addModifier(0.15F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_CRIT_CHANCE)
                .setName(NAME_INC_CRIT_CHANCE)
                .connect(key("travel_1"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("discidia_inner_3"), 51, 26)
                .addModifier(0.15F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_CRIT_CHANCE)
                .setName(NAME_INC_CRIT_CHANCE)
                .connect(key("discidia_crit_bridge_1"))
                .connect(key("discidia_inner_4"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("discidia_inner_4"), 49, 29)
                .addModifier(0.15F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_CRIT_CHANCE)
                .setName(NAME_INC_CRIT_CHANCE)
                .connect(key("travel_2"))
                .build(registrar);

        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("discidia_close_range_1"), 45, 21)
                .addModifier(0.04F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_PROJ_DAMAGE)
                .setName(NAME_INC_PROJ_DAMAGE)
                .connect(key("discidia_inner_1"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("discidia_close_range_2"), 46, 20)
                .addModifier(0.04F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_PROJ_DAMAGE)
                .setName(NAME_INC_PROJ_DAMAGE)
                .connect(key("discidia_close_range_1"))
                .build(registrar);
        PerkDataBuilder.ofType(KEY_PROJ_PROXIMITY)
                .create(key("discidia_close_range_3"), 47, 21)
                .setName(name("key.projectile_close_range"))
                .connect(key("discidia_close_range_2"))
                .build(registrar);

        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("discidia_distance_1"), 54, 28)
                .addModifier(0.04F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_PROJ_DAMAGE)
                .setName(NAME_INC_PROJ_DAMAGE)
                .connect(key("discidia_inner_3"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("discidia_distance_2"), 55, 27)
                .addModifier(0.04F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_PROJ_DAMAGE)
                .setName(NAME_INC_PROJ_DAMAGE)
                .connect(key("discidia_distance_1"))
                .build(registrar);
        PerkDataBuilder.ofType(KEY_RPOJ_DISTANCE)
                .create(key("discidia_distance_3"), 54, 26)
                .setName(name("key.projectile_distance"))
                .connect(key("discidia_distance_2"))
                .build(registrar);

        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("discidia_rampage_1"), 44, 26)
                .addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ATTACK_SPEED)
                .addModifier(0.2F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_RAMPAGE_DURATION)
                .setName(name("hybrid.attack_speed_rampage"))
                .connect(key("discidia_inner_2"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("discidia_rampage_2"), 43, 25)
                .addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ATTACK_SPEED)
                .addModifier(0.2F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_RAMPAGE_DURATION)
                .setName(name("hybrid.attack_speed_rampage"))
                .connect(key("discidia_rampage_1"))
                .build(registrar);
        PerkDataBuilder.ofType(KEY_RAMPAGE)
                .create(key("discidia_rampage_3"), 44, 24)
                .setName(name("key.rampage"))
                .connect(key("discidia_rampage_2"))
                .build(registrar);

        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("discidia_multi_1"), 51, 30)
                .addModifier(0.08F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_CRIT_MULTIPLIER)
                .setName(NAME_INC_CRIT_MULTIPLIER)
                .connect(key("discidia_inner_4"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("discidia_multi_2"), 52, 31)
                .addModifier(0.08F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_CRIT_MULTIPLIER)
                .setName(NAME_INC_CRIT_MULTIPLIER)
                .connect(key("discidia_multi_1"))
                .build(registrar);
        PerkDataBuilder.ofType(MAJOR_PERK)
                .create(key("discidia_multi_3"), 53, 30)
                .addModifier(0.16F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_CRIT_MULTIPLIER)
                .addModifier(1.5F, ModifierType.ADDITION, ATTR_TYPE_INC_CRIT_CHANCE)
                .setName(name("named.combat_focus"))
                .connect(key("discidia_multi_2"))
                .build(registrar);
    }

    private void registerArmaraInner(Consumer<FinishedPerk> registrar) {
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("armara_inner_1"), 57, 44)
                .addModifier(0.1F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_DODGE)
                .setName(NAME_INC_DODGE)
                .connect(key("armara_dodge_bridge_3"))
                .connect(key("armara_inner_2"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("armara_inner_2"), 54, 41)
                .addModifier(0.1F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_DODGE)
                .setName(NAME_INC_DODGE)
                .connect(key("travel_4"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("armara_inner_3"), 56, 48)
                .addModifier(0.1F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_DODGE)
                .setName(NAME_INC_DODGE)
                .connect(key("armara_dodge_bridge_1"))
                .connect(key("armara_inner_4"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("armara_inner_4"), 52, 47)
                .addModifier(0.1F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_DODGE)
                .setName(NAME_INC_DODGE)
                .connect(key("travel_5"))
                .build(registrar);

        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("armara_dmgarmor_1"), 51, 49)
                .addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ARMOR)
                .setName(NAME_INC_ARMOR)
                .connect(key("armara_inner_4"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("armara_dmgarmor_2"), 52, 50)
                .addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ARMOR)
                .setName(NAME_INC_ARMOR)
                .connect(key("armara_dmgarmor_1"))
                .build(registrar);
        PerkDataBuilder.ofType(KEY_DAMAGE_ARMOR)
                .create(key("armara_dmgarmor_3"), 51, 51)
                .setName(name("key.damage_armor"))
                .connect(key("armara_dmgarmor_2"))
                .build(registrar);

        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("armara_noknockback_1"), 59, 41)
                .addModifier(0.5F, ModifierType.ADDITION, ATTR_TYPE_HEALTH)
                .setName(NAME_ADD_LIFE)
                .connect(key("armara_inner_1"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("armara_noknockback_2"), 60, 40)
                .addModifier(0.5F, ModifierType.ADDITION, ATTR_TYPE_HEALTH)
                .setName(NAME_ADD_LIFE)
                .connect(key("armara_noknockback_1"))
                .build(registrar);
        PerkDataBuilder.ofType(KEY_NO_KNOCKBACK)
                .create(key("armara_noknockback_3"), 59, 39)
                .setName(name("key.no_knockback"))
                .connect(key("armara_noknockback_2"))
                .build(registrar);

        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("amara_maxcharge_inner_1"), 53, 38)
                .addModifier(0.07F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ALIGNMENT_CHARGE_MAXIMUM)
                .setName(NAME_INC_CHARGE_MAX)
                .connect(key("armara_inner_2"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("amara_maxcharge_inner_2"), 54, 37)
                .addModifier(0.07F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ALIGNMENT_CHARGE_MAXIMUM)
                .setName(NAME_INC_CHARGE_MAX)
                .connect(key("amara_maxcharge_inner_1"))
                .build(registrar);
        PerkDataBuilder.ofType(MAJOR_PERK)
                .create(key("amara_maxcharge_inner_3"), 55, 38)
                .addModifier(0.2F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ALIGNMENT_CHARGE_MAXIMUM)
                .setName(name("named.ample_semblance"))
                .connect(key("amara_maxcharge_inner_2"))
                .build(registrar);

        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("armara_armor_inner_1"), 57, 52)
                .addModifier(1F, ModifierType.ADDITION, ATTR_TYPE_ARMOR)
                .addModifier(0.5F, ModifierType.ADDITION, ATTR_TYPE_ARMOR_TOUGHNESS)
                .setName(name("hybrid.armor_armor_toughness"))
                .connect(key("armara_inner_3"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("armara_armor_inner_2"), 56, 53)
                .addModifier(1F, ModifierType.ADDITION, ATTR_TYPE_ARMOR)
                .addModifier(0.5F, ModifierType.ADDITION, ATTR_TYPE_ARMOR_TOUGHNESS)
                .setName(name("hybrid.armor_armor_toughness"))
                .connect(key("armara_armor_inner_1"))
                .build(registrar);
        PerkDataBuilder.ofType(MAJOR_PERK)
                .create(key("armara_armor_inner_3"), 55, 52)
                .addModifier(4F, ModifierType.ADDITION, ATTR_TYPE_ARMOR)
                .addModifier(2F, ModifierType.ADDITION, ATTR_TYPE_ARMOR_TOUGHNESS)
                .setName(name("named.tough"))
                .connect(key("armara_armor_inner_2"))
                .build(registrar);
    }

    private void registerVicioInner(Consumer<FinishedPerk> registrar) {
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("vicio_inner_1"), 42, 57)
                .addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ATTACK_SPEED)
                .setName(NAME_INC_ATTACK_SPEED)
                .connect(key("vicio_ats_bridge_3"))
                .connect(key("vicio_inner_2"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("vicio_inner_2"), 43, 54)
                .addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ATTACK_SPEED)
                .setName(NAME_INC_ATTACK_SPEED)
                .connect(key("travel_7"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("vicio_inner_3"), 38, 57)
                .addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ATTACK_SPEED)
                .setName(NAME_INC_ATTACK_SPEED)
                .connect(key("vicio_ats_bridge_1"))
                .connect(key("vicio_inner_4"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("vicio_inner_4"), 39, 53)
                .addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ATTACK_SPEED)
                .setName(NAME_INC_ATTACK_SPEED)
                .connect(key("travel_8"))
                .build(registrar);

        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("vicio_food_reduction_1"), 45, 58)
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_MOVESPEED)
                .setName(NAME_INC_MOVESPEED)
                .connect(key("vicio_inner_1"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("vicio_food_reduction_2"), 46, 57)
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_MOVESPEED)
                .setName(NAME_INC_MOVESPEED)
                .connect(key("vicio_food_reduction_1"))
                .build(registrar);
        PerkDataBuilder.ofType(KEY_REDUCED_FOOD)
                .create(key("vicio_food_reduction_3"), 45, 56)
                .setName(name("key.reduced_food"))
                .connect(key("vicio_food_reduction_2"))
                .build(registrar);

        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("vicio_place_lights_1"), 36, 54)
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_HARVEST_SPEED)
                .setName(NAME_INC_MINING_SPEED)
                .connect(key("vicio_inner_4"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("vicio_place_lights_2"), 35, 53)
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_HARVEST_SPEED)
                .setName(NAME_INC_MINING_SPEED)
                .connect(key("vicio_place_lights_1"))
                .build(registrar);
        PerkDataBuilder.ofType(KEY_SPAWN_LIGHTS)
                .create(key("vicio_place_lights_3"), 36, 52)
                .setName(name("key.place_lights"))
                .connect(key("vicio_place_lights_2"))
                .build(registrar);

        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("vicio_ats_inner_1"), 35, 58)
                .addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ATTACK_SPEED)
                .setName(NAME_INC_ATTACK_SPEED)
                .connect(key("vicio_inner_3"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("vicio_ats_inner_2"), 34, 57)
                .addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ATTACK_SPEED)
                .setName(NAME_INC_ATTACK_SPEED)
                .connect(key("vicio_ats_inner_1"))
                .build(registrar);
        PerkDataBuilder.ofType(MAJOR_PERK)
                .create(key("vicio_ats_inner_3"), 33, 58)
                .addModifier(0.1F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ATTACK_SPEED)
                .setName(name("named.zeal"))
                .connect(key("vicio_ats_inner_2"))
                .build(registrar);

        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("vicio_charge_inner_1"), 45, 53)
                .addModifier(0.05F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ALIGNMENT_CHARGE_REGENERATION)
                .addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_HARVEST_SPEED)
                .setName(name("hybrid.charge_regen_mining"))
                .connect(key("vicio_inner_2"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("vicio_charge_inner_2"), 44, 52)
                .addModifier(0.05F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ALIGNMENT_CHARGE_REGENERATION)
                .addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_HARVEST_SPEED)
                .setName(name("hybrid.charge_regen_mining"))
                .connect(key("vicio_charge_inner_1"))
                .build(registrar);
        PerkDataBuilder.ofType(MAJOR_PERK)
                .create(key("vicio_charge_inner_3"), 45, 51)
                .addModifier(0.1F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ALIGNMENT_CHARGE_REGENERATION)
                .addModifier(0.04F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_HARVEST_SPEED)
                .setName(name("named.haste"))
                .connect(key("vicio_charge_inner_2"))
                .build(registrar);
    }

    private void registerAevitasInner(Consumer<FinishedPerk> registrar) {
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("aevitas_inner_1"), 24, 49)
                .addModifier(0.5F, ModifierType.ADDITION, ATTR_TYPE_HEALTH)
                .setName(NAME_ADD_LIFE)
                .connect(key("aevitas_life_bridge_3"))
                .connect(key("aevitas_inner_2"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("aevitas_inner_2"), 28, 47)
                .addModifier(0.5F, ModifierType.ADDITION, ATTR_TYPE_HEALTH)
                .setName(NAME_ADD_LIFE)
                .connect(key("travel_10"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("aevitas_inner_3"), 23, 43)
                .addModifier(0.5F, ModifierType.ADDITION, ATTR_TYPE_HEALTH)
                .setName(NAME_ADD_LIFE)
                .connect(key("aevitas_life_bridge_1"))
                .connect(key("aevitas_inner_4"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("aevitas_inner_4"), 27, 42)
                .addModifier(0.5F, ModifierType.ADDITION, ATTR_TYPE_HEALTH)
                .setName(NAME_ADD_LIFE)
                .connect(key("travel_11"))
                .build(registrar);

        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("aevitas_cleanse_1"), 21, 41)
                .addModifier(0.1F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_LIFE_RECOVERY)
                .setName(NAME_INC_LIFE_RECOVERY)
                .connect(key("aevitas_inner_3"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("aevitas_cleanse_2"), 20, 40)
                .addModifier(0.1F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_LIFE_RECOVERY)
                .setName(NAME_INC_LIFE_RECOVERY)
                .connect(key("aevitas_cleanse_1"))
                .build(registrar);
        PerkDataBuilder.ofType(KEY_REMOVE_BAD_POTIONS)
                .create(key("aevitas_cleanse_3"), 21, 39)
                .setName(name("key.cleansing"))
                .connect(key("aevitas_cleanse_2"))
                .build(registrar);

        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("aevitas_plant_growth_1"), 23, 53)
                .addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_REACH)
                .addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_HARVEST_SPEED)
                .setName(name("hybrid.reach_mining"))
                .connect(key("aevitas_inner_1"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("aevitas_plant_growth_2"), 24, 54)
                .addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_REACH)
                .addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_HARVEST_SPEED)
                .setName(name("hybrid.reach_mining"))
                .connect(key("aevitas_plant_growth_1"))
                .build(registrar);
        PerkDataBuilder.ofType(KEY_GROWABLES)
                .create(key("aevitas_plant_growth_3"), 25, 53)
                .setName(name("key.plant_growth"))
                .connect(key("aevitas_plant_growth_2"))
                .build(registrar);

        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("aevitas_inner_charge_1"), 29, 49)
                .addModifier(0.1F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ALIGNMENT_CHARGE_REGENERATION)
                .addModifier(0.05F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ALIGNMENT_CHARGE_MAXIMUM)
                .setName(name("hybrid.charge_max_regen"))
                .connect(key("aevitas_inner_2"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("aevitas_inner_charge_2"), 30, 50)
                .addModifier(0.1F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ALIGNMENT_CHARGE_REGENERATION)
                .addModifier(0.05F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ALIGNMENT_CHARGE_MAXIMUM)
                .setName(name("hybrid.charge_max_regen"))
                .connect(key("aevitas_inner_charge_1"))
                .build(registrar);
        PerkDataBuilder.ofType(MAJOR_PERK)
                .create(key("aevitas_inner_charge_3"), 31, 49)
                .addModifier(0.15F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ALIGNMENT_CHARGE_REGENERATION)
                .setName(name("named.stellar_vessel"))
                .connect(key("aevitas_inner_charge_2"))
                .build(registrar);

        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("aevitas_armor_life_1"), 26, 39)
                .addModifier(-0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_MOVESPEED)
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_HEALTH)
                .setName(name("hybrid.life_movespeed"))
                .connect(key("aevitas_inner_4"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("aevitas_armor_life_2"), 27, 38)
                .addModifier(-0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_MOVESPEED)
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_HEALTH)
                .setName(name("hybrid.life_movespeed"))
                .connect(key("aevitas_armor_life_1"))
                .build(registrar);
        PerkDataBuilder.ofType(MAJOR_PERK)
                .create(key("aevitas_armor_life_3"), 28, 39)
                .addModifier(-0.04F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_MOVESPEED)
                .addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_HEALTH)
                .addModifier(2F, ModifierType.ADDITION, ATTR_TYPE_ARMOR)
                .setName(name("named.iron_heart"))
                .connect(key("aevitas_armor_life_2"))
                .build(registrar);
    }

    private void registerTravel(Consumer<FinishedPerk> registrar) {
        this.makeTravelNode(40, 29, key("travel_0"))
                .connect(key("travel_1"))
                .connect(key("travel_14"))
                .connect(key("ev_ds_effect_1"))
                .connect(key("ev_ds_exp_1"))
                .build(registrar);
        this.makeTravelNode(44, 30, key("travel_1"))
                .connect(key("travel_0"))
                .connect(key("travel_2"))
                .build(registrar);
        this.makeTravelNode(48, 33, key("travel_2"))
                .connect(key("travel_1"))
                .connect(key("travel_3"))
                .build(registrar);
        this.makeTravelNode(50, 36, key("travel_3"))
                .connect(key("travel_2"))
                .connect(key("travel_4"))
                .connect(key("ar_ds_effect_1"))
                .connect(key("ar_ds_exp_1"))
                .build(registrar);
        this.makeTravelNode(50, 40, key("travel_4"))
                .connect(key("travel_3"))
                .connect(key("travel_5"))
                .build(registrar);
        this.makeTravelNode(49, 45, key("travel_5"))
                .connect(key("travel_4"))
                .connect(key("travel_6"))
                .build(registrar);
        this.makeTravelNode(46, 49, key("travel_6"))
                .connect(key("travel_5"))
                .connect(key("travel_7"))
                .connect(key("ar_vi_effect_1"))
                .connect(key("ar_vi_exp_1"))
                .build(registrar);
        this.makeTravelNode(42, 50, key("travel_7"))
                .connect(key("travel_6"))
                .connect(key("travel_8"))
                .build(registrar);
        this.makeTravelNode(38, 50, key("travel_8"))
                .connect(key("travel_7"))
                .connect(key("travel_9"))
                .build(registrar);
        this.makeTravelNode(34, 49, key("travel_9"))
                .connect(key("travel_8"))
                .connect(key("travel_10"))
                .connect(key("ae_vi_effect_1"))
                .connect(key("ae_vi_exp_1"))
                .build(registrar);
        this.makeTravelNode(31, 45, key("travel_10"))
                .connect(key("travel_9"))
                .connect(key("travel_11"))
                .build(registrar);
        this.makeTravelNode(30, 40, key("travel_11"))
                .connect(key("travel_10"))
                .connect(key("travel_12"))
                .build(registrar);
        this.makeTravelNode(30, 36, key("travel_12"))
                .connect(key("travel_11"))
                .connect(key("travel_13"))
                .connect(key("ae_ev_effect_1"))
                .connect(key("ae_ev_exp_1"))
                .build(registrar);
        this.makeTravelNode(32, 33, key("travel_13"))
                .connect(key("travel_12"))
                .connect(key("travel_14"))
                .build(registrar);
        this.makeTravelNode(36, 30, key("travel_14"))
                .connect(key("travel_13"))
                .connect(key("travel_0"))
                .build(registrar);


        this.makeTravelNode(40, 20, key("travel_15"))
                .connect(key("ev_ds_effect_1"))
                .connect(key("ev_ds_exp_1"))
                .connect(key("ev_ds_effect_2"))
                .connect(key("ev_ds_exp_2"))
                .connect(key("discidia_c_ats_bridge_2"))
                .connect(key("evorsio_c_armor_2"))
                .build(registrar);
        this.makeTravelNode(40, 10, key("travel_16"))
                .connect(key("ev_ds_effect_2"))
                .connect(key("ev_ds_exp_2"))
                .build(registrar);

        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("ev_ds_effect_1"), 41, 25)
                .addModifier(0.04F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT)
                .setName(NAME_INC_PERK_EFFECT)
                .connect(key("travel_0"))
                .connect(key("travel_15"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("ev_ds_exp_1"), 39, 23)
                .addModifier(0.04F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EXP)
                .setName(NAME_INC_PERK_EXP)
                .connect(key("travel_0"))
                .connect(key("travel_15"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("ev_ds_effect_2"), 41, 14)
                .addModifier(0.04F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT)
                .setName(NAME_INC_PERK_EFFECT)
                .connect(key("travel_15"))
                .connect(key("travel_16"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("ev_ds_exp_2"), 39, 16)
                .addModifier(0.04F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EXP)
                .setName(NAME_INC_PERK_EXP)
                .connect(key("travel_15"))
                .connect(key("travel_16"))
                .build(registrar);


        this.makeTravelNode(59, 32, key("travel_17"))
                .connect(key("ar_ds_effect_1"))
                .connect(key("ar_ds_exp_1"))
                .connect(key("ar_ds_effect_2"))
                .connect(key("ar_ds_exp_2"))
                .connect(key("discidia_c_damage_2"))
                .connect(key("armara_c_recovery_2"))
                .build(registrar);
        this.makeTravelNode(71, 28, key("travel_18"))
                .connect(key("ar_ds_effect_2"))
                .connect(key("ar_ds_exp_2"))
                .build(registrar);

        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("ar_ds_effect_1"), 55, 35)
                .addModifier(0.04F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT)
                .setName(NAME_INC_PERK_EFFECT)
                .connect(key("travel_3"))
                .connect(key("travel_17"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("ar_ds_exp_1"), 53, 33)
                .addModifier(0.04F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EXP)
                .setName(NAME_INC_PERK_EXP)
                .connect(key("travel_3"))
                .connect(key("travel_17"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("ar_ds_effect_2"), 66, 31)
                .addModifier(0.04F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT)
                .setName(NAME_INC_PERK_EFFECT)
                .connect(key("travel_17"))
                .connect(key("travel_18"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("ar_ds_exp_2"), 64, 29)
                .addModifier(0.04F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EXP)
                .setName(NAME_INC_PERK_EXP)
                .connect(key("travel_17"))
                .connect(key("travel_18"))
                .build(registrar);


        this.makeTravelNode(50, 58, key("travel_19"))
                .connect(key("ar_vi_effect_1"))
                .connect(key("ar_vi_exp_1"))
                .connect(key("ar_vi_effect_2"))
                .connect(key("ar_vi_exp_2"))
                .connect(key("armara_c_movespeed_2"))
                .connect(key("vicio_c_armor_2"))
                .build(registrar);
        this.makeTravelNode(55, 68, key("travel_20"))
                .connect(key("ar_vi_effect_2"))
                .connect(key("ar_vi_exp_2"))
                .build(registrar);

        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("ar_vi_effect_1"), 47, 54)
                .addModifier(0.04F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT)
                .setName(NAME_INC_PERK_EFFECT)
                .connect(key("travel_6"))
                .connect(key("travel_19"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("ar_vi_exp_1"), 49, 52)
                .addModifier(0.04F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EXP)
                .setName(NAME_INC_PERK_EXP)
                .connect(key("travel_6"))
                .connect(key("travel_19"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("ar_vi_effect_2"), 52, 64)
                .addModifier(0.04F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT)
                .setName(NAME_INC_PERK_EFFECT)
                .connect(key("travel_19"))
                .connect(key("travel_20"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("ar_vi_exp_2"), 54, 62)
                .addModifier(0.04F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EXP)
                .setName(NAME_INC_PERK_EXP)
                .connect(key("travel_19"))
                .connect(key("travel_20"))
                .build(registrar);


        this.makeTravelNode(30, 58, key("travel_21"))
                .connect(key("ae_vi_effect_1"))
                .connect(key("ae_vi_exp_1"))
                .connect(key("ae_vi_effect_2"))
                .connect(key("ae_vi_exp_2"))
                .connect(key("vicio_c_mining_2"))
                .connect(key("aevitas_c_reach_2"))
                .build(registrar);
        this.makeTravelNode(25, 68, key("travel_22"))
                .connect(key("ae_vi_effect_2"))
                .connect(key("ae_vi_exp_2"))
                .build(registrar);

        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("ae_vi_effect_1"), 31, 52)
                .addModifier(0.04F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT)
                .setName(NAME_INC_PERK_EFFECT)
                .connect(key("travel_9"))
                .connect(key("travel_21"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("ae_vi_exp_1"), 33, 54)
                .addModifier(0.04F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EXP)
                .setName(NAME_INC_PERK_EXP)
                .connect(key("travel_9"))
                .connect(key("travel_21"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("ae_vi_effect_2"), 26, 62)
                .addModifier(0.04F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT)
                .setName(NAME_INC_PERK_EFFECT)
                .connect(key("travel_21"))
                .connect(key("travel_22"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("ae_vi_exp_2"), 28, 64)
                .addModifier(0.04F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EXP)
                .setName(NAME_INC_PERK_EXP)
                .connect(key("travel_21"))
                .connect(key("travel_22"))
                .build(registrar);

        this.makeTravelNode(21, 32, key("travel_23"))
                .connect(key("ae_ev_effect_1"))
                .connect(key("ae_ev_exp_1"))
                .connect(key("ae_ev_effect_2"))
                .connect(key("ae_ev_exp_2"))
                .connect(key("aevitas_c_armor_2"))
                .connect(key("evorsio_c_mining_2"))
                .build(registrar);
        this.makeTravelNode(9, 28, key("travel_24"))
                .connect(key("ae_ev_effect_2"))
                .connect(key("ae_ev_exp_2"))
                .build(registrar);

        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("ae_ev_effect_1"), 27, 33)
                .addModifier(0.04F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT)
                .setName(NAME_INC_PERK_EFFECT)
                .connect(key("travel_12"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("ae_ev_exp_1"), 25, 35)
                .addModifier(0.04F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EXP)
                .setName(NAME_INC_PERK_EXP)
                .connect(key("travel_12"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("ae_ev_effect_2"), 16, 29)
                .addModifier(0.04F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT)
                .setName(NAME_INC_PERK_EFFECT)
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("ae_ev_exp_2"), 14, 31)
                .addModifier(0.04F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EXP)
                .setName(NAME_INC_PERK_EXP)
                .build(registrar);
    }

    private void registerRoots(Consumer<FinishedPerk> registrar) {
        registerAevitasRoot(registrar);
        registerVicioRoot(registrar);
        registerArmaraRoot(registrar);
        registerDiscidiaRoot(registrar);
        registerEvorsioRoot(registrar);
    }

    private void registerEvorsioRoot(Consumer<FinishedPerk> registrar) {
        PerkDataBuilder.ofType(ROOT_EVORSIO)
                .create(key("evorsio"), 21, 14)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.evorsio))
                .addModifier(1F, ModifierType.ADDITION, ATTR_TYPE_REACH)
                .connect(key("evorsio_damage_1"))
                .connect(key("evorsio_mining_1"))
                .build(registrar);

        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("evorsio_damage_1"), 25, 16)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.evorsio))
                .addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_MELEE_DAMAGE)
                .setName(NAME_INC_MELEE_DAMAGE)
                .connect(key("evorsio_damage_2"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("evorsio_damage_2"), 27, 17)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.evorsio))
                .addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_MELEE_DAMAGE)
                .setName(NAME_INC_MELEE_DAMAGE)
                .connect(key("evorsio_m_dmg_ats"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("evorsio_mining_1"), 23, 18)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.evorsio))
                .addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_HARVEST_SPEED)
                .setName(NAME_INC_MINING_SPEED)
                .connect(key("evorsio_mining_2"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("evorsio_mining_2"), 24, 20)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.evorsio))
                .addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_HARVEST_SPEED)
                .setName(NAME_INC_MINING_SPEED)
                .connect(key("evorsio_m_mining_reach"))
                .build(registrar);

        PerkDataBuilder.ofType(MAJOR_PERK)
                .create(key("evorsio_m_dmg_ats"), 30, 18)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.evorsio))
                .addModifier(0.05F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_MELEE_DAMAGE)
                .addModifier(0.05F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ATTACK_SPEED)
                .setName(name("named.expertise"))
                .connect(key("evorsio_mining_bridge_1"))
                .build(registrar);
        PerkDataBuilder.ofType(MAJOR_PERK)
                .create(key("evorsio_m_mining_reach"), 25, 23)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.evorsio))
                .addModifier(0.05F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_HARVEST_SPEED)
                .addModifier(0.1F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_REACH)
                .setName(name("named.tunneling"))
                .connect(key("evorsio_mining_bridge_3"))
                .build(registrar);

        PerkDataBuilder.ofType(GEM_SLOT_PERK)
                .create(key("evorsio_m_gem"), 28, 21)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.evorsio))
                .connect(key("evorsio_damage_2"))
                .connect(key("evorsio_mining_2"))
                .connect(key("evorsio_mining_bridge_1"))
                .connect(key("evorsio_mining_bridge_3"))
                .build(registrar);

        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("evorsio_mining_bridge_1"), 31, 20)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.evorsio))
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_MOVESPEED)
                .setName(NAME_INC_MOVESPEED)
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("evorsio_mining_bridge_2"), 30, 23)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.evorsio))
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_MOVESPEED)
                .setName(NAME_INC_MOVESPEED)
                .connect(key("evorsio_mining_bridge_1"))
                .connect(key("evorsio_mining_bridge_3"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("evorsio_mining_bridge_3"), 27, 24)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.evorsio))
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_MOVESPEED)
                .setName(NAME_INC_MOVESPEED)
                .build(registrar);

        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("evorsio_c_armor_1"), 33, 17)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.evorsio))
                .addModifier(0.05F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ARMOR)
                .setName(NAME_INC_ARMOR)
                .connect(key("evorsio_m_dmg_ats"))
                .connect(key("evorsio_c_armor_2"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("evorsio_c_armor_2"), 37, 18)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.evorsio))
                .addModifier(0.05F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ARMOR)
                .setName(NAME_INC_ARMOR)
                .connect(key("travel_15"))
                .build(registrar);

        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("evorsio_c_mining_1"), 23, 26)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.evorsio))
                .addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_HARVEST_SPEED)
                .setName(NAME_INC_MINING_SPEED)
                .connect(key("evorsio_m_mining_reach"))
                .connect(key("evorsio_c_mining_2"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("evorsio_c_mining_2"), 22, 29)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.evorsio))
                .addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_HARVEST_SPEED)
                .setName(NAME_INC_MINING_SPEED)
                .connect(key("travel_23"))
                .build(registrar);
    }

    private void registerDiscidiaRoot(Consumer<FinishedPerk> registrar) {
        PerkDataBuilder.ofType(ROOT_DISCIDIA)
                .create(key("discidia"), 59, 14)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.discidia))
                .addModifier(1.1F, ModifierType.STACKING_MULTIPLY, ATTR_TYPE_MELEE_DAMAGE)
                .addModifier(1.1F, ModifierType.STACKING_MULTIPLY, ATTR_TYPE_PROJ_DAMAGE)
                .connect(key("discidia_proj_1"))
                .connect(key("discidia_melee_1"))
                .build(registrar);

        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("discidia_proj_1"), 57, 18)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.discidia))
                .addModifier(0.04F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_PROJ_DAMAGE)
                .setName(NAME_INC_PROJ_DAMAGE)
                .connect(key("discidia_proj_2"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("discidia_proj_2"), 56, 20)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.discidia))
                .addModifier(0.04F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_PROJ_DAMAGE)
                .setName(NAME_INC_PROJ_DAMAGE)
                .connect(key("discidia_m_proj_dmg_speed"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("discidia_melee_1"), 55, 16)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.discidia))
                .addModifier(0.04F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_MELEE_DAMAGE)
                .setName(NAME_INC_MELEE_DAMAGE)
                .connect(key("discidia_melee_2"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("discidia_melee_2"), 53, 17)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.discidia))
                .addModifier(0.04F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_MELEE_DAMAGE)
                .setName(NAME_INC_MELEE_DAMAGE)
                .connect(key("discidia_m_melee_reach"))
                .build(registrar);

        PerkDataBuilder.ofType(MAJOR_PERK)
                .create(key("discidia_m_proj_dmg_speed"), 55, 23)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.discidia))
                .addModifier(0.05F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_PROJ_DAMAGE)
                .addModifier(0.2F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_PROJ_SPEED)
                .setName(name("named.deadly_draw"))
                .connect(key("discidia_crit_bridge_1"))
                .build(registrar);
        PerkDataBuilder.ofType(MAJOR_PERK)
                .create(key("discidia_m_melee_reach"), 50, 18)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.discidia))
                .addModifier(0.05F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_MELEE_DAMAGE)
                .addModifier(0.1F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_REACH)
                .setName(name("named.strong_arms"))
                .connect(key("discidia_crit_bridge_3"))
                .build(registrar);

        PerkDataBuilder.ofType(GEM_SLOT_PERK)
                .create(key("discidia_m_gem"), 52, 21)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.discidia))
                .connect(key("discidia_proj_2"))
                .connect(key("discidia_melee_2"))
                .connect(key("discidia_crit_bridge_1"))
                .connect(key("discidia_crit_bridge_3"))
                .build(registrar);

        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("discidia_crit_bridge_1"), 53, 24)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.discidia))
                .addModifier(1F, ModifierType.ADDITION, ATTR_TYPE_INC_CRIT_CHANCE)
                .setName(NAME_ADD_CRIT_CHANCE)
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("discidia_crit_bridge_2"), 50, 23)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.discidia))
                .addModifier(1F, ModifierType.ADDITION, ATTR_TYPE_INC_CRIT_CHANCE)
                .setName(NAME_ADD_CRIT_CHANCE)
                .connect(key("discidia_crit_bridge_1"))
                .connect(key("discidia_crit_bridge_3"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("discidia_crit_bridge_3"), 49, 20)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.discidia))
                .addModifier(1F, ModifierType.ADDITION, ATTR_TYPE_INC_CRIT_CHANCE)
                .setName(NAME_ADD_CRIT_CHANCE)
                .build(registrar);

        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("discidia_c_damage_1"), 57, 26)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.discidia))
                .addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_MELEE_DAMAGE)
                .addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_PROJ_DAMAGE)
                .setName(name("hybrid.melee_proj_dmg"))
                .connect(key("discidia_m_proj_dmg_speed"))
                .connect(key("discidia_c_damage_2"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("discidia_c_damage_2"), 58, 29)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.discidia))
                .addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_MELEE_DAMAGE)
                .addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_PROJ_DAMAGE)
                .setName(name("hybrid.melee_proj_dmg"))
                .connect(key("travel_17"))
                .build(registrar);

        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("discidia_c_ats_bridge_1"), 47, 17)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.discidia))
                .addModifier(0.05F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ATTACK_SPEED)
                .setName(NAME_INC_ATTACK_SPEED)
                .connect(key("discidia_m_melee_reach"))
                .connect(key("discidia_c_ats_bridge_2"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("discidia_c_ats_bridge_2"), 43, 18)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.discidia))
                .addModifier(0.05F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ATTACK_SPEED)
                .setName(NAME_INC_ATTACK_SPEED)
                .connect(key("travel_15"))
                .build(registrar);
    }

    private void registerArmaraRoot(Consumer<FinishedPerk> registrar) {
        PerkDataBuilder.ofType(ROOT_ARMARA)
                .create(key("armara"), 70, 51)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.armara))
                .addModifier(1.20F, ModifierType.STACKING_MULTIPLY, ATTR_TYPE_ARMOR)
                .connect(key("armara_armor_1"))
                .connect(key("armara_resist_1"))
                .build(registrar);

        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("armara_armor_1"), 66, 52)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.armara))
                .addModifier(1F, ModifierType.ADDITION, ATTR_TYPE_ARMOR)
                .setName(NAME_ADD_ARMOR)
                .connect(key("armara_armor_2"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("armara_armor_2"), 64, 51)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.armara))
                .addModifier(1F, ModifierType.ADDITION, ATTR_TYPE_ARMOR)
                .setName(NAME_ADD_ARMOR)
                .connect(key("armara_m_life_armor"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("armara_resist_1"), 68, 48)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.armara))
                .addModifier(0.05F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_ALL_ELEMENTAL_RESIST)
                .setName(NAME_INC_ALL_RES)
                .connect(key("armara_resist_2"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("armara_resist_2"), 66, 47)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.armara))
                .addModifier(0.05F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_ALL_ELEMENTAL_RESIST)
                .setName(NAME_INC_ALL_RES)
                .connect(key("armara_m_resist_armor"))
                .build(registrar);

        PerkDataBuilder.ofType(MAJOR_PERK)
                .create(key("armara_m_life_armor"), 61, 52)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.armara))
                .addModifier(0.05F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_HEALTH)
                .addModifier(1F, ModifierType.ADDITION, ATTR_TYPE_ARMOR)
                .setName(name("named.resilience"))
                .connect(key("armara_dodge_bridge_1"))
                .build(registrar);
        PerkDataBuilder.ofType(MAJOR_PERK)
                .create(key("armara_m_resist_armor"), 64, 44)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.armara))
                .addModifier(0.1F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_ALL_ELEMENTAL_RESIST)
                .addModifier(2F, ModifierType.ADDITION, ATTR_TYPE_ARMOR)
                .setName(name("named.bulwark"))
                .connect(key("armara_dodge_bridge_3"))
                .build(registrar);

        PerkDataBuilder.ofType(GEM_SLOT_PERK)
                .create(key("armara_m_gem"), 63, 48)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.armara))
                .connect(key("armara_armor_2"))
                .connect(key("armara_resist_2"))
                .connect(key("armara_dodge_bridge_1"))
                .connect(key("armara_dodge_bridge_3"))
                .build(registrar);

        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("armara_dodge_bridge_1"), 60, 50)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.armara))
                .addModifier(1F, ModifierType.ADDITION, ATTR_TYPE_INC_DODGE)
                .setName(NAME_ADD_DODGE)
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("armara_dodge_bridge_2"), 59, 47)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.armara))
                .addModifier(1F, ModifierType.ADDITION, ATTR_TYPE_INC_DODGE)
                .setName(NAME_ADD_DODGE)
                .connect(key("armara_dodge_bridge_1"))
                .connect(key("armara_dodge_bridge_3"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("armara_dodge_bridge_3"), 61, 45)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.armara))
                .addModifier(1F, ModifierType.ADDITION, ATTR_TYPE_INC_DODGE)
                .setName(NAME_ADD_DODGE)
                .build(registrar);

        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("armara_c_movespeed_1"), 58, 56)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.armara))
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_MOVESPEED)
                .setName(NAME_INC_MOVESPEED)
                .connect(key("armara_m_life_armor"))
                .connect(key("armara_c_movespeed_2"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("armara_c_movespeed_2"), 54, 57)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.armara))
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_MOVESPEED)
                .setName(NAME_INC_MOVESPEED)
                .connect(key("travel_19"))
                .build(registrar);

        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("armara_c_recovery_1"), 63, 40)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.armara))
                .addModifier(0.1F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_LIFE_RECOVERY)
                .setName(NAME_INC_LIFE_RECOVERY)
                .connect(key("armara_m_resist_armor"))
                .connect(key("armara_c_recovery_2"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("armara_c_recovery_2"), 61, 36)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.armara))
                .addModifier(0.1F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_LIFE_RECOVERY)
                .setName(NAME_INC_LIFE_RECOVERY)
                .connect(key("travel_17"))
                .build(registrar);
    }

    private void registerVicioRoot(Consumer<FinishedPerk> registrar) {
        PerkDataBuilder.ofType(ROOT_VICIO)
                .create(key("vicio"), 40, 70)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.vicio))
                .addModifier(0.1F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_MOVESPEED)
                .connect(key("vicio_reach_1"))
                .connect(key("vicio_dodge_1"))
                .build(registrar);

        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("vicio_reach_1"), 38, 67)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.vicio))
                .addModifier(0.04F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_REACH)
                .setName(NAME_INC_REACH)
                .connect(key("vicio"))
                .connect(key("vicio_reach_2"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("vicio_reach_2"), 37, 65)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.vicio))
                .addModifier(0.04F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_REACH)
                .setName(NAME_INC_REACH)
                .connect(key("vicio_reach_1"))
                .connect(key("vicio_m_reach_movespeed"))
                .connect(key("vicio_m_gem"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("vicio_dodge_1"), 42, 67)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.vicio))
                .addModifier(2F, ModifierType.ADDITION, ATTR_TYPE_INC_DODGE)
                .setName(NAME_ADD_DODGE)
                .connect(key("vicio"))
                .connect(key("vicio_dodge_2"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("vicio_dodge_2"), 43, 65)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.vicio))
                .addModifier(2F, ModifierType.ADDITION, ATTR_TYPE_INC_DODGE)
                .setName(NAME_ADD_DODGE)
                .connect(key("vicio_dodge_1"))
                .connect(key("vicio_m_dodge_movespeed"))
                .connect(key("vicio_m_gem"))
                .build(registrar);

        PerkDataBuilder.ofType(MAJOR_PERK)
                .create(key("vicio_m_reach_movespeed"), 35, 63)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.vicio))
                .addModifier(0.04F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_MOVESPEED)
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_REACH)
                .setName(name("named.swiftness"))
                .connect(key("vicio_reach_2"))
                .connect(key("vicio_ats_bridge_1"))
                .connect(key("vicio_c_mining_1"))
                .build(registrar);
        PerkDataBuilder.ofType(MAJOR_PERK)
                .create(key("vicio_m_dodge_movespeed"), 45, 63)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.vicio))
                .addModifier(0.04F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_MOVESPEED)
                .addModifier(0.1F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_DODGE)
                .setName(name("named.agility"))
                .connect(key("vicio_dodge_2"))
                .connect(key("vicio_ats_bridge_3"))
                .connect(key("vicio_c_armor_1"))
                .build(registrar);

        PerkDataBuilder.ofType(GEM_SLOT_PERK)
                .create(key("vicio_m_gem"), 40, 64)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.vicio))
                .connect(key("vicio_reach_2"))
                .connect(key("vicio_dodge_2"))
                .connect(key("vicio_ats_bridge_1"))
                .connect(key("vicio_ats_bridge_3"))
                .build(registrar);

        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("vicio_ats_bridge_1"), 37, 61)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.vicio))
                .addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ATTACK_SPEED)
                .setName(NAME_INC_ATTACK_SPEED)
                .connect(key("vicio_m_reach_movespeed"))
                .connect(key("vicio_ats_bridge_2"))
                .connect(key("vicio_m_gem"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("vicio_ats_bridge_2"), 40, 59)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.vicio))
                .addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ATTACK_SPEED)
                .setName(NAME_INC_ATTACK_SPEED)
                .connect(key("vicio_ats_bridge_1"))
                .connect(key("vicio_ats_bridge_3"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("vicio_ats_bridge_3"), 43, 61)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.vicio))
                .addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ATTACK_SPEED)
                .setName(NAME_INC_ATTACK_SPEED)
                .connect(key("vicio_m_dodge_movespeed"))
                .connect(key("vicio_ats_bridge_2"))
                .connect(key("vicio_m_gem"))
                .build(registrar);

        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("vicio_c_mining_1"), 34, 60)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.vicio))
                .addModifier(0.04F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_HARVEST_SPEED)
                .setName(NAME_INC_MINING_SPEED)
                .connect(key("vicio_m_reach_movespeed"))
                .connect(key("vicio_c_mining_2"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("vicio_c_mining_2"), 31, 61)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.vicio))
                .addModifier(0.04F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_HARVEST_SPEED)
                .setName(NAME_INC_MINING_SPEED)
                .connect(key("travel_21"))
                .connect(key("vicio_c_mining_1"))
                .build(registrar);

        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("vicio_c_armor_1"), 46, 60)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.vicio))
                .addModifier(0.05F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ARMOR)
                .setName(NAME_INC_ARMOR)
                .connect(key("vicio_m_dodge_movespeed"))
                .connect(key("vicio_c_armor_2"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("vicio_c_armor_2"), 49, 61)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.vicio))
                .addModifier(0.05F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ARMOR)
                .setName(NAME_INC_ARMOR)
                .connect(key("vicio_c_armor_1"))
                .connect(key("travel_19"))
                .build(registrar);
    }

    private void registerAevitasRoot(Consumer<FinishedPerk> registrar) {
        PerkDataBuilder.ofType(ROOT_AEVITAS)
                .create(key("aevitas"), 10, 51)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.aevitas))
                .addModifier(2, ModifierType.ADDITION, ATTR_TYPE_HEALTH)
                .connect(key("aevitas_life_armor_1"))
                .connect(key("aevitas_life_reach_1"))
                .build(registrar);

        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("aevitas_life_armor_1"), 12, 48)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.aevitas))
                .addModifier(0.05F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ARMOR)
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_HEALTH)
                .setName(name("hybrid.life_armor"))
                .connect(key("aevitas"))
                .connect(key("aevitas_life_armor_2"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("aevitas_life_armor_2"), 14, 47)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.aevitas))
                .addModifier(0.05F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ARMOR)
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_HEALTH)
                .setName(name("hybrid.life_armor"))
                .connect(key("aevitas_life_armor_1"))
                .connect(key("aevitas_m_life_armor"))
                .connect(key("aevitas_m_gem"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("aevitas_life_reach_1"), 14, 52)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.aevitas))
                .addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_REACH)
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_HEALTH)
                .setName(name("hybrid.life_reach"))
                .connect(key("aevitas"))
                .connect(key("aevitas_life_reach_2"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("aevitas_life_reach_2"), 16, 51)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.aevitas))
                .addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_REACH)
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_HEALTH)
                .setName(name("hybrid.life_reach"))
                .connect(key("aevitas_life_reach_1"))
                .connect(key("aevitas_m_life_resist"))
                .connect(key("aevitas_m_gem"))
                .build(registrar);

        PerkDataBuilder.ofType(MAJOR_PERK)
                .create(key("aevitas_m_life_armor"), 16, 44)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.aevitas))
                .addModifier(0.05F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ARMOR)
                .addModifier(1, ModifierType.ADDITION, ATTR_TYPE_HEALTH)
                .setName(name("named.thick_skin"))
                .connect(key("aevitas_life_armor_2"))
                .connect(key("aevitas_life_bridge_1"))
                .connect(key("aevitas_c_armor_1"))
                .build(registrar);
        PerkDataBuilder.ofType(MAJOR_PERK)
                .create(key("aevitas_m_life_resist"), 19, 52)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.aevitas))
                .addModifier(0.05F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_ALL_ELEMENTAL_RESIST)
                .addModifier(1, ModifierType.ADDITION, ATTR_TYPE_HEALTH)
                .setName(name("named.melding"))
                .connect(key("aevitas_life_reach_2"))
                .connect(key("aevitas_life_bridge_3"))
                .connect(key("aevitas_c_reach_1"))
                .build(registrar);

        PerkDataBuilder.ofType(GEM_SLOT_PERK)
                .create(key("aevitas_m_gem"), 17, 48)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.aevitas))
                .connect(key("aevitas_life_armor_2"))
                .connect(key("aevitas_life_reach_2"))
                .connect(key("aevitas_life_bridge_1"))
                .connect(key("aevitas_life_bridge_3"))
                .build(registrar);

        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("aevitas_life_bridge_1"), 19, 45)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.aevitas))
                .addModifier(1F, ModifierType.ADDITION, ATTR_TYPE_HEALTH)
                .setName(NAME_ADD_LIFE)
                .connect(key("aevitas_m_life_armor"))
                .connect(key("aevitas_life_bridge_2"))
                .connect(key("aevitas_m_gem"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("aevitas_life_bridge_2"), 21, 47)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.aevitas))
                .addModifier(1F, ModifierType.ADDITION, ATTR_TYPE_HEALTH)
                .setName(NAME_ADD_LIFE)
                .connect(key("aevitas_life_bridge_1"))
                .connect(key("aevitas_life_bridge_3"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("aevitas_life_bridge_3"), 20, 50)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.aevitas))
                .addModifier(1F, ModifierType.ADDITION, ATTR_TYPE_HEALTH)
                .setName(NAME_ADD_LIFE)
                .connect(key("aevitas_m_life_resist"))
                .connect(key("aevitas_life_bridge_2"))
                .connect(key("aevitas_m_gem"))
                .build(registrar);

        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("aevitas_c_armor_1"), 17, 40)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.aevitas))
                .addModifier(0.05F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ARMOR)
                .setName(NAME_INC_ARMOR)
                .connect(key("aevitas_m_life_armor"))
                .connect(key("aevitas_c_armor_2"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("aevitas_c_armor_2"), 19, 36)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.aevitas))
                .addModifier(0.05F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ARMOR)
                .setName(NAME_INC_ARMOR)
                .connect(key("travel_23"))
                .connect(key("aevitas_c_armor_1"))
                .build(registrar);

        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("aevitas_c_reach_1"), 22, 56)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.aevitas))
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_REACH)
                .setName(NAME_INC_REACH)
                .connect(key("aevitas_m_life_resist"))
                .connect(key("aevitas_c_reach_2"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("aevitas_c_reach_2"), 26, 57)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.aevitas))
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_REACH)
                .setName(NAME_INC_REACH)
                .connect(key("travel_21"))
                .connect(key("aevitas_c_reach_1"))
                .build(registrar);
    }

    private PerkDataBuilder<AttributeModifierPerk> makeTravelNode(float x, float y, ResourceLocation perkKey) {
        return PerkDataBuilder.ofType(PerkTypeHandler.MODIFIER_PERK)
                .create(perkKey, x, y)
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT)
                .setName(NAME_INC_PERK_EFFECT);
    }
}
