# MQTT over WebSocket  library for JDK1.6/Android

This library offers MQTT client functionality over WebSocket transport with [Paho](http://www.eclipse.org/paho/) library and [Jetty 8](http://www.eclipse.org/jetty/) library.

# Supported MQTT Version

1. MQTT v3.1   (with Sub-Protocol: `mqttv3.1`)
1. MQTT v3.1.1 (with Sub-Protocol: `mqtt`) ... DEFAULT

# Supported Paho MQTT library version and Jetty WebSocket Client version

1. [Paho org.eclipse.paho.mqtt.java 1.0.0](http://git.eclipse.org/c/paho/org.eclipse.paho.mqtt.java.git/tag/?id=v1.0.0)
1. [Jetty websocket-client 8.1.16.v20140903](http://download.eclipse.org/jetty/stable-8/apidocs/index.html?org/eclipse/jetty/websocket/package-summary.html)

# Supported JDK/JRE Version

JDK/JRE 1.6+ is supported.

# Dependencies

The following libraries are requried as well as `org.eclipse.paho.client.mqttv3-1.0.0.jar`.

| GroupId         | ArtifactId     | Version        |
|-----------------|----------------|----------------|
|org.eclipse.jetty|jetty-http      |8.1.16.v20140903|
|org.eclipse.jetty|jetty-io        |8.1.16.v20140903|
|org.eclipse.jetty|jetty-util      |8.1.16.v20140903|
|org.eclipse.jetty|jetty-websocket |8.1.16.v20140903|

## maven pom.xml settings

Adds the following elements to your pom.xml if you're using maven.

```
  <dependency>
    <groupId>io.inventit.dev</groupId>
    <artifactId>mqtt-websocket-jdk16-android</artifactId>
    <version>1.0.0</version>
  </dependency>
  <dependency>
    <groupId>io.inventit.dev</groupId>
    <artifactId>mqtt-websocket-java</artifactId>
    <version>1.0.1</version>
    <classifier>jdk16</classifier>
    </dependency>
  <dependency>
    <groupId>org.eclipse.jetty</groupId>
    <artifactId>jetty-websocket</artifactId>
    <version>8.1.16.v20140903</version>
  </dependency>
  <dependency>
    <groupId>org.eclipse.paho</groupId>
    <artifactId>org.eclipse.paho.client.mqttv3</artifactId>
    <version>1.0.0</version>
  </dependency>
  <dependency>
    <groupId>com.noveogroup.android</groupId>
    <artifactId>android-logger</artifactId>
    <version>1.3.1</version>
  </dependency>
```

The `android-logger` is included because it includes SLF4J API, which Jetty uses as a default logging framework. You can use other SLF4J API compliant logger library as you like.

# How to install
Download the jar file from the [releases tab](https://github.com/inventit/mqtt-websocket-jdk16-android/releases/) and add it to your IDE or your favorite building tool.

For maven users, the [releases page](https://github.com/inventit/mqtt-websocket-jdk16-android/releases/) instruction helps you to install the jar file to your local repo.

# How to use
You can use this library as the same manner as Paho's library but use `Jdk16MqttWebSocketAsyncClient` instead of Paho's classes such as `MqttClient` and `MqttAsyncClient`.

The `Jdk16MqttWebSocketAsyncClient` supports the following URI schimes:

1. `ws://<host>:<port>`  ... for a plain WebSocket
1. `wss://<host>:<port>` ... for a WebSocket with SSL/TLS
1. `tcp://<host>:<port>` ... for a plain TCP MQTT socket
1. `ssl://<host>:<port>` ... for a secure SSL/TLS MQTT socket

Here is sample code to use `Jdk16MqttWebSocketAsyncClient`.

      // Plain MQTT
      // final String uriString = "tcp://your-mqtt-broker:1883";

      // MQTT over WebSocket
      final String uriString = "wss://your-ws-broker/mqtt";

      // Credentials
      final String clientId = "your-client-id";
      final String userName = "your-user-name";
      final String password = "your-password";

      final IMqttAsyncClient client = new Jdk16MqttWebSocketAsyncClient(
      		uriString, clientId, new MemoryPersistence());
      final MqttConnectOptions options = new MqttConnectOptions();
      options.setMqttVersion(MqttConnectOptions.MQTT_VERSION_3_1_1);
      options.setCleanSession(true);
      options.setUserName(userName);
      options.setPassword(password.toCharArray());
      client.connect(options, null, new IMqttActionListener() {

        @Override
        public void onSuccess(IMqttToken asyncActionToken) {
          // on successfully connected
        }

        @Override
        public void onFailure(IMqttToken asyncActionToken,
            Throwable exception) {
          // on connection failure
        }
      );
      client.setCallback(new MqttCallback() {

        @Override
        public void messageArrived(String topic, MqttMessage message)
            throws Exception {
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken token) {
        }

        @Override
        public void connectionLost(Throwable cause) {
        }
      });

# How to build

## Library jar

Install maven then run the following command on the project root directory.

Note that Paho Java library is included in this project as the binary isn't uploaded to any maven repository yet.

    $ mvn clean package

Then you'll get `mqtt-websocket-jdk16-android-<version>.jar` under the `target` directory.

## Android Test app

With maven, run the following commands after installing the library jar file (mqtt-websocket-jdk16-android).

    $ cd src/test/android
    $ mvn clean package
    $ adb install -r target/mqtt-websocket-test.apk

Or use IDE to build an APK and install it to your Android device.

 * Tap an applicaiton icon image to publish a message, see `MainApplication#publish()` for detail
 * The app automatically subscribes a topic `/io/inventit/dev/android/testapp/android`, see `MainApplication#startTesting()` for detail

# Source Code License

See [LICENSE](https://github.com/inventit/mqtt-websocket-jdk16-android/blob/master/LICENSE) file.

# Change History

[1.0.0 : ???? ???, 2014](https://github.com/inventit/mqtt-websocket-jdk16-android/releases/tag/1.0.0)

* Initial
