package hellfirepvp.astralsorcery.common.potion;

import hellfirepvp.astralsorcery.client.util.resource.AssetLibrary;
import hellfirepvp.astralsorcery.client.util.resource.AssetLoader;
import hellfirepvp.astralsorcery.client.util.resource.BindableResource;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PotionHunt
 * Created by HellFirePvP
 * Date: 18.11.2016 / 01:51
 */
public class PotionHunt extends PotionCustomTexture {

    private static Object texBuffer = null;

    public PotionHunt() {
        super(false, 0x00752D);
        setPotionName("effect.as.hunt");
        setBeneficial();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public BindableResource getResource() {
        if(texBuffer == null) {
            texBuffer = AssetLibrary.loadTexture(AssetLoader.TextureLocation.MISC, "potion_hunt");
        }
        return (BindableResource) texBuffer;
    }
}
