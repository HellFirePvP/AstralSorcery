/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.world.config;

import net.minecraft.world.gen.settings.StructureSeparationSettings;

import java.util.function.Supplier;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: DynamicStructureSpacing
 * Created by HellFirePvP
 * Date: 26.08.2020 / 18:47
 */
public class DynamicStructureSpacing extends StructureSeparationSettings {

    private final Supplier<Integer> spacing;
    private final Supplier<Integer> separation;
    private final Supplier<Integer> salt;

    public DynamicStructureSpacing(Supplier<Integer> spacing, Supplier<Integer> separation, Supplier<Integer> salt) {
        super(0, 0, 0);
        this.spacing = spacing;
        this.separation = separation;
        this.salt = salt;
    }

    @Override
    public int func_236668_a_() {
        return this.spacing.get();
    }

    @Override
    public int func_236671_b_() {
        return this.separation.get();
    }

    @Override
    public int func_236673_c_() {
        return this.salt.get();
    }
}
