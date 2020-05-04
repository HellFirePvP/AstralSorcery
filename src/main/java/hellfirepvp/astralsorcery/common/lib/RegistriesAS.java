/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.lib;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.constellation.effect.ConstellationEffectProvider;
import hellfirepvp.astralsorcery.common.constellation.engraving.EngravingEffect;
import hellfirepvp.astralsorcery.common.constellation.mantle.MantleEffect;
import hellfirepvp.astralsorcery.common.crafting.recipe.altar.effect.AltarRecipeEffect;
import hellfirepvp.astralsorcery.common.crystal.CrystalProperty;
import hellfirepvp.astralsorcery.common.crystal.calc.PropertyUsage;
import hellfirepvp.astralsorcery.common.perk.AbstractPerk;
import hellfirepvp.astralsorcery.common.perk.reader.PerkAttributeReader;
import hellfirepvp.astralsorcery.common.perk.type.PerkAttributeType;
import hellfirepvp.astralsorcery.common.structure.types.StructureType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryModifiable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RegistriesAS
 * Created by HellFirePvP
 * Date: 02.06.2019 / 09:17
 */
public class RegistriesAS {

    private RegistriesAS() {}

    public static final ResourceLocation REGISTRY_NAME_CONSTELLATIONS = AstralSorcery.key("constellations");
    public static final ResourceLocation REGISTRY_NAME_CONSTELLATION_EFFECTS = AstralSorcery.key("constellation_effect");
    public static final ResourceLocation REGISTRY_NAME_MANTLE_EFFECTS = AstralSorcery.key("mantle_effect");
    public static final ResourceLocation REGISTRY_NAME_ENGRAVING_EFFECT = AstralSorcery.key("engraving_effect");
    public static final ResourceLocation REGISTRY_NAME_PERKS = AstralSorcery.key("perks");
    public static final ResourceLocation REGISTRY_NAME_STRUCTURE_TYPES = AstralSorcery.key("structure_types");
    public static final ResourceLocation REGISTRY_NAME_PERK_ATTRIBUTE_TYPES = AstralSorcery.key("perk_attribute_types");
    public static final ResourceLocation REGISTRY_NAME_PERK_ATTRIBUTE_READERS = AstralSorcery.key("perk_attribute_readers");
    public static final ResourceLocation REGISTRY_NAME_CRYSTAL_PROPERTIES = AstralSorcery.key("attribute_crystal_properties");
    public static final ResourceLocation REGISTRY_NAME_CRYSTAL_USAGES = AstralSorcery.key("attribute_crystal_usages");
    public static final ResourceLocation REGISTRY_NAME_ALTAR_EFFECTS = AstralSorcery.key("altar_recipe_effects");

    public static IForgeRegistry<IConstellation> REGISTRY_CONSTELLATIONS;
    public static IForgeRegistry<ConstellationEffectProvider> REGISTRY_CONSTELLATION_EFFECT;
    public static IForgeRegistry<MantleEffect> REGISTRY_MANTLE_EFFECT;
    public static IForgeRegistry<EngravingEffect> REGISTRY_ENGRAVING_EFFECT;
    public static IForgeRegistryModifiable<AbstractPerk> REGISTRY_PERKS;
    public static IForgeRegistry<StructureType> REGISTRY_STRUCTURE_TYPES;
    public static IForgeRegistry<PerkAttributeType> REGISTRY_PERK_ATTRIBUTE_TYPES;
    public static IForgeRegistry<PerkAttributeReader> REGISTRY_PERK_ATTRIBUTE_READERS;
    public static IForgeRegistry<CrystalProperty> REGISTRY_CRYSTAL_PROPERTIES;
    public static IForgeRegistry<PropertyUsage> REGISTRY_CRYSTAL_USAGES;
    public static IForgeRegistry<AltarRecipeEffect> REGISTRY_ALTAR_EFFECTS;

}
