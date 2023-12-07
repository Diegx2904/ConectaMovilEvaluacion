package com.example.conectamovilevaluacion;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.conectamovilevaluacion.Mqtt.MQTTClient;

public class SendMessageActivity extends AppCompatActivity {

    private EditText messageEditText;
    private Button sendButton;
    private MQTTClient mqttClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_message);

        messageEditText = findViewById(R.id.messageEditText);
        sendButton = findViewById(R.id.sendButton);

        mqttClient = new MQTTClient(getApplicationContext());
        mqttClient.connectToBroker();

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = messageEditText.getText().toString();
                if (!message.isEmpty()) {
                    mqttClient.publishMessage("topic_name", message, 1, false);
                    messageEditText.setText(""); // Limpiar el campo después de enviar el mensaje
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mqttClient.disconnect();
    }
}
