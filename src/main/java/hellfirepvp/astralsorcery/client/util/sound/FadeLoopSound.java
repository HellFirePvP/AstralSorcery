/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
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
 * Class: FadeLoopSound
 * Created by HellFirePvP
 * Date: 25.09.2019 / 16:55
 */
public class FadeLoopSound extends PositionedLoopSound {

    private float fadeInTicks = 40;
    private float fadeOutTicks = 1;

    private int tick = 0;
    private int stopTick = 0;
    private boolean shouldStop = false;

    public FadeLoopSound(CategorizedSoundEvent sound, float volume, float pitch, Vector3 pos, boolean isGlobal) {
        super(sound, volume, pitch, pos, isGlobal);
    }

    public FadeLoopSound(SoundEvent sound, SoundCategory category, float volume, float pitch, Vector3 pos, boolean isGlobal) {
        super(sound, category, volume, pitch, pos, isGlobal);
    }

    public <T extends FadeLoopSound> T setFadeInTicks(float fadeInTicks) {
        this.fadeInTicks = fadeInTicks;
        return (T) this;
    }

    public <T extends FadeLoopSound> T setFadeOutTicks(float fadeOutTicks) {
        this.fadeOutTicks = fadeOutTicks;
        return (T) this;
    }

    @Override
    public boolean isDonePlaying() {
        return (this.shouldStop = super.isDonePlaying()) && this.stopTick > this.fadeOutTicks;
    }

    @Override
    public void tick() {
        super.tick();

        this.tick++;
        if (this.shouldStop) {
            this.stopTick++;
        }
    }

    @Override
    public boolean canBeSilent() {
        return true;
    }

    @Override
    public float getVolume() {
        float mulFadeIn = MathHelper.clamp(this.tick / this.fadeInTicks, 0F, 1F);
        float mulFadeOut = MathHelper.clamp(1F - this.stopTick / this.fadeOutTicks, 0F, 1F);
        return mulFadeIn * mulFadeOut * super.getVolume();
    }
}
