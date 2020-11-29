/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crafting.recipe.interaction;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import hellfirepvp.astralsorcery.common.util.data.ByteBufUtils;
import hellfirepvp.astralsorcery.common.util.data.JsonHelper;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.astralsorcery.common.util.item.ItemUtils;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ResultDropItem
 * Created by HellFirePvP
 * Date: 31.10.2020 / 14:03
 */
public class ResultDropItem extends InteractionResult {

    private ItemStack output = ItemStack.EMPTY;

    ResultDropItem() {
        super(InteractionResultRegistry.ID_DROP_ITEM);
    }

    public static ResultDropItem dropItem(ItemStack output) {
        ResultDropItem drop = new ResultDropItem();
        drop.output = output.copy();
        return drop;
    }

    public ItemStack getOutput() {
        return output.copy();
    }

    @Override
    public void doResult(World world, Vector3 at) {
        ItemUtils.dropItemNaturally(world, at.getX(), at.getY(), at.getZ(), this.output.copy());
    }

    @Override
    public void read(JsonObject json) throws JsonParseException {
        this.output = JsonHelper.getItemStack(json, "output");
        if (this.output.isEmpty()) {
            throw new JsonParseException("Output stack must not be empty!");
        }
    }

    @Override
    public void write(JsonObject json) {
        json.add("output", JsonHelper.serializeItemStack(this.output));
    }

    @Override
    public void read(PacketBuffer buf) {
        this.output = ByteBufUtils.readItemStack(buf);
    }

    @Override
    public void write(PacketBuffer buf) {
        ByteBufUtils.writeItemStack(buf, this.output);
    }
}
