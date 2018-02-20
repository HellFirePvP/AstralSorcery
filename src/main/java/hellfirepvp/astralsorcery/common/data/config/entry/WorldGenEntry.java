/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
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
 * Class: WorldGenEntry
 * Created by HellFirePvP
 * Date: 30.03.2017 / 10:36
 */
public class WorldGenEntry extends ConfigEntry {

    private int generationChance;
    private boolean doGenerate = false;
    private boolean doIgnoreBiomeSpecifications = false;
    private BiomeDictionary.Type[] defaultBiomeTypes;
    private List<BiomeDictionary.Type> biomeTypes = new ArrayList<>();
    private int minY, maxY;

    private boolean loaded = false;

    public WorldGenEntry(String key, int defaultChance, boolean ignoreBiomeSpecifications, BiomeDictionary.Type... applicableTypes) {
        super(Section.WORLDGEN, key);
        this.doIgnoreBiomeSpecifications = ignoreBiomeSpecifications;
        this.generationChance = defaultChance;
        this.defaultBiomeTypes = applicableTypes;
        this.minY = 0;
        this.maxY = 255;
    }

    @Override
    public void loadFromConfig(Configuration cfg) {
        doGenerate = cfg.getBoolean("Generate", getConfigurationSection(), true, "Generate " + getKey());
        doIgnoreBiomeSpecifications = cfg.getBoolean("IgnoreBiomes", getConfigurationSection(), this.doIgnoreBiomeSpecifications, "Ignore Biome specifications when trying to generate " + getKey());
        generationChance = cfg.getInt("Chance", getConfigurationSection(), this.generationChance, 1, Integer.MAX_VALUE, "Chance to generate the structure in a chunk. The higher, the lower the chance.");
        minY = cfg.getInt("MinY" , getConfigurationSection(), this.minY, 0, 255, "Set the minimum Y level to spawn this structure on");
        maxY = cfg.getInt("MaxY" , getConfigurationSection(), this.maxY, 0, 255, "Set the maximum Y level to spawn this structure on");
        String[] strTypes = cfg.getStringList("BiomeTypes", getConfigurationSection(), getDefaultBiomeTypes(), "Set the BiomeTypes (according to the BiomeDicitionary) this structure will spawn in.");
        List<BiomeDictionary.Type> resolvedTypes = new LinkedList<>();
        for (String s : strTypes) {
            try {
                resolvedTypes.add(BiomeDictionary.Type.getType(s));
            } catch (Exception e) {
                AstralSorcery.log.error("[AstralSorcery] Could not find BiomeType by name '" + s + "' - Ignoring BiomeType specification for structure " + getKey());
            }
        }
        biomeTypes = Lists.newArrayList(resolvedTypes);
        loaded = true;
    }

    @Override
    public String getConfigurationSection() {
        return super.getConfigurationSection() + "." + getKey();
    }

    private String[] getDefaultBiomeTypes() {
        String[] def = new String[defaultBiomeTypes.length];
        for (int i = 0; i < defaultBiomeTypes.length; i++) {
            BiomeDictionary.Type t = defaultBiomeTypes[i];
            def[i] = t.getName();
        }
        return def;
    }

    public void setMinY(int minY) {
        if(loaded) return;
        this.minY = minY;
    }

    public void setMaxY(int maxY) {
        if(loaded) return;
        this.maxY = maxY;
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

    public boolean tryGenerate(Random random, double chanceMultiplier) {
        return random.nextInt(Math.max((int) Math.round(generationChance * chanceMultiplier), 1)) == 0;
    }

}
