package hellfirepvp.astralsorcery.common.block;

import hellfirepvp.astralsorcery.common.registry.RegistryItems;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockAttunementRelay
 * Created by HellFirePvP
 * Date: 30.11.2016 / 13:16
 */
public class BlockAttunementRelay extends Block {

    public BlockAttunementRelay() {
        super(Material.GLASS, MapColor.QUARTZ);
        setHardness(0.5F);
        setHarvestLevel("pickaxe", 0);
        setResistance(1.0F);
        setSoundType(SoundType.GLASS);
        setCreativeTab(RegistryItems.creativeTabAstralSorcery);
    }

}
