package fr.naruse.carepackage.config;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import fr.naruse.carepackage.carepackage.CarePackageType;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class Configurations {
    private JavaPlugin pl;

    private File carePackageModelFile;

    private File messageFile;
    private FileConfiguration messageConfiguration;

    private List<FileConfiguration> models = Lists.newArrayList();
    private Map<String, FileConfiguration> modelMap = Maps.newHashMap();
    private Map<FileConfiguration, File> configurationFile = Maps.newHashMap();

    public Configurations(JavaPlugin pl) {
        this.pl = pl;
        reload();
    }

    public void reload() {
        this.models.clear();
        this.messageFile = new File(pl.getDataFolder(), "messages.yml");
        this.messageConfiguration = new YamlConfiguration();
        this.carePackageModelFile = new File(pl.getDataFolder(), "model");

        try{
            if(!carePackageModelFile.exists()){
                carePackageModelFile.mkdirs();
            }
            if(!messageFile.exists()){
                messageFile.createNewFile();
                saveResource("resources/messages.yml", messageFile);
            }

            if(carePackageModelFile.listFiles() != null){
                for (File file : carePackageModelFile.listFiles()) {
                    if(file.isFile() && file.getName().contains(".yml")){
                        FileConfiguration configuration = new YamlConfiguration();
                        configuration.load(file);
                        if(configuration.getString("name") != null){
                            CarePackageType type = CarePackageType.registerCarePackage(configuration.getString("name"));
                            if(type == null){
                                pl.getLogger().log(Level.SEVERE, "Can't register CarePackageType '"+configuration.getString("name")+"'");
                            }else{
                                pl.getLogger().log(Level.INFO, "CarePackageType '"+configuration.getString("name")+"' registered");
                            }
                        }
                        models.add(configuration);
                        modelMap.put(file.getName().replace(".yml", ""), configuration);
                        configurationFile.put(configuration, file);
                    }
                }
            }

            messageConfiguration.load(messageFile);

            Reader reader = new InputStreamReader(pl.getResource("resources/messages.yml"), "UTF8");
            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(reader);
            messageConfiguration.setDefaults(defConfig);
        }catch (Exception e){
            e.printStackTrace();
        }

        saveConfigs();
    }

    private void saveResource(String path, File messageFile) {
        try{
            InputStream inputStream = pl.getResource(path);
            byte[] buffer = new byte[inputStream.available()];
            inputStream.read(buffer);

            OutputStream outStream = new FileOutputStream(messageFile);
            outStream.write(buffer);

            inputStream.close();
            outStream.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void saveConfigs() {
        try{
            messageConfiguration.save(messageFile);
            for (FileConfiguration model : models) {
                File f = configurationFile.get(model);
                model.save(f);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void reset(int id) {
        if(id == 0){
            if(messageFile.exists()){
                messageFile.delete();
            }
        }
        reload();
    }

    public FileConfiguration getMessageConfiguration() {
        return messageConfiguration;
    }

    public List<FileConfiguration> getModels() {
        return models;
    }

    public FileConfiguration createConfigurationModel(String name) {
        File file = new File(carePackageModelFile, name+".yml");
        FileConfiguration configuration = new YamlConfiguration();
        try{
            if(!file.exists()){
                file.createNewFile();
            }
            configuration.load(file);
            models.add(configuration);
            modelMap.put(name, configuration);
            configurationFile.put(configuration, file);
            return configuration;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}

