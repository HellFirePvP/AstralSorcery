package hellfire.astralSorcery.common;

import hellfire.astralSorcery.common.data.sync.SyncDataHolder;
import hellfire.astralSorcery.common.event.EventHandlerNetwork;
import hellfire.astralSorcery.common.event.EventHandlerServer;
import hellfire.astralSorcery.common.integration.IntegrationHandler;
import hellfire.astralSorcery.common.lib.LibConstants;
import hellfire.astralSorcery.common.lib.LibMisc;
import hellfire.astralSorcery.common.net.PacketChannel;
import hellfire.astralSorcery.common.registry.RegistryBlocks;
import hellfire.astralSorcery.common.registry.RegistryConstellations;
import hellfire.astralSorcery.common.registry.RegistryItems;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.network.IGuiHandler;

/**
 * This class is part of the Astral Sorcery Mod
 * <p/>
 * Created by HellFirePvP @ 24.01.2016 20:34
 */
public class CommonProxy implements IGuiHandler {

    public void preInit() {
        LibMisc.creativeTabAstralSorcery = new CreativeTabs(LibConstants.MODID) {
            @Override
            public Item getTabIconItem() {
                return new ItemStack(Blocks.stone).getItem();
            }
        };
        RegistryConstellations.init();

        PacketChannel.init();

        RegistryBlocks.init();
        RegistryItems.init();
    }

    public void init() {
        registerDisplayInformationInit();

        MinecraftForge.EVENT_BUS.register(new EventHandlerNetwork());
        MinecraftForge.EVENT_BUS.register(new EventHandlerServer());

        SyncDataHolder.initialize();
    }

    public void postInit() {
        IntegrationHandler.initIntegrations();
    }

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        return null;
    }

    public void registerBlockRender(Block block, int metadata, String name) {}

    public void registerVariantName(Item item, String name) {}

    public void registerItemRender(Item block, int metadata, String name) {}

    public <T extends Item> void registerItemRender(T item, int metadata, String name, boolean variant) {}

    public void registerFromSubItems(Item item, String name) {}

    public void registerDisplayInformationInit() {}

}
