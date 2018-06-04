package fr.benoit.modbus.exemple.client;

import fr.benoit.modbus.exemple.SocketReader;
import fr.benoit.modbus.lib.CommunicationFactory;

import java.io.ByteArrayOutputStream;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

/**
 * <br>
 * Author : <a href="https://git-01.dev.uhcwork.net/u/Rapace">Wazo</a><br>
 * Project modbusunite
 * <br>
 */
public class Main {

    public static void main(String[] args) {
        InetAddress addr = null;
        InetAddress local = null;
        try {
            addr = Inet4Address.getByName("172.22.209.253");
            local = Inet4Address.getByName("172.22.209.78");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        try(Socket s = new Socket(addr, 502, local, 503)) {
            SocketReader reader = new SocketReader(s);
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            CommunicationFactory factory = new CommunicationFactory(48, 8, 0,
                    3, 8, 0);
            ArrayList<Integer> value = new ArrayList<>();
            value.add(1);
            output.write(factory.generateWriteVarFrame(301, value));
            output.writeTo(s.getOutputStream());
            System.out.println("Printed");
            output.flush();
            output.reset();
            Thread.sleep(1000);
            output.write(factory.generatereadVarFrame(300, 3));
            output.writeTo(s.getOutputStream());
            output.flush();
            output.reset();
            reader.join();

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

}
