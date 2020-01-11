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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PositionedLoopSound
 * Created by HellFirePvP
 * Date: 30.06.2019 / 22:59
 */
public class PositionedLoopSound extends SimpleSound implements ITickableSound, ISound {

    private ActivityFunction func = null;
    private boolean hasStoppedPlaying = false;
    private float volumeMultiplier = 1F;

    public PositionedLoopSound(CategorizedSoundEvent sound, float volume, float pitch, Vector3 pos, boolean isGlobal) {
        this(sound, sound.getCategory(), volume, pitch, pos, isGlobal);
    }

    public PositionedLoopSound(SoundEvent sound, SoundCategory category, float volume, float pitch, Vector3 pos, boolean isGlobal) {
        super(sound.getName(), category, volume, pitch, true, 0, AttenuationType.LINEAR, (float) pos.getX(), (float) pos.getY(), (float) pos.getZ(), isGlobal);
    }

    public void setRefreshFunction(ActivityFunction func) {
        this.func = func;
    }

    @Override
    public boolean isDonePlaying() {
        hasStoppedPlaying = func == null || func.shouldStop();
        return hasStoppedPlaying;
    }

    public boolean hasStoppedPlaying() {
        return hasStoppedPlaying || !Minecraft.getInstance().getSoundHandler().isPlaying(this);
    }

    public void setVolumeMultiplier(float volumeMultiplier) {
        this.volumeMultiplier = MathHelper.clamp(volumeMultiplier, 0F, 1F);
    }

    @Override
    public float getVolume() {
        return super.getVolume() * volumeMultiplier;
    }

    @Override
    public void tick() {}

    @OnlyIn(Dist.CLIENT)
    public static interface ActivityFunction {

        public boolean shouldStop();

    }
}