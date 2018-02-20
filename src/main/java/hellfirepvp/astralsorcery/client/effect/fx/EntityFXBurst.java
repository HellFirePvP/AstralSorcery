/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.effect.fx;

import hellfirepvp.astralsorcery.client.effect.IComplexEffect;
import hellfirepvp.astralsorcery.client.util.SpriteLibrary;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EntityFXBurst
 * Created by HellFirePvP
 * Date: 17.09.2016 / 23:52
 */
public class EntityFXBurst extends EntityFXFacingSprite implements IComplexEffect.PreventRemoval {

    public EntityFXBurst(double x, double y, double z) {
        super(SpriteLibrary.spriteCollectorBurst, x, y, z);
    }

    public EntityFXBurst(double x, double y, double z, float scale) {
        super(SpriteLibrary.spriteCollectorBurst, x, y, z, scale);
    }

}
