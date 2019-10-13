package hellfirepvp.astralsorcery.common.crafting.serializer;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.constellation.IWeakConstellation;
import hellfirepvp.astralsorcery.common.crafting.helper.CustomRecipeSerializer;
import hellfirepvp.astralsorcery.common.crafting.recipe.BlockTransmutation;
import hellfirepvp.astralsorcery.common.lib.RecipeSerializersAS;
import hellfirepvp.astralsorcery.common.lib.RegistriesAS;
import hellfirepvp.astralsorcery.common.util.block.BlockMatchInformation;
import hellfirepvp.astralsorcery.common.util.block.BlockStateHelper;
import hellfirepvp.astralsorcery.common.util.data.ByteBufUtils;
import hellfirepvp.astralsorcery.common.util.data.JsonHelper;
import hellfirepvp.astralsorcery.common.util.object.PredicateBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

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

    @Override
    public BlockTransmutation read(ResourceLocation recipeId, JsonObject json) {
        List<BlockMatchInformation> matchInformation = new ArrayList<>();
        JsonHelper.parseMultipleStrings(json, "input", str -> matchInformation.add(BlockStateHelper.deserializeMatcher(str)));
        BlockState output = BlockStateHelper.deserialize(JSONUtils.getString(json, "output"));
        float starlight = JSONUtils.getFloat(json, "starlight");
        IWeakConstellation matchConstellation = null;
        if (json.has("constellation")) {
            ResourceLocation cstKey = new ResourceLocation(JSONUtils.getString(json, "constellation"));
            IConstellation cst = RegistriesAS.REGISTRY_CONSTELLATIONS.getValue(cstKey);
            if (cst == null) {
                throw new JsonSyntaxException(String.format("Unknown constellation %s!", cstKey.toString()));
            }
            if (!(cst instanceof IWeakConstellation)) {
                throw new JsonSyntaxException(String.format("Constellation %s has to be either a major or dim constellation!", cstKey.toString()));
            }
            matchConstellation = (IWeakConstellation) cst;
        }
        return new BlockTransmutation(recipeId, matchInformation, output, starlight, matchConstellation);
    }

    @Nullable
    @Override
    public BlockTransmutation read(ResourceLocation recipeId, PacketBuffer buffer) {
        List<BlockMatchInformation> matchInformation = ByteBufUtils.readList(buffer,
                buf -> new BlockMatchInformation(ByteBufUtils.readBlockState(buf), buf.readBoolean()));
        BlockState output = ByteBufUtils.readBlockState(buffer);
        double starlight = buffer.readDouble();
        IWeakConstellation cst = ByteBufUtils.readOptional(buffer, ByteBufUtils::readRegistryEntry);
        return new BlockTransmutation(recipeId, matchInformation, output, starlight, cst);
    }

    @Override
    public void write(PacketBuffer buffer, BlockTransmutation recipe) {
        ByteBufUtils.writeList(buffer, recipe.getInputOptions(), (buf, match) -> {
            ByteBufUtils.writeBlockState(buf, match.getMatchState());
            buf.writeBoolean(match.doesMatchExact());
        });
        ByteBufUtils.writeBlockState(buffer, recipe.getOutput());
        buffer.writeDouble(recipe.getStarlightRequired());
        ByteBufUtils.writeOptional(buffer, recipe.getRequiredConstellation(), ByteBufUtils::writeRegistryEntry);
    }
}
