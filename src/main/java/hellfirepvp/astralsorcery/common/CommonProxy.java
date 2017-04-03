/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.auxiliary.link.LinkHandler;
import hellfirepvp.astralsorcery.common.auxiliary.tick.TickManager;
import hellfirepvp.astralsorcery.common.base.*;
import hellfirepvp.astralsorcery.common.constellation.distribution.ConstellationSkyHandler;
import hellfirepvp.astralsorcery.common.constellation.effect.ConstellationEffectRegistry;
import hellfirepvp.astralsorcery.common.constellation.perk.ConstellationPerkLevelManager;
import hellfirepvp.astralsorcery.common.constellation.perk.ConstellationPerks;
import hellfirepvp.astralsorcery.common.constellation.perk.PlayerPerkHandler;
import hellfirepvp.astralsorcery.common.container.*;
import hellfirepvp.astralsorcery.common.crafting.helper.CraftingAccessManager;
import hellfirepvp.astralsorcery.common.data.SyncDataHolder;
import hellfirepvp.astralsorcery.common.data.config.Config;
import hellfirepvp.astralsorcery.common.data.world.WorldCacheManager;
import hellfirepvp.astralsorcery.common.event.listener.EventHandlerAchievements;
import hellfirepvp.astralsorcery.common.event.listener.EventHandlerMisc;
import hellfirepvp.astralsorcery.common.event.listener.EventHandlerNetwork;
import hellfirepvp.astralsorcery.common.event.listener.EventHandlerServer;
import hellfirepvp.astralsorcery.common.integrations.ModIntegrationCrafttweaker;
import hellfirepvp.astralsorcery.common.item.ItemJournal;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.network.PacketChannel;
import hellfirepvp.astralsorcery.common.network.packet.server.PktLightningEffect;
import hellfirepvp.astralsorcery.common.registry.*;
import hellfirepvp.astralsorcery.common.starlight.network.StarlightNetworkRegistry;
import hellfirepvp.astralsorcery.common.starlight.network.StarlightTransmissionHandler;
import hellfirepvp.astralsorcery.common.starlight.network.StarlightUpdateHandler;
import hellfirepvp.astralsorcery.common.starlight.network.TransmissionChunkTracker;
import hellfirepvp.astralsorcery.common.starlight.transmission.registry.SourceClassRegistry;
import hellfirepvp.astralsorcery.common.starlight.transmission.registry.TransmissionClassRegistry;
import hellfirepvp.astralsorcery.common.tile.TileAltar;
import hellfirepvp.astralsorcery.common.tile.TileMapDrawingTable;
import hellfirepvp.astralsorcery.common.tile.TileTelescope;
import hellfirepvp.astralsorcery.common.tile.TileTreeBeacon;
import hellfirepvp.astralsorcery.common.util.*;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.astralsorcery.common.world.AstralWorldGenerator;
import hellfirepvp.astralsorcery.common.world.retrogen.RetroGenController;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;

import java.awt.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CommonProxy
 * Created by HellFirePvP
 * Date: 07.05.2016 / 00:23
 */
public class CommonProxy implements IGuiHandler {

    public static DamageSource dmgSourceBleed   = new DamageSource("as.bleed").setDamageBypassesArmor();
    public static DamageSource dmgSourceStellar = new DamageSource("as.stellar").setDamageBypassesArmor().setMagicDamage();

    public static AstralWorldGenerator worldGenerator = new AstralWorldGenerator();
    private CommonScheduler commonScheduler = new CommonScheduler();

    public void preLoadConfigEntries() {
        worldGenerator.pushConfigEntries();
        ConstellationEffectRegistry.addDynamicConfigEntries();
        ConstellationPerks.addDynamicConfigEntries();
        Config.addDynamicEntry(TileTreeBeacon.ConfigEntryTreeBeacon.instance);
        Config.addDynamicEntry(ConstellationPerkLevelManager.getLevelConfigurations());
    }

    public void preInit() {
        RegistryItems.setupDefaults();

        RegistryEnchantments.init();
        RegistryConstellations.init();

        PacketChannel.init();

        RegistryBlocks.init();
        RegistryItems.init();
        RegistryEntities.init();
        RegistryStructures.init();
        RegistryPotions.init();

        //Transmission registry
        SourceClassRegistry.setupRegistry();
        TransmissionClassRegistry.setupRegistry();

        StarlightNetworkRegistry.setupRegistry();

        RegistryBlocks.initRenderRegistry();
        RegistryRecipes.init();
        RegistryResearch.init();

        LootTableUtil.initLootTable();
        ConstellationEffectRegistry.init();

        registerOreDictEntries();
        RegistryAchievements.init();
        RegistryPerks.init();

        registerCapabilities();
    }

    private void registerCapabilities() {
        //CapabilityManager.INSTANCE.register(IPlayerCapabilityPerks.class, new IPlayerCapabilityPerks.Storage(), IPlayerCapabilityPerks.Impl.class);
    }

    private void registerOreDictEntries() {
        //*sigh*
        OreDictionary.registerOre(OreDictAlias.BLOCK_MARBLE, new ItemStack(BlocksAS.blockMarble, 1, 0));
        OreDictionary.registerOre("blockMarble", new ItemStack(BlocksAS.blockMarble, 1, 0));
    }

    public void init() {
        if (Mods.CRAFTTWEAKER.isPresent()) {
            AstralSorcery.log.info("Crafttweaker found! Adding recipe handlers...");
            ModIntegrationCrafttweaker.instance.load();
        } else {
            AstralSorcery.log.info("Crafttweaker not found!");
        }

        NetworkRegistry.INSTANCE.registerGuiHandler(AstralSorcery.instance, this);

        MinecraftForge.TERRAIN_GEN_BUS.register(TreeCaptureHelper.eventInstance);

        MinecraftForge.EVENT_BUS.register(new EventHandlerNetwork());
        MinecraftForge.EVENT_BUS.register(new EventHandlerServer());
        MinecraftForge.EVENT_BUS.register(new EventHandlerAchievements());
        MinecraftForge.EVENT_BUS.register(new EventHandlerMisc());
        MinecraftForge.EVENT_BUS.register(TransmissionChunkTracker.getInstance());
        MinecraftForge.EVENT_BUS.register(TickManager.getInstance());
        MinecraftForge.EVENT_BUS.register(StarlightTransmissionHandler.getInstance());
        MinecraftForge.EVENT_BUS.register(new LootTableUtil());
        MinecraftForge.EVENT_BUS.register(BlockDropCaptureAssist.instance);

        GameRegistry.registerWorldGenerator(worldGenerator.setupAttributes(), 50);
        if(Config.enableRetroGen) {
            MinecraftForge.EVENT_BUS.register(new RetroGenController());
        }
        RegistrySounds.init();

        TickManager manager = TickManager.getInstance();
        registerTickHandlers(manager);

        SyncDataHolder.initialize();
        TileAccelerationBlacklist.init();
        OreTypes.init();
        LightOreTransmutations.init();
        HerdableAnimal.init();
        WellLiquefaction.init();
        TreeTypes.init();
    }

    protected void registerTickHandlers(TickManager manager) {
        manager.register(ConstellationSkyHandler.getInstance());
        manager.register(StarlightTransmissionHandler.getInstance());
        manager.register(StarlightUpdateHandler.getInstance());
        manager.register(WorldCacheManager.getInstance());
        manager.register(new LinkHandler()); //Only used as instance for tick handling
        manager.register(SyncDataHolder.getTickInstance());
        manager.register(new PlayerPerkHandler());
        manager.register(commonScheduler);

        //TickTokenizedMaps
        manager.register(EventHandlerServer.spawnDenyRegions);
        manager.register(EventHandlerServer.perkCooldowns);
        manager.register(EventHandlerServer.invulnerabilityCooldown);
    }

    public void postInit() {
        AstralSorcery.log.info("[AstralSorcery] Post compile recipes");

        CraftingAccessManager.compile();
    }

    public void registerVariantName(Item item, String name) {}

    public void registerBlockRender(Block block, int metadata, String name) {}

    public void registerItemRender(Item item, int metadata, String name) {}

    public <T extends Item> void registerItemRender(T item, int metadata, String name, boolean variant) {}

    public void registerFromSubItems(Item item, String name) {}

    public void scheduleClientside(Runnable r, int tickDelay) {}

    public void scheduleClientside(Runnable r) {
        scheduleClientside(r, 0);
    }

    public void scheduleDelayed(Runnable r, int tickDelay) {
        commonScheduler.addRunnable(r, tickDelay);
    }

    public void scheduleDelayed(Runnable r) {
        scheduleClientside(r, 0);
    }

    public void fireLightning(World world, Vector3 from, Vector3 to) {
        fireLightning(world, from, to, null);
    }

    public void fireLightning(World world, Vector3 from, Vector3 to, Color overlay) {
        PktLightningEffect effect = new PktLightningEffect(from, to);
        if(overlay != null) {
            effect.setColorOverlay(overlay);
        }
        PacketChannel.CHANNEL.sendToAllAround(effect, PacketChannel.pointFromPos(world, from.toBlockPos(), 40));
    }

    @Override
    public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        if(id < 0 || id >= EnumGuiId.values().length) return null; //Out of range.
        EnumGuiId guiType = EnumGuiId.values()[id];

        TileEntity t = null;
        if(guiType.getTileClass() != null) {
            t = MiscUtils.getTileAt(world, new BlockPos(x, y, z), guiType.getTileClass(), true);
            if(t == null) {
                return null;
            }
        }

        switch (guiType) {
            case ALTAR_DISCOVERY:
                return new ContainerAltarDiscovery(player.inventory, (TileAltar) t);
            case ALTAR_ATTUNEMENT:
                return new ContainerAltarAttunement(player.inventory, (TileAltar) t);
            case ALTAR_CONSTELLATION:
                return new ContainerAltarConstellation(player.inventory, (TileAltar) t);
            case ALTAR_TRAIT:
                return new ContainerAltarTrait(player.inventory, (TileAltar) t);
            case JOURNAL_STORAGE: {
                ItemStack held = player.getHeldItem(EnumHand.MAIN_HAND);
                if(!held.isEmpty()) {
                    if(held.getItem() instanceof ItemJournal) {
                        return new ContainerJournal(player.inventory, held, player.inventory.currentItem);
                    }
                }
            }
        }
        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        return null;
    }

    public void openGui(EnumGuiId guiId, EntityPlayer player, World world, int x, int y, int z) {
        player.openGui(AstralSorcery.instance, guiId.ordinal(), world, x, y, z);
    }

    public static enum EnumGuiId {

        TELESCOPE(TileTelescope.class),
        HAND_TELESCOPE,
        CONSTELLATION_PAPER,
        ALTAR_DISCOVERY(TileAltar.class),
        ALTAR_ATTUNEMENT(TileAltar.class),
        ALTAR_CONSTELLATION(TileAltar.class),
        ALTAR_TRAIT(TileAltar.class),
        MAP_DRAWING(TileMapDrawingTable.class),
        JOURNAL,
        JOURNAL_STORAGE;

        private final Class<? extends TileEntity> tileClass;

        private EnumGuiId() {
            this(null);
        }

        private EnumGuiId(Class<? extends TileEntity> tileClass) {
            this.tileClass = tileClass;
        }

        public Class<? extends TileEntity> getTileClass() {
            return tileClass;
        }

    }

}
