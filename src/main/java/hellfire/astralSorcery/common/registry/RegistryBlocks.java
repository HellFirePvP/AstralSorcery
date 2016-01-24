package hellfire.astralSorcery.common.registry;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.registry.GameRegistry;

/**
 * This class is part of the Astral Sorcery Mod
 * <p/>
 * Created by HellFirePvP @ 24.01.2016 21:12
 */
public class RegistryBlocks {

    public static void init() {
        registerBlocks();
        registerTiles();
    }

    private static void registerBlocks() {

    }

    private static void registerTiles() {

    }

    private static <T extends Block> T registerBlock(String name, T block) {
        block.setUnlocalizedName(name);
        GameRegistry.registerBlock(block, name);
        return block;
    }

    private static <T extends Block> T registerBlock(String name, T block, Class<? extends ItemBlock> itemClass) {
        block.setUnlocalizedName(name);
        GameRegistry.registerBlock(block, itemClass, name);
        return block;
    }

    private static <T extends Block> T registerBlock(T block, Class<? extends ItemBlock> itemClass) {
        registerBlock(block.getClass().getSimpleName(), block, itemClass);
        return block;
    }

    private static <T extends Block> T registerBlock(T block) {
        registerBlock(block.getClass().getSimpleName(), block);
        return block;
    }

    private static void registerTile(Class<? extends TileEntity> tile, String name) {
        GameRegistry.registerTileEntity(tile, name);
    }

    private static void registerTile(Class<? extends TileEntity> tile) {
        registerTile(tile, tile.getSimpleName());
    }

}
