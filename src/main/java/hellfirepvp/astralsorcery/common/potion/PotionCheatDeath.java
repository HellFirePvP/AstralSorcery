package hellfirepvp.astralsorcery.common.potion;

import hellfirepvp.astralsorcery.client.util.resource.AssetLibrary;
import hellfirepvp.astralsorcery.client.util.resource.AssetLoader;
import hellfirepvp.astralsorcery.client.util.resource.BindableResource;
import hellfirepvp.astralsorcery.common.constellation.effect.aoe.CEffectPhoenix;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PotionCheatDeath
 * Created by HellFirePvP
 * Date: 13.11.2016 / 01:32
 */
public class PotionCheatDeath extends PotionCustomTexture {

    private static Object texBuffer = null;

    public PotionCheatDeath() {
        super(false, CEffectPhoenix.PHOENIX_COLOR.getRGB());
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
}
