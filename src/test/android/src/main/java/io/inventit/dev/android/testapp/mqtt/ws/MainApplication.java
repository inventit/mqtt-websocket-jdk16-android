/*
 * Copyright (c) 2014 Inventit Inc.
 */
package io.inventit.dev.android.testapp.mqtt.ws;

import io.inventit.dev.mqtt.paho.PahoConsoleLogger;
import io.inventit.dev.mqtt.paho.jetty8.Jdk16MqttWebSocketAsyncClient;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

public class MainApplication extends Activity implements MqttCallback {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(MainApplication.class);

	/**
	 * {@link ScrollView}
	 */
	private ScrollView scrollView;

	/**
	 * {@link TextView}
	 */
	private TextView textView;

	private Jdk16MqttWebSocketAsyncClient client;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		LOGGER.info("Starting the activity...");
		super.onCreate(savedInstanceState);
		// Widgets
		setContentView(R.layout.simple);
		scrollView = (ScrollView) findViewById(R.id.scroll_view);
		textView = (TextView) findViewById(R.id.scroll_content);

		// MQTTT
		try {
			startTesting();
		} catch (MqttException e) {
			LOGGER.error("MQTT Error", e);
			throw new IllegalStateException(e);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		if (client != null) {
			try {
				client.disconnect();
			} catch (MqttException e) {
				e.printStackTrace();
			}
			try {
				client.close();
			} catch (MqttException e) {
				e.printStackTrace();
			}
			client = null;
		}
	}

	protected void startTesting() throws MqttException {
		// MQTT over WebSocket (MQTT v3.1)
		LOGGER.info("Starting tests for MQTT over WebSocket (MQTT v3.1)...");
		PahoConsoleLogger.enableLog();

		// Hive MQ Public MQTT Server
		// (http://www.hivemq.com/showcase/public-mqtt-broker/) =>
		// broker.mqtt-dashboard.com:8000 (non-SSL)
		// Mosquitto (http://test.mosquitto.org/ws.html) =>
		// ws://test.mosquitto.org/mqtt (non-SSL)
		// Or whatever you want to test

		// final String uriString = "ws://broker.mqtt-dashboard.com:8000/mqtt";
		// final String uriString = "ws://test.mosquitto.org:8080/mqtt";
		final String uriString = "ws://localhosy/mqtt";

		// Credentials
		final String clientId = "IVI:" + System.currentTimeMillis();

		appendText("ClientID=>" + clientId + "\n");

		client = new Jdk16MqttWebSocketAsyncClient(uriString, clientId,
				new MemoryPersistence());
		final MqttConnectOptions options = new MqttConnectOptions();
		options.setMqttVersion(MqttConnectOptions.MQTT_VERSION_3_1);
		options.setCleanSession(true);
		client.connect(options, null, new IMqttActionListener() {

			@Override
			public void onSuccess(IMqttToken asyncActionToken) {
				final String topic = "/io/inventit/dev/android/testapp/android";
				try {
					client.subscribe(topic, 0);
				} catch (MqttException e) {
					onFailure(asyncActionToken, e);
				}
				LOGGER.info("Subscribing [{}]", topic);
			}

			@Override
			public void onFailure(IMqttToken asyncActionToken,
					Throwable exception) {
				connectionLost(exception);
			}
		});
		client.setCallback(this);
		LOGGER.info("Reday for tests...");
	}

	/**
	 * When the image is tapped.
	 * 
	 * @param view
	 */
	public void publish(View view) {
		try {
			final String topic = "/io/inventit/dev/android/testapp/mqtt";
			final String message = "mqtt-websocket-test:"
					+ System.currentTimeMillis();
			LOGGER.info("Publishing a message => {} to the topic[{}]", message);
			client.publish(topic, message.getBytes(), 0, false);
			appendText("Published:" + message + "\n");
		} catch (MqttPersistenceException e) {
			appendText("Failed to publish:" + e.getMessage() + "\n");
			e.printStackTrace();
		} catch (MqttException e) {
			appendText("Failed to publish:" + e.getMessage() + "\n");
			e.printStackTrace();
		}
	}

	@Override
	public void messageArrived(String topic, MqttMessage message)
			throws Exception {
		LOGGER.info("messageArrived => {}, {}", topic, message);
		appendText("messageArrived:" + topic + "/" + message + "\n");
	}

	@Override
	public void deliveryComplete(IMqttDeliveryToken token) {
		LOGGER.info("deliveryComplete => {}", token);
		appendText("deliveryComplete:" + token + "\n");
	}

	@Override
	public void connectionLost(Throwable cause) {
		LOGGER.info("connectionLost", cause);
		appendText("connectionLost:" + cause.getMessage() + "\n");
		cause.printStackTrace();
	}

	/**
	 * Append a text to the textView widget
	 * 
	 * @param line
	 */
	protected void appendText(final String line) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				textView.append(line);
				scrollView.smoothScrollTo(0, textView.getBottom());
			}
		});
	}

}
