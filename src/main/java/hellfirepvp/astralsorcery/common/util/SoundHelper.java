package hellfirepvp.astralsorcery.common.util;

import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
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
        if(sound instanceof CategorizedSoundEvent) {
            category = ((CategorizedSoundEvent) sound).getCategory();
        }
        world.playSound(null, posX, posY, posZ, sound, category, volume, pitch);
    }

    @SideOnly(Side.CLIENT)
    public static void playSoundClient(SoundEvent sound, float volume, float pitch) {
        EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
        player.playSound(sound, volume, pitch);
    }

}
