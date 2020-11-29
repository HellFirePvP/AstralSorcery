/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.perk.data;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.perk.AbstractPerk;
import hellfirepvp.astralsorcery.common.perk.ProgressGatedPerk;
import hellfirepvp.astralsorcery.common.perk.modifier.AttributeConverterPerk;
import hellfirepvp.astralsorcery.common.perk.modifier.AttributeModifierPerk;
import hellfirepvp.astralsorcery.common.perk.node.ConstellationPerk;
import hellfirepvp.astralsorcery.common.perk.node.socket.GemSocketMajorPerk;
import hellfirepvp.astralsorcery.common.perk.node.KeyPerk;
import hellfirepvp.astralsorcery.common.perk.node.MajorPerk;
import hellfirepvp.astralsorcery.common.perk.node.focus.KeyAlcara;
import hellfirepvp.astralsorcery.common.perk.node.focus.KeyGelu;
import hellfirepvp.astralsorcery.common.perk.node.focus.KeyUlteria;
import hellfirepvp.astralsorcery.common.perk.node.focus.KeyVorux;
import hellfirepvp.astralsorcery.common.perk.node.key.*;
import hellfirepvp.astralsorcery.common.perk.node.root.*;
import hellfirepvp.astralsorcery.common.util.TriFunction;
import net.minecraft.util.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PerkTypeHandler
 * Created by HellFirePvP
 * Date: 12.08.2020 / 21:08
 */
public class PerkTypeHandler {

    private static final Map<ResourceLocation, Type<? extends AbstractPerk>> CONVERTER_MAP = new HashMap<>();

    public static final Type<AbstractPerk> DEFAULT = new Type<>(AstralSorcery.key("default"), AbstractPerk::new);

    public static final Type<KeyAddEnchantment> KEY_ADD_ENCHANTMENT = registerConverter(AstralSorcery.key("key_add_enchantment"), KeyAddEnchantment::new);
    public static final Type<KeyAreaOfEffect> KEY_AOE = registerConverter(AstralSorcery.key("key_area_of_effect"), KeyAreaOfEffect::new);
    public static final Type<KeyBleed> KEY_BLEED = registerConverter(AstralSorcery.key("key_bleed"), KeyBleed::new);
    public static final Type<KeyChargeBalancing> KEY_CHARGE_BALANCING = registerConverter(AstralSorcery.key("key_charge_balancing"), KeyChargeBalancing::new);
    public static final Type<KeyCheatDeath> KEY_CHEATDEATH = registerConverter(AstralSorcery.key("key_cheatdeath"), KeyCheatDeath::new);
    public static final Type<KeyCleanseBadPotions> KEY_REMOVE_BAD_POTIONS = registerConverter(AstralSorcery.key("key_remove_bad_potions"), KeyCleanseBadPotions::new);
    public static final Type<KeyCullingAttack> KEY_CULLING = registerConverter(AstralSorcery.key("key_culling"), KeyCullingAttack::new);
    public static final Type<KeyDamageArmor> KEY_DAMAGE_ARMOR = registerConverter(AstralSorcery.key("key_damage_armor"), KeyDamageArmor::new);
    public static final Type<KeyDamageEffects> KEY_ON_HIT_POTIONS = registerConverter(AstralSorcery.key("key_on_hit_potions"), KeyDamageEffects::new);
    public static final Type<KeyDigTypes> KEY_DIG_TYPES = registerConverter(AstralSorcery.key("key_dig_types"), KeyDigTypes::new);
    public static final Type<KeyDisarm> KEY_DISARM = registerConverter(AstralSorcery.key("key_disarm"), KeyDisarm::new);
    public static final Type<KeyEntityReach> KEY_ENTITY_REACH = registerConverter(AstralSorcery.key("key_entity_reach"), KeyEntityReach::new);
    public static final Type<KeyGrowables> KEY_GROWABLES = registerConverter(AstralSorcery.key("key_growables"), KeyGrowables::new);
    public static final Type<KeyLastBreath> KEY_LOW_LIFE = registerConverter(AstralSorcery.key("key_low_life"), KeyLastBreath::new);
    public static final Type<KeyLightningArc> KEY_LIGHTNING_ARC = registerConverter(AstralSorcery.key("key_lightning_arc"), KeyLightningArc::new);
    public static final Type<KeyMagnetDrops> KEY_MAGNET_DROPS = registerConverter(AstralSorcery.key("key_magnet_drops"), KeyMagnetDrops::new);
    public static final Type<KeyMantleFlight> KEY_MANTLE_CREATIVE_FLIGHT = registerConverter(AstralSorcery.key("key_vicio_mantle_creative_flight"), KeyMantleFlight::new);
    public static final Type<KeyMending> KEY_ARMOR_MENDING = registerConverter(AstralSorcery.key("key_armor_mending"), KeyMending::new);
    public static final Type<KeyNoArmor> KEY_NO_ARMOR = registerConverter(AstralSorcery.key("key_no_armor"), KeyNoArmor::new);
    public static final Type<KeyNoKnockback> KEY_NO_KNOCKBACK = registerConverter(AstralSorcery.key("key_no_knockback"), KeyNoKnockback::new);
    public static final Type<KeyProjectileDistance> KEY_RPOJ_DISTANCE = registerConverter(AstralSorcery.key("key_proj_distance"), KeyProjectileDistance::new);
    public static final Type<KeyProjectileProximity> KEY_PROJ_PROXIMITY = registerConverter(AstralSorcery.key("key_proj_proximity"), KeyProjectileProximity::new);
    public static final Type<KeyRampage> KEY_RAMPAGE = registerConverter(AstralSorcery.key("key_rampage"), KeyRampage::new);
    public static final Type<KeyReducedFood> KEY_REDUCED_FOOD = registerConverter(AstralSorcery.key("key_reduced_food_need"), KeyReducedFood::new);
    public static final Type<KeySpawnLights> KEY_SPAWN_LIGHTS = registerConverter(AstralSorcery.key("key_spawn_lights"), KeySpawnLights::new);
    public static final Type<KeyStepAssist> KEY_STEP_ASSIST = registerConverter(AstralSorcery.key("key_step_assist"), KeyStepAssist::new);
    public static final Type<KeyStoneEnrichment> KEY_STONE_ENRICHMENT = registerConverter(AstralSorcery.key("key_stone_enrichment"), KeyStoneEnrichment::new);
    public static final Type<KeyVoidTrash> KEY_VOID_TRASH = registerConverter(AstralSorcery.key("key_void_trash"), KeyVoidTrash::new);

    public static final Type<KeyAlcara> FOCUS_ALCARA = registerConverter(AstralSorcery.key("focus_alcara"), KeyAlcara::new);
    public static final Type<KeyGelu> FOCUS_GELU = registerConverter(AstralSorcery.key("focus_gelu"), KeyGelu::new);
    public static final Type<KeyUlteria> FOCUS_ULTERIA = registerConverter(AstralSorcery.key("focus_ulteria"), KeyUlteria::new);
    public static final Type<KeyVorux> FOCUS_VORUX = registerConverter(AstralSorcery.key("focus_vorux"), KeyVorux::new);

    public static final Type<RootAevitas> ROOT_AEVITAS = registerConverter(AstralSorcery.key("root_aevitas"), RootAevitas::new);
    public static final Type<RootArmara> ROOT_ARMARA = registerConverter(AstralSorcery.key("root_armara"), RootArmara::new);
    public static final Type<RootDiscidia> ROOT_DISCIDIA = registerConverter(AstralSorcery.key("root_discidia"), RootDiscidia::new);
    public static final Type<RootEvorsio> ROOT_EVORSIO = registerConverter(AstralSorcery.key("root_evorsio"), RootEvorsio::new);
    public static final Type<RootVicio> ROOT_VICIO = registerConverter(AstralSorcery.key("root_vicio"), RootVicio::new);

    public static final Type<KeyTreeConnector> EPIPHANY_PERK = registerConverter(AstralSorcery.key("epiphany_perk"), KeyTreeConnector::new);
    public static final Type<KeyPerk> KEY_PERK = registerConverter(AstralSorcery.key("key_perk"), KeyPerk::new);
    public static final Type<ConstellationPerk> CONSTELLATION_PERK = registerConverter(AstralSorcery.key("constellation_perk"), ConstellationPerk::convertToThis);
    public static final Type<MajorPerk> MAJOR_PERK = registerConverter(AstralSorcery.key("major_perk"), MajorPerk::new);
    public static final Type<GemSocketMajorPerk> GEM_SLOT_PERK = registerConverter(AstralSorcery.key("gem_slot_perk"), GemSocketMajorPerk::new);
    public static final Type<AttributeModifierPerk> MODIFIER_PERK = registerConverter(AstralSorcery.key("modifier_perk"), AttributeModifierPerk::new);
    public static final Type<AttributeConverterPerk> CONVERTER_PERK = registerConverter(AstralSorcery.key("converter_perk"), AttributeConverterPerk::new);
    public static final Type<ProgressGatedPerk> GATED_PERK = registerConverter(AstralSorcery.key("gated_perk"), ProgressGatedPerk::new);

    public static <T extends AbstractPerk> Type<T> registerConverter(ResourceLocation name, TriFunction<ResourceLocation, Float, Float, T> converter) {
        Type<T> type = new Type<>(name, converter);
        CONVERTER_MAP.put(name, type);
        return type;
    }

    public static <T extends AbstractPerk> T convert(ResourceLocation perkKey, float x, float y, ResourceLocation alternativeBase) {
        return (T) CONVERTER_MAP.getOrDefault(alternativeBase, DEFAULT).convert(perkKey, x, y);
    }

    public static boolean hasCustomType(ResourceLocation key) {
        return CONVERTER_MAP.containsKey(key);
    }

    public static void init() {
        registerConverter(DEFAULT.getKey(), AbstractPerk::new);
    }

    public static class Type<T extends AbstractPerk> {

        private final ResourceLocation key;
        private final TriFunction<ResourceLocation, Float, Float, T> converter;

        private Type(ResourceLocation key, TriFunction<ResourceLocation, Float, Float, T> converter) {
            this.key = key;
            this.converter = converter;
        }

        public T convert(ResourceLocation perkKey, float x, float y) {
            return this.converter.apply(perkKey, x, y);
        }

        public final ResourceLocation getKey() {
            return key;
        }
    }
}
