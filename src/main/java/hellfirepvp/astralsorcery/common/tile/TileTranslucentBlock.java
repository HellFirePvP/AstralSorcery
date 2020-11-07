/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.tile;

import hellfirepvp.astralsorcery.common.lib.TileEntityTypesAS;
import hellfirepvp.astralsorcery.common.tile.base.TileFakedState;
import net.minecraft.nbt.CompoundNBT;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TileTranslucentBlock
 * Created by HellFirePvP
 * Date: 28.11.2019 / 19:25
 */
public class TileTranslucentBlock extends TileFakedState {

    private UUID playerUUID = null;

    public TileTranslucentBlock() {
        super(TileEntityTypesAS.TRANSLUCENT_BLOCK);
    }

    @Nullable
    public UUID getPlayerUUID() {
        return playerUUID;
    }

    public void setPlayerUUID(UUID playerUUID) {
        this.playerUUID = playerUUID;
        this.markForUpdate();
    }

    @Override
    public void readCustomNBT(CompoundNBT compound) {
        super.readCustomNBT(compound);

        if (compound.hasUniqueId("playerUUID")) {
            this.playerUUID = compound.getUniqueId("playerUUID");
        } else {
            this.playerUUID = null;
        }
    }

    @Override
    public void writeCustomNBT(CompoundNBT compound) {
        super.writeCustomNBT(compound);

        if (this.playerUUID != null) {
            compound.putUniqueId("playerUUID", this.playerUUID);
        }
    }
}
