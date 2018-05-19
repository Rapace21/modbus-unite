package fr.benoit.modbus;

import java.io.ByteArrayOutputStream;
import java.net.InetAddress;
import java.util.ArrayList;

/**
 * <br>
 * Author : <a href="https://git-01.dev.uhcwork.net/u/Rapace">Wazo</a><br>
 * Project modbusunite
 * <br>
 */
public class Main {

    private static byte[] modbusHeader = {(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x13, (byte) 0x00};
    private static byte[] npduHeader = {(byte) 0xf0, (byte) 0x30, (byte) 0x80, (byte) 0x0A, (byte) 0x80};
    private static byte[] adpuHeader = {(byte) 0x37, (byte) 0x06, (byte) 0x68, (byte) 0x07};
    private static byte[] data = {(byte) 0x64, (byte) 0x00, (byte) 0x03, (byte) 0x00, (byte) 0x0A, (byte) 0x00, (byte) 0x14, (byte) 0x00, (byte) 0x1E, (byte) 0x00};

    public static void main(String[] args) {
        InetAddress addr = null;
        InetAddress local = null;
        /*try {
            addr = Inet4Address.getByName("172.22.209.253");
            local = Inet4Address.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }*/
        try/*(Socket s = new Socket(addr, 502, local, 502))*/ {
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            output.write(modbusHeader);
            output.write(npduHeader);
            output.write(adpuHeader);
            output.write(data);
            CommunicationFactory factory = new CommunicationFactory(48, 3, 0, 10, 8, 0);
            ArrayList<Integer> value = new ArrayList<>();
            value.add(10);
            value.add(20);
            value.add(30);
            //output.writeTo(s.getOutputStream());
            output.flush();
            for(byte b : factory.generateWriteVarFrame(100, value)){
                System.out.printf("0x%02X ", b);
            }
            System.out.println();

            for(byte b : output.toByteArray()){
                System.out.printf("0x%02X ", b);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

}
