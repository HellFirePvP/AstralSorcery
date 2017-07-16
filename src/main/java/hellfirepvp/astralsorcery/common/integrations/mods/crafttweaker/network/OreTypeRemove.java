/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.integrations.mods.crafttweaker.network;

import hellfirepvp.astralsorcery.common.base.OreTypes;
import hellfirepvp.astralsorcery.common.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: OreTypeRemove
 * Created by HellFirePvP
 * Date: 27.02.2017 / 11:27
 */
public class OreTypeRemove implements SerializeableRecipe {

    private String oreDictName;

    OreTypeRemove() {}

    public OreTypeRemove(String oreDictName) {
        this.oreDictName = oreDictName;
    }

    @Override
    public CraftingType getType() {
        return CraftingType.ORETYPE_REMOVE;
    }

    @Override
    public void read(ByteBuf buf) {
        this.oreDictName = ByteBufUtils.readString(buf);
    }

    @Override
    public void write(ByteBuf buf) {
        ByteBufUtils.writeString(buf, this.oreDictName);
    }

    @Override
    public void applyRecipe() {
        OreTypes.removeOreEntry(oreDictName);
    }

}
