/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util;

import hellfirepvp.astralsorcery.client.util.PositionedLoopSound;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: SoundHelper
 * Created by HellFirePvP
 * Date: 06.12.2016 / 12:45
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
        if(sound instanceof SoundUtils.CategorizedSoundEvent) {
            category = ((SoundUtils.CategorizedSoundEvent) sound).getCategory();
        }
        world.playSound(null, posX, posY, posZ, sound, category, volume, pitch);
    }

    @SideOnly(Side.CLIENT)
    public static PositionedLoopSound playSoundLoopClient(SoundEvent sound, Vector3 pos, float volume, float pitch, PositionedLoopSound.ActivityFunction func) {
        SoundCategory cat = SoundCategory.MASTER;
        if(sound instanceof SoundUtils.CategorizedSoundEvent) {
            cat = ((SoundUtils.CategorizedSoundEvent) sound).getCategory();
        }
        PositionedLoopSound posSound = new PositionedLoopSound(sound, cat, volume, pitch, pos);
        posSound.setRefreshFunction(func);
        Minecraft.getMinecraft().getSoundHandler().playSound(posSound);
        return posSound;
    }

    @SideOnly(Side.CLIENT)
    public float getSoundVolume(SoundCategory cat) {
        return Minecraft.getMinecraft().gameSettings.getSoundLevel(cat);
    }

    @SideOnly(Side.CLIENT)
    public static void playSoundClient(SoundEvent sound, float volume, float pitch) {
        EntityPlayerSP player = Minecraft.getMinecraft().player;
        player.playSound(sound, volume, pitch);
    }

    @SideOnly(Side.CLIENT)
    public static void playSoundClientWorld(SoundUtils.CategorizedSoundEvent sound, BlockPos pos, float volume, float pitch) {
        playSoundClientWorld(sound, sound.getCategory(), pos, volume, pitch);
    }

    @SideOnly(Side.CLIENT)
    public static void playSoundClientWorld(SoundEvent sound, SoundCategory cat, BlockPos pos, float volume, float pitch) {
        if(Minecraft.getMinecraft().world != null) {
            Minecraft.getMinecraft().world.playSound(Minecraft.getMinecraft().player, pos.getX(), pos.getY(), pos.getZ(), sound, cat, volume, pitch);
        }
    }

}
