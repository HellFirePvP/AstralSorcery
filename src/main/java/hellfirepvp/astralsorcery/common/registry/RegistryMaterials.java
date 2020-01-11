/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.registry;

import hellfirepvp.astralsorcery.common.block.base.MaterialBuilderAS;
import net.minecraft.block.material.MaterialColor;

import static hellfirepvp.astralsorcery.common.lib.MaterialsAS.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RegistryMaterials
 * Created by HellFirePvP
 * Date: 30.05.2019 / 22:58
 */
public class RegistryMaterials {

    private RegistryMaterials() {}

    public static void init() {
        MARBLE = new MaterialBuilderAS(MaterialColor.WHITE_TERRACOTTA)
                .requiresTool()
                .build();
        BLACK_MARBLE = new MaterialBuilderAS(MaterialColor.BLACK)
                .requiresTool()
                .build();

        INFUSED_WOOD = new MaterialBuilderAS(MaterialColor.BROWN)
                .flammable()
                .build();
    }

}
