/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.integrations;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.base.Mods;
import hellfirepvp.astralsorcery.common.constellation.perk.attribute.PerkAttributeModifier;
import hellfirepvp.astralsorcery.common.constellation.perk.tree.PerkTree;
import hellfirepvp.astralsorcery.common.event.APIRegistryEvent;
import hellfirepvp.astralsorcery.common.integrations.mods.thaumcraft.perks.AttributeTypeRunicShielding;
import hellfirepvp.astralsorcery.common.integrations.mods.thaumcraft.perks.ModifierPerkThaumcraft;
import hellfirepvp.astralsorcery.common.integrations.mods.thaumcraft.perks.key.KeyEnergyShield;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import static hellfirepvp.astralsorcery.common.constellation.perk.attribute.AttributeTypeRegistry.ATTR_TYPE_ARMOR;
import static hellfirepvp.astralsorcery.common.constellation.perk.attribute.AttributeTypeRegistry.ATTR_TYPE_INC_ALL_ELEMENTAL_RESIST;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ModIntegrationThaumcraft
 * Created by HellFirePvP
 * Date: 18.11.2018 / 22:15
 */
public class ModIntegrationThaumcraft {

    public static final ModIntegrationThaumcraft INSTANCE = new ModIntegrationThaumcraft();

    public static final String ATTR_TYPE_RUNIC_SHIELDING = AstralSorcery.MODID + ".compat." + Mods.THAUMCRAFT.modid + ".runicshield";

    private ModIntegrationThaumcraft() {}

    @SubscribeEvent
    public void onTypeRegister(APIRegistryEvent.PerkAttributeTypeRegister register) {
        register.registerAttribute(new AttributeTypeRunicShielding());
    }

    @SubscribeEvent
    public void onPerkRegister(APIRegistryEvent.PerkRegister register) {
        KeyEnergyShield visInoc = new KeyEnergyShield("mod_tc_key_ci", 21, 26);
        visInoc.addModifier(2F, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_RUNIC_SHIELDING);
        visInoc.addModifier(0.25F, PerkAttributeModifier.Mode.STACKING_MULTIPLY, ATTR_TYPE_ARMOR);
        visInoc.addModifier(1.15F, PerkAttributeModifier.Mode.STACKING_MULTIPLY, ATTR_TYPE_INC_ALL_ELEMENTAL_RESIST);

        ModifierPerkThaumcraft addedShield = new ModifierPerkThaumcraft("mod_tc_added_runic", 22, 24);
        addedShield.addModifier(2, PerkAttributeModifier.Mode.ADDITION, ATTR_TYPE_RUNIC_SHIELDING);

        ModifierPerkThaumcraft incShield = new ModifierPerkThaumcraft("mod_tc_inc_runic", 19, 27);
        incShield.addModifier(0.1F, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_RUNIC_SHIELDING);
        ModifierPerkThaumcraft incShield2 = new ModifierPerkThaumcraft("mod_tc_inc_runic_1", 22, 28).setNameOverride(incShield);
        incShield2.addModifier(0.1F, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_RUNIC_SHIELDING);
        ModifierPerkThaumcraft incShield3 = new ModifierPerkThaumcraft("mod_tc_inc_runic_2", 20, 30).setNameOverride(incShield);
        incShield3.addModifier(0.07F, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_RUNIC_SHIELDING);
        ModifierPerkThaumcraft incShield4 = new ModifierPerkThaumcraft("mod_tc_inc_runic_3", 16, 28).setNameOverride(incShield);
        incShield4.addModifier(0.07F, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_RUNIC_SHIELDING);
        ModifierPerkThaumcraft incShield5 = new ModifierPerkThaumcraft("mod_tc_inc_runic_4", 17, 32).setNameOverride(incShield);
        incShield5.addModifier(0.04F, PerkAttributeModifier.Mode.ADDED_MULTIPLY, ATTR_TYPE_RUNIC_SHIELDING);

        register.registerPerk(addedShield)
                .connect(PerkTree.PERK_TREE.getAstralSorceryPerk("outer_s_inc_def_4"));
        register.registerPerk(visInoc)
                .connect(addedShield);
        register.registerPerk(incShield)
                .connect(visInoc);
        register.registerPerk(incShield2)
                .connect(visInoc);
        register.registerPerk(incShield3)
                .connect(incShield);
        register.registerPerk(incShield4)
                .connect(incShield);
        register.registerPerk(incShield5)
                .connect(incShield4);
    }

}
