package hellfirepvp.astralsorcery.client.util;

import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ILightReader;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: LightmapUtil
 * Created by HellFirePvP
 * Date: 05.06.2020 / 21:36
 */
public class LightmapUtil {

    public static int getPackedFullbrightCoords() {
        return 0xF000F0;
    }

    public static int getPackedLightCoords(int lightValue) {
        return getPackedLightCoords(lightValue, lightValue);
    }

    public static int getPackedLightCoords(int skyLight, int blockLight) {
        return skyLight << 20 | blockLight << 4;
    }

    public static int getPackedLightCoords(ILightReader world, BlockPos at) {
        return WorldRenderer.getCombinedLight(world, at);
    }
}
