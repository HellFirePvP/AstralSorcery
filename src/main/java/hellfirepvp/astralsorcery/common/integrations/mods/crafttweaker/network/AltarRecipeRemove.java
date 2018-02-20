/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.integrations.mods.crafttweaker.network;

import hellfirepvp.astralsorcery.common.crafting.helper.CraftingAccessManager;
import hellfirepvp.astralsorcery.common.tile.TileAltar;
import hellfirepvp.astralsorcery.common.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AltarRecipeRemove
 * Created by HellFirePvP
 * Date: 27.02.2017 / 14:04
 */
public class AltarRecipeRemove implements SerializeableRecipe {

    private ItemStack matchOutRemove;
    private TileAltar.AltarLevel level;

    AltarRecipeRemove() {}

    public AltarRecipeRemove(ItemStack matchOutRemove, TileAltar.AltarLevel altarLevel) {
        this.matchOutRemove = matchOutRemove;
        this.level = altarLevel;
    }

    @Override
    public CraftingType getType() {
        return CraftingType.ALTAR_REMOVE;
    }

    @Override
    public void read(ByteBuf buf) {
        this.matchOutRemove = ByteBufUtils.readItemStack(buf);
        this.level = TileAltar.AltarLevel.values()[buf.readInt()];
    }

    @Override
    public void write(ByteBuf buf) {
        ByteBufUtils.writeItemStack(buf, this.matchOutRemove);
        buf.writeInt(this.level.ordinal());
    }

    @Override
    public void applyRecipe() {
        CraftingAccessManager.tryRemoveAltarRecipeByOutputAndLevel(this.matchOutRemove, this.level);
    }

}
