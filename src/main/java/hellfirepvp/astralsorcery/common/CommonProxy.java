/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common;

import com.mojang.authlib.GameProfile;
import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.auxiliary.CelestialGatewaySystem;
import hellfirepvp.astralsorcery.common.auxiliary.link.LinkHandler;
import hellfirepvp.astralsorcery.common.auxiliary.tick.TickManager;
import hellfirepvp.astralsorcery.common.base.*;
import hellfirepvp.astralsorcery.common.base.patreon.PatreonDataManager;
import hellfirepvp.astralsorcery.common.base.patreon.flare.PatreonFlareManager;
import hellfirepvp.astralsorcery.common.block.BlockCustomOre;
import hellfirepvp.astralsorcery.common.block.BlockCustomSandOre;
import hellfirepvp.astralsorcery.common.block.BlockMarble;
import hellfirepvp.astralsorcery.common.constellation.cape.CapeEffectRegistry;
import hellfirepvp.astralsorcery.common.constellation.charge.PlayerChargeHandler;
import hellfirepvp.astralsorcery.common.constellation.distribution.ConstellationSkyHandler;
import hellfirepvp.astralsorcery.common.constellation.effect.ConstellationEffectRegistry;
import hellfirepvp.astralsorcery.common.constellation.perk.PerkEffectHelper;
import hellfirepvp.astralsorcery.common.constellation.perk.PerkLevelManager;
import hellfirepvp.astralsorcery.common.constellation.perk.attribute.AttributeTypeLimiter;
import hellfirepvp.astralsorcery.common.container.*;
import hellfirepvp.astralsorcery.common.crafting.ItemHandle;
import hellfirepvp.astralsorcery.common.crafting.helper.CraftingAccessManager;
import hellfirepvp.astralsorcery.common.data.SyncDataHolder;
import hellfirepvp.astralsorcery.common.data.config.Config;
import hellfirepvp.astralsorcery.common.data.research.ResearchIOThread;
import hellfirepvp.astralsorcery.common.data.world.WorldCacheManager;
import hellfirepvp.astralsorcery.common.enchantment.amulet.AmuletEnchantHelper;
import hellfirepvp.astralsorcery.common.enchantment.amulet.AmuletHolderCapability;
import hellfirepvp.astralsorcery.common.enchantment.amulet.PlayerAmuletHandler;
import hellfirepvp.astralsorcery.common.enchantment.amulet.registry.AmuletEnchantmentRegistry;
import hellfirepvp.astralsorcery.common.event.listener.*;
import hellfirepvp.astralsorcery.common.integrations.ModIntegrationBloodMagic;
import hellfirepvp.astralsorcery.common.integrations.ModIntegrationChisel;
import hellfirepvp.astralsorcery.common.integrations.ModIntegrationCrafttweaker;
import hellfirepvp.astralsorcery.common.integrations.ModIntegrationThaumcraft;
import hellfirepvp.astralsorcery.common.item.ItemCraftingComponent;
import hellfirepvp.astralsorcery.common.item.ItemJournal;
import hellfirepvp.astralsorcery.common.item.gem.GemAttributeHelper;
import hellfirepvp.astralsorcery.common.item.tool.sextant.SextantFinder;
import hellfirepvp.astralsorcery.common.migration.MappingMigrationHandler;
import hellfirepvp.astralsorcery.common.network.PacketChannel;
import hellfirepvp.astralsorcery.common.network.packet.server.PktLightningEffect;
import hellfirepvp.astralsorcery.common.registry.*;
import hellfirepvp.astralsorcery.common.registry.internal.InternalRegistryPrimer;
import hellfirepvp.astralsorcery.common.registry.internal.PrimerEventHandler;
import hellfirepvp.astralsorcery.common.starlight.network.StarlightNetworkRegistry;
import hellfirepvp.astralsorcery.common.starlight.network.StarlightTransmissionHandler;
import hellfirepvp.astralsorcery.common.starlight.network.StarlightUpdateHandler;
import hellfirepvp.astralsorcery.common.starlight.network.TransmissionChunkTracker;
import hellfirepvp.astralsorcery.common.starlight.transmission.registry.SourceClassRegistry;
import hellfirepvp.astralsorcery.common.starlight.transmission.registry.TransmissionClassRegistry;
import hellfirepvp.astralsorcery.common.structure.change.StructureIntegrityObserver;
import hellfirepvp.astralsorcery.common.tile.*;
import hellfirepvp.astralsorcery.common.util.*;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.astralsorcery.common.util.effect.time.TimeStopController;
import hellfirepvp.astralsorcery.common.util.log.LogUtil;
import hellfirepvp.astralsorcery.common.world.AstralWorldGenerator;
import hellfirepvp.astralsorcery.common.world.retrogen.ChunkVersionController;
import hellfirepvp.astralsorcery.common.world.retrogen.RetroGenController;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.UUID;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CommonProxy
 * Created by HellFirePvP
 * Date: 07.05.2016 / 00:23
 */
public class CommonProxy implements IGuiHandler {

    private static final UUID AS_FAKEPLAYER_UUID = UUID.fromString("5ae0411c-9069-4d79-8892-bc112c4b3a08");

    public static DamageSource dmgSourceBleed   = DamageSourceUtil.newType("as.bleed").setDamageBypassesArmor();
    public static DamageSource dmgSourceStellar = DamageSourceUtil.newType("as.stellar").setDamageBypassesArmor().setMagicDamage();
    public static DamageSource dmgSourceReflect = DamageSourceUtil.newType("thorns");
    public static InternalRegistryPrimer registryPrimer;

    public static AstralWorldGenerator worldGenerator = new AstralWorldGenerator();
    private CommonScheduler commonScheduler = new CommonScheduler();

    public void setupConfiguration() {
        worldGenerator.pushConfigEntries();
        ConstellationEffectRegistry.addDynamicConfigEntries();
        CapeEffectRegistry.addDynamicConfigEntries();
        Config.addDynamicEntry(TileAttunementAltar.ConfigEntryAttunementAltar.instance);
        Config.addDynamicEntry(TileTreeBeacon.ConfigEntryTreeBeacon.instance);
        Config.addDynamicEntry(TileOreGenerator.ConfigEntryMultiOre.instance);
        Config.addDynamicEntry(TileChalice.ConfigEntryChalice.instance);
        Config.addDynamicEntry(TileBore.CfgEntry.instance);
        Config.addDynamicEntry(new AmuletEnchantHelper.CfgEntry());
        Config.addDynamicEntry(new GemAttributeHelper.CfgEntry());
        Config.addDynamicEntry(new TileAccelerationBlacklist.TileAccelBlacklistEntry());
        Config.addDynamicEntry(new ShootingStarHandler.StarConfigEntry());
        Config.addDynamicEntry(PerkLevelManager.INSTANCE);
        Config.addDynamicEntry(new LogUtil.CfgEntry());
    }

    public void registerConfigDataRegistries() {
        Config.addDataRegistry(OreTypes.RITUAL_MINERALIS);
        Config.addDataRegistry(OreTypes.AEVITAS_ORE_PERK);
        Config.addDataRegistry(OreTypes.TREASURE_SHRINE_GEN);
        Config.addDataRegistry(OreTypes.PERK_VOID_TRASH_REPLACEMENT);
        Config.addDataRegistry(FluidRarityRegistry.INSTANCE);
        Config.addDataRegistry(AmuletEnchantmentRegistry.INSTANCE);
        Config.addDataRegistry(HerdableAnimal.HerdableAdapter.INSTANCE);
    }

    public void preInit() {
        registryPrimer = new InternalRegistryPrimer();
        MinecraftForge.EVENT_BUS.register(new PrimerEventHandler(registryPrimer));

        RegistryItems.setupDefaults();

        RegistryConstellations.init();
        RegistryAdvancements.init();

        PacketChannel.init();

        RegistryEntities.init();

        //Transmission registry
        SourceClassRegistry.setupRegistry();
        TransmissionClassRegistry.setupRegistry();
        StarlightNetworkRegistry.setupRegistry();

        LootTableUtil.initLootTable();
        ConstellationEffectRegistry.init();

        if (Mods.THAUMCRAFT.isPresent()) {
            MinecraftForge.EVENT_BUS.register(ModIntegrationThaumcraft.INSTANCE);
        }

        RegistryPerks.initPerkTree();

        registerCapabilities();

        if (Mods.CRAFTTWEAKER.isPresent()) {
            AstralSorcery.log.info("Crafttweaker found! Adding recipe handlers...");
            ModIntegrationCrafttweaker.instance.load();
        } else {
            AstralSorcery.log.info("Crafttweaker not found!");
        }
    }

    private void registerCapabilities() {
        //Chunk Fluid storage for Neromantic primes
        CapabilityManager.INSTANCE.register(FluidRarityRegistry.ChunkFluidEntry.class, new Capability.IStorage<FluidRarityRegistry.ChunkFluidEntry>() {
            @Nullable
            @Override
            public NBTBase writeNBT(Capability<FluidRarityRegistry.ChunkFluidEntry> capability, FluidRarityRegistry.ChunkFluidEntry instance, EnumFacing side) {
                return instance.serializeNBT();
            }

            @Override
            public void readNBT(Capability<FluidRarityRegistry.ChunkFluidEntry> capability, FluidRarityRegistry.ChunkFluidEntry instance, EnumFacing side, NBTBase nbt) {
                instance.deserializeNBT((NBTTagCompound) nbt);
            }
        }, new FluidRarityRegistry.ChunkFluidEntryFactory());

        //Item data storage to find player + item combinations
        CapabilityManager.INSTANCE.register(AmuletHolderCapability.class, new Capability.IStorage<AmuletHolderCapability>() {
            @Nullable
            @Override
            public NBTBase writeNBT(Capability<AmuletHolderCapability> capability, AmuletHolderCapability instance, EnumFacing side) {
                return instance.serializeNBT();
            }

            @Override
            public void readNBT(Capability<AmuletHolderCapability> capability, AmuletHolderCapability instance, EnumFacing side, NBTBase nbt) {
                instance.deserializeNBT((NBTTagCompound) nbt);
            }
        }, new AmuletHolderCapability.Factory());

        //Chunk rock crystal storage for rock crystal generation
        CapabilityManager.INSTANCE.register(RockCrystalHandler.RockCrystalPositions.class, new Capability.IStorage<RockCrystalHandler.RockCrystalPositions>() {
            @Nullable
            @Override
            public NBTBase writeNBT(Capability<RockCrystalHandler.RockCrystalPositions> capability, RockCrystalHandler.RockCrystalPositions instance, EnumFacing side) {
                return instance.serializeNBT();
            }

            @Override
            public void readNBT(Capability<RockCrystalHandler.RockCrystalPositions> capability, RockCrystalHandler.RockCrystalPositions instance, EnumFacing side, NBTBase nbt) {
                instance.deserializeNBT((NBTTagCompound) nbt);
            }
        }, new RockCrystalHandler.ChunkFluidEntryFactory());
    }

    public void registerOreDictEntries() {
        OreDictionary.registerOre(OreDictAlias.BLOCK_MARBLE, BlockMarble.MarbleBlockType.RAW.asStack());
        OreDictionary.registerOre(OreDictAlias.BLOCK_MARBLE, BlockMarble.MarbleBlockType.BRICKS.asStack());
        OreDictionary.registerOre(OreDictAlias.BLOCK_MARBLE, BlockMarble.MarbleBlockType.PILLAR.asStack());
        OreDictionary.registerOre(OreDictAlias.BLOCK_MARBLE, BlockMarble.MarbleBlockType.ARCH.asStack());
        OreDictionary.registerOre(OreDictAlias.BLOCK_MARBLE, BlockMarble.MarbleBlockType.CHISELED.asStack());
        OreDictionary.registerOre(OreDictAlias.BLOCK_MARBLE, BlockMarble.MarbleBlockType.ENGRAVED.asStack());
        OreDictionary.registerOre(OreDictAlias.BLOCK_MARBLE, BlockMarble.MarbleBlockType.RUNED.asStack());
        OreDictionary.registerOre("blockMarble", BlockMarble.MarbleBlockType.RAW.asStack());
        OreDictionary.registerOre("blockMarble", BlockMarble.MarbleBlockType.BRICKS.asStack());
        OreDictionary.registerOre("blockMarble", BlockMarble.MarbleBlockType.PILLAR.asStack());
        OreDictionary.registerOre("blockMarble", BlockMarble.MarbleBlockType.ARCH.asStack());
        OreDictionary.registerOre("blockMarble", BlockMarble.MarbleBlockType.CHISELED.asStack());
        OreDictionary.registerOre("blockMarble", BlockMarble.MarbleBlockType.ENGRAVED.asStack());
        OreDictionary.registerOre("blockMarble", BlockMarble.MarbleBlockType.RUNED.asStack());

        OreDictionary.registerOre("oreAstralStarmetal", BlockCustomOre.OreType.STARMETAL.asStack());
        OreDictionary.registerOre(OreDictAlias.ITEM_STARMETAL_INGOT, ItemCraftingComponent.MetaType.STARMETAL_INGOT.asStack());
        OreDictionary.registerOre(OreDictAlias.ITEM_STARMETAL_DUST, ItemCraftingComponent.MetaType.STARDUST.asStack());

        OreDictionary.registerOre("oreAquamarine", BlockCustomSandOre.OreType.AQUAMARINE.asStack());
        OreDictionary.registerOre(OreDictAlias.ITEM_AQUAMARINE, ItemCraftingComponent.MetaType.AQUAMARINE.asStack());
    }

    public void init() {
        RegistryStructures.init();
        RegistryResearch.init();
        RegistryRecipes.initGrindstoneOreRecipes();
        SextantFinder.initialize();
        RegistryKnowledgeFragments.init();
        PatreonDataManager.loadPatreonEffects();

        RegistryConstellations.initMapEffects();

        if(Mods.CRAFTTWEAKER.isPresent()) {
            ModIntegrationCrafttweaker.instance.pushChanges();
        }
        ModIntegrationChisel.sendVariantIMC();
        MappingMigrationHandler.init();

        ModIntegrationBloodMagic.sendIMC();

        NetworkRegistry.INSTANCE.registerGuiHandler(AstralSorcery.instance, this);

        MinecraftForge.TERRAIN_GEN_BUS.register(TreeCaptureHelper.eventInstance);

        MinecraftForge.EVENT_BUS.register(new EventHandlerNetwork());
        MinecraftForge.EVENT_BUS.register(new EventHandlerServer());
        MinecraftForge.EVENT_BUS.register(new EventHandlerMisc());
        MinecraftForge.EVENT_BUS.register(new EventHandlerEntity());
        MinecraftForge.EVENT_BUS.register(new EventHandlerIO());
        MinecraftForge.EVENT_BUS.register(TransmissionChunkTracker.getInstance());
        MinecraftForge.EVENT_BUS.register(TickManager.getInstance());
        MinecraftForge.EVENT_BUS.register(StarlightTransmissionHandler.getInstance());
        MinecraftForge.EVENT_BUS.register(new LootTableUtil());
        MinecraftForge.EVENT_BUS.register(BlockDropCaptureAssist.instance);
        MinecraftForge.EVENT_BUS.register(ChunkVersionController.instance);
        MinecraftForge.EVENT_BUS.register(CelestialGatewaySystem.instance);
        MinecraftForge.EVENT_BUS.register(EventHandlerCapeEffects.INSTANCE);
        MinecraftForge.EVENT_BUS.register(TimeStopController.INSTANCE);
        MinecraftForge.EVENT_BUS.register(FluidRarityRegistry.INSTANCE);
        MinecraftForge.EVENT_BUS.register(RockCrystalHandler.INSTANCE);
        MinecraftForge.EVENT_BUS.register(PlayerAmuletHandler.INSTANCE);
        MinecraftForge.EVENT_BUS.register(PerkEffectHelper.EVENT_INSTANCE);
        MinecraftForge.EVENT_BUS.register(AttributeTypeLimiter.INSTANCE);
        MinecraftForge.EVENT_BUS.register(PlayerActivityManager.INSTANCE);
        MinecraftForge.EVENT_BUS.register(StructureIntegrityObserver.INSTANCE);

        GameRegistry.registerWorldGenerator(worldGenerator.setupAttributes(), 50);
        if(Config.enableRetroGen) {
            MinecraftForge.EVENT_BUS.register(new RetroGenController());
        }

        TickManager manager = TickManager.getInstance();
        registerTickHandlers(manager);

        SyncDataHolder.initialize();
        TileAccelerationBlacklist.init();
        HerdableAnimal.init();
        TreeTypes.init();
    }

    protected void registerTickHandlers(TickManager manager) {
        manager.register(ConstellationSkyHandler.getInstance());
        manager.register(StarlightTransmissionHandler.getInstance());
        manager.register(StarlightUpdateHandler.getInstance());
        manager.register(WorldCacheManager.getInstance());
        manager.register(new LinkHandler()); //Only used as PERK_TREE for tick handling
        manager.register(SyncDataHolder.getTickInstance());
        manager.register(commonScheduler);
        manager.register(PlayerChargeHandler.INSTANCE);
        manager.register(EventHandlerCapeEffects.INSTANCE);
        manager.register(TimeStopController.INSTANCE);
        manager.register(PlayerAmuletHandler.INSTANCE);
        //manager.register(SpellCastingManager.PERK_TREE);
        manager.register(PatreonFlareManager.INSTANCE);
        manager.register(PerkEffectHelper.EVENT_INSTANCE);
        manager.register(ShootingStarHandler.getInstance());
        manager.register(ParticleEffectWatcher.INSTANCE);
        manager.register(PlayerActivityManager.INSTANCE);

        //TickTokenizedMaps
        manager.register(EventHandlerEntity.spawnDenyRegions);
        manager.register(EventHandlerEntity.invulnerabilityCooldown);
        manager.register(EventHandlerEntity.ritualFlight);
        manager.register(PerkEffectHelper.perkCooldowns);
        manager.register(PerkEffectHelper.perkCooldownsClient); //Doesn't matter being registered on servers aswell. And prevent fckery in integrated.
    }

    public void postInit() {
        AltarRecipeEffectRecovery.attemptRecipeRecovery();
        RegistryPerks.postProcessPerks();

        AstralSorcery.log.info("Post compile recipes");

        CraftingAccessManager.compile();
        ResearchIOThread.startIOThread();
    }

    public void clientFinishedLoading() {
        ItemHandle.ignoreGatingRequirement = false;
    }

    public FakePlayer getASFakePlayerServer(WorldServer world) {
        return FakePlayerFactory.get(world, new GameProfile(AS_FAKEPLAYER_UUID, "AS-FakePlayer"));
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
        scheduleDelayed(r, 0);
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
            case OBSERVATORY:
                return new ContainerObservatory();
            default:
                break;
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
        JOURNAL_STORAGE,
        OBSERVATORY(TileObservatory.class),
        SEXTANT,
        KNOWLEDGE_CONSTELLATION;

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
