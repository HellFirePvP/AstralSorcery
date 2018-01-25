/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.potion;

import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import hellfirepvp.astralsorcery.client.util.resource.AssetLibrary;
import hellfirepvp.astralsorcery.client.util.resource.AssetLoader;
import hellfirepvp.astralsorcery.client.util.resource.BindableResource;
import hellfirepvp.astralsorcery.common.network.packet.server.PktParticleEvent;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.awt.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PotionCheatDeath
 * Created by HellFirePvP
 * Date: 13.11.2016 / 01:32
 */
public class PotionCheatDeath extends PotionCustomTexture {

    private static Object texBuffer = null;

    public static final Color PHOENIX_COLOR = new Color(0xFF5711);

    public PotionCheatDeath() {
        super(false, 0xFF5711);
        setPotionName("effect.as.cheatdeath");
        setBeneficial();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public BindableResource getResource() {
        if(texBuffer == null) {
            texBuffer = AssetLibrary.loadTexture(AssetLoader.TextureLocation.MISC, "potion_cheatdeath");
        }
        return (BindableResource) texBuffer;
    }

    @SideOnly(Side.CLIENT)
    public static void playEntityDeathEffect(PktParticleEvent event) {
        for (int i = 0; i < 25; i++) {
            Vector3 at = event.getVec();
            EntityFXFacingParticle p = EffectHelper.genericFlareParticle(
                    at.getX(),
                    at.getY() + rand.nextFloat(),
                    at.getZ());
            p.motion((rand.nextFloat() * 0.1F) * (rand.nextBoolean() ? 1 : -1),
                     (rand.nextFloat() * 0.1F) * (rand.nextBoolean() ? 1 : -1),
                     (rand.nextFloat() * 0.1F) * (rand.nextBoolean() ? 1 : -1));
            p.scale(0.25F).setColor(PHOENIX_COLOR);
        }
    }

}
