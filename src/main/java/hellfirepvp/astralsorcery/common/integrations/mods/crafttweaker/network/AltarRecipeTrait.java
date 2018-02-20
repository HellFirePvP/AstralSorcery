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
import hellfirepvp.astralsorcery.common.crafting.ItemHandle;
import hellfirepvp.astralsorcery.common.crafting.altar.AbstractAltarRecipe;
import hellfirepvp.astralsorcery.common.crafting.altar.recipes.TraitRecipe;
import hellfirepvp.astralsorcery.common.crafting.helper.CraftingAccessManager;
import hellfirepvp.astralsorcery.common.tile.TileAltar;
import hellfirepvp.astralsorcery.common.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AltarRecipeTrait
 * Created by HellFirePvP
 * Date: 24.07.2017 / 19:46
 */
public class AltarRecipeTrait extends BaseAltarRecipe {

    @Nullable
    private IConstellation focusRequiredConstellation;

    AltarRecipeTrait() {
        super(null, null, 0, 0);
        this.focusRequiredConstellation = null;
    }

    public AltarRecipeTrait(ItemHandle[] inputs, ItemStack output, int starlightRequired, int craftingTickTime, @Nullable IConstellation focus) {
        super(inputs, output, starlightRequired, craftingTickTime);
        this.focusRequiredConstellation = focus;
    }

    @Override
    public CraftingType getType() {
        return CraftingType.ALTAR_T4_ADD;
    }

    @Override
    public void read(ByteBuf buf) {
        super.read(buf);
        if(buf.readBoolean()) {
            this.focusRequiredConstellation = ConstellationRegistry.getConstellationByName(ByteBufUtils.readString(buf));
        } else {
            this.focusRequiredConstellation = null;
        }
    }

    @Override
    public void write(ByteBuf buf) {
        super.write(buf);
        buf.writeBoolean(this.focusRequiredConstellation == null);
        if(this.focusRequiredConstellation != null) {
            ByteBufUtils.writeString(buf, this.focusRequiredConstellation.getUnlocalizedName());
        }
    }

    @Override
    public void applyRecipe() {
        AbstractAltarRecipe aar = buildRecipeUnsafe(
                TileAltar.AltarLevel.TRAIT_CRAFT,
                this.starlightRequired,
                this.craftingTickTime,
                this.output,
                this.inputs);
        if(aar instanceof TraitRecipe && focusRequiredConstellation != null) {
            ((TraitRecipe) aar).setRequiredConstellation(focusRequiredConstellation);
        }
        CraftingAccessManager.registerMTAltarRecipe(aar);
    }

}
