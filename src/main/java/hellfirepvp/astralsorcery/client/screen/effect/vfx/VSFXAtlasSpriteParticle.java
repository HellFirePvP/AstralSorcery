/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.screen.effect.vfx;

import hellfirepvp.astralsorcery.client.resource.AbstractRenderTexture;
import hellfirepvp.astralsorcery.client.resource.AtlasSpriteTexture;
import hellfirepvp.astralsorcery.client.resource.SpriteSheet;
import hellfirepvp.astralsorcery.client.screen.effect.ScreenEffectTicket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: VSFXAtlasSpriteParticle
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class VSFXAtlasSpriteParticle extends VSFXPlaneParticle {

    public VSFXAtlasSpriteParticle(ScreenEffectTicket<?, ?> ticket, double x, double y, AbstractRenderTexture texture) {
        super(ticket, x, y, texture);
    }

    public VSFXAtlasSpriteParticle(ScreenEffectTicket<?, ?> ticket, double x, double y, SpriteSheet spriteSheet) {
        super(ticket, x, y, spriteSheet);
    }

    public VSFXAtlasSpriteParticle setSprite(ResourceLocation atlas, ResourceLocation spriteId) {
        TextureAtlasSprite tas = Minecraft.getInstance().getModelManager().getAtlas(atlas).getSprite(spriteId);
        this.setSpriteSheet(AtlasSpriteTexture.fromAtlasSprite(tas).asSpriteSheet());
        return this;
    }
}
