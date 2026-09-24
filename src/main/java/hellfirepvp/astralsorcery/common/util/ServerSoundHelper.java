/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util;

import hellfirepvp.astralsorcery.common.sound.CategorizedSoundEvent;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.core.Vec3i;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: ServerSoundHelper
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class ServerSoundHelper {

    public static void playSoundAround(CategorizedSoundEvent sound, Level world, Vec3i position, float volume, float pitch) {
        playSoundAround(sound.sound().get(), sound.category(), world, position.getX(), position.getY(), position.getZ(), volume, pitch);
    }

    public static void playSoundAround(CategorizedSoundEvent sound, Level world, Vec3 position, float volume, float pitch) {
        playSoundAround(sound.sound().get(), sound.category(), world, position.x(), position.y(), position.z(), volume, pitch);
    }

    public static void playSoundAround(CategorizedSoundEvent sound, Level world, Vector3 position, float volume, float pitch) {
        playSoundAround(sound.sound().get(), sound.category(), world, position.getX(), position.getY(), position.getZ(), volume, pitch);
    }

    public static void playSoundAround(SoundEvent sound, SoundSource category, Level world, Vec3i position, float volume, float pitch) {
        playSoundAround(sound, category, world, position.getX(), position.getY(), position.getZ(), volume, pitch);
    }

    public static void playSoundAround(SoundEvent sound, SoundSource category, Level world, Vec3 position, float volume, float pitch) {
        playSoundAround(sound, category, world, position.x(), position.y(), position.z(), volume, pitch);
    }

    public static void playSoundAround(SoundEvent sound, SoundSource category, Level world, Vector3 position, float volume, float pitch) {
        playSoundAround(sound, category, world, position.getX(), position.getY(), position.getZ(), volume, pitch);
    }

    public static void playSoundAround(SoundEvent sound, SoundSource category, Level world, double posX, double posY, double posZ, float volume, float pitch) {
        world.playSound(null, posX, posY, posZ, sound, category, volume, pitch);
    }
}
