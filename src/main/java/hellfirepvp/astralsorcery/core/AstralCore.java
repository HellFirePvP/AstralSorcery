/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.core;

import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.relauncher.IFMLCallHook;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AstralCore
 * Created by HellFirePvP
 * Date: 07.05.2016 / 02:55
 */
@IFMLLoadingPlugin.Name(value = "AstralCore")
@IFMLLoadingPlugin.TransformerExclusions({"hellfirepvp.astralsorcery.core"})
@IFMLLoadingPlugin.SortingIndex(1005)
public class AstralCore implements IFMLLoadingPlugin, IFMLCallHook {

    public AstralCore() {
        FMLLog.info("[AstralCore] Initialized.");
    }

    @Override
    public Void call() throws Exception {
        return null;
    }

    @Override
    public String[] getASMTransformerClass() {
        return null;
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {
    }

    @Override
    public String getAccessTransformerClass() {
        return "hellfirepvp.astralsorcery.core.AstralTransformer";
    }

}
