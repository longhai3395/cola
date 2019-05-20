package com.honvay.cola.nc.provider;

import org.springframework.stereotype.Component;

import com.honvay.cola.nc.api.NotificationService;
import com.honvay.cola.nc.api.model.Notification;

/**
 * @author LIQIU created on 2018/12/25
 **/
@Component
public class NotificationServiceImpl implements NotificationService {

	private NotificationDispatcher dispatcher;

	public NotificationServiceImpl(NotificationDispatcher dispatcher) {
		this.dispatcher = dispatcher;
	}

	@Override
	public void notify(Notification notification) {
		this.dispatcher.dispatch(notification);
	}

}
