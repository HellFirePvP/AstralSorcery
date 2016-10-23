package hellfirepvp.astralsorcery.common.block;

import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: MaterialAirish
 * Created by HellFirePvP
 * Date: 22.10.2016 / 17:10
 */
public class MaterialAirish extends Material {

    public MaterialAirish() {
        super(MapColor.AIR);
        setNoPushMobility();
        setReplaceable();
        setImmovableMobility();
    }

    @Override
    public EnumPushReaction getMobilityFlag() {
        return EnumPushReaction.IGNORE;
    }
}
