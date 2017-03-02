package com.sergeyd;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.sergeyd.socket.test.R;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 *
 */
public class MainActivity extends Activity {
	private static final String HOST = "change to your host";
	private static final int PORT = 3000;
	public static final String TAG = "YYY";
	private volatile boolean running = false;

	private Runnable socketWriter = new Runnable() {
		@Override
		public void run() {
			Socket socket;
			try {
				socket = new Socket(HOST, PORT);
				Log.i(TAG, String.format("write size: %d, read size: %d, keep-alive: %b, OOBInline: %b, linger: %d, No-delay: %b",
						socket.getSendBufferSize(), socket.getReceiveBufferSize(), socket.getKeepAlive(), socket.getOOBInline(), socket.getSoLinger(), socket.getTcpNoDelay()));

				// you could play with this setting and
				// see how it makes socket behavior different on android
				//socket.setSendBufferSize(4096);

				OutputStream out = socket.getOutputStream();
				int iter = 0;

				while (true) {
					out.write(UUID.randomUUID().toString().getBytes(StandardCharsets.UTF_8));
					// I'm flushing here on purpose to show that it doesn't help
					out.flush();
					iter++;
					Log.d(TAG, "ping " + iter);
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						Log.e("YYY", e.getMessage(), e);
					}
				}

			} catch (IOException e) {
				Log.e(TAG, e.getMessage(), e);
			} finally {
				running = false;
			}
		}
	};

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Button button = (Button) findViewById(R.id.button);
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (running) {
					Toast.makeText(MainActivity.this, "Already running", Toast.LENGTH_LONG).show();
				} else {
					running = true;
					new Thread(socketWriter).start();
				}
			}
		});
	}
}
