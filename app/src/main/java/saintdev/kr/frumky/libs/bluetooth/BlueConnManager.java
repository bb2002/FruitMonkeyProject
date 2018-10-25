package saintdev.kr.frumky.libs.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.HandlerThread;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class BlueConnManager {
    private static BlueSocket socket = null;        // Bluetooth Connection

    private AppCompatActivity context = null;
    private BluetoothAdapter btAdapter = null;

    public BlueConnManager(AppCompatActivity context) {
        this.context = context;
        this.btAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    /**
     * 블루투스 지원 여부를 확인한다.
     * @return boolean
     */
    public boolean isSupportBluetooth() {
        return this.btAdapter != null;
    }


    public boolean isEnableBluetooth() {
        return btAdapter.isEnabled();
    }

    /**
     * 연결할 장치를 선택 한다.
     */
    public void openConnectableList(final OnTargetDeviceSelected listener) {
        final Set<BluetoothDevice> devices = btAdapter.getBondedDevices();
        int connectableSize = devices.size();       // 페어링된 장치


        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("블루투스 장치 선택");

        if(connectableSize == 0) {
            /**
             * 페어링 된 장치가 없습니다.
             */
            builder.setTitle("장치가 없습니다.");
            builder.setItems(new CharSequence[]{"페어링 후 다시 시도하세요."}, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });

        } else {
            // List 에 장치를 추가한다.
            final ArrayList<BluetoothDevice> deviceList = new ArrayList<BluetoothDevice>(devices);        // 장치 목록
            final ArrayList<String> deviceNames = new ArrayList<String>();        // 장치 이름 목록
            for(BluetoothDevice dev : deviceList) {
                deviceNames.add(dev.getName());
            }

            final CharSequence[] items = deviceNames.toArray(new CharSequence[deviceNames.size()]);
            deviceNames.toArray(new CharSequence[deviceNames.size()]);

            builder.setItems(items, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int item) {
                    listener.onSelected(deviceList.get(item));
                }

            });
        }

        builder.setCancelable(true);
        AlertDialog alert = builder.create();
        alert.show();
    }

    public interface OnTargetDeviceSelected {
        void onSelected(BluetoothDevice devName);
    }


    /**
     * 특정 장치와 연결을 시도 합니다.
     * @param targetDevice
     * @return  실패시 False 를 리턴합니다.
     */
    public boolean tryConnectDevice(BluetoothDevice targetDevice) {
        UUID uuid = java.util.UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

        try {
            BluetoothSocket socket = targetDevice.createRfcommSocketToServiceRecord(uuid);
            socket.connect();       // 연결을 시도한다.

            BlueConnManager.socket = new BlueSocket(socket);
            BlueConnManager.socket.start();     // 소켓에 thread 를 start 한다.
            return true;
        } catch (Exception e) { // 블루투스 연결 중 오류 발생
            return false;
        }
    }

    public void close() {
        try {
            socket.interrupt();
            socket = null;
        } catch(Exception ex) {}
    }

    public static BlueSocket getSocket() {
        return socket;
    }
}
