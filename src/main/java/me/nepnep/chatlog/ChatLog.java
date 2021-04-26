package me.nepnep.chatlog;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.WebhookClientBuilder;
import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.text.Text;
import org.apache.logging.log4j.LogManager;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

public class ChatLog implements ClientModInitializer {

	private static boolean useWebhook;
	private static String webhookUrl;
	private static String webhookName;
	private static String webhookAvatar;
	private static WebhookClient webhook;

	@Override
	public void onInitializeClient() {
		AutoConfig.register(WebhookConfig.class, GsonConfigSerializer::new);
		WebhookConfig config = AutoConfig.getConfigHolder(WebhookConfig.class).getConfig();

		useWebhook = config.useWebhook;
		webhookUrl = config.webHookUrl;
		webhookName = config.webhookName;
		webhookAvatar = config.webhookAvatar;

		if (useWebhook) {
			WebhookClientBuilder webhookBuilder = new WebhookClientBuilder(webhookUrl);
			webhookBuilder.setThreadFactory(job -> {
				Thread thread = new Thread(job);
				thread.setName("Webhook");
				thread.setDaemon(true);
				return thread;
			});
			webhook = webhookBuilder.build();
		}
	}

	public static void handle(Text text) {
		final String date = new Date().toString();
		final String log = String.format("logs/chat-log-%s.log", date).replaceAll("\\s|:", "-");
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(log, true))) {
			writer.append(date + " " + text.getString());
			writer.newLine();
		} catch (IOException e) {
			LogManager.getLogger("ChatLog").error("IOException at ChatLog.handle()", e);
		}

		if (useWebhook) {
			WebhookMessageBuilder builder = new WebhookMessageBuilder();
			builder.setUsername(webhookName);
			builder.setAvatarUrl(webhookAvatar);
			builder.setContent(text.getString());
			webhook.send(builder.build());
		}
	}
}
