/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crafting.serializer;

import com.google.gson.JsonArray;
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
import net.minecraft.block.AirBlock;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
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
        JsonHelper.parseMultipleJsonObjects(json, "input", object -> {
            BlockState state = BlockStateHelper.deserializeObject(object);
            boolean fullyDefined = !BlockStateHelper.isMissingStateInformation(object);
            ItemStack display = new ItemStack(state.getBlock());
            if (object.has("display")) {
                display = JsonHelper.getItemStack(object, "display");
            }
            matchInformation.add(new BlockMatchInformation(state, display, fullyDefined));
        });
        if (matchInformation.isEmpty()) {
            throw new IllegalArgumentException("A block transmutation has to have at least 1 input!");
        }
        for (BlockMatchInformation info : matchInformation) {
            if (info.getMatchState().getBlock() instanceof AirBlock) {
                throw new JsonSyntaxException("A block transmutation must not convert an air-block into something!");
            }
        }

        BlockState output = BlockStateHelper.deserializeObject(JSONUtils.getJsonObject(json, "output"));
        ItemStack outputDisplay = new ItemStack(output.getBlock());
        if (JSONUtils.hasField(json, "display")) {
            outputDisplay = JsonHelper.getItemStack(json, "display");
        }
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
        BlockTransmutation tr = new BlockTransmutation(recipeId, output, starlight, matchConstellation);
        matchInformation.forEach(tr::addInputOption);
        tr.setOutputDisplay(outputDisplay);
        return tr;
    }

    @Nullable
    @Override
    public BlockTransmutation read(ResourceLocation recipeId, PacketBuffer buffer) {
        List<BlockMatchInformation> matchInformation = ByteBufUtils.readList(buffer,
                buf -> new BlockMatchInformation(ByteBufUtils.readBlockState(buf), ByteBufUtils.readItemStack(buf), buf.readBoolean()));
        BlockState output = ByteBufUtils.readBlockState(buffer);
        ItemStack display = ByteBufUtils.readItemStack(buffer);
        double starlight = buffer.readDouble();
        IWeakConstellation cst = ByteBufUtils.readOptional(buffer, ByteBufUtils::readRegistryEntry);
        BlockTransmutation tr = new BlockTransmutation(recipeId, output, starlight, cst);
        matchInformation.forEach(tr::addInputOption);
        tr.setOutputDisplay(display);
        return tr;
    }

    @Override
    public void write(JsonObject object, BlockTransmutation recipe) {
        JsonArray inputs = new JsonArray();
        for (BlockMatchInformation info : recipe.getInputOptions()) {
            JsonObject serializedInfo = BlockStateHelper.serializeObject(info.getMatchState(), info.doesMatchExact());
            serializedInfo.add("display", JsonHelper.serializeItemStack(info.getDisplayStack()));
            inputs.add(serializedInfo);
        }
        object.add("input", inputs);

        object.add("output", BlockStateHelper.serializeObject(recipe.getOutput(), true));
        object.add("display", JsonHelper.serializeItemStack(recipe.getOutputDisplay()));
        object.addProperty("starlight", recipe.getStarlightRequired());

        if (recipe.getRequiredConstellation() != null) {
            object.addProperty("constellation", recipe.getRequiredConstellation().getRegistryName().toString());
        }
    }

    @Override
    public void write(PacketBuffer buffer, BlockTransmutation recipe) {
        ByteBufUtils.writeList(buffer, recipe.getInputOptions(), (buf, match) -> {
            ByteBufUtils.writeBlockState(buf, match.getMatchState());
            ByteBufUtils.writeItemStack(buf, match.getDisplayStack());
            buf.writeBoolean(match.doesMatchExact());
        });
        ByteBufUtils.writeBlockState(buffer, recipe.getOutput());
        ByteBufUtils.writeItemStack(buffer, recipe.getOutputDisplay());
        buffer.writeDouble(recipe.getStarlightRequired());
        ByteBufUtils.writeOptional(buffer, recipe.getRequiredConstellation(), ByteBufUtils::writeRegistryEntry);
    }
}
