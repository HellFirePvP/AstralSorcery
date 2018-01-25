/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.world;

import net.minecraft.world.World;

import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: WorldGenAttribute
 * Created by HellFirePvP
 * Date: 12.01.2017 / 21:41
 */
public abstract class WorldGenAttribute {

    public final int attributeVersion;

    public WorldGenAttribute(int attributeVersion) {
        this.attributeVersion = attributeVersion;
    }

    public abstract void generate(Random random, int chunkX, int chunkZ, World world);

}
