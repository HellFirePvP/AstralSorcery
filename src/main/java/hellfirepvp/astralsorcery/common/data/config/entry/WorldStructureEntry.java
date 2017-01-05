/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.data.config.entry;

import net.minecraftforge.common.config.Configuration;

import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: WorldStructureEntry
 * Created by HellFirePvP
 * Date: 21.10.2016 / 13:13
 */
public class WorldStructureEntry extends ConfigEntry {

    private int generationChance = Integer.MAX_VALUE;
    private boolean doGenerate = false;

    public WorldStructureEntry(String key) {
        super(Section.WORLDGEN, key);
    }

    @Override
    public void loadFromConfig(Configuration cfg) {
        doGenerate = cfg.getBoolean(getKey(), getConfigurationSection(), true, "Generate " + getKey());
        generationChance = cfg.getInt(getKey() + "Chance", getConfigurationSection(), 140, 1, Integer.MAX_VALUE, "Chance to generate the structure in a chunk. The higher, the lower the chance.");
    }

    public boolean shouldGenerate() {
        return doGenerate;
    }

    public boolean tryGenerate(Random random) {
        return random.nextInt(generationChance) == 0;
    }
}
