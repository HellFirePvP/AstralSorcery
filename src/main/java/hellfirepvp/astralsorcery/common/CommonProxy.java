package hellfirepvp.astralsorcery.common;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.auxiliary.link.LinkHandler;
import hellfirepvp.astralsorcery.common.auxiliary.tick.TickManager;
import hellfirepvp.astralsorcery.common.constellation.CelestialHandler;
import hellfirepvp.astralsorcery.common.data.SyncDataHolder;
import hellfirepvp.astralsorcery.common.data.world.WorldCacheManager;
import hellfirepvp.astralsorcery.common.event.listener.EventHandlerNetwork;
import hellfirepvp.astralsorcery.common.event.listener.EventHandlerServer;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.network.PacketChannel;
import hellfirepvp.astralsorcery.common.registry.RegistryBlocks;
import hellfirepvp.astralsorcery.common.registry.RegistryConstellations;
import hellfirepvp.astralsorcery.common.registry.RegistryEntities;
import hellfirepvp.astralsorcery.common.registry.RegistryItems;
import hellfirepvp.astralsorcery.common.registry.RegistryStructures;
import hellfirepvp.astralsorcery.common.starlight.transmission.SourceClassRegistry;
import hellfirepvp.astralsorcery.common.world.AstralWorldGenerator;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CommonProxy
 * Created by HellFirePvP
 * Date: 07.05.2016 / 00:23
 */
public class CommonProxy implements IGuiHandler {

    public void preInit() {
        RegistryItems.initTabs();

        RegistryConstellations.init();

        PacketChannel.init();

        RegistryBlocks.init();
        RegistryItems.init();
        RegistryEntities.init();
        RegistryStructures.init();
        SourceClassRegistry.setupRegistry();

        RegistryBlocks.initRenderRegistry();

        GameRegistry.registerWorldGenerator(new AstralWorldGenerator().init(), 0);

        registerOreDictEntries();
    }

    private void registerOreDictEntries() {
        OreDictionary.registerOre("blockMarble", BlocksAS.blockMarble);
    }

    public void init() {
        NetworkRegistry.INSTANCE.registerGuiHandler(AstralSorcery.instance, this);

        MinecraftForge.EVENT_BUS.register(new EventHandlerNetwork());
        MinecraftForge.EVENT_BUS.register(new EventHandlerServer());
        MinecraftForge.EVENT_BUS.register(TickManager.getInstance());

        registerTickHandlers();

        SyncDataHolder.initialize();
    }

    private void registerTickHandlers() {
        TickManager manager = TickManager.getInstance();

        manager.register(new CelestialHandler.CelestialTickHandler());
        manager.register(new WorldCacheManager()); //Only used as instance for tick handling
        manager.register(new LinkHandler()); //Only used as instance for tick handling

    }

    public void postInit() {}

    public void registerVariantName(Item item, String name) {}

    public void registerBlockRender(Block block, int metadata, String name) {}

    public void registerItemRender(Item item, int metadata, String name) {}

    public <T extends Item> void registerItemRender(T item, int metadata, String name, boolean variant) {}

    public void registerFromSubItems(Item item, String name) {}

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        return null;
    }
}
