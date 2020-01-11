/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.util;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IEnviromentBlockReader;
import net.minecraft.world.LightType;
import net.minecraft.world.biome.Biome;

import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EmptyRenderWorld
 * Created by HellFirePvP
 * Date: 18.07.2019 / 16:35
 */
public class EmptyRenderWorld implements IEnviromentBlockReader {

    private final Biome biome;

    public EmptyRenderWorld(Biome biome) {
        this.biome = biome;
    }

    @Override
    public Biome getBiome(BlockPos blockPos) {
        return this.biome;
    }

    @Override
    public int getLightFor(LightType lightType, BlockPos blockPos) {
        return this.getMaxLightLevel();
    }

    @Nullable
    @Override
    public TileEntity getTileEntity(BlockPos blockPos) {
        return null;
    }

    @Override
    public BlockState getBlockState(BlockPos blockPos) {
        return Blocks.AIR.getDefaultState();
    }

    @Override
    public IFluidState getFluidState(BlockPos blockPos) {
        return Fluids.EMPTY.getDefaultState();
    }
}
