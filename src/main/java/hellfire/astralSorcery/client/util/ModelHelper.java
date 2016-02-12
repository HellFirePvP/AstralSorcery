package hellfire.astralSorcery.client.util;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * This class is part of the Astral Sorcery Mod
 * <p/>
 * Created by HellFirePvP @ 12.02.2016 00:25
 */
@SideOnly(Side.CLIENT)
public class ModelHelper {

    public static void registerItem(Item item, int metadata, String itemName) {
        getItemModelMesher().register(item, metadata, new ModelResourceLocation(itemName, "inventory"));
    }

    public static void registerBlock(Block block, int metadata, String blockName) {
        registerItem(Item.getItemFromBlock(block), metadata, blockName);
    }

    public static void registerBlock(Block block, String blockName) {
        registerBlock(block, 0, blockName);
    }

    public static void registerItem(Item item, String itemName) {
        registerItem(item, 0, itemName);
    }

    public static ItemModelMesher getItemModelMesher() {
        return Minecraft.getMinecraft().getRenderItem().getItemModelMesher();
    }

    public static BlockModelShapes getBlockModelShapes() {
        return getItemModelMesher().getModelManager().getBlockModelShapes();
    }
}
