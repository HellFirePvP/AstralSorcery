package hellfirepvp.astralsorcery.common.registry;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.block.BlockCustomSandOre;
import hellfirepvp.astralsorcery.common.block.network.BlockLens;
import hellfirepvp.astralsorcery.common.block.network.BlockPrism;
import hellfirepvp.astralsorcery.common.block.network.BlockAltar;
import hellfirepvp.astralsorcery.common.block.BlockCustomOre;
import hellfirepvp.astralsorcery.common.block.BlockMarble;
import hellfirepvp.astralsorcery.common.block.BlockStoneMachine;
import hellfirepvp.astralsorcery.common.block.BlockStructural;
import hellfirepvp.astralsorcery.common.block.BlockVariants;
import hellfirepvp.astralsorcery.common.block.network.BlockCollectorCrystal;
import hellfirepvp.astralsorcery.common.tile.TileAltar;
import hellfirepvp.astralsorcery.common.tile.network.TileCollectorCrystal;
import hellfirepvp.astralsorcery.common.tile.network.TileCrystalLens;
import hellfirepvp.astralsorcery.common.tile.network.TileCrystalPrismLens;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
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
        customSandOre = registerBlock(new BlockCustomSandOre());
        blockMarble = registerBlock(new BlockMarble());

        //Mechanics
        blockStructural = registerBlock(new BlockStructural());
        blockAltar = registerBlock(new BlockAltar());

        lens = registerBlock(new BlockLens());
        lensPrism = registerBlock(new BlockPrism());
        queueDefaultItemBlock(lens);
        queueDefaultItemBlock(lensPrism);

        //Machines&Related
        //stoneMachine = registerBlock(new BlockStoneMachine());
        collectorCrystal = registerBlock(new BlockCollectorCrystal());
    }

    //Called after items are registered.
    public static void initRenderRegistry() {
        registerBlockRender(blockMarble);
        registerBlockRender(customOre);
        registerBlockRender(customSandOre);
    }

    //Tiles
    private static void registerTileEntities() {
        registerTile(TileAltar.class);
        registerTile(TileCollectorCrystal.class);

        registerTile(TileCrystalLens.class);
        registerTile(TileCrystalPrismLens.class);
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

    private static void registerBlockRender(Block block) {
        if(block instanceof BlockVariants) {
            for (IBlockState state : ((BlockVariants) block).getValidStates()) {
                String unlocName = ((BlockVariants) block).getBlockName(state);
                String name = unlocName + "_" + ((BlockVariants) block).getStateName(state);
                AstralSorcery.proxy.registerVariantName(Item.getItemFromBlock(block), name);
                AstralSorcery.proxy.registerBlockRender(block, block.getMetaFromState(state), name);
            }
        } else {
            AstralSorcery.proxy.registerVariantName(Item.getItemFromBlock(block), block.getUnlocalizedName());
            AstralSorcery.proxy.registerBlockRender(block, 0, block.getUnlocalizedName());
        }
    }

    private static void registerTile(Class<? extends TileEntity> tile, String name) {
        GameRegistry.registerTileEntity(tile, name);
    }

    private static void registerTile(Class<? extends TileEntity> tile) {
        registerTile(tile, tile.getSimpleName());
    }

}
