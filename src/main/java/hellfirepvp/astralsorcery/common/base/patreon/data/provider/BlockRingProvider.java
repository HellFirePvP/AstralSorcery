/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.base.patreon.data.provider;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import hellfirepvp.astralsorcery.common.base.patreon.PatreonEffectHelper;
import hellfirepvp.astralsorcery.common.base.patreon.base.PtEffectBlockRing;
import hellfirepvp.astralsorcery.common.base.patreon.data.EffectProvider;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockRingProvider
 * Created by HellFirePvP
 * Date: 05.04.2019 / 21:35
 */
public class BlockRingProvider implements EffectProvider<PtEffectBlockRing> {

    private static final JsonParser PARSER = new JsonParser();

    @Override
    public PtEffectBlockRing buildEffect(UUID uuid, List<String> effectParameters) throws Exception {
        UUID effectUniqueId = UUID.fromString(effectParameters.get(0));
        PatreonEffectHelper.FlareColor fc = null;
        if (!"null".equals(effectParameters.get(1))) {
            fc = PatreonEffectHelper.FlareColor.valueOf(effectParameters.get(1));
        }
        float distance = Float.parseFloat(effectParameters.get(2));
        float rotationAngle = Float.parseFloat(effectParameters.get(3));
        int repeats = Integer.parseInt(effectParameters.get(4));
        int tickRotationSpeed = Integer.parseInt(effectParameters.get(5));
        JsonArray jo = (JsonArray) PARSER.parse(effectParameters.get(6));
        HashMap<BlockPos, IBlockState> pattern = new HashMap<>();
        for (JsonElement patternElement : jo) {
            JsonObject obj = (JsonObject) patternElement;
            BlockPos pos = new BlockPos(
                    obj.getAsJsonPrimitive("posX").getAsInt(),
                    obj.getAsJsonPrimitive("posY").getAsInt(),
                    obj.getAsJsonPrimitive("posZ").getAsInt());
            Block b = Block.getBlockFromName(obj.getAsJsonPrimitive("block").getAsString());
            int data = obj.has("data") ? obj.getAsJsonPrimitive("data").getAsInt() : 0;
            pattern.put(pos, b.getStateFromMeta(data));
        }
        return new PtEffectBlockRing(effectUniqueId,
                fc, uuid,
                distance,
                rotationAngle,
                repeats,
                tickRotationSpeed,
                pattern);
    }

}
