/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.util.sound;

import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.astralsorcery.common.util.sound.CategorizedSoundEvent;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.MathHelper;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: FadeInLoopSound
 * Created by HellFirePvP
 * Date: 25.09.2019 / 16:55
 */
public class FadeInLoopSound extends PositionedLoopSound {

    private float fadeInTicks = 40;
    private int tick = 0;

    public FadeInLoopSound(CategorizedSoundEvent sound, float volume, float pitch, Vector3 pos, boolean isGlobal) {
        super(sound, volume, pitch, pos, isGlobal);
    }

    public FadeInLoopSound(SoundEvent sound, SoundCategory category, float volume, float pitch, Vector3 pos, boolean isGlobal) {
        super(sound, category, volume, pitch, pos, isGlobal);
    }

    public <T extends FadeInLoopSound> T setFadeInTicks(float fadeInTicks) {
        this.fadeInTicks = fadeInTicks;
        return (T) this;
    }

    @Override
    public void tick() {
        super.tick();

        tick++;
    }

    @Override
    public float getVolume() {
        float mul = MathHelper.clamp(this.tick / fadeInTicks, 0F, 1F);
        return mul * super.getVolume();
    }
}
