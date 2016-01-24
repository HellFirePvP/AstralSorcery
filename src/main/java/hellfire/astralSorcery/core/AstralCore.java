package hellfire.astralSorcery.core;

import net.minecraftforge.fml.relauncher.IFMLCallHook;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 *
 * Created by HellFirePvP @ 24.01.2016 20:15
 */
@IFMLLoadingPlugin.TransformerExclusions("hellfire.astralSorcery.core")
@IFMLLoadingPlugin.Name("AstralCore")
public class AstralCore implements IFMLLoadingPlugin, IFMLCallHook {

    @Override
    public Void call() throws Exception {
        return null;
    }

    @Override
    public String[] getASMTransformerClass() {
        return new String[0];
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
        return "hellfire.astralSorcery.core.AstralAccessTransformer";
    }
}
