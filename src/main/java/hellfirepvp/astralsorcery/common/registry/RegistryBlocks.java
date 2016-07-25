package hellfirepvp.astralsorcery.common.registry;

import hellfirepvp.astralsorcery.common.block.BlockCustomOre;
import hellfirepvp.astralsorcery.common.block.BlockMarble;
import hellfirepvp.astralsorcery.common.block.BlockStoneMachine;
import hellfirepvp.astralsorcery.common.block.tile.TileAltar;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.util.LinkedList;
import java.util.List;

import static hellfirepvp.astralsorcery.common.lib.BlocksAS.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RegistryBlocks
 * Created by HellFirePvP
 * Date: 07.05.2016 / 18:16
 */
public class RegistryBlocks {

    public static List<Block> defaultItemBlocksToRegister = new LinkedList<>();

    public static void init() {
        registerBlocks();

        registerTileEntities();
    }

    //Blocks
    private static void registerBlocks() {
        //WorldGen&Related
        customOre = registerBlock(new BlockCustomOre());
        blockMarble = registerBlock(new BlockMarble());

        //Machines&Related
        stoneMachine = registerBlock(new BlockStoneMachine());
    }

    //Tiles
    private static void registerTileEntities() {
        registerTile(TileAltar.class);
    }

    private static void queueDefaultItemBlock(Block block) {
        defaultItemBlocksToRegister.add(block);
    }

    private static <T extends Block> T registerBlock(T block, String name) {
        GameRegistry.register(block.setUnlocalizedName(name).setRegistryName(name));
        return block;
    }

    private static <T extends Block> T registerBlock(T block) {
        return registerBlock(block, block.getClass().getSimpleName());
    }

    private static void registerTile(Class<? extends TileEntity> tile, String name) {
        GameRegistry.registerTileEntity(tile, name);
    }

    private static void registerTile(Class<? extends TileEntity> tile) {
        registerTile(tile, tile.getSimpleName());
    }

}
