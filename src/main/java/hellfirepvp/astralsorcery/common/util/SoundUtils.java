package hellfirepvp.astralsorcery.common.util;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: SoundUtils
 * Created by HellFirePvP
 * Date: 06.12.2016 / 17:02
 */
public class SoundUtils {

    /*@SideOnly(Side.CLIENT)
    public static class LoopingSound {

        private final LoopableSoundEvent sound;
        private final World world;
        private final Vector3 pos;
        private final float volume, pitch;
        private final ActivityFunction refreshFunc;

        private int tick = 0;

        public LoopingSound(LoopableSoundEvent sound, World world, Vector3 pos, float volume, float pitch, ActivityFunction refreshFunc) {
            this.sound = sound;
            this.world = world;
            this.pos = pos;
            this.volume = volume;
            this.pitch = pitch;
            this.refreshFunc = refreshFunc;
        }

        public void tick() {
            if(world.isRemote) {
                playSoundClient();
            } else {
                if(tick <= 0) {
                    world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), sound, sound.getCategory(), volume, pitch);
                    tick = sound.tickLength;
                }
                tick--;
            }
        }

        @SideOnly(Side.CLIENT)
        private void playSoundClient() {
            SoundHandler sh = Minecraft.getMinecraft().getSoundHandler();
            if(!sh.isSoundPlaying(sound)) {

            }
        }

        @SideOnly(Side.CLIENT)
        private EntityPlayer getClientPlayer() {
            return Minecraft.getMinecraft().thePlayer;
        }

    }*/

    public static class LoopableSoundEvent extends CategorizedSoundEvent {

        private final int tickLength;

        public LoopableSoundEvent(ResourceLocation soundNameIn, SoundCategory category, int tickLoopLength) {
            super(soundNameIn, category);
            this.tickLength = tickLoopLength;
        }

    }

    public static class CategorizedSoundEvent extends SoundEvent {

        private final SoundCategory category;

        public CategorizedSoundEvent(ResourceLocation soundNameIn, SoundCategory category) {
            super(soundNameIn);
            this.category = category;
        }

        public SoundCategory getCategory() {
            return category;
        }

    }
}
