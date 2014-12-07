/*
 * Copyright (c) 2014 Inventit Inc.
 */
package io.inventit.dev.mqtt.paho.jetty8;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.ConnectException;
import java.net.URI;
import java.util.concurrent.TimeUnit;

import org.eclipse.jetty.websocket.WebSocket;
import org.eclipse.jetty.websocket.WebSocketClient;
import org.eclipse.jetty.websocket.WebSocketClientFactory;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.internal.NetworkModule;
import org.eclipse.paho.client.mqttv3.logging.Logger;
import org.eclipse.paho.client.mqttv3.logging.LoggerFactory;

public class WebSocketNetworkModule implements NetworkModule,
		WebSocket.OnBinaryMessage {

	private static final String CLASS_NAME = WebSocketNetworkModule.class
			.getName();
	private static final Logger log = LoggerFactory.getLogger(
			LoggerFactory.MQTT_CLIENT_MSG_CAT, CLASS_NAME);

	/**
	 * WebSocket URI
	 */
	private final URI uri;

	/**
	 * Sub-Protocol
	 */
	private final String subProtocol;

	/**
	 * A stream for outgoing data
	 */
	private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream() {
		@Override
		public void flush() throws IOException {
			final byte[] data;
			synchronized (this) {
				data = toByteArray();
				reset();
			}
			getConnection().sendMessage(data, 0, data.length);
		}
	};

	/**
	 * A pair of streams for incoming data
	 */
	private final PipedOutputStream receiverStream = new PipedOutputStream();
	private final PipedInputStream inputStream;

	private WebSocketClientFactory factory;
	private WebSocketClient client;
	private int conTimeout;
	private Connection connection;

	/**
	 * Constructs a new WebSocketNetworkModule using the specified URI.
	 * 
	 * @param uri
	 * @param subProtocol
	 * @param resourceContext
	 */
	public WebSocketNetworkModule(URI uri, String subProtocol,
			String resourceContext) {
		log.setResourceName(resourceContext);
		this.uri = uri;
		this.subProtocol = subProtocol;
		try {
			this.inputStream = new PipedInputStream(receiverStream);
		} catch (IOException unexpected) {
			throw new IllegalStateException(unexpected);
		}
	}

	protected Connection getConnection() {
		return connection;
	}

	/**
	 * A factory method for {@link WebSocketClient} class
	 * 
	 * @return
	 */
	protected WebSocketClient createWebSocketClient() {
		factory = new WebSocketClientFactory();
		factory.getSslContextFactory().setTrustAll(false);
		try {
			factory.start();
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
		final WebSocketClient client = factory.newWebSocketClient();
		// you can manipulate the client by overriding this method.
		return client;
	}

	/**
	 * Starts the module, by creating a TCP socket to the server.
	 */
	@Override
	public void start() throws IOException, MqttException {
		final String methodName = "start";
		try {
			// @TRACE 252=connect to host {0} port {1} timeout {2}
			if (log.isLoggable(Logger.FINE)) {
				log.fine(
						CLASS_NAME,
						methodName,
						"252",
						new Object[] { uri.toString(),
								Integer.valueOf(uri.getPort()),
								Long.valueOf(conTimeout * 1000) });
			}
			client = createWebSocketClient();
			client.setProtocol(subProtocol);
			if (conTimeout > 0) {
				connection = client.open(uri, this, conTimeout,
						TimeUnit.SECONDS);
			} else {
				// wait until a connection is established
				connection = client.open(uri, this).get();
			}

		} catch (ConnectException ex) {
			// @TRACE 250=Failed to create TCP socket
			log.fine(CLASS_NAME, methodName, "250", null, ex);
			throw new MqttException(
					MqttException.REASON_CODE_SERVER_CONNECT_ERROR, ex);

		} catch (Exception ex) {
			// @TRACE 250=Failed to create TCP socket
			log.fine(CLASS_NAME, methodName, "250", null, ex);
			throw new MqttException(MqttException.REASON_CODE_UNEXPECTED_ERROR,
					ex);
		}
	}

	@Override
	public InputStream getInputStream() throws IOException {
		return inputStream;
	}

	@Override
	public OutputStream getOutputStream() throws IOException {
		return outputStream;
	}

	/**
	 * Stops the module, by closing the web socket.
	 */
	@Override
	public void stop() throws IOException {
		if (connection != null && connection.isOpen()) {
			connection.close();
		}
		if (factory != null && factory.isRunning()) {
			try {
				factory.stop();
			} catch (Exception e) {
				throw new IllegalStateException(e);
			}
			factory.destroy();
		}
		connection = null;
		client = null;
		factory = null;
	}

	/**
	 * Set the maximum time in seconds to wait for a socket to be established
	 * 
	 * @param timeout
	 *            in seconds
	 */
	public void setConnectTimeout(int timeout) {
		this.conTimeout = timeout;
	}

	@Override
	public void onMessage(byte[] payload, int offset, int len) {
		try {
			this.receiverStream.write(payload, offset, len);
			this.receiverStream.flush();
		} catch (IOException e) {
			log.fine(CLASS_NAME, "onWebSocketError", "401", null, e);
			throw new IllegalStateException(e);
		}
	}

	@Override
	public void onOpen(Connection connection) {
		if (log.isLoggable(Logger.FINE)) {
			log.fine(CLASS_NAME, "onOpen", "116", new Object[] { uri.toString()
					+ ", WebSocket CONNECTED." });
		}
	}

	@Override
	public void onClose(int closeCode, String message) {
		if (log.isLoggable(Logger.FINE)) {
			log.fine(CLASS_NAME, "onClose", "116",
					new Object[] { uri.toString() + ", WebSocket CLOSED." });
		}
	}
}
