package hellfirepvp.astralsorcery.common.constellation.effect;

import hellfirepvp.astralsorcery.common.constellation.Constellation;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.nbt.NBTUtils;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.HashMap;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CEffectPositionMap
 * Created by HellFirePvP
 * Date: 01.11.2016 / 01:24
 */
public abstract class CEffectPositionMap<K extends NBTBase, V extends NBTBase> extends CEffectPositionListGen<GenListEntries.PosDefinedTuple<K, V>> {

    public CEffectPositionMap(Constellation c, String cfgName, int searchRange, int maxCount, Verifier verifier) {
        super(c, cfgName, searchRange, maxCount, verifier, GenListEntries.PosDefinedTuple::new);
    }

}
