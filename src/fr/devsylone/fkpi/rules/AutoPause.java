package fr.devsylone.fkpi.rules;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.bukkit.configuration.ConfigurationSection;

public class AutoPause implements RuleValue {

    private boolean afterDay;
    private boolean afterCapture;

    public AutoPause() {
        this.afterDay = false;
        this.afterCapture = false;
    }

    public boolean doAfterDay() {
        return this.afterDay;
    }

    public boolean doAfterCapture() {
        return this.afterCapture;
    }

    public void setAfterDay(boolean afterDay) {
        this.afterDay = afterDay;
    }

    public void setAfterCapture(boolean afterCapture) {
        this.afterCapture = afterCapture;
    }

    @Override
    public String format() {
        if (!this.afterDay && !this.afterCapture) {
            return "§cDisabled";
        }
        return "§e" + (this.afterDay ? "After day" : "") + (this.afterCapture ? "After capture" : "");
    }

    @Override
    public JsonElement toJSON() {
        final JsonObject json = new JsonObject();
        json.addProperty("after-day", this.afterDay);
        json.addProperty("after-capture", this.afterCapture);
        return json;
    }

    @Override
    public void load(ConfigurationSection config) {
        this.afterDay = config.getBoolean("after-day");
        this.afterCapture = config.getBoolean("after-capture");
    }

    @Override
    public void save(ConfigurationSection config) {
        config.set("after-day", this.afterDay);
        config.set("after-capture", this.afterCapture);
    }
}
