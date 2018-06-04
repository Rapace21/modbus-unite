package fr.benoit.modbus.exemple;

import fr.benoit.modbus.lib.CommunicationFactory;
import fr.benoit.modbus.lib.DecodedResponse;
import fr.benoit.modbus.lib.UniteInterpreter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class SocketReader extends Thread {

    InputStream inputStream;
    OutputStream outputStream;
    CommunicationFactory com;
    byte[] overflow = null;

    public SocketReader(Socket s) {
        try {
            this.inputStream = s.getInputStream();
            this.outputStream = s.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        start();
    }

    @Override
    public void run() {
        int count = 0;
        byte[] buffer = new byte[4096];
        while (true) {
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            output.reset();
            try {
                while (inputStream.available() > 0 || overflow != null) {
                    if(overflow == null) {
                        count = inputStream.read(buffer);
                        output.write(buffer, 0, count);
                        System.out.println("Count : " + count);
                    }else{
                        output.write(overflow);
                        overflow = null;
                        System.out.println("Using overflow...");
                    }

                    System.out.println("Data:");
                    for (byte b : output.toByteArray()) {
                        System.out.printf("0x%02X ", b);
                    }
                    System.out.println("Done");
                    DecodedResponse decode = UniteInterpreter.decode(output.toByteArray());
                    output.reset();
                    System.out.println(decode);
                    if (com == null && decode != null) {
                        com = new CommunicationFactory(decode.getReceiverStation(), decode.getReceiverNetwork(), decode.getReceiverPort(),
                                decode.getTransmitterStation(), decode.getTransmitterNetwork(), decode.getTransmitterPort());
                    }
                    if (decode != null && decode.getOverflow() != null) {
                        System.out.println("Overflow : ");
                        for (byte b : decode.getOverflow()) {
                            System.out.printf("0x%02X ", b);
                        }
                        overflow = decode.getOverflow();
                    }
                    switch (decode.getResponseType()) {
                        case WRITE_OBJECT:
                            outputStream.write(com.generateAck(true));
                            break;
                        case READ_OBJECT:
                            List<Integer> lst = new ArrayList<>();
                            lst.add(5);
                            outputStream.write(com.generateReadVarResponse(lst));
                            break;
                        case READ_OBJECT_OK:
                            System.out.println("Readed value : " + decode.getData());
                            break;
                        case ACK_NOK:
                            System.out.println("Receive NOK");
                            break;
                        case ACK_OK:
                            System.out.println("Receive OK");
                            break;
                        default:
                            outputStream.write(com.generateAck(false));
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }
    }
}
