package hellfirepvp.astralsorcery.common.crafting.nojson.fountain;

import net.minecraft.nbt.CompoundNBT;

import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: VortexContext
 * Created by HellFirePvP
 * Date: 01.11.2020 / 13:35
 */
public class VortexContext extends FountainEffect.EffectContext {

    public Object fountainSprite, facingVortexPlane;
    public List<Object> ctrlEffectNoise = null;

    @Override
    public void readFromNBT(CompoundNBT compound) {}

    @Override
    public void writeToNBT(CompoundNBT compound) {}
}
