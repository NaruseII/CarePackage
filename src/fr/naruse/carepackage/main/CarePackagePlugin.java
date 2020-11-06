package fr.naruse.carepackage.main;

import fr.naruse.carepackage.carepackage.CarePackage;
import fr.naruse.carepackage.carepackage.CarePackages;
import fr.naruse.carepackage.cmd.CarePackageCommand;
import fr.naruse.carepackage.config.Configurations;
import fr.naruse.carepackage.manager.MessageManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class CarePackagePlugin extends JavaPlugin {

    private Configurations configurations;
    private MessageManager.StringManager messageManager;
    private CarePackages carePackages;

    @Override
    public void onEnable() {
        super.onEnable();

        if(!new File(getDataFolder(), "config.yml").exists()){
            saveResource("config.yml", false);
        }

        this.configurations = new Configurations(this);
        this.messageManager = new MessageManager.StringManager(this);
        this.carePackages = new CarePackages(this);

        getCommand("cp").setExecutor(new CarePackageCommand(this));
        getCommand("carepackage").setExecutor(new CarePackageCommand(this));
    }

    @Override
    public void onDisable() {
        super.onDisable();
        for (CarePackage carePackage : carePackages.getCarePackages()) {
            carePackage.disable();
        }
    }

    public Configurations getConfigurations() {
        return configurations;
    }

    public MessageManager.StringManager getMessageManager() {
        return messageManager;
    }

    public CarePackages getCarePackages() {
        return carePackages;
    }
}
