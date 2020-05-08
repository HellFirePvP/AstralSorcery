/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.registry.internal;

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
import hellfirepvp.astralsorcery.common.registry.*;
import hellfirepvp.astralsorcery.common.starlight.transmission.registry.SourceClassRegistry;
import hellfirepvp.astralsorcery.common.starlight.transmission.registry.TransmissionClassRegistry;
import hellfirepvp.astralsorcery.common.structure.types.StructureType;
import hellfirepvp.observerlib.api.ObserverProvider;
import hellfirepvp.observerlib.api.structure.MatchableStructure;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluid;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.potion.Effect;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.Feature;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DataSerializerEntry;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PrimerEventHandler
 * Created by HellFirePvP
 * Date: 26.06.2017 / 14:50
 */
public class PrimerEventHandler {

    private InternalRegistryPrimer registry;

    public PrimerEventHandler(InternalRegistryPrimer registry) {
        this.registry = registry;
    }

    public void attachEventHandlers(IEventBus eventBus) {
        eventBus.addGenericListener(Item.class, this::registerItems);
        eventBus.addGenericListener(Block.class, this::registerBlocks);
        eventBus.addGenericListener(Fluid.class, this::registerFluids);
        eventBus.addGenericListener(TileEntityType.class, this::registerTiles);
        eventBus.addGenericListener(EntityType.class, this::registerEntities);
        eventBus.addGenericListener(Biome.class, this::registerBiomes);
        eventBus.addGenericListener(Feature.class, this::registerFeatures);
        eventBus.addGenericListener(Effect.class, this::registerEffects);
        eventBus.addGenericListener(Enchantment.class, this::registerEnchantments);
        eventBus.addGenericListener(SoundEvent.class, this::registerSounds);
        eventBus.addGenericListener(GlobalLootModifierSerializer.class, this::registerGlobalLootModifierSerializers);
        eventBus.addGenericListener(IConstellation.class, this::registerConstellations);
        eventBus.addGenericListener(AbstractPerk.class, this::registerPerks);
        eventBus.addGenericListener(DataSerializerEntry.class, this::registerDataSerializers);
        eventBus.addGenericListener(IRecipeSerializer.class, this::registerRecipeSerializers);
        eventBus.addGenericListener(MatchableStructure.class, this::registerStructures);
        eventBus.addGenericListener(StructureType.class, this::registerStructureTypes);
        eventBus.addGenericListener(ObserverProvider.class, this::registerStructureProviders);
        eventBus.addGenericListener(ConstellationEffectProvider.class, this::registerConstellationEffects);
        eventBus.addGenericListener(MantleEffect.class, this::registerMantleEffects);
        eventBus.addGenericListener(EngravingEffect.class, this::registerEngravingEffects);
        eventBus.addGenericListener(PerkAttributeType.class, this::registerPerkAttributeTypes);
        eventBus.addGenericListener(PerkAttributeReader.class, this::registerPerkAttributeReaders);
        eventBus.addGenericListener(ContainerType.class, this::registerContainerTypes);
        eventBus.addGenericListener(CrystalProperty.class, this::registerCrystalProperties);
        eventBus.addGenericListener(PropertyUsage.class, this::registerCrystalUsages);
        eventBus.addGenericListener(AltarRecipeEffect.class, this::registerAltarRecipeEffects);
    }

    //This exists because you can't sort registries in any fashion or make one load after another in forge.
    //So. thanks. this is the result i guess.
    private void registerRemainingData() {
        RegistryConstellations.init();
        RegistryConstellationEffects.init();
        RegistryMantleEffects.init();
        RegistryEngravingEffects.init();

        RegistryStructures.init();

        RegistryCrystalPropertyUsages.init();
        RegistryCrystalProperties.init();
        RegistryCrystalProperties.initDefaultAttributes();

        RegistryRecipeTypes.init();
        RegistryRecipeSerializers.init();
        RegistryResearch.init();

        TransmissionClassRegistry.setupRegistry();
        SourceClassRegistry.setupRegistry();
    }

    private void registerItems(RegistryEvent.Register<Item> event) {
        RegistryItems.registerItems();
        RegistryItems.registerItemBlocks();
        RegistryItems.registerFluidContainerItems();
        fillRegistry(event.getRegistry().getRegistrySuperType(), event.getRegistry());

        //Item registration happens after block registration. Register misc stuff here.
        registerRemainingData();
    }

    private void registerBlocks(RegistryEvent.Register<Block> event) {
        RegistryFluids.registerFluids();
        RegistryBlocks.registerBlocks();
        RegistryBlocks.registerFluidBlocks();
        fillRegistry(event.getRegistry().getRegistrySuperType(), event.getRegistry());
    }

    private void registerFluids(RegistryEvent.Register<Fluid> event) {
        fillRegistry(event.getRegistry().getRegistrySuperType(), event.getRegistry());
    }

    private void registerTiles(RegistryEvent.Register<TileEntityType<?>> event) {
        RegistryTileEntities.registerTiles();
        fillRegistry(event.getRegistry().getRegistrySuperType(), event.getRegistry());
    }

    private void registerEntities(RegistryEvent.Register<EntityType<?>> event) {
        RegistryEntities.init();
        fillRegistry(event.getRegistry().getRegistrySuperType(), event.getRegistry());
    }

    private void registerBiomes(RegistryEvent.Register<Biome> event) {
        //? maybe. one day.
        fillRegistry(event.getRegistry().getRegistrySuperType(), event.getRegistry());
    }

    private void registerFeatures(RegistryEvent.Register<Feature<?>> event) {
        RegistryWorldGeneration.registerFeatures();
        fillRegistry(event.getRegistry().getRegistrySuperType(), event.getRegistry());
    }

    private void registerEffects(RegistryEvent.Register<Effect> event) {
        RegistryEffects.init();
        fillRegistry(event.getRegistry().getRegistrySuperType(), event.getRegistry());
    }

    private void registerEnchantments(RegistryEvent.Register<Enchantment> event) {
        RegistryEnchantments.init();
        fillRegistry(event.getRegistry().getRegistrySuperType(), event.getRegistry());
    }

    private void registerGlobalLootModifierSerializers(RegistryEvent.Register<GlobalLootModifierSerializer<?>> event) {
        RegistryLoot.init();
        fillRegistry(event.getRegistry().getRegistrySuperType(), event.getRegistry());
    }

    private void registerConstellations(RegistryEvent.Register<IConstellation> event) {
        fillRegistry(event.getRegistry().getRegistrySuperType(), event.getRegistry());
    }

    private void registerPerks(RegistryEvent.Register<AbstractPerk> event) {
        RegistryPerks.init();
        fillRegistry(event.getRegistry().getRegistrySuperType(), event.getRegistry());
    }

    private void registerConstellationEffects(RegistryEvent.Register<ConstellationEffectProvider> event) {
        fillRegistry(event.getRegistry().getRegistrySuperType(), event.getRegistry());
    }

    private void registerMantleEffects(RegistryEvent.Register<MantleEffect> event) {
        fillRegistry(event.getRegistry().getRegistrySuperType(), event.getRegistry());
    }

    private void registerEngravingEffects(RegistryEvent.Register<EngravingEffect> event) {
        fillRegistry(event.getRegistry().getRegistrySuperType(), event.getRegistry());
    }

    private void registerPerkAttributeTypes(RegistryEvent.Register<PerkAttributeType> event) {
        fillRegistry(event.getRegistry().getRegistrySuperType(), event.getRegistry());
    }

    private void registerPerkAttributeReaders(RegistryEvent.Register<PerkAttributeReader> event) {
        fillRegistry(event.getRegistry().getRegistrySuperType(), event.getRegistry());
    }

    private void registerContainerTypes(RegistryEvent.Register<ContainerType<?>> event) {
        RegistryContainerTypes.init();
        fillRegistry(event.getRegistry().getRegistrySuperType(), event.getRegistry());
    }

    private void registerCrystalProperties(RegistryEvent.Register<CrystalProperty> event) {
        fillRegistry(event.getRegistry().getRegistrySuperType(), event.getRegistry());
    }

    private void registerCrystalUsages(RegistryEvent.Register<PropertyUsage> event) {
        fillRegistry(event.getRegistry().getRegistrySuperType(), event.getRegistry());
    }

    private void registerAltarRecipeEffects(RegistryEvent.Register<AltarRecipeEffect> event) {
        RegistryRecipeTypes.initAltarEffects();
        fillRegistry(event.getRegistry().getRegistrySuperType(), event.getRegistry());
    }

    private void registerDataSerializers(RegistryEvent.Register<DataSerializerEntry> event) {
        RegistryDataSerializers.registerSerializers();
        fillRegistry(event.getRegistry().getRegistrySuperType(), event.getRegistry());
    }

    private void registerRecipeSerializers(RegistryEvent.Register<IRecipeSerializer<?>> event) {
        fillRegistry(event.getRegistry().getRegistrySuperType(), event.getRegistry());
    }

    private void registerStructures(RegistryEvent.Register<MatchableStructure> event) {
        fillRegistry(event.getRegistry().getRegistrySuperType(), event.getRegistry());
    }

    private void registerStructureProviders(RegistryEvent.Register<ObserverProvider> event) {
        fillRegistry(event.getRegistry().getRegistrySuperType(), event.getRegistry());
    }

    private void registerStructureTypes(RegistryEvent.Register<StructureType> event) {
        fillRegistry(event.getRegistry().getRegistrySuperType(), event.getRegistry());
    }

    private void registerSounds(RegistryEvent.Register<SoundEvent> event) {
        RegistrySounds.init();
        fillRegistry(event.getRegistry().getRegistrySuperType(), event.getRegistry());
    }

    private <T extends IForgeRegistryEntry<T>> void fillRegistry(Class<T> registrySuperType, IForgeRegistry<T> forgeRegistry) {
        registry.getEntries(registrySuperType).forEach(e -> forgeRegistry.register((T) e));
    }

}
