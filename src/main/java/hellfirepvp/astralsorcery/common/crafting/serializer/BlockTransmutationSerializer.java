package hellfirepvp.astralsorcery.common.crafting.serializer;

import com.google.gson.JsonObject;
import hellfirepvp.astralsorcery.common.crafting.helper.CustomRecipeSerializer;
import hellfirepvp.astralsorcery.common.crafting.recipe.BlockTransmutation;
import hellfirepvp.astralsorcery.common.lib.RecipeSerializersAS;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockTransmutationSerializer
 * Created by HellFirePvP
 * Date: 10.10.2019 / 19:27
 */
public class BlockTransmutationSerializer extends CustomRecipeSerializer<BlockTransmutation> {

    public BlockTransmutationSerializer() {
        super(RecipeSerializersAS.BLOCK_TRANSMUTATION);
    }
    //TODO build serializer

    @Override
    public BlockTransmutation read(ResourceLocation recipeId, JsonObject json) {
        return null;
    }

    @Nullable
    @Override
    public BlockTransmutation read(ResourceLocation recipeId, PacketBuffer buffer) {
        return null;
    }

    @Override
    public void write(PacketBuffer buffer, BlockTransmutation recipe) {

    }
}
