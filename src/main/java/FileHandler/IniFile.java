package FileHandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class IniFile {
    private String filename;
    private Properties properties;

    public IniFile(String filename) throws IOException {
        this.filename = filename;
        this.properties = new Properties();

        File file = new File(filename);
        if (file.exists()) {
            // If the file exists, load its contents into the Properties object
            try (FileInputStream fis = new FileInputStream(file)) {
                properties.load(fis);
            }
        } else {
            // If the file does not exist, create it
            file.createNewFile();
        }
    }

    public String getProperty(String section, String key) {
        return properties.getProperty(section + "." + key, "");
    }

    public void setProperty(String section, String key, String value) throws IOException {
        properties.setProperty(section + "." + key, value);
        System.out.println("Set " + section + ":" + key + " = " + value);
    }

    public void save() throws IOException {
        try (FileOutputStream fos = new FileOutputStream(filename)) {
            properties.store(fos, null);
        }catch (IOException e){
            System.out.println(e);
        }
        System.out.println("Saved");
    }
}
