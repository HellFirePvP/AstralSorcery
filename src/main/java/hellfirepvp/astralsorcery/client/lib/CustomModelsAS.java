/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.lib;

import hellfirepvp.astralsorcery.AstralSorcery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.neoforged.neoforge.client.event.ModelEvent;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: CustomModelsAS
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class CustomModelsAS {

    public static final ModelResourceLocation ASTROLABE_IN_HAND = ModelResourceLocation.standalone(AstralSorcery.key("item/astrolabe_in_hand"));

    public static void registerCustomModels(ModelEvent.RegisterAdditional event) {
        event.register(ASTROLABE_IN_HAND);
    }
}
