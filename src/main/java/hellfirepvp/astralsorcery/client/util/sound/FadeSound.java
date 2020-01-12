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
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.ITickableSound;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.MathHelper;

import java.util.function.Predicate;

public class FadeSound extends SimpleSound implements ITickableSound, ISound {

    private Predicate<FadeSound> func = null;
    private boolean hasStoppedPlaying = false;
    private float volumeMultiplier = 1F;

    private float fadeInTicks = 40;
    private float fadeOutTicks = 1;

    private int tick = 0;
    private int stopTick = 0;

    public FadeSound(CategorizedSoundEvent sound, float volume, float pitch, Vector3 pos, boolean isGlobal) {
        this(sound, sound.getCategory(), volume, pitch, pos, isGlobal);
    }

    public FadeSound(SoundEvent sound, SoundCategory category, float volume, float pitch, Vector3 pos, boolean isGlobal) {
        super(sound.getName(), category, volume, pitch, true, 0, AttenuationType.LINEAR, (float) pos.getX(), (float) pos.getY(), (float) pos.getZ(), isGlobal);
    }

    public void setRefreshFunction(Predicate<FadeSound> func) {
        this.func = func;
    }

    public <T extends FadeSound> T setFadeInTicks(float fadeInTicks) {
        this.fadeInTicks = fadeInTicks;
        return (T) this;
    }

    public <T extends FadeSound> T setFadeOutTicks(float fadeOutTicks) {
        this.fadeOutTicks = fadeOutTicks;
        return (T) this;
    }

    @Override
    public boolean isDonePlaying() {
        return (this.hasStoppedPlaying = (func == null || func.test(this))) && this.stopTick > this.fadeOutTicks;
    }

    public boolean hasStoppedPlaying() {
        return hasStoppedPlaying || !Minecraft.getInstance().getSoundHandler().isPlaying(this);
    }

    public void setVolumeMultiplier(float volumeMultiplier) {
        this.volumeMultiplier = MathHelper.clamp(volumeMultiplier, 0F, 1F);
    }

    @Override
    public float getVolume() {
        float mulFadeIn = MathHelper.clamp(this.tick / this.fadeInTicks, 0F, 1F);
        float mulFadeOut = MathHelper.clamp(1F - this.stopTick / this.fadeOutTicks, 0F, 1F);
        return mulFadeIn * mulFadeOut * super.getVolume()* volumeMultiplier;
    }

    @Override
    public void tick() {
        this.tick++;
        if (this.hasStoppedPlaying) {
            this.stopTick++;
        }
    }

    @Override
    public boolean canBeSilent() {
        return true;
    }
}
