/*
 * Copyright (c) 2014 Inventit Inc.
 */
package io.inventit.dev.mqtt.paho.jetty8;

import static org.junit.Assert.*;

import java.net.URI;

import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.junit.Test;

public class Jdk16MqttWebSocketAsyncClientTest {

	@Test
	public void test_newWebSocketNetworkModule() throws MqttException {
		final URI uri = URI.create("wss://your-ws-broker/mqtt");
		assertEquals(
				WebSocketNetworkModule.class,
				new Jdk16MqttWebSocketAsyncClient(uri.toString(), "clientId")
						.newWebSocketNetworkModule(uri, "mqtt",
								new MqttConnectOptions()).getClass());
	}

}
