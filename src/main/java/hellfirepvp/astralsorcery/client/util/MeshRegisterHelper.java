package hellfirepvp.astralsorcery.client.util;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: MeshRegisterHelper
 * Created by HellFirePvP
 * Date: 07.05.2016 / 15:37
 */
public class MeshRegisterHelper {

    public static void registerItem(Item item, int metadata, String itemName) {
        getIMM().register(item, metadata, new ModelResourceLocation(itemName, "inventory"));
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

    public static ItemModelMesher getIMM() {
        return Minecraft.getMinecraft().getRenderItem().getItemModelMesher();
    }

    public static BlockModelShapes getBMShapes() {
        return getIMM().getModelManager().getBlockModelShapes();
    }

}
