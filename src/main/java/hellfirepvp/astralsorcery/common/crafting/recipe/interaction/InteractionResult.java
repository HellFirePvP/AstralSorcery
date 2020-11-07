package hellfirepvp.astralsorcery.common.crafting.recipe.interaction;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import hellfirepvp.astralsorcery.common.crafting.recipe.LiquidInteraction;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: InteractionResult
 * Created by HellFirePvP
 * Date: 31.10.2020 / 13:56
 */
public abstract class InteractionResult {

    private final ResourceLocation id;

    protected InteractionResult(ResourceLocation id) {
        this.id = id;
    }

    public final ResourceLocation getId() {
        return id;
    }

    public abstract void doResult(World world, Vector3 at);

    public abstract void read(JsonObject json) throws JsonParseException;

    public abstract void write(JsonObject json);

    public abstract void read(PacketBuffer buf);

    public abstract void write(PacketBuffer buf);
}
