/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.perk.node.key;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.constellation.ConstellationRegistry;
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import hellfirepvp.astralsorcery.common.perk.node.KeyPerk;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: KeyCore
 * Created by HellFirePvP
 * Date: 25.08.2019 / 18:17
 */
public class KeyCore extends KeyPerk {

    public static final ResourceLocation NAME = new ResourceLocation(AstralSorcery.MODID, "core");

    private static final int AMT_PERK_POINTS = 2;

    public KeyCore() {
        super(NAME, 0, 0);

        ConstellationRegistry.getMajorConstellations()
                .forEach(this::setRequireDiscoveredConstellation);
    }

    @Override
    public void onUnlockPerkServer(@Nullable PlayerEntity player, PlayerProgress progress, CompoundNBT dataStorage) {
        super.onUnlockPerkServer(player, progress, dataStorage);

        ListNBT listTokens = new ListNBT();
        for (int i = 0; i < AMT_PERK_POINTS; i++) {
            String token = "core-root-tk-" + i;
            if (ResearchManager.grantFreePerkPoint(player, token)) {
                listTokens.add(new StringNBT(token));
            }
        }
        dataStorage.put("tokens", listTokens);
    }

    @Override
    public void onRemovePerkServer(PlayerEntity player, PlayerProgress progress, CompoundNBT dataStorage) {
        super.onRemovePerkServer(player, progress, dataStorage);

        ListNBT listTokens = dataStorage.getList("tokens", Constants.NBT.TAG_STRING);
        for (int i = 0; i < listTokens.size(); i++) {
            String tk = listTokens.getString(i);
            ResearchManager.revokeFreePoint(player, tk);
        }
    }
}
