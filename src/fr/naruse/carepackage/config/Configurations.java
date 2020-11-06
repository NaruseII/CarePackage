package fr.naruse.carepackage.config;

import com.google.common.collect.Lists;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.List;

public class Configurations {
    private JavaPlugin pl;

    private File messageFile;
    private FileConfiguration messageConfiguration;

    private List<FileConfiguration> models = Lists.newArrayList();

    public Configurations(JavaPlugin pl) {
        this.pl = pl;
        reload();
    }

    public void reload() {
        this.models.clear();
        this.messageFile = new File(pl.getDataFolder(), "messages.yml");
        this.messageConfiguration = new YamlConfiguration();
        File carePackageModelFile = new File(pl.getDataFolder(), "model");

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
                        models.add(configuration);
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
}

