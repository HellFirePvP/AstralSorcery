/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.integrations.mods.crafttweaker.network;

import hellfirepvp.astralsorcery.common.crafting.helper.CraftingAccessManager;
import hellfirepvp.astralsorcery.common.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: LightTransmutationAdd
 * Created by HellFirePvP
 * Date: 27.02.2017 / 12:02
 */
public class LightTransmutationAdd implements SerializeableRecipe {

    private ItemStack in, out;
    private double cost;

    LightTransmutationAdd() {}

    public LightTransmutationAdd(ItemStack in, ItemStack out, double cost) {
        this.in = in;
        this.out = out;
        this.cost = cost;
    }

    @Override
    public CraftingType getType() {
        return CraftingType.TRANSMUTE_ADD;
    }

    @Override
    public void read(ByteBuf buf) {
        this.in = ByteBufUtils.readItemStack(buf);
        this.out = ByteBufUtils.readItemStack(buf);
        this.cost = buf.readDouble();
    }

    @Override
    public void write(ByteBuf buf) {
        ByteBufUtils.writeItemStack(buf, this.in);
        ByteBufUtils.writeItemStack(buf, this.out);
        buf.writeDouble(this.cost);
    }

    @Override
    public void applyRecipe() {
        CraftingAccessManager.addMTTransmutation(this.in, this.out, this.cost);
    }

}
