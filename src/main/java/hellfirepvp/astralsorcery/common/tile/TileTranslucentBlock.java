/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.tile;

import hellfirepvp.astralsorcery.common.lib.TileEntityTypesAS;
import hellfirepvp.astralsorcery.common.tile.base.TileEntityTick;
import hellfirepvp.astralsorcery.common.util.nbt.NBTHelper;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.DyeColor;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;
import java.util.function.Predicate;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TileTranslucentBlock
 * Created by HellFirePvP
 * Date: 28.11.2019 / 19:25
 */
public class TileTranslucentBlock extends TileEntityTick {

    private BlockState fakedState = Blocks.AIR.getDefaultState();
    private DyeColor overlayColor = DyeColor.WHITE;
    private UUID playerUUID = null;

    private Predicate<TileTranslucentBlock> persistenceFunction = (tile) -> true;

    public TileTranslucentBlock() {
        super(TileEntityTypesAS.TRANSLUCENT_BLOCK);
    }

    @Override
    public boolean hasFastRenderer() {
        return true;
    }

    @Override
    public void tick() {
        super.tick();

        if (!persistenceFunction.test(this)) {
            this.getWorld().setBlockState(this.getPos(), this.getFakedState(), Constants.BlockFlags.DEFAULT_AND_RERENDER);
        }
    }

    @Nonnull
    public BlockState getFakedState() {
        return fakedState;
    }

    @Nonnull
    public DyeColor getOverlayColor() {
        return overlayColor;
    }

    @Nullable
    public UUID getPlayerUUID() {
        return playerUUID;
    }

    public void setFakedState(BlockState fakedState) {
        if (fakedState == null) {
            this.fakedState = Blocks.AIR.getDefaultState();
        } else {
            this.fakedState = fakedState;
        }
        this.markForUpdate();
    }

    public void setOverlayColor(DyeColor overlayColor) {
        if (overlayColor == null) {
            this.overlayColor = DyeColor.WHITE;
        } else {
            this.overlayColor = overlayColor;
        }
        this.markForUpdate();
    }

    public void setPlayerUUID(UUID playerUUID) {
        this.playerUUID = playerUUID;
        this.markForUpdate();
    }

    public void setPersistenceFunction(Predicate<TileTranslucentBlock> persistenceFunction) {
        if (persistenceFunction != null) {
            this.persistenceFunction = persistenceFunction;
        }
    }

    @Override
    public void readCustomNBT(CompoundNBT compound) {
        super.readCustomNBT(compound);

        this.fakedState = NBTHelper.getBlockStateFromTag(compound.getCompound("fakedState"), Blocks.AIR.getDefaultState());
        this.overlayColor = DyeColor.byId(compound.getInt("overlayColor"));

        if (compound.hasUniqueId("playerUUID")) {
            this.playerUUID = compound.getUniqueId("playerUUID");
        } else {
            this.playerUUID = null;
        }
    }

    @Override
    public void writeCustomNBT(CompoundNBT compound) {
        super.writeCustomNBT(compound);

        NBTHelper.setBlockState(compound, "fakedState", this.fakedState);
        compound.putInt("overlayColor", this.overlayColor.getId());

        if (this.playerUUID != null) {
            compound.putUniqueId("playerUUID", this.playerUUID);
        }
    }
}
