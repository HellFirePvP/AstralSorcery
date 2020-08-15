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
    }

    private void registerVicioRoot(Consumer<FinishedPerk> registrar) {

    }

    private void registerAevitasRoot(Consumer<FinishedPerk> registrar) {
        PerkDataBuilder.ofType(ROOT_AEVITAS)
                .create("aevitas", 10, 51)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.aevitas))
                .addModifier(2, ModifierType.ADDITION, ATTR_TYPE_HEALTH)
                .build(registrar);

        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create("aevitas_life_armor_1", 12, 48)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.aevitas))
                .addModifier(0.05F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ARMOR)
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_HEALTH)
                .setName(name("hybrid.life_armor"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create("aevitas_life_armor_2", 14, 47)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.aevitas))
                .addModifier(0.05F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ARMOR)
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_HEALTH)
                .setName(name("hybrid.life_armor"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create("aevitas_life_reach_1", 14, 52)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.aevitas))
                .addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_REACH)
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_HEALTH)
                .setName(name("hybrid.life_reach"))
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create("aevitas_life_reach_2", 16, 51)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.aevitas))
                .addModifier(0.03F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_REACH)
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_HEALTH)
                .setName(name("hybrid.life_reach"))
                .build(registrar);

        PerkDataBuilder.ofType(MAJOR_PERK)
                .create("aevitas_m_life_armor", 16, 44)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.aevitas))
                .addModifier(0.05F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ARMOR)
                .addModifier(1, ModifierType.ADDITION, ATTR_TYPE_HEALTH)
                .setName(name("named.thick_skin"))
                .build(registrar);
        PerkDataBuilder.ofType(MAJOR_PERK)
                .create("aevitas_m_life_resist", 19, 52)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.aevitas))
                .addModifier(0.05F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_INC_ALL_ELEMENTAL_RESIST)
                .addModifier(1, ModifierType.ADDITION, ATTR_TYPE_HEALTH)
                .setName(name("named.melding"))
                .build(registrar);

        PerkDataBuilder.ofType(GEM_SLOT_PERK)
                .create("aevitas_m_gem", 17, 48)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.aevitas))
                .build(registrar);

        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create("aevitas_life_bridge_1", 19, 45)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.aevitas))
                .addModifier(1F, ModifierType.ADDITION, ATTR_TYPE_HEALTH)
                .setName(NAME_ADD_LIFE)
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create("aevitas_life_bridge_2", 21, 47)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.aevitas))
                .addModifier(1F, ModifierType.ADDITION, ATTR_TYPE_HEALTH)
                .setName(NAME_ADD_LIFE)
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create("aevitas_life_bridge_3", 20, 50)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.aevitas))
                .addModifier(1F, ModifierType.ADDITION, ATTR_TYPE_HEALTH)
                .setName(NAME_ADD_LIFE)
                .build(registrar);

        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create("aevitas_c_armor_1", 17, 40)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.aevitas))
                .addModifier(0.05F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ARMOR)
                .setName(NAME_INC_ARMOR)
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create("aevitas_c_armor_2", 19, 36)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.aevitas))
                .addModifier(0.05F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_ARMOR)
                .setName(NAME_INC_ARMOR)
                .build(registrar);

        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create("aevitas_c_reach_1", 22, 56)
                .modify(perk -> perk.addRequireConstellation(ConstellationsAS.aevitas))
                .addModifier(0.02F, ModifierType.ADDED_MULTIPLY, ATTR_TYPE_REACH)
                .setName(NAME_INC_REACH)
                .build(registrar);
        PerkDataBuilder.ofType(MODIFIER_PERK)
                .create("aevitas_c_reach_2", 26, 57)
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
