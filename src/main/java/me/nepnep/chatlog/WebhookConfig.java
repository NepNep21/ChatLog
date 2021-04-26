package me.nepnep.chatlog;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;

@Config(name = "chatlog")
public class WebhookConfig implements ConfigData {
    boolean useWebhook = false;
    String webHookUrl = "";
    String webhookName = "";
    String webhookAvatar = "";
}
