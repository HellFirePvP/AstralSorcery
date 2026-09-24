/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.util;

import hellfirepvp.astralsorcery.client.ClientProxy;
import hellfirepvp.astralsorcery.common.config.server.GeneralConfig;
import hellfirepvp.astralsorcery.common.lib.constants.ColorsAS;
import hellfirepvp.astralsorcery.common.util.MiscUtil;
import hellfirepvp.astralsorcery.common.util.data.ColorWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: EffectUtil
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class EffectUtil {

    public static float flicker(float speed, float partialTicks) {
        return flicker(speed, partialTicks, 0.5F, 0.3F);
    }

    public static float flicker(float speed, float partialTicks, float amplitude, float offset) {
        int dayLength = MiscUtil.safeGetConfig(GeneralConfig.CONFIG.dayLength, 24000);
        int time = (int) (ClientProxy.getClientTick() % (dayLength / 2));
        float radians = (time + partialTicks) * speed;
        return Mth.sin(radians) * amplitude + offset;
    }

    public static ColorWrapper randomStarlightColor(RandomSource rand) {
        return MiscUtil.getRandomEntry(rand, ColorWrapper.WHITE, ColorsAS.CELESTIAL_STRIKE_LIGHT, ColorsAS.CELESTIAL_STRIKE_DARK).orElseThrow();
    }

    public static void playBlockBreakParticles(BlockPos pos, BlockState state) {
        try {
            Minecraft.getInstance().particleEngine.destroy(pos, state);
        } catch (Exception ignored) {}
    }
}
