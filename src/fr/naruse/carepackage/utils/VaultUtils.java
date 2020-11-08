package fr.naruse.carepackage.utils;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultUtils {

    private static VaultUtils INSTANCE;

    private Economy economy = null;

    public VaultUtils() {
        INSTANCE = this;
        RegisteredServiceProvider<Economy> economyProvider = Bukkit.getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (economyProvider != null) {
            economy = economyProvider.getProvider();
        }
    }

    public boolean giveReward(Player player, int money) {
        if(economy == null){
            return false;
        }
        economy.depositPlayer(player, money);
        return true;
    }

    public static VaultUtils get(){
        if(INSTANCE == null){
            new VaultUtils();
        }
        return INSTANCE;
    }
}
