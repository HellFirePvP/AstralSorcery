/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.world.structure;

import hellfirepvp.astralsorcery.common.data.world.WorldCacheManager;
import hellfirepvp.astralsorcery.common.data.world.data.StructureGenBuffer;
import hellfirepvp.astralsorcery.common.util.struct.StructureBlockArray;
import hellfirepvp.astralsorcery.common.world.WorldGenAttributeCommon;
import net.minecraft.world.World;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.config.Configuration;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: WorldGenAttributeStructure
 * Created by HellFirePvP
 * Date: 30.03.2017 / 10:51
 */
public abstract class WorldGenAttributeStructure extends WorldGenAttributeCommon {

    protected final StructureGenBuffer.StructureType type;
    protected float idealDistance = 256F;
    private final StructureQuery query;

    public WorldGenAttributeStructure(int attributeVersion, String entry, StructureQuery query, StructureGenBuffer.StructureType type, BiomeDictionary.Type... types) {
        super(attributeVersion, true, entry, types);
        this.query = query;
        this.type = type;
    }

    public StructureGenBuffer.StructureType getStructureType() {
        return type;
    }

    public float getIdealDistance() {
        return idealDistance;
    }

    public StructureBlockArray getStructureTemplate() {
        return query.getStructure();
    }

    protected StructureGenBuffer getBuffer(World world) {
        return WorldCacheManager.getOrLoadData(world, WorldCacheManager.SaveKey.STRUCTURE_GEN);
    }

    @Override
    protected void loadAdditionalConfigEntries(Configuration cfg) {
        super.loadAdditionalConfigEntries(cfg);

        idealDistance = cfg.getFloat("idealDistance", cfgEntry.getConfigurationSection(), idealDistance, 1F, 16384F,
                "Sets the 'ideal' distance between 2 structures of the same type. If the distance is lower, it's unlikely that the same type of structure will spawn," +
                        "if it's higher it's more likely that this type of structure will spawn. Only has influence if the config option 'respectIdealStructureDistances' is enabled.");
    }

}
