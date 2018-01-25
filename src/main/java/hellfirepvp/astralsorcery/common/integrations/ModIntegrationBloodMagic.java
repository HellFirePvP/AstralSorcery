/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.integrations;

import hellfirepvp.astralsorcery.common.base.Mods;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ModIntegrationBloodMagic
 * Created by HellFirePvP
 * Date: 17.06.2017 / 22:56
 */
public class ModIntegrationBloodMagic {

    public static void sendIMC() {
        //Teleposer blacklist
        Mods.BLOODMAGIC.sendIMC("teleposerBlacklist", BlocksAS.attunementRelay);
        Mods.BLOODMAGIC.sendIMC("teleposerBlacklist", BlocksAS.celestialGateway);
        Mods.BLOODMAGIC.sendIMC("teleposerBlacklist", BlocksAS.blockFakeTree);
        Mods.BLOODMAGIC.sendIMC("teleposerBlacklist", BlocksAS.blockMachine);
        Mods.BLOODMAGIC.sendIMC("teleposerBlacklist", BlocksAS.ritualLink);
        Mods.BLOODMAGIC.sendIMC("teleposerBlacklist", BlocksAS.blockStructural);
        Mods.BLOODMAGIC.sendIMC("teleposerBlacklist", BlocksAS.treeBeacon);
        Mods.BLOODMAGIC.sendIMC("teleposerBlacklist", BlocksAS.blockIlluminator);
        Mods.BLOODMAGIC.sendIMC("teleposerBlacklist", BlocksAS.blockAltar);
        Mods.BLOODMAGIC.sendIMC("teleposerBlacklist", BlocksAS.celestialCollectorCrystal);
        Mods.BLOODMAGIC.sendIMC("teleposerBlacklist", BlocksAS.collectorCrystal);
        Mods.BLOODMAGIC.sendIMC("teleposerBlacklist", BlocksAS.lens);
        Mods.BLOODMAGIC.sendIMC("teleposerBlacklist", BlocksAS.lensPrism);
        Mods.BLOODMAGIC.sendIMC("teleposerBlacklist", BlocksAS.blockWell);
        Mods.BLOODMAGIC.sendIMC("teleposerBlacklist", BlocksAS.ritualPedestal);
    }

}
