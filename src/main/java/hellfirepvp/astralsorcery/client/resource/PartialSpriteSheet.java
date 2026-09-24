/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.resource;

import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: PartialSpriteSheet
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class PartialSpriteSheet extends SpriteSheet {

    private final float uOffsetFraction;
    private final float vOffsetFraction;
    private final float uFraction;
    private final float vFraction;

    private PartialSpriteSheet(SpriteSheet spriteSheet, float uOffsetFraction, float vOffsetFraction, float uFraction, float vFraction) {
        super(spriteSheet.getTexture(), spriteSheet.getRows(), spriteSheet.getColumns());
        this.uOffsetFraction = Mth.clamp(uOffsetFraction, 0F, 1F);
        this.vOffsetFraction = Mth.clamp(vOffsetFraction, 0F, 1F);
        this.uFraction = Mth.clamp(uFraction, 0F, 1F);
        this.vFraction = Mth.clamp(vFraction, 0F, 1F);
    }

    public static PartialSpriteSheet partial(SpriteSheet spriteSheet, float uOffsetFraction, float vOffsetFraction, float uFraction, float vFraction) {
        return new PartialSpriteSheet(spriteSheet, uOffsetFraction, vOffsetFraction, uFraction, vFraction);
    }

    public static PartialSpriteSheet partialRandomOffset(SpriteSheet spriteSheet, RandomSource rand, float fraction) {
        return partial(spriteSheet, (1F - fraction) * rand.nextFloat(), (1F - fraction) * rand.nextFloat(), fraction, fraction);
    }

    @Override
    public UVFrame getUV(long frameTimer) {
        UVFrame frame = super.getUV(frameTimer);

        return new UVFrame(
                frame.u() + frame.uWidth() * this.uOffsetFraction,
                frame.v() + frame.vHeight() * this.vOffsetFraction,
                frame.uWidth() * this.uFraction,
                frame.vHeight() * this.vFraction
        );
    }
}
