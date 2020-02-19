/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 *  All rights reserved.
 *  The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 *  For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.mantle;

import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.data.config.base.ConfigEntry;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: MantleEffect
 * Created by HellFirePvP
 * Date: 17.02.2020 / 20:13
 */
public abstract class MantleEffect {

    protected static final Random rand = new Random();

    private final CompoundNBT data;

    public MantleEffect(CompoundNBT mantleData) {
        this.data = mantleData;
    }

    public final CompoundNBT getData() {
        return data;
    }

    public final void flush(CompoundNBT out) {
        out.merge(this.getData());
    }

    public abstract IConstellation getAssociatedConstellation();

    public abstract Config getConfig();

    public abstract static class Config extends ConfigEntry {

        private final boolean defaultEnabled = true;

        public ForgeConfigSpec.BooleanValue enabled;

        public Config(String constellationName) {
            super(String.format("constellation.mantle.%s", constellationName));
        }

        @Override
        public void createEntries(ForgeConfigSpec.Builder cfgBuilder) {
            this.enabled = cfgBuilder
                    .comment("Set this to false to disable this ritual effect")
                    .translation(translationKey("enabled"))
                    .define("enabled", this.defaultEnabled);
        }
    }
}
