package fr.benoit.modbus.lib;

import java.util.ArrayList;
import java.util.Arrays;

public class UniteInterpreter {


    public static DecodedResponse decode(byte[] raw) {

        DecodedResponse response = new DecodedResponse();
        if (!checkMagic(raw)) {
            System.out.println("Magic modbus missmatch, return : null");
            return null;
        }

        int size = getSize(raw);
        if (size != raw.length - 6) {
            if (size < raw.length - 6) {
                response.setOverflow(Arrays.copyOfRange(raw, size + 6, raw.length));
                raw = Arrays.copyOfRange(raw, 0, size + 6);
            } else {
                System.out.println("Size missmatch, return : null");
                return null;
            }
        }

        /*if (raw[7] != (byte) 0xF0) {
            System.out.println("Bad address type, or bad NPDU type, return : null");
            System.out.printf("0x%04X", raw[7]);
            return null;
        }*/

        //Decode addresses
        response.setTransmitterStation(raw[8]);
        response.setTransmitterNetwork((byte) ((raw[9] >> 4) & (byte) 0x0F));
        response.setTransmitterPort((byte) (raw[9] & (byte) 0x0F));

        response.setReceiverStation(raw[10]);
        response.setReceiverNetwork((byte) ((raw[11] >> 4) & (byte) 0x0F));
        response.setReceiverPort((byte) (raw[11] & (byte) 0x0F));

        //Decode APDU
        RequestType type = RequestType.getByByte(raw[12]);
        if (type == null) {
            System.out.println("Request type is not supported or does not exist, return : null");
            return null;
        }
        response.setResponseType(type);
        if (type == RequestType.ACK_OK || type == RequestType.ACK_NOK) {
            return response;
        }
        switch (type) {
            case ACK_NOK:
            case ACK_OK:
                return response;
            case WRITE_OBJECT:
                return getWriteObjectResponse(raw, response);
            case READ_OBJECT:
                return getReadObjectFrame(raw, response);
            case READ_OBJECT_OK:
                return getReadObjectResponse(raw, response);
            default:
                return null;
        }

    }

    private static DecodedResponse getReadObjectFrame(byte[] raw, DecodedResponse response) {
        response.setStartAddr((raw[17] << 8) + raw[16]);
        ArrayList<Integer> data = new ArrayList<>();
        data.add((raw[19] << 8) + raw[18]);
        response.setData(data);
        return response;
    }

    private static DecodedResponse getReadObjectResponse(byte[] raw, DecodedResponse response) {
        ArrayList<Integer> data = new ArrayList<>();
        int offset = 0;
        for (int i = 0; i < (raw.length - 13) / 2; i++) {
            data.add((raw[15 + offset] << 8) + raw[14 + offset]);
            offset += 2;
        }
        response.setData(data);
        return response;
    }

    private static DecodedResponse getWriteObjectResponse(byte[] raw, DecodedResponse response) {
        response.setStartAddr((raw[17] << 8) + raw[16]);
        int nbr = (raw[19] << 8) + raw[18];
        ArrayList<Integer> data = new ArrayList<>();
        int offset = 0;
        for (int i = 0; i < nbr; i++) {
            data.add((raw[21 + offset] << 8) + raw[20 + offset]);
            offset += 2;
        }
        response.setData(data);
        return response;
    }

    private static boolean checkMagic(byte[] raw) {
        return raw[0] == 0x00 && raw[1] == 0x00 && raw[2] == 0x00 && raw[3] == 0x01 && raw[4] == 0x00;
    }

    private static int getSize(byte[] raw) {
        return (raw[6] << 8) + raw[5];
    }


}
