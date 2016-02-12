package hellfire.astralSorcery.client;

import hellfire.astralSorcery.client.event.ClientTickHandler;
import hellfire.astralSorcery.client.event.SkyboxRenderEventHandler;
import hellfire.astralSorcery.client.util.ModelHelper;
import hellfire.astralSorcery.common.CommonProxy;
import hellfire.astralSorcery.common.items.IMetaItem;
import hellfire.astralSorcery.common.lib.LibConstants;
import hellfire.astralSorcery.common.lib.LibMisc;
import net.minecraft.block.Block;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * <p/>
 * Created by HellFirePvP @ 24.01.2016 20:34
 */
public class ClientProxy extends CommonProxy {

    @Override
    public void preInit() {
        super.preInit();
    }

    @Override
    public void init() {
        super.init();

        MinecraftForge.EVENT_BUS.register(new SkyboxRenderEventHandler());
        MinecraftForge.EVENT_BUS.register(new ClientTickHandler());
    }

    @Override
    public void postInit() {
        super.postInit();
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        return null;
    }

    public void registerDisplayInformationInit() {
        for (RenderInfoItem modelEntry : itemRegister) {
            if (modelEntry.variant) {
                registerVariantName(modelEntry.item, modelEntry.name);
            }
            ModelHelper.getItemModelMesher().register(modelEntry.item, modelEntry.metadata, new ModelResourceLocation(LibConstants.MODID + ":" + modelEntry.name, "inventory"));
        }

        for (RenderInfoBlock modelEntry : blockRegister) {
            ModelHelper.registerBlock(modelEntry.block, modelEntry.metadata, LibConstants.MODID + ":" + modelEntry.name);
        }
    }

    @Override
    public void registerFromSubItems(Item item, String name) {
        if(item instanceof IMetaItem) {
            int[] additionalMetas = ((IMetaItem) item).getSubItems();
            if(additionalMetas != null) {
                for (int meta : additionalMetas) {
                    registerItemRender(item, meta, name);
                }
            }
            return;
        }
        List<ItemStack> list = new ArrayList<ItemStack>();
        item.getSubItems(item, LibMisc.creativeTabAstralSorcery, list);
        if (list.size() > 0) {
            for (ItemStack i : list) {
                registerItemRender(item, i.getItemDamage(), name);
            }
        } else {
            registerItemRender(item, 0, name);
        }
    }

    public void registerVariantName(Item item, String name) {
        ModelBakery.registerItemVariants(item, new ResourceLocation(LibConstants.MODID, name));
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
