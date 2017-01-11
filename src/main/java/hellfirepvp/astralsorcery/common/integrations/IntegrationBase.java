/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.integrations;

import net.minecraftforge.fml.common.Loader;

import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: IntegrationBase
 * Created by HellFirePvP
 * Date: 10.01.2017 / 23:13
 */
public class IntegrationBase {

    private final String integrationModId;
    private final String integrationClassName;
    private IModIntegration loadedIntegration = null;

    public IntegrationBase(String modId, String fullyQualifiedIntegrationClassName) {
        this.integrationModId = modId;
        this.integrationClassName = fullyQualifiedIntegrationClassName;
    }

    public boolean instantiateAndInit() {
        if(!isLoaded()) return false;
        Class<? extends IModIntegration> integrationClass = getIntegrationClass();
        if(integrationClass == null) return false;
        try {
            loadedIntegration = integrationClass.newInstance();
        } catch (Exception e) {
            return false;
        }
        loadedIntegration.init();
        return true;
    }

    @Nullable
    public Class<? extends IModIntegration> getIntegrationClass() {
        try {
            return (Class<? extends IModIntegration>) Class.forName(integrationClassName);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    public String getIntegrationModId() {
        return integrationModId;
    }

    public boolean isLoaded() {
        return Loader.isModLoaded(integrationModId);
    }

}
