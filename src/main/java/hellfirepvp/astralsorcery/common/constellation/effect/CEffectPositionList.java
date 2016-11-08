package hellfirepvp.astralsorcery.common.constellation.effect;

import hellfirepvp.astralsorcery.common.constellation.Constellation;
import hellfirepvp.astralsorcery.common.constellation.effect.ConstellationEffect;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.nbt.NBTUtils;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.LinkedList;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CEffectPositionList
 * Created by HellFirePvP
 * Date: 17.10.2016 / 09:33
 */
public abstract class CEffectPositionList extends CEffectPositionListGen<GenListEntries.SimpleBlockPosEntry> {

    public CEffectPositionList(Constellation c, String cfgName, int searchRange, int maxCount, Verifier verifier) {
        super(c, cfgName, searchRange, maxCount, verifier, GenListEntries.SimpleBlockPosEntry::new);
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
