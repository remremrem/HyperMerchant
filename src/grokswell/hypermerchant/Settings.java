package grokswell.hypermerchant;

import java.io.File;

import net.citizensnpcs.api.util.DataKey;
import net.citizensnpcs.api.util.YamlStorage;

public class Settings {
    private final YamlStorage config;

    public Settings(HyperMerchantPlugin plugin) {
        config = new YamlStorage(new File(plugin.getDataFolder() + File.separator + "config.yml"), "HyperMerchant Configuration");
    }

    public void load() {
        config.load();
        DataKey root = config.getKey("");
        for (Setting setting : Setting.values())
            if (!root.keyExists(setting.path))
                root.setRaw(setting.path, setting.get());
            else
                setting.set(root.getRaw(setting.path));

        config.save();
    }

    public YamlStorage getConfig() {
        return config;
    }

    public enum Setting {
        ENABLE_SIGNS("enable_signs.default", 1),
        ENABLE_COMMAND("enable_command.default", 1),
        ENABLE_NPC("enable_npc.default", 1),
        WELCOME("welcome.default", "Welcome to our little shop."),
        FAREWELL("farewell.default", "We thank you for your continued patronage."),
        DENIAL("denial.default", "I'm afraid you are not a store member. " +
        		"I am not authorized to do business with you."),
        CLOSED("closed.default", "I am sorry, we are closed for business at this time.");

        private String path;
        private Object value;

        Setting(String path, Object value) {
            this.path = path;
            this.value = value;
        }

        public boolean asBoolean() {
            return (Boolean) value;
        }

        public double asDouble() {
            if (value instanceof String)
                return Double.valueOf((String) value);
            if (value instanceof Integer)
                return (Integer) value;
            return (Double) value;
        }

        public int asInt() {
            return (Integer) value;
        }

        public String asString() {
            return value.toString();
        }

        private Object get() {
            return value;
        }

        private void set(Object value) {
            this.value = value;
        }
    }
}
