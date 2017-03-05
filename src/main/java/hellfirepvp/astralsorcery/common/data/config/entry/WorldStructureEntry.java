/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.data.config.entry;

import com.google.common.collect.Lists;
import hellfirepvp.astralsorcery.AstralSorcery;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.config.Configuration;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
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
    private boolean doIgnoreBiomeSpecifications = false;
    private BiomeDictionary.Type[] defaultBiomeTypes;
    private List<BiomeDictionary.Type> biomeTypes = new ArrayList<>();
    private int minY, maxY;

    public WorldStructureEntry(String key, BiomeDictionary.Type[] defaultBiomeTypes) {
        super(Section.WORLDGEN, key);
        this.defaultBiomeTypes = defaultBiomeTypes;
        this.minY = 0;
        this.maxY = 255;
    }

    @Override
    public void loadFromConfig(Configuration cfg) {
        doGenerate = cfg.getBoolean("Generate", getConfigurationSection(), true, "Generate " + getKey());
        doIgnoreBiomeSpecifications = cfg.getBoolean("IgnoreBiomes", getConfigurationSection(), false, "Ignore Biome specifications when trying to generate " + getKey());
        generationChance = cfg.getInt("Chance", getConfigurationSection(), 140, 1, Integer.MAX_VALUE, "Chance to generate the structure in a chunk. The higher, the lower the chance.");
        minY = cfg.getInt("MinY" , getConfigurationSection(), 0, 0, 255, "Set the minimum Y level to spawn this structure on");
        maxY = cfg.getInt("MaxY" , getConfigurationSection(), 255, 0, 255, "Set the maximum Y level to spawn this structure on");
        String[] strTypes = cfg.getStringList("BiomeTypes", getConfigurationSection(), getDefaultBiomeTypes(), "Set the BiomeTypes (according to the BiomeDicitionary) this structure will spawn in.");
        List<BiomeDictionary.Type> resolvedTypes = new LinkedList<>();
        for (String s : strTypes) {
            try {
                resolvedTypes.add(BiomeDictionary.Type.valueOf(s));
            } catch (Exception e) {
                AstralSorcery.log.error("Could not find BiomeType by name '" + s + "' - Ignoring BiomeType specification for structure " + getKey());
            }
        }
        biomeTypes = Lists.newArrayList(resolvedTypes);
    }

    @Override
    public String getConfigurationSection() {
        return super.getConfigurationSection() + "." + getKey();
    }

    private String[] getDefaultBiomeTypes() {
        String[] def = new String[defaultBiomeTypes.length];
        for (int i = 0; i < defaultBiomeTypes.length; i++) {
            BiomeDictionary.Type t = defaultBiomeTypes[i];
            def[i] = t.name();
        }
        return def;
    }

    public int getMinY() {
        return minY;
    }

    public int getMaxY() {
        return maxY;
    }

    public List<BiomeDictionary.Type> getTypes() {
        return biomeTypes;
    }

    public boolean shouldGenerate() {
        return doGenerate;
    }

    public boolean shouldIgnoreBiomeSpecifications() {
        return doIgnoreBiomeSpecifications;
    }

    public boolean tryGenerate(Random random) {
        return random.nextInt(generationChance) == 0;
    }
}
