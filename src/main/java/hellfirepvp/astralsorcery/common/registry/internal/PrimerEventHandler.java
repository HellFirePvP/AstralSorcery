/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.registry.internal;

import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.registry.RegistryBlocks;
import hellfirepvp.astralsorcery.common.registry.RegistryConstellations;
import hellfirepvp.astralsorcery.common.registry.RegistryItems;
import hellfirepvp.astralsorcery.common.registry.RegistryTileEntities;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.potion.Potion;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
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
    }

    private void registerItems(RegistryEvent.Register<Item> event) {
        registry.wipe(event.getClass());
        RegistryItems.registerItems();
        RegistryItems.registerItemBlocks();
        fillRegistry(event.getRegistry().getRegistrySuperType(), event.getRegistry());
        RegistryConstellations.initConstellationSignatures();
    }

    private void registerBlocks(RegistryEvent.Register<Block> event) {
        registry.wipe(event.getClass());
        RegistryBlocks.registerBlocks();
        //RegistryBlocks.initRenderRegistry();
        fillRegistry(event.getRegistry().getRegistrySuperType(), event.getRegistry());
    }

    private void registerTiles(RegistryEvent.Register<TileEntityType<?>> event) {
        registry.wipe(event.getClass());
        RegistryTileEntities.registerTiles();
        fillRegistry(event.getRegistry().getRegistrySuperType(), event.getRegistry());
    }

    private void registerBiomes(RegistryEvent.Register<Biome> event) {
        registry.wipe(event.getClass());
        //? maybe. one day.
        fillRegistry(event.getRegistry().getRegistrySuperType(), event.getRegistry());
    }

    private void registerPotions(RegistryEvent.Register<Potion> event) {
        registry.wipe(event.getClass());
        //RegistryPotions.init();
        fillRegistry(event.getRegistry().getRegistrySuperType(), event.getRegistry());
    }

    private void registerEnchantments(RegistryEvent.Register<Enchantment> event) {
        registry.wipe(event.getClass());
        //RegistryEnchantments.init();
        fillRegistry(event.getRegistry().getRegistrySuperType(), event.getRegistry());
    }

    private void registerConstellations(RegistryEvent.Register<IConstellation> event) {
        registry.wipe(event.getClass());
        RegistryConstellations.init();
        fillRegistry(event.getRegistry().getRegistrySuperType(), event.getRegistry());
    }

    //private void registerRecipes(RegistryEvent.Register<IRecipe> event) {
    //    registry.wipe(event.getClass());
    //    RegistryRecipes.initVanillaRecipes();
    //    RegistryRecipes.initAstralRecipes();
    //    WellLiquefaction.init();
    //    LiquidInteraction.init();
    //    LightOreTransmutations.init();
    //    fillRegistry(event.getRegistry().getRegistrySuperType(), event.getRegistry());
    //}

    private void registerSounds(RegistryEvent.Register<SoundEvent> event) {
        registry.wipe(event.getClass());
        //RegistrySounds.init();
        fillRegistry(event.getRegistry().getRegistrySuperType(), event.getRegistry());
    }

    private <T extends IForgeRegistryEntry<T>> void fillRegistry(Class<T> registrySuperType, IForgeRegistry<T> forgeRegistry) {
        List<?> entries = registry.getEntries(registrySuperType);
        if(entries != null) {
            entries.forEach((e) -> forgeRegistry.register((T) e));
        }
    }

}
