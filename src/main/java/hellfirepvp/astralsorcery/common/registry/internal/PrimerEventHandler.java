/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.registry.internal;

import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.constellation.effect.ConstellationEffectProvider;
import hellfirepvp.astralsorcery.common.constellation.perk.AbstractPerk;
import hellfirepvp.astralsorcery.common.data.fragment.KnowledgeFragment;
import hellfirepvp.astralsorcery.common.registry.*;
import hellfirepvp.astralsorcery.common.starlight.transmission.registry.SourceClassRegistry;
import hellfirepvp.astralsorcery.common.starlight.transmission.registry.TransmissionClassRegistry;
import hellfirepvp.astralsorcery.common.structure.types.StructureType;
import hellfirepvp.astralsorcery.common.util.sextant.TargetObject;
import hellfirepvp.observerlib.api.structure.MatchableStructure;
import hellfirepvp.observerlib.api.structure.ObserverProvider;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.potion.Potion;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.biome.Biome;
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
        eventBus.addGenericListener(TileEntityType.class, this::registerTiles);
        eventBus.addGenericListener(Biome.class, this::registerBiomes);
        eventBus.addGenericListener(Potion.class, this::registerPotions);
        eventBus.addGenericListener(Enchantment.class, this::registerEnchantments);
        eventBus.addGenericListener(SoundEvent.class, this::registerSounds);
        eventBus.addGenericListener(IConstellation.class, this::registerConstellations);
        eventBus.addGenericListener(AbstractPerk.class, this::registerPerks);
        eventBus.addGenericListener(TargetObject.class, this::registerSextantTargets);
        eventBus.addGenericListener(DataSerializerEntry.class, this::registerDataSerializers);
        eventBus.addGenericListener(IRecipeSerializer.class, this::registerRecipeSerializers);
        eventBus.addGenericListener(MatchableStructure.class, this::registerStructures);
        eventBus.addGenericListener(StructureType.class, this::registerStructureTypes);
        eventBus.addGenericListener(ObserverProvider.class, this::registerStructureProviders);
        eventBus.addGenericListener(KnowledgeFragment.class, this::registerKnowledgeFragments);
        eventBus.addGenericListener(ConstellationEffectProvider.class, this::registerConstellationEffects);
    }

    //This exists because you can't sort registries in any fashion or make one load after another in forge.
    //So. thanks. this is the result i guess.
    private void registerRemainingData() {
        RegistryConstellations.init();
        RegistryConstellations.initConstellationSignatures();

        RegistryStructures.registerStructures();
        RegistryKnowledgeFragments.init();

        TransmissionClassRegistry.setupRegistry();
        SourceClassRegistry.setupRegistry();
    }

    private void registerItems(RegistryEvent.Register<Item> event) {
        RegistryItems.registerItems();
        RegistryItems.registerItemBlocks();
        fillRegistry(event.getRegistry().getRegistrySuperType(), event.getRegistry());

        //Item registration happens after block registration. Register misc stuff here.
        registerRemainingData();
    }

    private void registerBlocks(RegistryEvent.Register<Block> event) {
        RegistryBlocks.registerBlocks();
        //RegistryBlocks.initRenderRegistry();
        fillRegistry(event.getRegistry().getRegistrySuperType(), event.getRegistry());
    }

    private void registerTiles(RegistryEvent.Register<TileEntityType<?>> event) {
        RegistryTileEntities.registerTiles();
        fillRegistry(event.getRegistry().getRegistrySuperType(), event.getRegistry());
    }

    private void registerBiomes(RegistryEvent.Register<Biome> event) {
        //? maybe. one day.
        fillRegistry(event.getRegistry().getRegistrySuperType(), event.getRegistry());
    }

    private void registerPotions(RegistryEvent.Register<Potion> event) {
        //RegistryPotions.init();
        fillRegistry(event.getRegistry().getRegistrySuperType(), event.getRegistry());
    }

    private void registerEnchantments(RegistryEvent.Register<Enchantment> event) {
        //RegistryEnchantments.init();
        fillRegistry(event.getRegistry().getRegistrySuperType(), event.getRegistry());
    }

    private void registerConstellations(RegistryEvent.Register<IConstellation> event) {
        fillRegistry(event.getRegistry().getRegistrySuperType(), event.getRegistry());
    }

    private void registerPerks(RegistryEvent.Register<AbstractPerk> event) {
        //RegistryPerks.init();
        fillRegistry(event.getRegistry().getRegistrySuperType(), event.getRegistry());
    }

    private void registerConstellationEffects(RegistryEvent.Register<ConstellationEffectProvider> event) {
        RegistryConstellationEffects.init();
        fillRegistry(event.getRegistry().getRegistrySuperType(), event.getRegistry());
    }

    /**
     * has to run after {@link net.minecraft.world.gen.feature.Feature}
     * currently guaranteed by: F before T
     */
    private void registerSextantTargets(RegistryEvent.Register<TargetObject> event) {
        RegistrySextantTargets.init();
        fillRegistry(event.getRegistry().getRegistrySuperType(), event.getRegistry());
    }

    private void registerDataSerializers(RegistryEvent.Register<DataSerializerEntry> event) {
        RegistryDataSerializers.registerSerializers();
        fillRegistry(event.getRegistry().getRegistrySuperType(), event.getRegistry());
    }

    private void registerRecipeSerializers(RegistryEvent.Register<IRecipeSerializer<?>> event) {
        RegistryRecipeSerializers.init();
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

    private void registerKnowledgeFragments(RegistryEvent.Register<KnowledgeFragment> event) {
        fillRegistry(event.getRegistry().getRegistrySuperType(), event.getRegistry());
    }

    private void registerSounds(RegistryEvent.Register<SoundEvent> event) {
        //RegistrySounds.init();
        fillRegistry(event.getRegistry().getRegistrySuperType(), event.getRegistry());
    }

    private <T extends IForgeRegistryEntry<T>> void fillRegistry(Class<T> registrySuperType, IForgeRegistry<T> forgeRegistry) {
        registry.getEntries(registrySuperType).forEach(e -> forgeRegistry.register((T) e));
    }

}
