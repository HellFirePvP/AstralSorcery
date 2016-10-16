package hellfirepvp.astralsorcery.common.block.fluid;

import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.lib.Materials;
import hellfirepvp.astralsorcery.common.registry.RegistryItems;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialLiquid;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.BlockFluidFinite;

import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: FluidBlockLiquidStarlight
 * Created by HellFirePvP
 * Date: 14.09.2016 / 11:38
 */
public class FluidBlockLiquidStarlight extends BlockFluidClassic {

    //TODO proper water-ish handling?

    public FluidBlockLiquidStarlight() {
        super(BlocksAS.fluidLiquidStarlight, new MaterialLiquid(MapColor.SILVER));
        setDefaultState(this.blockState.getBaseState().withProperty(LEVEL, 0));
    }

}
