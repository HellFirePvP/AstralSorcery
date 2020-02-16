/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 *  All rights reserved.
 *  The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 *  For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.tile;

import hellfirepvp.astralsorcery.common.lib.TileEntityTypesAS;
import hellfirepvp.astralsorcery.common.tile.base.TileEntitySynchronized;
import hellfirepvp.astralsorcery.common.util.tile.NamedInventoryTile;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TileTelescope
 * Created by HellFirePvP
 * Date: 15.01.2020 / 15:37
 */
public class TileTelescope extends TileEntitySynchronized implements NamedInventoryTile {

    private TelescopeRotation rotation = TelescopeRotation.N;

    public TileTelescope() {
        super(TileEntityTypesAS.TELESCOPE);
    }

    public TelescopeRotation getRotation() {
        return rotation;
    }

    public void setRotation(TelescopeRotation rotation) {
        this.rotation = rotation;
        markForUpdate();
    }

    @Override
    public void readCustomNBT(CompoundNBT compound) {
        super.readCustomNBT(compound);

        this.rotation = TelescopeRotation.values()[compound.getInt("rotation")];
    }

    @Override
    public void writeCustomNBT(CompoundNBT compound) {
        super.writeCustomNBT(compound);

        compound.putInt("rotation", rotation.ordinal());
    }

    @Override
    public ITextComponent getDisplayName() {
        return new TranslationTextComponent("screen.astralsorcery.telescope");
    }

    public static enum TelescopeRotation {

        N,
        N_E,
        E,
        S_E,
        S,
        S_W,
        W,
        N_W;

        public TelescopeRotation nextClockWise() {
            return values()[(ordinal() + 1) % values().length];
        }

        public TelescopeRotation nextCounterClockWise() {
            return values()[(ordinal() + 7) % values().length];
        }

    }
}
