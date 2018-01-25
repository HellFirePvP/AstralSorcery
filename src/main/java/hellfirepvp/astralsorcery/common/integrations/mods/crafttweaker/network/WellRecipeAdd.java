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
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

import java.awt.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: WellRecipeAdd
 * Created by HellFirePvP
 * Date: 28.02.2017 / 00:09
 */
public class WellRecipeAdd implements SerializeableRecipe {

    private ItemStack inStack;
    private Fluid fluidOut;
    private float productionMultiplier, shatterMultiplier;
    private int colorHex;

    WellRecipeAdd() {}

    public WellRecipeAdd(ItemStack inStack, Fluid fluidOut, float productionMultiplier, float shatterMultiplier, int colorHex) {
        this.inStack = inStack;
        this.fluidOut = fluidOut;
        this.productionMultiplier = productionMultiplier;
        this.shatterMultiplier = shatterMultiplier;
        this.colorHex = colorHex;
    }

    @Override
    public CraftingType getType() {
        return CraftingType.WELL_ADD;
    }

    @Override
    public void read(ByteBuf buf) {
        this.inStack = ByteBufUtils.readItemStack(buf);
        this.fluidOut = FluidRegistry.getFluid(ByteBufUtils.readString(buf));
        this.productionMultiplier = buf.readFloat();
        this.shatterMultiplier = buf.readFloat();
        this.colorHex = buf.readInt();
    }

    @Override
    public void write(ByteBuf buf) {
        ByteBufUtils.writeItemStack(buf, this.inStack);
        ByteBufUtils.writeString(buf, this.fluidOut.getName());
        buf.writeFloat(this.productionMultiplier);
        buf.writeFloat(this.shatterMultiplier);
        buf.writeInt(this.colorHex);
    }

    @Override
    public void applyRecipe() {
        CraftingAccessManager.addMTLiquefaction(this.inStack, this.fluidOut, this.productionMultiplier, this.shatterMultiplier, new Color(this.colorHex));
    }

}
