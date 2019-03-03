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
 * Class: PotionTimeFreeze
 * Created by HellFirePvP
 * Date: 12.02.2018 / 23:03
 */
public class PotionTimeFreeze extends PotionCustomTexture {

    private static Object texBuffer = null;

    public static final Color TIMEFREEZE_COLOR = new Color(0xB89AFF);

    public PotionTimeFreeze() {
        super(false, 0xB89AFF);
        setPotionName("effect.as.timefreeze");
        setBeneficial();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public BindableResource getResource() {
        if(texBuffer == null) {
            texBuffer = AssetLibrary.loadTexture(AssetLoader.TextureLocation.MISC, "potion_timefreeze");
        }
        return (BindableResource) texBuffer;
    }
}
