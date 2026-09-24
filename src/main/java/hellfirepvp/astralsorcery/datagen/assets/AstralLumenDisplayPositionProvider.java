/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.datagen.assets;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.client.datagen.LumenDisplayPositionProvider;
import hellfirepvp.astralsorcery.common.lib.LumenAS;
import net.minecraft.data.PackOutput;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: AstralLumenDisplayPositionProvider
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class AstralLumenDisplayPositionProvider extends LumenDisplayPositionProvider {

    public AstralLumenDisplayPositionProvider(PackOutput output) {
        super(output, AstralSorcery.MODID);
    }

    @Override
    public void registerPositions() {
        this.put(LumenAS.PRISMATIC, 0, 0);

        this.put(LumenAS.HYLE, 3, -1);
        this.put(LumenAS.DYNAMIS, 1, 3);
        this.put(LumenAS.AION, -2, -2);
        this.put(LumenAS.AKASHA, -3, 2);

        this.put(LumenAS.VIREL, 4, 2);
        this.put(LumenAS.SOLYN, 2, -5);
        this.put(LumenAS.NULLAE, -2, 5);
        this.put(LumenAS.CALDOR, -5, -1);

        this.put(LumenAS.AEVITAS, 6, -3);
        this.put(LumenAS.ARMARA, -1, -6);
        this.put(LumenAS.DISCIDIA, -6, -4);
        this.put(LumenAS.EVORSIO, -5, 6);
        this.put(LumenAS.VICIO, 5, 6);
    }
}
