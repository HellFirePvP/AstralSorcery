/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.integrations.mods.crafttweaker.network;

import hellfirepvp.astralsorcery.common.constellation.ConstellationRegistry;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.constellation.IWeakConstellation;
import hellfirepvp.astralsorcery.common.crafting.helper.CraftingAccessManager;
import hellfirepvp.astralsorcery.common.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;

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
    private IWeakConstellation cst;

    LightTransmutationAdd() {}

    public LightTransmutationAdd(ItemStack in, ItemStack out, double cost, @Nullable IWeakConstellation cst) {
        this.in = in;
        this.out = out;
        this.cost = cost;
        this.cst = cst;
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
        if (buf.readBoolean()) {
            IConstellation c = ConstellationRegistry.getConstellationByName(ByteBufUtils.readString(buf));
            this.cst = c instanceof IWeakConstellation ? (IWeakConstellation) c : null;
        }
    }

    @Override
    public void write(ByteBuf buf) {
        ByteBufUtils.writeItemStack(buf, this.in);
        ByteBufUtils.writeItemStack(buf, this.out);
        buf.writeDouble(this.cost);
        buf.writeBoolean(this.cst != null);
        if (this.cst != null) {
            ByteBufUtils.writeString(buf, this.cst.getUnlocalizedName());
        }
    }

    @Override
    public void applyRecipe() {
        CraftingAccessManager.addMTTransmutation(this.in, this.out, this.cost, this.cst);
    }

}
