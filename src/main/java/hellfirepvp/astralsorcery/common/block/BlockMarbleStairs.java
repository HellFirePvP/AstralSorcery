package hellfirepvp.astralsorcery.common.block;

import hellfirepvp.astralsorcery.common.registry.RegistryItems;
import net.minecraft.block.BlockStairs;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockMarbleStairs
 * Created by HellFirePvP
 * Date: 26.04.2017 / 11:32
 */
public class BlockMarbleStairs extends BlockStairs {

    public BlockMarbleStairs() {
        super(BlockMarble.MarbleBlockType.BRICKS.asBlock());
        setHardness(1.0F);
        setHarvestLevel("pickaxe", 1);
        setResistance(3.0F);
        setCreativeTab(RegistryItems.creativeTabAstralSorcery);
    }
}
