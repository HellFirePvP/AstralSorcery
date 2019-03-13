/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.potion;

import hellfirepvp.astralsorcery.client.util.resource.AssetLibrary;
import hellfirepvp.astralsorcery.client.util.resource.AssetLoader;
import hellfirepvp.astralsorcery.client.util.resource.BindableResource;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.awt.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PotionDropModifier
 * Created by HellFirePvP
 * Date: 11.02.2018 / 17:17
 */
public class PotionDropModifier extends PotionCustomTexture {

    private static Object texBuffer = null;

    public static final Color DROP_COLOR = new Color(0xFFD114);

    public PotionDropModifier() {
        super(false, 0xFFD114);
        setPotionName("effect.as.dropmod");
        setBeneficial();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public BindableResource getResource() {
        if(texBuffer == null) {
            texBuffer = AssetLibrary.loadTexture(AssetLoader.TextureLocation.MISC, "potion_dropmod");
        }
        return (BindableResource) texBuffer;
    }

}
