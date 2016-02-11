package hellfire.astralSorcery.common.lib;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.EnumRarity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.util.EnumHelper;

/**
 * This class is part of the Astral Sorcery Mod
 * <p/>
 * Created by HellFirePvP @ 24.01.2016 21:15
 */
public class LibMisc {

    public static CreativeTabs creativeTabAstralSorcery;

    public static EnumRarity rarityOld = EnumHelper.addRarity("Old", EnumChatFormatting.GRAY, "Old");
    public static EnumRarity raritySacred = EnumHelper.addRarity("Sacred", EnumChatFormatting.GOLD, "Sacred");

}
