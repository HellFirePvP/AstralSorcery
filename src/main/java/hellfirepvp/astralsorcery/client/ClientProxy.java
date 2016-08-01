package hellfirepvp.astralsorcery.client;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.client.effect.EffectHandler;
import hellfirepvp.astralsorcery.client.event.SkyboxRenderEventHandler;
import hellfirepvp.astralsorcery.client.gui.GuiConstellationPaper;
import hellfirepvp.astralsorcery.client.gui.GuiTelescope;
import hellfirepvp.astralsorcery.client.render.entity.RenderEntityItemHighlight;
import hellfirepvp.astralsorcery.client.render.item.RenderItemTelescope;
import hellfirepvp.astralsorcery.client.render.tile.TESRAltar;
import hellfirepvp.astralsorcery.client.render.tile.TESRCollectorCrystal;
import hellfirepvp.astralsorcery.client.util.MeshRegisterHelper;
import hellfirepvp.astralsorcery.client.util.item.AstralTEISR;
import hellfirepvp.astralsorcery.client.util.item.DummyModelLoader;
import hellfirepvp.astralsorcery.client.util.item.ItemRenderRegistry;
import hellfirepvp.astralsorcery.client.util.item.ItemRendererFilteredTESR;
import hellfirepvp.astralsorcery.client.util.item.ItemRendererTESR;
import hellfirepvp.astralsorcery.common.CommonProxy;
import hellfirepvp.astralsorcery.common.block.BlockAltar;
import hellfirepvp.astralsorcery.common.block.BlockStoneMachine;
import hellfirepvp.astralsorcery.common.block.tile.TileAltar;
import hellfirepvp.astralsorcery.common.block.tile.TileCollectorCrystal;
import hellfirepvp.astralsorcery.common.constellation.Constellation;
import hellfirepvp.astralsorcery.common.constellation.ConstellationRegistry;
import hellfirepvp.astralsorcery.common.entities.EntityItemHighlighted;
import hellfirepvp.astralsorcery.common.entities.EntityTelescope;
import hellfirepvp.astralsorcery.common.item.base.IMetaItem;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import hellfirepvp.astralsorcery.common.registry.RegistryItems;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

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

    @Override
    public void preInit() {
        TileEntityItemStackRenderer.instance = new AstralTEISR(TileEntityItemStackRenderer.instance); //Wrapping TEISR

        ModelLoaderRegistry.registerLoader(new DummyModelLoader()); //IItemRenderer Hook ModelLoader

        super.preInit();

        registerEntityRenderers();
    }

    @Override
    public void init() {
        super.init();

        MinecraftForge.EVENT_BUS.register(new SkyboxRenderEventHandler());
        MinecraftForge.EVENT_BUS.register(EffectHandler.getInstance());

        registerDisplayInformationInit();

        registerTileRenderers();

        registerItemRenderers();
    }

    @Override
    public void postInit() {
        super.postInit();
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        switch (ID) {
            case 0: { //Telescope
                Entity e = world.getEntityByID(x); //Suggested entity id;
                if (e == null || !(e instanceof EntityTelescope)) {
                    AstralSorcery.log.info("Tried opening Telescope GUI without valid telescope entity?");
                    return null;
                } else {
                    return new GuiTelescope(player, (EntityTelescope) e);
                }
            }
            case 1: { //ConstPaper
                Constellation c = ConstellationRegistry.getConstellationById(x); //Suggested Constellation id;
                if(c == null) {
                    AstralSorcery.log.info("Tried opening ConstellationPaper GUI with out-of-range constellation id!");
                    return null;
                } else {
                    return new GuiConstellationPaper(c);
                }
            }
        }
        return null;
    }

    private void registerItemRenderers() {
        ItemRendererFilteredTESR stoneMachineRender = new ItemRendererFilteredTESR();
        for (BlockAltar.AltarType type : BlockAltar.AltarType.values()) {
            stoneMachineRender.addRender(type.ordinal(), new TESRAltar(), type.provideTileEntity(null, null));
        }
        ItemRenderRegistry.register(Item.getItemFromBlock(BlocksAS.blockAltar), stoneMachineRender);
        ItemRenderRegistry.register(ItemsAS.telescopePlacer, new RenderItemTelescope());
        ItemRenderRegistry.register(Item.getItemFromBlock(BlocksAS.collectorCrystal), new TESRCollectorCrystal());

        //ItemRenderRegistry.register(ItemsAS.something, new ? implements IItemRenderer());
    }

    private void registerTileRenderers() {
        registerTESR(TileAltar.class, new TESRAltar());
        registerTESR(TileCollectorCrystal.class, new TESRCollectorCrystal());
    }

    private <T extends TileEntity> void registerTESR(Class<T> tile, TileEntitySpecialRenderer<T> renderer) {
        ClientRegistry.bindTileEntitySpecialRenderer(tile, renderer);
    }

    public void registerEntityRenderers() {
        //RenderingRegistry.registerEntityRenderingHandler(EntityTelescope.class, new RenderEntityTelescope.Factory());
        RenderingRegistry.registerEntityRenderingHandler(EntityItemHighlighted.class, new RenderEntityItemHighlight.Factory());
    }

    public void registerDisplayInformationInit() {
        for (RenderInfoItem modelEntry : itemRegister) {
            if (modelEntry.variant) {
                registerVariantName(modelEntry.item, modelEntry.name);
            }
            MeshRegisterHelper.getIMM().register(modelEntry.item, modelEntry.metadata, new ModelResourceLocation(AstralSorcery.MODID + ":" + modelEntry.name, "inventory"));
        }

        //registerAdditionalItemRenderers();

        for (RenderInfoBlock modelEntry : blockRegister) {
            MeshRegisterHelper.registerBlock(modelEntry.block, modelEntry.metadata, AstralSorcery.MODID + ":" + modelEntry.name);
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
        List<ItemStack> list = new ArrayList<>();
        item.getSubItems(item, RegistryItems.creativeTabAstralSorcery, list);
        item.getSubItems(item, RegistryItems.creativeTabAstralSorceryPapers, list);
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
        itemRegister.add(new RenderInfoItem(item, metadata, name, false));
    }

    public void registerItemRender(Item item, int metadata, String name, boolean variant) {
        itemRegister.add(new RenderInfoItem(item, metadata, name, variant));
    }

    private static List<RenderInfoBlock> blockRegister = new ArrayList<RenderInfoBlock>();
    private static List<RenderInfoItem> itemRegister = new ArrayList<RenderInfoItem>();

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
