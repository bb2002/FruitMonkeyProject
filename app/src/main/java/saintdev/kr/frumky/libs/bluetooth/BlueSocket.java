package saintdev.kr.frumky.libs.bluetooth;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class BlueSocket extends Thread {
    private BluetoothSocket socket = null;

    private OutputStream oStream = null;
    private InputStream iStream = null;

    private OnBlueSocketListener recvListener = null;


    public BlueSocket(BluetoothSocket socket) throws IOException {
        this.socket = socket;
        this.oStream = socket.getOutputStream();
        this.iStream = socket.getInputStream();
    }

    /**
     * Bluetooth connection 의 데이터 처리
     */
    @Override
    public void run() {
        super.run();

        while(true) {
            String msg = receiveDataFromDevice();

            if(this.recvListener != null && msg != null) {
                this.recvListener.onReceive(msg);
            }
        }
    }

    @Override
    public void interrupt() {
        super.interrupt();
        try {
            oStream.close();
            iStream.close();
            socket.close();
        } catch(Exception ex) {}
    }

    /**
     * 상대 장치로 데이터를 보냅니다.
     */
    private String mStrDelimiter = "\n";

    public boolean send(String msg) {
        msg += mStrDelimiter;
        try {
            oStream.write(msg.getBytes());
            return true;
        } catch(Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    /**
     * 장치로부터 값을 받을 리스너를 설정합니다.
     */
    public void setReceiveListener(OnBlueSocketListener listener) {
        this.recvListener = listener;
    }

    public interface OnBlueSocketListener {
        void onReceive(String msg);
    }

    /**
     * 실제 장치로부터 데이터를 받습니다.
     */
    private char mCharDelimiter = '\n';
    private int readBufferPosition = 0;
    private byte[] readBuffer = new byte[1024];

    private String receiveDataFromDevice() {
        try {
            int byteAvailable = iStream.available();
            if (byteAvailable > 0) {
                byte[] packetBytes = new byte[byteAvailable];

                iStream.read(packetBytes);
                for (int i = 0; i < byteAvailable; i++) {
                    byte b = packetBytes[i];
                    if (b == mCharDelimiter) {
                        byte[] encodedBytes = new byte[readBufferPosition];

                        System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
                        readBufferPosition = 0;

                        return new String(encodedBytes, "US-ASCII");
                    } else {
                        readBuffer[readBufferPosition++] = b;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
