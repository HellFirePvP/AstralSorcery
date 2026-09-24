/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.effect.vfx;

import hellfirepvp.astralsorcery.client.resource.*;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: VFXAtlasSpriteParticle
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class VFXAtlasSpriteParticle extends VFXFacingParticle {

    public VFXAtlasSpriteParticle(Vector3 pos, AbstractRenderTexture texture) {
        super(pos, texture);
    }

    public VFXAtlasSpriteParticle(Vector3 pos, SpriteSheet spriteSheet) {
        super(pos, spriteSheet);
    }

    public VFXAtlasSpriteParticle setSprite(ResourceLocation atlas, ResourceLocation spriteId) {
        TextureAtlasSprite tas = Minecraft.getInstance().getModelManager().getAtlas(atlas).getSprite(spriteId);
        return this.setSprite(tas);
    }

    public VFXAtlasSpriteParticle setSprite(TextureAtlasSprite sprite) {
        this.setSpriteSheet(AtlasSpriteTexture.fromAtlasSprite(sprite).asSpriteSheet());
        return this;
    }

    public VFXAtlasSpriteParticle setSpriteFraction(float fraction) {
        this.setSpriteSheet(PartialSpriteSheet.partialRandomOffset(this.getSpriteSheet(), rand, fraction));
        return this;
    }
}
