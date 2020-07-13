/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.registry;

import hellfirepvp.astralsorcery.common.constellation.ConstellationRegistry;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.constellation.effect.ConstellationEffectProvider;
import hellfirepvp.astralsorcery.common.constellation.engraving.EngravingEffect;
import hellfirepvp.astralsorcery.common.constellation.mantle.MantleEffect;
import hellfirepvp.astralsorcery.common.crafting.recipe.altar.effect.AltarRecipeEffect;
import hellfirepvp.astralsorcery.common.crystal.CrystalProperty;
import hellfirepvp.astralsorcery.common.crystal.calc.PropertyUsage;
import hellfirepvp.astralsorcery.common.perk.AbstractPerk;
import hellfirepvp.astralsorcery.common.perk.PerkTree;
import hellfirepvp.astralsorcery.common.perk.reader.PerkAttributeReader;
import hellfirepvp.astralsorcery.common.perk.type.PerkAttributeType;
import hellfirepvp.astralsorcery.common.perk.type.PerkAttributeTypeHelper;
import hellfirepvp.astralsorcery.common.structure.types.StructureType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryModifiable;
import net.minecraftforge.registries.RegistryBuilder;

import static hellfirepvp.astralsorcery.common.lib.RegistriesAS.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RegistryRegistries
 * Created by HellFirePvP
 * Date: 02.06.2019 / 09:18
 */
// Yea don't worry, i'm also having second thoughts at this naming
public class RegistryRegistries {

    private RegistryRegistries() {}

    public static void buildRegistries(RegistryEvent.NewRegistry event) {
        REGISTRY_CONSTELLATIONS = new RegistryBuilder<IConstellation>()
                .setName(REGISTRY_NAME_CONSTELLATIONS)
                .setType(IConstellation.class)
                .add((IForgeRegistry.AddCallback<IConstellation>) (owner, stage, id, obj, oldObj) ->
                        ConstellationRegistry.addConstellation(obj))
                .disableSaving()
                .disableOverrides()
                .create();

        REGISTRY_CONSTELLATION_EFFECT = new RegistryBuilder<ConstellationEffectProvider>()
                .setName(REGISTRY_NAME_CONSTELLATION_EFFECTS)
                .setType(ConstellationEffectProvider.class)
                .disableSaving()
                .disableOverrides()
                .create();

        REGISTRY_MANTLE_EFFECT = new RegistryBuilder<MantleEffect>()
                .setName(REGISTRY_NAME_MANTLE_EFFECTS)
                .setType(MantleEffect.class)
                .disableSaving()
                .disableOverrides()
                .create();

        REGISTRY_ENGRAVING_EFFECT = new RegistryBuilder<EngravingEffect>()
                .setName(REGISTRY_NAME_ENGRAVING_EFFECT)
                .setType(EngravingEffect.class)
                .disableSaving()
                .disableOverrides()
                .create();

        REGISTRY_PERKS = (IForgeRegistryModifiable<AbstractPerk>) new RegistryBuilder<AbstractPerk>()
                .setName(REGISTRY_NAME_PERKS)
                .setType(AbstractPerk.class)
                .add((IForgeRegistry.AddCallback<AbstractPerk>) (owner, stage, id, obj, oldObj) -> {
                    if (PerkTree.PERK_TREE.getConnector(obj) == null) {
                        PerkTree.PERK_TREE.addPerk(obj);
                    }
                })
                .disableSaving()
                .disableOverrides()
                .allowModification()
                .create();

        REGISTRY_STRUCTURE_TYPES = new RegistryBuilder<StructureType>()
                .setName(REGISTRY_NAME_STRUCTURE_TYPES)
                .setType(StructureType.class)
                .disableSaving()
                .disableOverrides()
                .create();

        REGISTRY_PERK_ATTRIBUTE_TYPES = new RegistryBuilder<PerkAttributeType>()
                .setName(REGISTRY_NAME_PERK_ATTRIBUTE_TYPES)
                .setType(PerkAttributeType.class)
                .add((IForgeRegistry.AddCallback<PerkAttributeType>) (owner, stage, id, obj, oldObj) -> {
                    PerkAttributeTypeHelper.register(obj);
                })
                .disableSaving()
                .disableOverrides()
                .create();

        REGISTRY_PERK_ATTRIBUTE_READERS = new RegistryBuilder<PerkAttributeReader>()
                .setName(REGISTRY_NAME_PERK_ATTRIBUTE_READERS)
                .setType(PerkAttributeReader.class)
                .disableSaving()
                .disableOverrides()
                .create();

        REGISTRY_CRYSTAL_PROPERTIES = new RegistryBuilder<CrystalProperty>()
                .setName(REGISTRY_NAME_CRYSTAL_PROPERTIES)
                .setType(CrystalProperty.class)
                .disableSaving()
                .disableOverrides()
                .create();

        REGISTRY_CRYSTAL_USAGES = new RegistryBuilder<PropertyUsage>()
                .setName(REGISTRY_NAME_CRYSTAL_USAGES)
                .setType(PropertyUsage.class)
                .disableSaving()
                .disableOverrides()
                .create();

        REGISTRY_ALTAR_EFFECTS = new RegistryBuilder<AltarRecipeEffect>()
                .setName(REGISTRY_NAME_ALTAR_EFFECTS)
                .setType(AltarRecipeEffect.class)
                .disableSaving()
                .disableOverrides()
                .create();
    }

}
