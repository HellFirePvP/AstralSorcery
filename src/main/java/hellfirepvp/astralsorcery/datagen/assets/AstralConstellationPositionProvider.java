/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.datagen.assets;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.client.datagen.ConstellationPositionProvider;
import hellfirepvp.astralsorcery.client.sky.constellation.SkyConstellationPosition;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: AstralConstellationPositionProvider
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class AstralConstellationPositionProvider extends ConstellationPositionProvider {

    public AstralConstellationPositionProvider(PackOutput output) {
        super(output, AstralSorcery.MODID);
    }

    @Override
    public void registerPositions() {
        this.add(90, 30);
        this.add(140, 40);
        this.add(60, 50);
        this.add(100, 55);
        this.add(30, 42);

        this.add(240, 35);
        this.add(300, 30);
        this.add(210, 50);
        this.add(275, 45);
        this.add(330, 45);
    }
}
