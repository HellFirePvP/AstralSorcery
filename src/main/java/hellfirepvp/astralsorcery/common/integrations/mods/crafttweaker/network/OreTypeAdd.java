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
 * Class: OreTypeAdd
 * Created by HellFirePvP
 * Date: 27.02.2017 / 11:09
 */
public class OreTypeAdd implements SerializeableRecipe {

    private String oreTypeToAdd;
    private double weight;

    OreTypeAdd() {}

    public OreTypeAdd(String oreTypeToAdd, double weight) {
        this.oreTypeToAdd = oreTypeToAdd;
        this.weight = weight;
    }

    @Override
    public CraftingType getType() {
        return CraftingType.ORETPYE_ADD;
    }

    @Override
    public void read(ByteBuf buf) {
        this.oreTypeToAdd = ByteBufUtils.readString(buf);
        this.weight = buf.readDouble();
    }

    @Override
    public void write(ByteBuf buf) {
        ByteBufUtils.writeString(buf, this.oreTypeToAdd);
        buf.writeDouble(this.weight);
    }

    @Override
    public void applyRecipe() {
        OreTypes.registerOreEntry(oreTypeToAdd, weight);
    }

}
