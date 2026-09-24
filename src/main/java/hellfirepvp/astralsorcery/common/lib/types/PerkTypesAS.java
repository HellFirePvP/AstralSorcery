/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.lib.types;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.lib.RegistriesAS;
import hellfirepvp.astralsorcery.common.perk.tree.AbstractPerk;
import hellfirepvp.astralsorcery.common.perk.tree.AttributeModifierPerk;
import hellfirepvp.astralsorcery.common.perk.tree.perk.KeyPerk;
import hellfirepvp.astralsorcery.common.perk.tree.perk.MajorPerk;
import hellfirepvp.astralsorcery.common.perk.tree.PerkType;
import hellfirepvp.astralsorcery.common.perk.tree.perk.key.*;
import hellfirepvp.astralsorcery.common.perk.tree.perk.root.*;
import hellfirepvp.astralsorcery.common.perk.tree.perk.socket.GemSocketPerk;
import hellfirepvp.astralsorcery.common.util.data.PerkTypeRegistryObject;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: PerkTypesAS
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class PerkTypesAS {

    public static final DeferredRegister<PerkType<?>> PERK_TYPE_REGISTER =
            DeferredRegister.create(RegistriesAS.KEY_PERK_TYPES, AstralSorcery.MODID);

    public static final PerkTypeRegistryObject<AttributeModifierPerk<?>> MODIFIER_PERK =
            register("modifier_perk", AttributeModifierPerk.TYPE);
    public static final PerkTypeRegistryObject<MajorPerk> MAJOR_PERK =
            register("major_perk", MajorPerk.TYPE);
    public static final PerkTypeRegistryObject<KeyPerk> KEY_PERK =
            register("key_perk", KeyPerk.TYPE);
    public static final PerkTypeRegistryObject<GemSocketPerk> GEM_SOCKET_PERK =
            register("gem_socket_perk", GemSocketPerk.TYPE);

    public static final PerkTypeRegistryObject<RootPerkAevitas> ROOT_PERK_AEVITAS =
            register("root_perk_aevitas", RootPerkAevitas.TYPE);
    public static final PerkTypeRegistryObject<RootPerkArmara> ROOT_PERK_ARMARA =
            register("root_perk_armara", RootPerkArmara.TYPE);
    public static final PerkTypeRegistryObject<RootPerkDiscidia> ROOT_PERK_DISCIDIA =
            register("root_perk_discidia", RootPerkDiscidia.TYPE);
    public static final PerkTypeRegistryObject<RootPerkEvorsio> ROOT_PERK_EVORSIO =
            register("root_perk_evorsio", RootPerkEvorsio.TYPE);
    public static final PerkTypeRegistryObject<RootPerkVicio> ROOT_PERK_VICIO =
            register("root_perk_vicio", RootPerkVicio.TYPE);

    public static final PerkTypeRegistryObject<KeyPerkTeleportDrops> KEY_TELEPORT_DROPS =
            register("key_teleport_drops", KeyPerkTeleportDrops.TYPE);
    public static final PerkTypeRegistryObject<KeyPerkAllToolTypes> KEY_ALL_TOOL_TYPES =
            register("key_all_tool_types", KeyPerkAllToolTypes.TYPE);
    public static final PerkTypeRegistryObject<KeyPerkTreeConnector> KEY_TREE_CONNECTOR =
            register("key_tree_connector", KeyPerkTreeConnector.TYPE);
    public static final PerkTypeRegistryObject<KeyPerkTreeConnectorDelegate> KEY_TREE_CONNECTOR_DELEGATE =
            register("key_tree_connector_delegate", KeyPerkTreeConnectorDelegate.TYPE);
    public static final PerkTypeRegistryObject<KeyPerkNoKnockback> KEY_NO_KNOCKBACK =
            register("key_no_knockback", KeyPerkNoKnockback.TYPE);
    public static final PerkTypeRegistryObject<KeyPerkMendArmor> KEY_MEND_ARMOR =
            register("key_mend_armor", KeyPerkMendArmor.TYPE);
    public static final PerkTypeRegistryObject<KeyPerkAddEnchantments> KEY_ADD_ENCHANTMENTS =
            register("key_add_enchantments", KeyPerkAddEnchantments.TYPE);
    public static final PerkTypeRegistryObject<KeyPerkCleanseNegativeEffects> KEY_CLEANSE_NEGATIVE_EFFECTS =
            register("key_cleanse_negative_effects", KeyPerkCleanseNegativeEffects.TYPE);
    public static final PerkTypeRegistryObject<KeyPerkCullingAttack> KEY_CULLING_ATTACK =
            register("key_culling_attack", KeyPerkCullingAttack.TYPE);
    public static final PerkTypeRegistryObject<KeyPerkDamageArmor> KEY_DAMAGE_ARMOR =
            register("key_damage_armor", KeyPerkDamageArmor.TYPE);
    public static final PerkTypeRegistryObject<KeyPerkDisarm> KEY_DISARM =
            register("key_disarm", KeyPerkDisarm.TYPE);
    public static final PerkTypeRegistryObject<KeyPerkReducedFood> KEY_REDUCED_FOOD =
            register("key_reduced_food", KeyPerkReducedFood.TYPE);
    public static final PerkTypeRegistryObject<KeyPerkCheatDeath> KEY_CHEAT_DEATH =
            register("key_cheat_death", KeyPerkCheatDeath.TYPE);
    public static final PerkTypeRegistryObject<KeyPerkDamageEffects> KEY_DAMAGE_EFFECTS =
            register("key_damage_effects", KeyPerkDamageEffects.TYPE);
    public static final PerkTypeRegistryObject<KeyPerkProjectileDistance> KEY_PROJECTILE_DISTANCE =
            register("key_projectile_distance", KeyPerkProjectileDistance.TYPE);
    public static final PerkTypeRegistryObject<KeyPerkProjectileProximity> KEY_PROJECTILE_PROXIMITY =
            register("key_projectile_proximity", KeyPerkProjectileProximity.TYPE);
    public static final PerkTypeRegistryObject<KeyPerkLastBreath> KEY_LAST_BREATH =
            register("key_last_breath", KeyPerkLastBreath.TYPE);
    public static final PerkTypeRegistryObject<KeyPerkNoArmor> KEY_NO_ARMOR =
            register("key_no_armor", KeyPerkNoArmor.TYPE);
    public static final PerkTypeRegistryObject<KeyPerkRampage> KEY_RAMPAGE =
            register("key_rampage", KeyPerkRampage.TYPE);
    public static final PerkTypeRegistryObject<KeyPerkGrowPlants> KEY_GROW_PLANTS =
            register("key_grow_plants", KeyPerkGrowPlants.TYPE);
    public static final PerkTypeRegistryObject<KeyPerkRangeAreaOfEffect> KEY_RANGE_AREA_OF_EFFECT =
            register("key_range_area_of_effect", KeyPerkRangeAreaOfEffect.TYPE);
    
    private static <T extends AbstractPerk<?>> PerkTypeRegistryObject<T> register(String name, PerkType<T> type) {
        return new PerkTypeRegistryObject<>(PERK_TYPE_REGISTER.register(name, () -> type));
    }
}
