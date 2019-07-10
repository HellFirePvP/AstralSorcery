/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.registry.internal;

import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.constellation.perk.AbstractPerk;
import hellfirepvp.astralsorcery.common.registry.*;
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

import java.util.List;

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
    }

    private void registerItems(RegistryEvent.Register<Item> event) {
        registry.wipe(event);
        RegistryItems.registerItems();
        RegistryItems.registerItemBlocks();
        fillRegistry(event.getRegistry().getRegistrySuperType(), event.getRegistry());
        RegistryConstellations.initConstellationSignatures();
    }

    private void registerBlocks(RegistryEvent.Register<Block> event) {
        registry.wipe(event);
        RegistryBlocks.registerBlocks();
        //RegistryBlocks.initRenderRegistry();
        fillRegistry(event.getRegistry().getRegistrySuperType(), event.getRegistry());
    }

    private void registerTiles(RegistryEvent.Register<TileEntityType<?>> event) {
        registry.wipe(event);
        RegistryTileEntities.registerTiles();
        fillRegistry(event.getRegistry().getRegistrySuperType(), event.getRegistry());
    }

    private void registerBiomes(RegistryEvent.Register<Biome> event) {
        registry.wipe(event);
        //? maybe. one day.
        fillRegistry(event.getRegistry().getRegistrySuperType(), event.getRegistry());
    }

    private void registerPotions(RegistryEvent.Register<Potion> event) {
        registry.wipe(event);
        //RegistryPotions.init();
        fillRegistry(event.getRegistry().getRegistrySuperType(), event.getRegistry());
    }

    private void registerEnchantments(RegistryEvent.Register<Enchantment> event) {
        registry.wipe(event);
        //RegistryEnchantments.init();
        fillRegistry(event.getRegistry().getRegistrySuperType(), event.getRegistry());
    }

    private void registerConstellations(RegistryEvent.Register<IConstellation> event) {
        registry.wipe(event);
        RegistryConstellations.init();
        fillRegistry(event.getRegistry().getRegistrySuperType(), event.getRegistry());
    }

    private void registerPerks(RegistryEvent.Register<AbstractPerk> event) {
        registry.wipe(event);
        //RegistryPerks.init();
        fillRegistry(event.getRegistry().getRegistrySuperType(), event.getRegistry());
    }

    private void registerSextantTargets(RegistryEvent.Register<TargetObject> event) {
        registry.wipe(event);
        RegistrySextantTargets.init();
        fillRegistry(event.getRegistry().getRegistrySuperType(), event.getRegistry());
    }

    private void registerDataSerializers(RegistryEvent.Register<DataSerializerEntry> event) {
        registry.wipe(event);
        RegistryDataSerializers.registerSerializers();
        fillRegistry(event.getRegistry().getRegistrySuperType(), event.getRegistry());
    }

    private void registerRecipeSerializers(RegistryEvent.Register<IRecipeSerializer<?>> event) {
        registry.wipe(event);
        RegistryRecipeSerializers.init();
        fillRegistry(event.getRegistry().getRegistrySuperType(), event.getRegistry());
    }

    private void registerStructures(RegistryEvent.Register<MatchableStructure> event) {
        registry.wipe(event);
        RegistryStructures.registerStructures();
        fillRegistry(event.getRegistry().getRegistrySuperType(), event.getRegistry());
    }

    private void registerStructureProviders(RegistryEvent.Register<ObserverProvider> event) {
        registry.wipe(event);
        fillRegistry(event.getRegistry().getRegistrySuperType(), event.getRegistry());
    }

    private void registerStructureTypes(RegistryEvent.Register<StructureType> event) {
        registry.wipe(event);
        RegistryStructureTypes.init();
        fillRegistry(event.getRegistry().getRegistrySuperType(), event.getRegistry());
    }

    //private void registerRecipes(RegistryEvent.Register<IRecipe> event) {
    //    registry.wipe(event);
    //    RegistryRecipes.initVanillaRecipes();
    //    RegistryRecipes.initAstralRecipes();
    //    WellLiquefaction.init();
    //    LiquidInteraction.init();
    //    LightOreTransmutations.init();
    //    fillRegistry(event.getRegistry().getRegistrySuperType(), event.getRegistry());
    //}

    private void registerSounds(RegistryEvent.Register<SoundEvent> event) {
        registry.wipe(event);
        //RegistrySounds.init();
        fillRegistry(event.getRegistry().getRegistrySuperType(), event.getRegistry());
    }

    private <T extends IForgeRegistryEntry<T>> void fillRegistry(Class<T> registrySuperType, IForgeRegistry<T> forgeRegistry) {
        registry.getEntries(registrySuperType).forEach(e -> forgeRegistry.register((T) e));
    }

}
