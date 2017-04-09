/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.world.structure;

import hellfirepvp.astralsorcery.common.util.struct.StructureBlockArray;
import hellfirepvp.astralsorcery.common.world.WorldGenAttributeCommon;
import net.minecraftforge.common.BiomeDictionary;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: WorldGenAttributeStructure
 * Created by HellFirePvP
 * Date: 30.03.2017 / 10:51
 */
public abstract class WorldGenAttributeStructure extends WorldGenAttributeCommon {

    private StructureQuery query;

    public WorldGenAttributeStructure(int attributeVersion, String entry, StructureQuery query, BiomeDictionary.Type... types) {
        super(attributeVersion, true, entry, types);
        this.query = query;
    }

    public StructureBlockArray getStructureTemplate() {
        return query.getStructure();
    }

}
