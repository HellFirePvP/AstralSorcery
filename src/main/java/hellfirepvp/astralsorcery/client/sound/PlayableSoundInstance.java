/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.sound;

import hellfirepvp.astralsorcery.client.util.SoundUtil;
import hellfirepvp.astralsorcery.common.sound.CategorizedSoundEvent;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.TickableSoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.function.Predicate;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: PlayableSoundInstance
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
@OnlyIn(Dist.CLIENT)
public class PlayableSoundInstance extends SimpleSoundInstance implements TickableSoundInstance {

    private static final RandomSource rand = RandomSource.create();

    private float fadeOutTickCount = -1;
    private float fadeInTicks = -1, fadeOutTicks = -1;
    private float fadeVolumeMultiplier = 1F;
    private long tick = 0;

    private Predicate<PlayableSoundInstance> stopFunction = sound -> false;
    private boolean shouldStopPlaying = false;
    private float volumeMultiplier = 1F;

    protected PlayableSoundInstance(ResourceLocation location, SoundSource source, float volume, float pitch, RandomSource random, boolean looping, int delay, Attenuation attenuation, double x, double y, double z, boolean relative) {
        super(location, source, volume, pitch, random, looping, delay, attenuation, x, y, z, relative);
    }

    public static PlayableSoundInstance.Builder of(CategorizedSoundEvent sound) {
        return of(sound.getId(), sound.category());
    }

    public static PlayableSoundInstance.Builder of(SoundEvent sound) {
        return of(sound.getLocation(), SoundSource.MASTER);
    }

    public static PlayableSoundInstance.Builder of(SoundEvent sound, SoundSource category) {
        return of(sound.getLocation(), category);
    }

    public static PlayableSoundInstance.Builder of(ResourceLocation soundId, SoundSource category) {
        return new PlayableSoundInstance.Builder(soundId, category);
    }

    @Override
    public boolean isStopped() {
        return this.shouldStop();
    }

    public boolean hasStoppedPlaying() {
        return this.shouldStop() || !Minecraft.getInstance().getSoundManager().isActive(this);
    }

    private boolean shouldStop() {
        return (this.shouldStopPlaying && this.fadeOutTicks < 0) || SoundUtil.getSoundVolume(this.source) <= 0F;
    }

    @Override
    public void tick() {
        this.tick++;
        this.shouldStopPlaying |= this.stopFunction.test(this);

        if (this.shouldStopPlaying) {
            this.fadeOutTicks--;
        }

        float mulFadeIn = this.fadeInTicks >= 0 ? Mth.clamp(this.tick / this.fadeInTicks, 0F, 1F) : 1F;
        float mulFadeOut = this.fadeOutTicks >= 0 ? Mth.clamp(this.fadeOutTicks / this.fadeOutTickCount, 0F, 1F) : 1F;
        this.fadeVolumeMultiplier = mulFadeIn * mulFadeOut;
    }

    @Override
    public float getVolume() {
        return super.getVolume() * this.fadeVolumeMultiplier * this.volumeMultiplier;
    }

    @Override
    public boolean canStartSilent() {
        return true;
    }

    public void setVolumeMultiplier(float volumeMultiplier) {
        this.volumeMultiplier = volumeMultiplier;
    }

    public static class Builder {

        private final ResourceLocation soundId;
        private final SoundSource soundSource;
        private RandomSource random = rand;
        private float volume = 1F;
        private float pitch = 1F;
        private double x, y, z;
        private boolean relative = false;
        private boolean loop = false;
        private Predicate<PlayableSoundInstance> stopFunction = sound -> false;
        private int fadeInTicks = -1, fadeOutTicks = -1;

        private Builder(ResourceLocation soundId, SoundSource soundSource) {
            this.soundId = soundId;
            this.soundSource = soundSource;
        }

        public Builder volume(float volume) {
            this.volume = volume;
            return this;
        }

        public Builder pitch(float pitch) {
            this.pitch = pitch;
            return this;
        }

        public Builder random(RandomSource random) {
            this.random = random;
            return this;
        }

        public Builder pos(BlockEntity te) {
            return pos(te.getBlockPos());
        }

        public Builder pos(Vec3i pos) {
            return pos(pos.getX(), pos.getY(), pos.getZ());
        }

        public Builder pos(Vector3 pos) {
            return pos(pos.getX(), pos.getY(), pos.getZ());
        }

        public Builder pos(Entity entity) {
            return pos(entity.getX(), entity.getY(), entity.getZ());
        }

        public Builder pos(double x, double y, double z) {
            this.x = x;
            this.y = y;
            this.z = z;
            return this;
        }

        public Builder forUI() {
            this.x = 0;
            this.y = 0;
            this.z = 0;
            this.relative = true;
            return this;
        }

        public Builder loop(boolean loop) {
            this.loop = loop;
            return this;
        }

        public Builder stopFunction(Predicate<PlayableSoundInstance> stopFunction) {
            this.stopFunction = stopFunction;
            return this;
        }

        public Builder fadeInTicks(int fadeInTicks) {
            this.fadeInTicks = fadeInTicks;
            return this;
        }

        public Builder fadeOutTicks(int fadeOutTicks) {
            this.fadeOutTicks = fadeOutTicks;
            return this;
        }

        public PlayableSoundInstance play() {
            PlayableSoundInstance soundInstance = new PlayableSoundInstance(
                    this.soundId,
                    this.soundSource,
                    this.volume,
                    this.pitch,
                    this.random,
                    this.loop,
                    0,
                    this.relative ? Attenuation.NONE : Attenuation.LINEAR,
                    this.x,
                    this.y,
                    this.z,
                    this.relative
            );
            soundInstance.stopFunction = this.stopFunction;
            soundInstance.fadeInTicks = this.fadeInTicks;
            soundInstance.fadeOutTicks = this.fadeOutTicks;
            soundInstance.fadeOutTickCount = this.fadeOutTicks;

            Minecraft.getInstance().getSoundManager().play(soundInstance);
            return soundInstance;
        }
    }
}
