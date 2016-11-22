package hellfirepvp.astralsorcery.common.constellation.perk;

import hellfirepvp.astralsorcery.client.util.resource.BindableResource;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ConstellationPerkMap
 * Created by HellFirePvP
 * Date: 22.11.2016 / 01:15
 */
public abstract class ConstellationPerkMap {

    @SideOnly(Side.CLIENT)
    public abstract BindableResource getOverlayTexture();

}
