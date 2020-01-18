/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util.sound;

import hellfirepvp.astralsorcery.client.util.sound.FadeLoopSound;
import hellfirepvp.astralsorcery.client.util.sound.FadeSound;
import hellfirepvp.astralsorcery.client.util.sound.PositionedLoopSound;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.function.Predicate;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: SoundHelper
 * Created by HellFirePvP
 * Date: 30.06.2019 / 22:57
 */
public class SoundHelper {

    public static void playSoundAround(SoundEvent sound, World world, Vec3i position, float volume, float pitch) {
        playSoundAround(sound, SoundCategory.MASTER, world, position.getX(), position.getY(), position.getZ(), volume, pitch);
    }

    public static void playSoundAround(SoundEvent sound, SoundCategory category, World world, Vec3i position, float volume, float pitch) {
        playSoundAround(sound, category, world, position.getX(), position.getY(), position.getZ(), volume, pitch);
    }

    public static void playSoundAround(SoundEvent sound, World world, Vector3 position, float volume, float pitch) {
        playSoundAround(sound, SoundCategory.MASTER, world, position.getX(), position.getY(), position.getZ(), volume, pitch);
    }

    public static void playSoundAround(SoundEvent sound, SoundCategory category, World world, Vector3 position, float volume, float pitch) {
        playSoundAround(sound, category, world, position.getX(), position.getY(), position.getZ(), volume, pitch);
    }

    public static void playSoundAround(SoundEvent sound, SoundCategory category, World world, double posX, double posY, double posZ, float volume, float pitch) {
        if (sound instanceof CategorizedSoundEvent) {
            category = ((CategorizedSoundEvent) sound).getCategory();
        }
        world.playSound(null, posX, posY, posZ, sound, category, volume, pitch);
    }

    @OnlyIn(Dist.CLIENT)
    public static PositionedLoopSound playSoundLoopClient(SoundEvent sound, Vector3 pos, float volume, float pitch, boolean isGlobal, Predicate<PositionedLoopSound> func) {
        SoundCategory cat = SoundCategory.MASTER;
        if (sound instanceof CategorizedSoundEvent) {
            cat = ((CategorizedSoundEvent) sound).getCategory();
        }
        PositionedLoopSound posSound = new PositionedLoopSound(sound, cat, volume, pitch, pos, isGlobal);
        posSound.setRefreshFunction(func);
        Minecraft.getInstance().getSoundHandler().play(posSound);
        return posSound;
    }

    @OnlyIn(Dist.CLIENT)
    public static FadeLoopSound playSoundLoopFadeInClient(SoundEvent sound, Vector3 pos, float volume, float pitch, boolean isGlobal, Predicate<PositionedLoopSound> func) {
        SoundCategory cat = SoundCategory.MASTER;
        if (sound instanceof CategorizedSoundEvent) {
            cat = ((CategorizedSoundEvent) sound).getCategory();
        }
        FadeLoopSound posSound = new FadeLoopSound(sound, cat, volume, pitch, pos, isGlobal);
        posSound.setRefreshFunction(func);
        Minecraft.getInstance().getSoundHandler().play(posSound);
        return posSound;
    }

    @OnlyIn(Dist.CLIENT)
    public static FadeSound playSoundFadeInClient(SoundEvent sound, Vector3 pos, float volume, float pitch, boolean isGlobal, Predicate<FadeSound> func) {
        SoundCategory cat = SoundCategory.MASTER;
        if (sound instanceof CategorizedSoundEvent) {
            cat = ((CategorizedSoundEvent) sound).getCategory();
        }
        FadeSound posSound = new FadeSound(sound, cat, volume, pitch, pos, isGlobal);
        posSound.setRefreshFunction(func);
        Minecraft.getInstance().getSoundHandler().play(posSound);
        return posSound;
    }

    @OnlyIn(Dist.CLIENT)
    public static float getSoundVolume(SoundCategory cat) {
        return Minecraft.getInstance().gameSettings.getSoundLevel(cat);
    }

    @OnlyIn(Dist.CLIENT)
    public static void playSoundClient(SoundEvent sound, float volume, float pitch) {
        ClientPlayerEntity player = Minecraft.getInstance().player;
        if (player != null) {
            player.playSound(sound, volume, pitch);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static void playSoundClientWorld(CategorizedSoundEvent sound, BlockPos pos, float volume, float pitch) {
        playSoundClientWorld(sound, sound.getCategory(), pos, volume, pitch);
    }

    @OnlyIn(Dist.CLIENT)
    public static void playSoundClientWorld(SoundEvent sound, SoundCategory cat, BlockPos pos, float volume, float pitch) {
        if (Minecraft.getInstance().world != null) {
            Minecraft.getInstance().world.playSound(Minecraft.getInstance().player, pos.getX(), pos.getY(), pos.getZ(), sound, cat, volume, pitch);
        }
    }

}
