package hellfirepvp.astralsorcery.datagen.data.perks;

import hellfirepvp.astralsorcery.common.lib.ConstellationsAS;
import hellfirepvp.astralsorcery.common.perk.data.PerkTypeHandler;
import hellfirepvp.astralsorcery.common.perk.data.builder.PerkDataBuilder;
import hellfirepvp.astralsorcery.common.perk.data.builder.PerkDataProvider;
import hellfirepvp.astralsorcery.common.perk.modifier.AttributeModifierPerk;
import hellfirepvp.astralsorcery.common.perk.type.ModifierType;
import net.minecraft.data.DataGenerator;
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

    private static int travelNodeCount = 0;

    public AstralPerkTreeProvider(DataGenerator generator) {
        super(generator);
    }

    @Override
    public void registerPerks(Consumer<FinishedPerk> registrar) {
        registerRoots(registrar);
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
                .build(registrar);

        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("evorsio_damage_1"), 25, 16)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.evorsio))
                .addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_MELEE_DAMAGE)
                .setName(NAME_INC_MELEE_DAMAGE)
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("evorsio_damage_2"), 27, 17)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.evorsio))
                .addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_MELEE_DAMAGE)
                .setName(NAME_INC_MELEE_DAMAGE)
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("evorsio_mining_1"), 23, 18)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.evorsio))
                .addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_HARVEST_SPEED)
                .setName(NAME_INC_MINING_SPEED)
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("evorsio_mining_2"), 24, 20)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.evorsio))
                .addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_HARVEST_SPEED)
                .setName(NAME_INC_MINING_SPEED)
                .build(registrar);

        PerkDataBuilder.ofType(MAJOR_PERK)
                .create(key("evorsio_m_dmg_ats"), 30, 18)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.evorsio))
                .addModifier(0.05F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_MELEE_DAMAGE)
                .addModifier(0.05F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ATTACK_SPEED)
                .setName(name("named.expertise"))
                .build(registrar);
        PerkDataBuilder.ofType(MAJOR_PERK)
                .create(key("evorsio_m_mining_reach"), 25, 23)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.evorsio))
                .addModifier(0.05F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_HARVEST_SPEED)
                .addModifier(0.1F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_REACH)
                .setName(name("named.tunneling"))
                .build(registrar);

        PerkDataBuilder.ofType(GEM_SLOT_PERK)
                .create(key("evorsio_m_gem"), 28, 21)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.evorsio))
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
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("evorsio_c_armor_2"), 37, 18)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.evorsio))
                .addModifier(0.05F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ARMOR)
                .setName(NAME_INC_ARMOR)
                .build(registrar);

        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("evorsio_c_mining_1"), 23, 26)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.evorsio))
                .addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_HARVEST_SPEED)
                .setName(NAME_INC_MINING_SPEED)
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("evorsio_c_mining_2"), 22, 29)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.evorsio))
                .addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_HARVEST_SPEED)
                .setName(NAME_INC_MINING_SPEED)
                .build(registrar);
    }

    private void registerDiscidiaRoot(Consumer<FinishedPerk> registrar) {
        PerkDataBuilder.ofType(ROOT_DISCIDIA)
                .create(key("discidia"), 59, 14)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.discidia))
                .addModifier(1.1F, ModifierType.STACKING_MULTIPLY, ATTR_TYPE_MELEE_DAMAGE)
                .addModifier(1.1F, ModifierType.STACKING_MULTIPLY, ATTR_TYPE_PROJ_DAMAGE)
                .build(registrar);

        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("discidia_proj_1"), 57, 18)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.discidia))
                .addModifier(0.04F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_PROJ_DAMAGE)
                .setName(NAME_INC_PROJ_DAMAGE)
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("discidia_proj_2"), 56, 20)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.discidia))
                .addModifier(0.04F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_PROJ_DAMAGE)
                .setName(NAME_INC_PROJ_DAMAGE)
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("discidia_melee_1"), 55, 16)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.discidia))
                .addModifier(0.04F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_MELEE_DAMAGE)
                .setName(NAME_INC_MELEE_DAMAGE)
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("discidia_melee_2"), 53, 17)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.discidia))
                .addModifier(0.04F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_MELEE_DAMAGE)
                .setName(NAME_INC_MELEE_DAMAGE)
                .build(registrar);

        PerkDataBuilder.ofType(MAJOR_PERK)
                .create(key("discidia_m_proj_dmg_speed"), 55, 23)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.discidia))
                .addModifier(0.05F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_PROJ_DAMAGE)
                .addModifier(0.2F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_PROJ_SPEED)
                .setName(name("named.deadly_draw"))
                .build(registrar);
        PerkDataBuilder.ofType(MAJOR_PERK)
                .create(key("discidia_m_melee_reach"), 50, 18)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.discidia))
                .addModifier(0.05F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_MELEE_DAMAGE)
                .addModifier(0.1F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_REACH)
                .setName(name("named.strong_arms"))
                .build(registrar);

        PerkDataBuilder.ofType(GEM_SLOT_PERK)
                .create(key("discidia_m_gem"), 52, 21)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.discidia))
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
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("discidia_c_damage_2"), 58, 29)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.discidia))
                .addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_MELEE_DAMAGE)
                .addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_PROJ_DAMAGE)
                .setName(name("hybrid.melee_proj_dmg"))
                .build(registrar);

        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("discidia_c_ats_bridge_1"), 47, 17)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.discidia))
                .addModifier(0.05F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ATTACK_SPEED)
                .setName(NAME_INC_ATTACK_SPEED)
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("discidia_c_ats_bridge_2"), 43, 18)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.discidia))
                .addModifier(0.05F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ATTACK_SPEED)
                .setName(NAME_INC_ATTACK_SPEED)
                .build(registrar);
    }

    private void registerArmaraRoot(Consumer<FinishedPerk> registrar) {
        PerkDataBuilder.ofType(ROOT_ARMARA)
                .create(key("armara"), 70, 51)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.armara))
                .addModifier(1.20F, ModifierType.STACKING_MULTIPLY, ATTR_TYPE_ARMOR)
                .build(registrar);

        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("armara_armor_1"), 66, 52)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.armara))
                .addModifier(1F, ModifierType.ADDITION, ATTR_TYPE_ARMOR)
                .setName(NAME_ADD_ARMOR)
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("armara_armor_2"), 64, 51)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.armara))
                .addModifier(1F, ModifierType.ADDITION, ATTR_TYPE_ARMOR)
                .setName(NAME_ADD_ARMOR)
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("armara_resist_1"), 68, 48)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.armara))
                .addModifier(0.05F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_ALL_ELEMENTAL_RESIST)
                .setName(NAME_INC_ALL_RES)
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("armara_resist_2"), 66, 47)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.armara))
                .addModifier(0.05F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_ALL_ELEMENTAL_RESIST)
                .setName(NAME_INC_ALL_RES)
                .build(registrar);

        PerkDataBuilder.ofType(MAJOR_PERK)
                .create(key("armara_m_life_armor"), 61, 52)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.armara))
                .addModifier(0.05F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_HEALTH)
                .addModifier(1F, ModifierType.ADDITION, ATTR_TYPE_ARMOR)
                .setName(name("named.resilience"))
                .build(registrar);
        PerkDataBuilder.ofType(MAJOR_PERK)
                .create(key("armara_m_resist_armor"), 64, 44)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.armara))
                .addModifier(0.1F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_ALL_ELEMENTAL_RESIST)
                .addModifier(2F, ModifierType.ADDITION, ATTR_TYPE_ARMOR)
                .setName(name("named.bulwark"))
                .build(registrar);

        PerkDataBuilder.ofType(GEM_SLOT_PERK)
                .create(key("armara_m_gem"), 63, 48)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.armara))
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
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("armara_c_movespeed_2"), 54, 57)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.armara))
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_MOVESPEED)
                .setName(NAME_INC_MOVESPEED)
                .build(registrar);

        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("armara_c_recovery_1"), 63, 40)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.armara))
                .addModifier(0.1F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_LIFE_RECOVERY)
                .setName(NAME_INC_LIFE_RECOVERY)
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("armara_c_recovery_2"), 61, 36)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.armara))
                .addModifier(0.1F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_LIFE_RECOVERY)
                .setName(NAME_INC_LIFE_RECOVERY)
                .build(registrar);
    }

    private void registerVicioRoot(Consumer<FinishedPerk> registrar) {
        PerkDataBuilder.ofType(ROOT_VICIO)
                .create(key("vicio"), 40, 70)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.vicio))
                .addModifier(0.1F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_MOVESPEED)
                .build(registrar);

        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("vicio_reach_1"), 38, 67)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.vicio))
                .addModifier(0.04F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_REACH)
                .setName(NAME_INC_REACH)
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("vicio_reach_2"), 37, 65)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.vicio))
                .addModifier(0.04F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_REACH)
                .setName(NAME_INC_REACH)
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("vicio_dodge_1"), 42, 67)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.vicio))
                .addModifier(2F, ModifierType.ADDITION, ATTR_TYPE_INC_DODGE)
                .setName(NAME_ADD_DODGE)
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("vicio_dodge_2"), 43, 65)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.vicio))
                .addModifier(2F, ModifierType.ADDITION, ATTR_TYPE_INC_DODGE)
                .setName(NAME_ADD_DODGE)
                .build(registrar);

        PerkDataBuilder.ofType(MAJOR_PERK)
                .create(key("vicio_m_reach_movespeed"), 35, 63)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.vicio))
                .addModifier(0.04F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_MOVESPEED)
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_REACH)
                .setName(name("named.swiftness"))
                .build(registrar);
        PerkDataBuilder.ofType(MAJOR_PERK)
                .create(key("vicio_m_dodge_movespeed"), 45, 63)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.vicio))
                .addModifier(0.04F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_MOVESPEED)
                .addModifier(0.1F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_DODGE)
                .setName(name("named.agility"))
                .build(registrar);

        PerkDataBuilder.ofType(GEM_SLOT_PERK)
                .create(key("vicio_m_gem"), 40, 64)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.vicio))
                .build(registrar);

        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("vicio_ats_bridge_1"), 37, 61)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.vicio))
                .addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ATTACK_SPEED)
                .setName(NAME_INC_ATTACK_SPEED)
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("vicio_ats_bridge_2"), 40, 59)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.vicio))
                .addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ATTACK_SPEED)
                .setName(NAME_INC_ATTACK_SPEED)
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("vicio_ats_bridge_3"), 43, 61)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.vicio))
                .addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ATTACK_SPEED)
                .setName(NAME_INC_ATTACK_SPEED)
                .build(registrar);

        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("vicio_c_mining_1"), 34, 60)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.vicio))
                .addModifier(0.04F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_HARVEST_SPEED)
                .setName(NAME_INC_MINING_SPEED)
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("vicio_c_mining_2"), 31, 61)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.vicio))
                .addModifier(0.04F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_HARVEST_SPEED)
                .setName(NAME_INC_MINING_SPEED)
                .build(registrar);

        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("vicio_c_armor_1"), 46, 60)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.vicio))
                .addModifier(0.05F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ARMOR)
                .setName(NAME_INC_ARMOR)
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("vicio_c_armor_2"), 49, 61)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.vicio))
                .addModifier(0.05F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ARMOR)
                .setName(NAME_INC_ARMOR)
                .build(registrar);
    }

    private void registerAevitasRoot(Consumer<FinishedPerk> registrar) {
        PerkDataBuilder.ofType(ROOT_AEVITAS)
                .create(key("aevitas"), 10, 51)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.aevitas))
                .addModifier(2, ModifierType.ADDITION, ATTR_TYPE_HEALTH)
                .build(registrar);

        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("aevitas_life_armor_1"), 12, 48)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.aevitas))
                .addModifier(0.05F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ARMOR)
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_HEALTH)
                .setName(name("hybrid.life_armor"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("aevitas_life_armor_2"), 14, 47)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.aevitas))
                .addModifier(0.05F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ARMOR)
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_HEALTH)
                .setName(name("hybrid.life_armor"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("aevitas_life_reach_1"), 14, 52)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.aevitas))
                .addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_REACH)
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_HEALTH)
                .setName(name("hybrid.life_reach"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("aevitas_life_reach_2"), 16, 51)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.aevitas))
                .addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_REACH)
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_HEALTH)
                .setName(name("hybrid.life_reach"))
                .build(registrar);

        PerkDataBuilder.ofType(MAJOR_PERK)
                .create(key("aevitas_m_life_armor"), 16, 44)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.aevitas))
                .addModifier(0.05F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ARMOR)
                .addModifier(1, ModifierType.ADDITION, ATTR_TYPE_HEALTH)
                .setName(name("named.thick_skin"))
                .build(registrar);
        PerkDataBuilder.ofType(MAJOR_PERK)
                .create(key("aevitas_m_life_resist"), 19, 52)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.aevitas))
                .addModifier(0.05F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_ALL_ELEMENTAL_RESIST)
                .addModifier(1, ModifierType.ADDITION, ATTR_TYPE_HEALTH)
                .setName(name("named.melding"))
                .build(registrar);

        PerkDataBuilder.ofType(GEM_SLOT_PERK)
                .create(key("aevitas_m_gem"), 17, 48)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.aevitas))
                .build(registrar);

        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("aevitas_life_bridge_1"), 19, 45)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.aevitas))
                .addModifier(1F, ModifierType.ADDITION, ATTR_TYPE_HEALTH)
                .setName(NAME_ADD_LIFE)
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("aevitas_life_bridge_2"), 21, 47)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.aevitas))
                .addModifier(1F, ModifierType.ADDITION, ATTR_TYPE_HEALTH)
                .setName(NAME_ADD_LIFE)
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("aevitas_life_bridge_3"), 20, 50)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.aevitas))
                .addModifier(1F, ModifierType.ADDITION, ATTR_TYPE_HEALTH)
                .setName(NAME_ADD_LIFE)
                .build(registrar);

        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("aevitas_c_armor_1"), 17, 40)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.aevitas))
                .addModifier(0.05F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ARMOR)
                .setName(NAME_INC_ARMOR)
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("aevitas_c_armor_2"), 19, 36)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.aevitas))
                .addModifier(0.05F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ARMOR)
                .setName(NAME_INC_ARMOR)
                .build(registrar);

        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("aevitas_c_reach_1"), 22, 56)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.aevitas))
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_REACH)
                .setName(NAME_INC_REACH)
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create(key("aevitas_c_reach_2"), 26, 57)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.aevitas))
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_REACH)
                .setName(NAME_INC_REACH)
                .build(registrar);
    }

    private PerkDataBuilder<AttributeModifierPerk> makeTravelNode(float x, float y) {
        return makeTravelNode(x, y, key(String.format("travel_%s", travelNodeCount++)));
    }

    private PerkDataBuilder<AttributeModifierPerk> makeTravelNode(float x, float y, ResourceLocation perkKey) {
        return PerkDataBuilder.ofType(PerkTypeHandler.MODIFIER_PERK)
                .create(perkKey, x, y)
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_PERK_EFFECT)
                .setName(NAME_INC_PERK_EFFECT);
    }
}
