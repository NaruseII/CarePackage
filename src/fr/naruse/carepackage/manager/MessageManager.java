package fr.naruse.carepackage.manager;

import fr.naruse.carepackage.main.CarePackagePlugin;

public class MessageManager {

    protected CarePackagePlugin pl;

    protected MessageManager(CarePackagePlugin pl) {
        this.pl = pl;
    }

    public static class StringManager extends MessageManager{
        private String lang;
        public StringManager(CarePackagePlugin pl) {
            super(pl);
            String currentLang = pl.getConfig().getString("currentLang");
            if(currentLang.equalsIgnoreCase("english")){
                lang = "english.";
            }else if(currentLang.equalsIgnoreCase("french")){
                lang = "french.";
            }else{
                lang = "english.";
                pl.getLogger().warning("Lang '"+currentLang+"' can't be recognize! Using english.");
            }
        }

        public String get(String path){
            String msg = pl.getConfigurations().getMessageConfiguration().getString(lang+path);

            if(msg == null){
                throw new NullPointerException("Unable to get path '"+lang+path+"'!");
            }

            return msg.replace("&", "§");
        }

        public String get(String path, String[] toReplace, String[] replacedBy) {
            String msg = get(path);
            if(path.equals("sign.bow.isOpened.isWaiting.line4") && get("sign.bow.isOpened.isWaiting.line4").equals("§5Splegg")){
                msg = "§5BowSpleef";
            }
            if(toReplace.length != 0){
                for (int i = 0; i < toReplace.length; i++) {
                    String to = "{"+toReplace[i]+"}";
                    String by = replacedBy[i];
                    msg = msg.replace(to, by);
                }
            }
            return msg.replace("&", "§");
        }

        public void setLang(String lang) {
            this.lang = lang+".";
        }
    }
}

