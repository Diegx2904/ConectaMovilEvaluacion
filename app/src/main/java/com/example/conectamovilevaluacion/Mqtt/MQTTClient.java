package com.example.conectamovilevaluacion.Mqtt;

import android.content.Context;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class MQTTClient {

    private MqttClient mqttClient;
    private String brokerUrl = "tcp://broker.example.com:1883"; // Cambiar por la URL de tu broker
    private String clientId = "AndroidClient";

    public MQTTClient(Context context) {
        try {
            mqttClient = new MqttClient(brokerUrl, clientId, new MemoryPersistence());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setCallback(MqttCallback callback) {
        mqttClient.setCallback(callback);
    }

    public void connectToBroker() {
        MqttConnectOptions options = new MqttConnectOptions();
        options.setCleanSession(true);

        try {
            IMqttToken token = null;
            mqttClient.connect(options);
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // Conexión exitosa
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Error en la conexión
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void subscribeToTopic(String topic, int qos) {
        try {
            mqttClient.subscribe(topic, qos);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void publishMessage(String topic, String message, int qos, boolean retained) {
        try {
            MqttMessage mqttMessage = new MqttMessage();
            mqttMessage.setPayload(message.getBytes());
            mqttMessage.setQos(qos);
            mqttMessage.setRetained(retained);
            mqttClient.publish(topic, mqttMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void disconnect() {
        try {
            mqttClient.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
