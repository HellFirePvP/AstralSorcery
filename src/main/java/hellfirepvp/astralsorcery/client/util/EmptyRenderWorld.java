/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.util;

import hellfirepvp.observerlib.api.client.StructureRenderLightManager;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.FluidState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IBlockDisplayReader;
import net.minecraft.world.LightType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.lighting.WorldLightManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.function.Supplier;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EmptyRenderWorld
 * Created by HellFirePvP
 * Date: 18.07.2019 / 16:35
 */
public class EmptyRenderWorld implements IBlockDisplayReader {

    private final Biome biome;

    public EmptyRenderWorld(Supplier<Biome> biomeSupplier) {
        this.biome = biomeSupplier.get();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public float func_230487_a_(Direction direction, boolean b) {
        return 1.0F;
    }

    @Override
    public WorldLightManager getLightManager() {
        return new StructureRenderLightManager(this.getMaxLightLevel());
    }

    @Override
    public int getBlockColor(BlockPos blockPosIn, ColorResolver colorResolverIn) {
        return colorResolverIn.getColor(biome, blockPosIn.getX(), blockPosIn.getZ());
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
    public FluidState getFluidState(BlockPos blockPos) {
        return Fluids.EMPTY.getDefaultState();
    }
}
