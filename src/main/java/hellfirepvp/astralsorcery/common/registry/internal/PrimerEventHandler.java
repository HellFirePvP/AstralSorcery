package hellfirepvp.astralsorcery.common.registry.internal;

import hellfirepvp.astralsorcery.common.registry.*;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.potion.Potion;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @SubscribeEvent
    public void registerItems(RegistryEvent.Register<Item> event) {
        registry.wipe(event.getClass());
        RegistryItems.init();
        fillRegistry(event.getRegistry().getRegistrySuperType(), event.getRegistry());
    }

    @SubscribeEvent
    public void registerBlocks(RegistryEvent.Register<Block> event) {
        registry.wipe(event.getClass());
        RegistryBlocks.init();
        fillRegistry(event.getRegistry().getRegistrySuperType(), event.getRegistry());
    }

    @SubscribeEvent
    public void registerBiomes(RegistryEvent.Register<Biome> event) {
        registry.wipe(event.getClass());
        //? maybe. one day.
        fillRegistry(event.getRegistry().getRegistrySuperType(), event.getRegistry());
    }

    @SubscribeEvent
    public void registerPotions(RegistryEvent.Register<Potion> event) {
        registry.wipe(event.getClass());
        RegistryPotions.init();
        fillRegistry(event.getRegistry().getRegistrySuperType(), event.getRegistry());
    }

    @SubscribeEvent
    public void registerEnchantments(RegistryEvent.Register<Enchantment> event) {
        registry.wipe(event.getClass());
        RegistryEnchantments.init();
        fillRegistry(event.getRegistry().getRegistrySuperType(), event.getRegistry());
    }

    @SubscribeEvent
    public void registerRecipes(RegistryEvent.Register<IRecipe> event) {
        registry.wipe(event.getClass());
        RegistryRecipes.init();
        fillRegistry(event.getRegistry().getRegistrySuperType(), event.getRegistry());
    }

    @SubscribeEvent
    public void registerSounds(RegistryEvent.Register<SoundEvent> event) {
        registry.wipe(event.getClass());
        RegistrySounds.init();
        fillRegistry(event.getRegistry().getRegistrySuperType(), event.getRegistry());
    }

    private <T extends IForgeRegistryEntry<T>> void fillRegistry(Class<T> registrySuperType, IForgeRegistry<T> forgeRegistry) {
        List<?> entries = registry.getEntries(registrySuperType);
        if(entries != null) {
            entries.forEach((e) -> forgeRegistry.register((T) e));
        }
    }

}
