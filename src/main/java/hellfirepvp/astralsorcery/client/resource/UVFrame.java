/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.resource;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: UVFrame
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public record UVFrame(float u, float v, float uWidth, float vHeight) {

    public static final UVFrame FULL = new UVFrame(0F, 0F, 1F, 1F);

    public UVFrame multiplyUWidth(float factor) {
        return new UVFrame(u, v, uWidth * factor, vHeight);
    }

    public UVFrame multiplyVHeight(float factor) {
        return new UVFrame(u, v, uWidth, vHeight * factor);
    }

    public static UVFrame fromAtlasSprite(TextureAtlasSprite sprite) {
        return new UVFrame(
                sprite.getU0(),
                sprite.getV0(),
                sprite.getU1() - sprite.getU0(),
                sprite.getV1() - sprite.getV0()
        );
    }

}
