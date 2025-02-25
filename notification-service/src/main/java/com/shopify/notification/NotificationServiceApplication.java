package com.shopify.notification;

import com.shopify.notification.event.OrderNotificationEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.KafkaListener;

@SpringBootApplication
@Slf4j
public class NotificationServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(NotificationServiceApplication.class, args);
	}

	@KafkaListener(topics = "topic-order-place-event", groupId = "notification-group")
	public void sendNotification(OrderNotificationEvent orderNotificationEvent) {
		log.info(orderNotificationEvent.toString());
	}
}


