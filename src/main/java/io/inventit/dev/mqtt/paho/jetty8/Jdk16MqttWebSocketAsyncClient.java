/*
 * Copyright (c) 2014 Inventit Inc.
 */
package io.inventit.dev.mqtt.paho.jetty8;

import io.inventit.dev.mqtt.paho.MqttWebSocketAsyncClient;

import java.net.URI;

import org.eclipse.paho.client.mqttv3.MqttClientPersistence;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttPingSender;
import org.eclipse.paho.client.mqttv3.internal.NetworkModule;

public class Jdk16MqttWebSocketAsyncClient extends MqttWebSocketAsyncClient {

	public Jdk16MqttWebSocketAsyncClient(String serverURI, String clientId,
			MqttClientPersistence persistence, MqttPingSender pingSender,
			String loggerName) throws MqttException {
		super(serverURI, clientId, persistence, pingSender, loggerName);
	}

	public Jdk16MqttWebSocketAsyncClient(String serverURI, String clientId,
			MqttClientPersistence persistence, String loggerName)
			throws MqttException {
		super(serverURI, clientId, persistence, loggerName);
	}

	public Jdk16MqttWebSocketAsyncClient(String serverURI, String clientId,
			MqttClientPersistence persistence) throws MqttException {
		super(serverURI, clientId, persistence);
	}

	public Jdk16MqttWebSocketAsyncClient(String serverURI, String clientId,
			String loggerName) throws MqttException {
		super(serverURI, clientId, loggerName);
	}

	public Jdk16MqttWebSocketAsyncClient(String serverURI, String clientId)
			throws MqttException {
		super(serverURI, clientId);
	}

	/**
	 * A factory method for instantiating a {@link NetworkModule} with websocket
	 * support. Subclasses is able to extend this method in order to create an
	 * arbitrary {@link NetworkModule} class instance.
	 * 
	 * @param uri
	 * @param subProtocol
	 *            Either `mqtt` for MQTT v3 or `mqttv3.1` for MQTT v3.1
	 * @param options
	 * @return
	 */
	@Override
	protected NetworkModule newWebSocketNetworkModule(URI uri,
			String subProtocol, MqttConnectOptions options) {
		final WebSocketNetworkModule netModule = new WebSocketNetworkModule(
				uri, subProtocol, getClientId());
		netModule.setConnectTimeout(options.getConnectionTimeout());
		return netModule;
	}
}
