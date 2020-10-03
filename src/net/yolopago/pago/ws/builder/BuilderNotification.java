package net.yolopago.pago.ws.builder;

import net.yolopago.pago.db.entity.Notification;
import net.yolopago.pago.ws.dto.notification.NotificationDto;

public class BuilderNotification {

	public Notification build(NotificationDto notificationDto) {
		Notification notification = new Notification();

		notification.set_id(notificationDto.getId());
		notification.setContent(notificationDto.getContent());

		return notification;
	}
}
