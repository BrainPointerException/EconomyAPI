package de.brainexception.reventuxcore.module;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import de.brainexception.reventuxcore.ReventuxCorePlugin;

public class BinderModule extends AbstractModule {

    /**
     * Plugin instance
     */
    private final ReventuxCorePlugin plugin;

    public BinderModule(ReventuxCorePlugin plugin) {
        this.plugin = plugin;
    }

    public Injector createInjector() {
        return Guice.createInjector(this);
    }

    @Override
    protected void configure() {
        this.bind(ReventuxCorePlugin.class).toInstance(this.plugin);
    }
}
