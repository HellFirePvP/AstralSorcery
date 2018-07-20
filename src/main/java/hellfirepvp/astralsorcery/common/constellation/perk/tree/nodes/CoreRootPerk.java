/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.perk.tree.nodes;

import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CoreRootPerk
 * Created by HellFirePvP
 * Date: 11.07.2018 / 18:24
 */
public class CoreRootPerk extends KeyPerk {

    private static final int AMT_PERK_POINTS = 3;

    public CoreRootPerk() {
        super("core", 0, 0);
    }

    @Override
    public void onUnlockPerkServer(@Nullable EntityPlayer player, PlayerProgress progress, NBTTagCompound dataStorage) {
        super.onUnlockPerkServer(player, progress, dataStorage);

        NBTTagList listTokens = new NBTTagList();
        for (int i = 0; i < AMT_PERK_POINTS; i++) {
            String token = "core-root-tk-" + i;
            if (ResearchManager.grantFreePerkPoint(player, token)) {
                listTokens.appendTag(new NBTTagString(token));
            }
        }
        dataStorage.setTag("tokens", listTokens);
    }

    @Override
    public void onRemovePerkServer(EntityPlayer player, PlayerProgress progress, NBTTagCompound dataStorage) {
        super.onRemovePerkServer(player, progress, dataStorage);

        NBTTagList listTokens = dataStorage.getTagList("tokens", Constants.NBT.TAG_STRING);
        for (int i = 0; i < listTokens.tagCount(); i++) {
            String tk = listTokens.getStringTagAt(i);
            ResearchManager.revokeFreePoint(player, tk);
        }
    }

}
