package hellfirepvp.astralsorcery.common.tile;

import hellfirepvp.astralsorcery.common.lib.StructureTypesAS;
import hellfirepvp.astralsorcery.common.lib.TileEntityTypesAS;
import hellfirepvp.astralsorcery.common.structure.types.StructureType;
import hellfirepvp.astralsorcery.common.tile.base.TileEntityTick;
import hellfirepvp.astralsorcery.common.tile.base.TileRequiresMultiblock;

import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TileFountain
 * Created by HellFirePvP
 * Date: 29.10.2020 / 19:56
 */
public class TileFountain extends TileEntityTick {

    public TileFountain() {
        super(TileEntityTypesAS.FOUNTAIN);
    }

    @Override
    public void tick() {
        super.tick();

        if (!getWorld().isRemote()) {
            this.hasMultiblock();
        }
    }

    @Nullable
    @Override
    public StructureType getRequiredStructureType() {
        return StructureTypesAS.PTYPE_FOUNTAIN;
    }
}
