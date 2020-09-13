/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.tile.base;

import hellfirepvp.astralsorcery.common.util.nbt.NBTHelper;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nonnull;
import java.awt.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TileFakedState
 * Created by HellFirePvP
 * Date: 04.09.2020 / 19:19
 */
public abstract class TileFakedState extends TileEntityTick {

    private BlockState fakedState = Blocks.AIR.getDefaultState();
    private Color overlayColor = Color.WHITE;

    protected TileFakedState(TileEntityType<?> tileEntityTypeIn) {
        super(tileEntityTypeIn);
    }

    public boolean revert() {
        if (this.getWorld().isRemote()) {
            return false;
        }
        return this.getWorld().setBlockState(this.getPos(), this.getFakedState(), Constants.BlockFlags.DEFAULT_AND_RERENDER);
    }

    @Nonnull
    public BlockState getFakedState() {
        return fakedState;
    }

    @Nonnull
    public Color getOverlayColor() {
        return overlayColor;
    }

    public void setFakedState(@Nonnull BlockState fakedState) {
        this.fakedState = fakedState;
        this.markForUpdate();
    }

    public void setOverlayColor(@Nonnull Color overlayColor) {
        this.overlayColor = overlayColor;
        this.markForUpdate();
    }

    @Override
    public void readCustomNBT(CompoundNBT compound) {
        super.readCustomNBT(compound);

        this.fakedState = NBTHelper.getBlockStateFromTag(compound.getCompound("fakedState"), Blocks.AIR.getDefaultState());
        this.overlayColor = new Color(compound.getInt("color"), false);
    }

    @Override
    public void writeCustomNBT(CompoundNBT compound) {
        super.writeCustomNBT(compound);

        NBTHelper.setBlockState(compound, "fakedState", this.fakedState);
        compound.putInt("color", this.overlayColor.getRGB());
    }

    @OnlyIn(Dist.CLIENT)
    public double getMaxRenderDistanceSquared() {
        return 65536.0D;
    }
}
