/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.effect;

import hellfirepvp.astralsorcery.common.constellation.IWeakConstellation;
import hellfirepvp.astralsorcery.common.tile.TileRitualPedestal;
import hellfirepvp.astralsorcery.common.util.ILocatable;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CEffectPositionList
 * Created by HellFirePvP
 * Date: 17.10.2016 / 09:33
 */
public abstract class CEffectPositionList extends CEffectPositionListGen<GenListEntries.SimpleBlockPosEntry> {

    public CEffectPositionList(@Nullable ILocatable origin, IWeakConstellation c, String cfgName, int searchRange, int maxCount, Verifier verifier) {
        super(origin, c, cfgName, searchRange, maxCount, verifier, GenListEntries.SimpleBlockPosEntry::new);
    }

    public boolean offerNewBlockPos(BlockPos pos) {
        return offerNewElement(new GenListEntries.SimpleBlockPosEntry(pos));
    }

    @Nullable
    public BlockPos getRandomPosition() {
        GenListEntries.SimpleBlockPosEntry entry = getRandomElementByChance(rand);
        if(entry != null) {
            return entry.getPos();
        }
        return null;
    }

}
