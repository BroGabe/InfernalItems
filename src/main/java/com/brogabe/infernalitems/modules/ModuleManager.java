package com.brogabe.infernalitems.modules;

import com.brogabe.infernalitems.InfernalItems;
import com.brogabe.infernalitems.modules.types.CooldownModule;
import com.brogabe.infernalitems.modules.types.ItemModule;
import com.brogabe.infernalitems.modules.types.WaterModule;
import lombok.Getter;

public class ModuleManager {

    @Getter
    private final ItemModule itemModule;

    @Getter
    private final CooldownModule cooldownModule;

    @Getter
    private final WaterModule waterModule;

    public ModuleManager(InfernalItems plugin) {
        waterModule = new WaterModule(plugin);
        itemModule = new ItemModule(plugin);
        cooldownModule = new CooldownModule(plugin);
    }
}
