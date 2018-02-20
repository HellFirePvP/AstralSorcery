/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.client.effect.EffectHandler;
import hellfirepvp.astralsorcery.client.effect.light.ClientLightbeamHandler;
import hellfirepvp.astralsorcery.client.effect.light.EffectLightning;
import hellfirepvp.astralsorcery.client.event.ClientConnectionEventHandler;
import hellfirepvp.astralsorcery.client.event.ClientGatewayHandler;
import hellfirepvp.astralsorcery.client.event.ClientRenderEventHandler;
import hellfirepvp.astralsorcery.client.models.obj.OBJModelLibrary;
import hellfirepvp.astralsorcery.client.render.entity.*;
import hellfirepvp.astralsorcery.client.render.tile.*;
import hellfirepvp.astralsorcery.client.util.ItemColorizationHelper;
import hellfirepvp.astralsorcery.client.util.JournalRecipeDisplayRecovery;
import hellfirepvp.astralsorcery.client.util.camera.ClientCameraManager;
import hellfirepvp.astralsorcery.client.util.item.AstralTEISR;
import hellfirepvp.astralsorcery.client.util.item.DummyModelLoader;
import hellfirepvp.astralsorcery.client.util.item.ItemRenderRegistry;
import hellfirepvp.astralsorcery.client.util.item.ItemRendererFilteredTESR;
import hellfirepvp.astralsorcery.client.util.mappings.ClientJournalMapping;
import hellfirepvp.astralsorcery.client.util.mappings.ClientPerkTextureMapping;
import hellfirepvp.astralsorcery.client.util.resource.AssetLibrary;
import hellfirepvp.astralsorcery.common.CommonProxy;
import hellfirepvp.astralsorcery.common.auxiliary.tick.TickManager;
import hellfirepvp.astralsorcery.common.base.Mods;
import hellfirepvp.astralsorcery.common.block.BlockDynamicColor;
import hellfirepvp.astralsorcery.common.block.BlockDynamicStateMapper;
import hellfirepvp.astralsorcery.common.block.BlockMachine;
import hellfirepvp.astralsorcery.common.crafting.helper.CraftingAccessManager;
import hellfirepvp.astralsorcery.common.entities.*;
import hellfirepvp.astralsorcery.common.integrations.ModIntegrationGeolosys;
import hellfirepvp.astralsorcery.common.item.base.render.INBTModel;
import hellfirepvp.astralsorcery.common.item.base.render.ItemDynamicColor;
import hellfirepvp.astralsorcery.common.item.base.IMetaItem;
import hellfirepvp.astralsorcery.common.item.base.IOBJItem;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.registry.RegistryBlocks;
import hellfirepvp.astralsorcery.common.registry.RegistryItems;
import hellfirepvp.astralsorcery.common.tile.*;
import hellfirepvp.astralsorcery.common.tile.network.TileCollectorCrystal;
import hellfirepvp.astralsorcery.common.tile.network.TileCrystalLens;
import hellfirepvp.astralsorcery.common.tile.network.TileCrystalPrismLens;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ClientProxy
 * Created by HellFirePvP
 * Date: 07.05.2016 / 00:23
 */
public class ClientProxy extends CommonProxy {

    private final ClientScheduler scheduler = new ClientScheduler();
    @Override
    public void preInit() {
        MinecraftForge.EVENT_BUS.register(this);
        try {
            ((IReloadableResourceManager) Minecraft.getMinecraft().getResourceManager()).registerReloadListener(AssetLibrary.resReloadInstance);
        } catch (Exception exc) {
            AstralSorcery.log.warn("[AstralSorcery] Could not add AssetLibrary to resource manager! Texture reloading will have no effect on AstralSorcery textures.");
            AssetLibrary.resReloadInstance.onResourceManagerReload(null);
        }
        ModelLoaderRegistry.registerLoader(new DummyModelLoader()); //IItemRenderer Hook ModelLoader
        OBJLoader.INSTANCE.addDomain(AstralSorcery.MODID);

        super.preInit();

        CraftingAccessManager.ignoreJEI = false;
    }

    @SubscribeEvent
    public void registerModels(ModelRegistryEvent event) {
        registerFluidRenderers();
        registerEntityRenderers();
        registerDisplayInformationInit();
        registerTileRenderers();
        registerItemRenderers();
    }

    private void registerPendingIBlockColorBlocks() {
        BlockColors colors = Minecraft.getMinecraft().getBlockColors();
        for (BlockDynamicColor b : RegistryBlocks.pendingIBlockColorBlocks) {
            colors.registerBlockColorHandler(b::getColorMultiplier, (Block) b);
        }
    }

    private void registerPendingIItemColorItems() {
        ItemColors colors = Minecraft.getMinecraft().getItemColors();
        for (ItemDynamicColor i : RegistryItems.pendingDynamicColorItems) {
            colors.registerItemColorHandler(i::getColorForItemStack, (Item) i);
        }
    }

    private void registerFluidRenderers() {
        registerFluidRender(BlocksAS.fluidLiquidStarlight);
    }

    private void registerFluidRender(Fluid f) {
        RegistryBlocks.FluidCustomModelMapper mapper = new RegistryBlocks.FluidCustomModelMapper(f);
        Block block = f.getBlock();
        if(block != null) {
            Item item = Item.getItemFromBlock(block);
            if (item != Items.AIR) {
                ModelLoader.registerItemVariants(item);
                ModelLoader.setCustomMeshDefinition(item, mapper);
            } else {
                ModelLoader.setCustomStateMapper(block, mapper);
            }
        }
    }

    @Override
    public void init() {
        super.init();

        registerPendingIBlockColorBlocks();
        registerPendingIItemColorItems();

        MinecraftForge.EVENT_BUS.register(new ClientRenderEventHandler());
        MinecraftForge.EVENT_BUS.register(new ClientConnectionEventHandler());
        MinecraftForge.EVENT_BUS.register(EffectHandler.getInstance());
        MinecraftForge.EVENT_BUS.register(new ClientGatewayHandler());
    }

    @Override
    public void postInit() {
        super.postInit();

        TileEntityItemStackRenderer.instance = new AstralTEISR(TileEntityItemStackRenderer.instance); //Wrapping TEISR

        //TexturePreloader.doPreloadRoutine();

        ClientJournalMapping.init();
        ClientPerkTextureMapping.init();
        OBJModelLibrary.init();

        ((IReloadableResourceManager) Minecraft.getMinecraft().getResourceManager()).registerReloadListener(ItemColorizationHelper.instance);

        JournalRecipeDisplayRecovery.attemptRecipeRecovery();
    }

    @Override
    public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        if(id < 0 || id >= EnumGuiId.values().length) return null; //Out of range.
        EnumGuiId guiType = EnumGuiId.values()[id];
        return ClientGuiHandler.openGui(guiType, player, world, x, y, z);
    }

    public void registerItemRenderers() {
        //RenderTransformsHelper.init();

        ItemRendererFilteredTESR blockMachineRender = new ItemRendererFilteredTESR();
        blockMachineRender.addRender(BlockMachine.MachineType.TELESCOPE.getMeta(), new TESRTelescope(), new TileTelescope());
        blockMachineRender.addRender(BlockMachine.MachineType.GRINDSTONE.getMeta(), new TESRGrindstone(), new TileGrindstone());
        ItemRenderRegistry.register(Item.getItemFromBlock(BlocksAS.blockMachine), blockMachineRender);

        //ItemRenderRegistry.registerCameraTransforms(Item.getItemFromBlock(BlocksAS.blockMachine), RenderTransformsHelper.BLOCK_TRANSFORMS);

        ItemRenderRegistry.register(Item.getItemFromBlock(BlocksAS.collectorCrystal), new TESRCollectorCrystal());
        ItemRenderRegistry.register(Item.getItemFromBlock(BlocksAS.celestialCollectorCrystal), new TESRCollectorCrystal());
        ItemRenderRegistry.register(Item.getItemFromBlock(BlocksAS.celestialCrystals), new TESRCelestialCrystals());

        if(Mods.GEOLOSYS.isPresent() && Mods.ORESTAGES.isPresent()) {
            ModIntegrationGeolosys.registerGeolosysSampleItemRenderer();
        }

        //ItemRenderRegistry.register(ItemsAS.something, new ? implements IItemRenderer());
    }

    @Override
    protected void registerTickHandlers(TickManager manager) {
        super.registerTickHandlers(manager);
        manager.register(new ClientLightbeamHandler());
        manager.register(scheduler);
        manager.register(ClientCameraManager.getInstance());
    }

    @Override
    public void scheduleClientside(Runnable r, int tickDelay) {
        scheduler.addRunnable(r, tickDelay);
    }

    private void registerTileRenderers() {
        registerTESR(TileAltar.class, new TESRAltar());
        registerTESR(TileRitualPedestal.class, new TESRRitualPedestal());
        registerTESR(TileCollectorCrystal.class, new TESRCollectorCrystal());
        registerTESR(TileCelestialCrystals.class, new TESRCelestialCrystals());
        registerTESR(TileWell.class, new TESRWell());
        registerTESR(TileGrindstone.class, new TESRGrindstone());
        registerTESR(TileTelescope.class, new TESRTelescope());
        registerTESR(TileFakeTree.class, new TESRFakeTree());
        registerTESR(TileAttunementAltar.class, new TESRAttunementAltar());
        registerTESR(TileCrystalLens.class, new TESRLens());
        registerTESR(TileCrystalPrismLens.class, new TESRPrismLens());
        registerTESR(TileStarlightInfuser.class, new TESRStarlightInfuser());
        registerTESR(TileTranslucent.class, new TESRTranslucentBlock());
        registerTESR(TileAttunementRelay.class, new TESRAttunementRelay());
        registerTESR(TileMapDrawingTable.class, new TESRMapDrawingTable());
        registerTESR(TileChalice.class, new TESRChalice());
        if(Mods.GEOLOSYS.isPresent() && Mods.ORESTAGES.isPresent()) {
            ModIntegrationGeolosys.registerGeolosysSampleRender();
        }
    }

    private <T extends TileEntity> void registerTESR(Class<T> tile, TileEntitySpecialRenderer<T> renderer) {
        ClientRegistry.bindTileEntitySpecialRenderer(tile, renderer);
    }

    public void registerEntityRenderers() {
        //RenderingRegistry.registerEntityRenderingHandler(EntityTelescope.class, new RenderEntityTelescope.Factory());
        //RenderingRegistry.registerEntityRenderingHandler(EntityGrindstone.class, new RenderEntityGrindstone.Factory());
        RenderingRegistry.registerEntityRenderingHandler(EntityItemHighlighted.class, new RenderEntityItemHighlight.Factory());
        RenderingRegistry.registerEntityRenderingHandler(EntityFlare.class, new RenderEntityFlare.Factory());
        RenderingRegistry.registerEntityRenderingHandler(EntityStarburst.class, new RenderEntityStarburst.Factory());
        RenderingRegistry.registerEntityRenderingHandler(EntityNocturnalSpark.class, new RenderEntityNoOp.Factory<>());
        RenderingRegistry.registerEntityRenderingHandler(EntityIlluminationSpark.class, new RenderEntityNoOp.Factory<>());
        RenderingRegistry.registerEntityRenderingHandler(EntityGrapplingHook.class, new RenderEntityHook.Factory());
        RenderingRegistry.registerEntityRenderingHandler(EntitySpectralTool.class, new RenderSpectralTool.Factory());
        RenderingRegistry.registerEntityRenderingHandler(EntityLiquidSpark.class, new RenderLiquidSpark.Factory());
        //RenderingRegistry.registerEntityRenderingHandler(SpellProjectile.class, new RenderEntitySpellProjectile.Factory());
    }

    public void registerDisplayInformationInit() {
        for (RenderInfoItem modelEntry : itemRegister) {
            if (modelEntry.variant) {
                registerVariantName(modelEntry.item, modelEntry.name);
            }
            if(modelEntry.item instanceof IOBJItem) {
                if(!((IOBJItem) modelEntry.item).hasOBJAsSubmodelDefinition()) {
                    String[] models = ((IOBJItem) modelEntry.item).getOBJModelNames();
                    if(models != null) {
                        for (String modelDef : models) {
                            ModelResourceLocation mrl = new ModelResourceLocation(AstralSorcery.MODID + ":obj/" + modelDef + ".obj", "inventory");
                            ModelBakery.registerItemVariants(modelEntry.item, mrl);
                            ModelLoader.setCustomModelResourceLocation(modelEntry.item, modelEntry.metadata, mrl);
                        }
                    }
                } else { //We expect a wrapper in the blockstates..
                    ModelResourceLocation mrl = new ModelResourceLocation(AstralSorcery.MODID + ":obj/" + modelEntry.name, "inventory");
                    ModelBakery.registerItemVariants(modelEntry.item, mrl);
                    ModelLoader.setCustomModelResourceLocation(modelEntry.item, modelEntry.metadata, mrl);
                }
            } else {
                Item item = modelEntry.item;
                ModelResourceLocation def = new ModelResourceLocation(AstralSorcery.MODID + ":" + modelEntry.name, "inventory");
                if(item instanceof INBTModel) {
                    List<ResourceLocation> out = ((INBTModel) item).getAllPossibleLocations(def);
                    ResourceLocation[] arr = new ResourceLocation[out.size()];
                    arr = out.toArray(arr);
                    ModelBakery.registerItemVariants(item, arr);
                    ModelLoader.setCustomMeshDefinition(item, stack -> ((INBTModel) item).getModelLocation(stack, def));
                } else {
                    ModelLoader.setCustomModelResourceLocation(item, modelEntry.metadata, def);
                }
            }
        }

        for (RenderInfoBlock modelEntry : blockRegister) {
            if(modelEntry.block instanceof BlockDynamicStateMapper) {
                if(((BlockDynamicStateMapper) modelEntry.block).handleRegisterStateMapper()) {
                    ((BlockDynamicStateMapper) modelEntry.block).registerStateMapper();
                }
            }

            Item item = Item.getItemFromBlock(modelEntry.block);
            ModelResourceLocation def = new ModelResourceLocation(AstralSorcery.MODID + ":" + modelEntry.name, "inventory");
            if(item instanceof INBTModel) {
                List<ResourceLocation> out = ((INBTModel) item).getAllPossibleLocations(def);
                ResourceLocation[] arr = new ResourceLocation[out.size()];
                arr = out.toArray(arr);
                ModelBakery.registerItemVariants(item, arr);
                ModelLoader.setCustomMeshDefinition(item, (stack -> ((INBTModel) item).getModelLocation(stack, def)));
            } else {
                ModelLoader.setCustomModelResourceLocation(item, modelEntry.metadata, def);
            }
        }
    }

    @Override
    public void fireLightning(World world, Vector3 from, Vector3 to, Color overlay) {
        EffectLightning lightning = EffectHandler.getInstance().lightning(from, to);
        if(overlay != null) {
            lightning.setOverlayColor(overlay);
        }
    }

    @Override
    public void registerFromSubItems(Item item, String name) {
        if (item instanceof IMetaItem) {
            int[] additionalMetas = ((IMetaItem) item).getSubItems();
            if (additionalMetas != null) {
                for (int meta : additionalMetas) {
                    registerItemRender(item, meta, name);
                }
            }
            return;
        }
        NonNullList<ItemStack> list = NonNullList.create();
        item.getSubItems(item.getCreativeTab(), list);
        if (list.size() > 0) {
            for (ItemStack i : list) {
                registerItemRender(item, i.getItemDamage(), name);
            }
        } else {
            registerItemRender(item, 0, name);
        }
    }

    public void registerVariantName(Item item, String name) {
        ModelBakery.registerItemVariants(item, new ResourceLocation(AstralSorcery.MODID, name));
    }

    public void registerBlockRender(Block block, int metadata, String name) {
        blockRegister.add(new RenderInfoBlock(block, metadata, name));
    }

    public void registerItemRender(Item item, int metadata, String name) {
        registerItemRender(item, metadata, name, false);
    }

    public void registerItemRender(Item item, int metadata, String name, boolean variant) {
        itemRegister.add(new RenderInfoItem(item, metadata, name, variant));
    }

    private static List<RenderInfoBlock> blockRegister = new ArrayList<>();
    private static List<RenderInfoItem> itemRegister = new ArrayList<>();

    private static class RenderInfoBlock {

        public Block block;
        public int metadata;
        public String name;

        public RenderInfoBlock(Block block, int metadata, String name) {
            this.block = block;
            this.metadata = metadata;
            this.name = name;
        }
    }

    private static class RenderInfoItem {

        public Item item;
        public int metadata;
        public String name;
        public boolean variant;

        public RenderInfoItem(Item item, int metadata, String name, boolean variant) {
            this.item = item;
            this.metadata = metadata;
            this.name = name;
            this.variant = variant;
        }
    }

}
