package fr.benoit.modbus.exemple;

import fr.benoit.modbus.lib.CommunicationFactory;
import fr.benoit.modbus.lib.DecodedResponse;
import fr.benoit.modbus.lib.UniteInterpreter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class SocketReader extends Thread {

    InputStream inputStream;
    OutputStream outputStream;
    CommunicationFactory com;

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
                while (inputStream.available() > 0) {
                    count = inputStream.read(buffer);
                    output.write(buffer, 0, count);
                    System.out.println(count);
                    System.out.println("Data:");
                    for (byte b : output.toByteArray()) {
                        System.out.printf("0x%02X ", b);
                    }
                    System.out.println("Done");
                    DecodedResponse decode = UniteInterpreter.decode(output.toByteArray());
                    System.out.println(decode);
                    if (com == null && decode != null) {
                        com = new CommunicationFactory(decode.getReceiverStation(), decode.getReceiverNetwork(), decode.getReceiverPort(),
                                decode.getTransmitterStation(), decode.getTransmitterNetwork(), decode.getTransmitterPort());
                    }
                    switch (decode.getResponseType()) {
                        case WRITE_OBJECT:
                            outputStream.write(com.generateAck(true));
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
