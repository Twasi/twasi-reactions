package net.twasiplugin.reactions;

import net.twasi.core.plugin.TwasiPlugin;
import net.twasi.core.plugin.api.TwasiUserPlugin;

public class ReactionsPlugin extends TwasiPlugin {

    public Class<? extends TwasiUserPlugin> getUserPluginClass() {
        return ReactionsUserPlugin.class;
    }

}
