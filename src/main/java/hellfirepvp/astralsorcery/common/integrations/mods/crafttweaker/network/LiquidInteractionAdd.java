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
import net.minecraftforge.fluids.FluidStack;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: LiquidInteractionAdd
 * Created by HellFirePvP
 * Date: 20.04.2018 / 09:56
 */
public class LiquidInteractionAdd implements SerializeableRecipe {

    private FluidStack comp1, comp2;
    private float chance1, chance2;
    private ItemStack output;
    private int weight;

    LiquidInteractionAdd() {}

    public LiquidInteractionAdd(FluidStack comp1, FluidStack comp2, float chance1, float chance2, ItemStack output, int weight) {
        this.comp1 = comp1;
        this.comp2 = comp2;
        this.chance1 = chance1;
        this.chance2 = chance2;
        this.output = output;
        this.weight = weight;
    }

    @Override
    public CraftingType getType() {
        return CraftingType.LIQINTERACTION_ADD;
    }

    @Override
    public void read(ByteBuf buf) {
        this.comp1 = ByteBufUtils.readFluidStack(buf);
        this.comp2 = ByteBufUtils.readFluidStack(buf);
        this.chance1 = buf.readFloat();
        this.chance2 = buf.readFloat();
        this.output = ByteBufUtils.readItemStack(buf);
        this.weight = buf.readInt();
    }

    @Override
    public void write(ByteBuf buf) {
        ByteBufUtils.writeFluidStack(buf, this.comp1);
        ByteBufUtils.writeFluidStack(buf, this.comp2);
        buf.writeFloat(this.chance1);
        buf.writeFloat(this.chance2);
        ByteBufUtils.writeItemStack(buf, this.output);
        buf.writeInt(this.weight);
    }

    @Override
    public void applyRecipe() {
        CraftingAccessManager.addLiquidInteraction(this.weight, this.comp1, this.comp2, this.chance1, this.chance2, this.output);
    }

}
