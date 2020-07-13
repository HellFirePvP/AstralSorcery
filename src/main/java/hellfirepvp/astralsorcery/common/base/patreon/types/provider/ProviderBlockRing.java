/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.base.patreon.types.provider;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import hellfirepvp.astralsorcery.common.base.patreon.FlareColor;
import hellfirepvp.astralsorcery.common.base.patreon.PatreonEffectProvider;
import hellfirepvp.astralsorcery.common.base.patreon.types.TypeBlockRing;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ProviderBlockRing
 * Created by HellFirePvP
 * Date: 31.08.2019 / 10:31
 */
public class ProviderBlockRing implements PatreonEffectProvider<TypeBlockRing> {

    private static final JsonParser PARSER = new JsonParser();

    @Override
    public TypeBlockRing buildEffect(UUID playerUUID, List<String> effectParameters) throws Exception {
        UUID effectUniqueId = UUID.fromString(effectParameters.get(0));
        FlareColor fc = null;
        if (!"null".equals(effectParameters.get(1))) {
            fc = FlareColor.valueOf(effectParameters.get(1));
        }

        float distance = Float.parseFloat(effectParameters.get(2));
        float rotationAngle = Float.parseFloat(effectParameters.get(3));
        int repeats = Integer.parseInt(effectParameters.get(4));
        int tickRotationSpeed = Integer.parseInt(effectParameters.get(5));
        JsonArray jo = (JsonArray) PARSER.parse(effectParameters.get(6));
        HashMap<BlockPos, BlockState> pattern = new HashMap<>();
        for (JsonElement patternElement : jo) {
            JsonObject obj = (JsonObject) patternElement;
            BlockPos pos = new BlockPos(
                    obj.getAsJsonPrimitive("posX").getAsInt(),
                    obj.getAsJsonPrimitive("posY").getAsInt(),
                    obj.getAsJsonPrimitive("posZ").getAsInt());
            Block b = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(obj.getAsJsonPrimitive("block").getAsString()));
            pattern.put(pos, b.getDefaultState());
        }
        return new TypeBlockRing(effectUniqueId,
                fc, playerUUID,
                distance,
                rotationAngle,
                repeats,
                tickRotationSpeed,
                pattern);
    }
}
