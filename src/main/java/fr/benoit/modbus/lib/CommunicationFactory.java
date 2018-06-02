package fr.benoit.modbus.lib;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import static fr.benoit.modbus.lib.RequestType.ACK_NOK;
import static fr.benoit.modbus.lib.RequestType.ACK_OK;

/**
 * <br>
 * Author : <a href="https://git-01.dev.uhcwork.net/u/Rapace">Wazo</a><br>
 * Project modbusunite
 * <br>
 */
public class CommunicationFactory {

    private static final byte[] MODBUS_HEADER = {(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x00}; //+ 2 octet de longueur
    private static final byte[] NPDU_HEADER = {(byte) 0xf0, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00};
    //Type + Station E + network|porte E + Station D + network|porte D

    //APDU : Code requette + Cat 6 + seg + type
    private static final byte[] DATA_HEADER = {(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00};
    //addr pFaible + addr pFort + qte pFaible + qte pFort
    //Valeurs : 2 octet par valeur

    private byte transmitterStation;
    private byte transmitterNetwork;
    private byte transmitterPort;

    private byte receiverStation;
    private byte receiverNetwork;
    private byte receiverPort;

    public CommunicationFactory(int transmitterStation, int transmitterNetwork, int transmitterPort, int receiverStation,
                                int receiverNetwork, int receiverPort) {
        this.transmitterStation = (byte) transmitterStation;
        this.transmitterNetwork = (byte) transmitterNetwork;
        this.transmitterPort = (byte) transmitterPort;
        this.receiverStation = (byte) receiverStation;
        this.receiverNetwork = (byte) receiverNetwork;
        this.receiverPort = (byte) receiverPort;
    }


    public byte[] generateWriteVarFrame(int startAddr, List<Integer> values) {
        byte[] frame = null;
        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        byte[] modHeader = MODBUS_HEADER.clone();
        byte[] npduHeader = generateNPDU();

        byte[] apduHeader = createAPDU(RequestType.WRITE_OBJECT);
        byte[] dataHeader = DATA_HEADER.clone();
        int size = values.size();
        byte[] valuesBytes = new byte[size * 2];


        dataHeader[0] = (byte) (startAddr & 0x00FF);
        dataHeader[1] = (byte) ((startAddr & 0xFF00) >> 8);

        dataHeader[2] = (byte) (size & 0x00FF);
        dataHeader[3] = (byte) ((size & 0xFF00) >> 8);

        int counter = 0;
        for (int data : values) {
            valuesBytes[counter] = (byte) (data & 0x00FF);
            valuesBytes[counter + 1] = (byte) ((data & 0xFF00) >> 16);
            counter += 2;
        }


        byte[] endOfFrame = mergeBytesArrays(npduHeader, apduHeader, dataHeader, valuesBytes);
        int bytesCount = endOfFrame.length;


        //size
        modHeader[5] = (byte) (bytesCount & 0x00FF);
        modHeader[6] = (byte) (bytesCount & 0xFF00 >> 16);
        try {
            stream.write(modHeader);
            stream.write(endOfFrame);
            frame = stream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }


        return frame;
    }

    public byte[] generateAck(boolean isOk) {
        byte[] modHeader = MODBUS_HEADER.clone();
        byte[] npduHeader = generateNPDU();
        byte[] apduHeader = isOk ? new byte[]{ACK_OK.type} : new byte[]{ACK_NOK.type};
        byte[] endOfFrame = mergeBytesArrays(npduHeader, apduHeader);
        int bytesCount = endOfFrame.length;
        modHeader[5] = (byte) (bytesCount & 0x00FF);
        modHeader[6] = (byte) (bytesCount & 0xFF00 >> 16);
        return mergeBytesArrays(modHeader, endOfFrame);
    }

    private byte[] generateNPDU() {
        byte[] npduHeader = NPDU_HEADER.clone();
        npduHeader[1] = transmitterStation;
        npduHeader[2] = (byte) (((transmitterNetwork & 0x0F) << 4) + (transmitterPort & 0x0F));
        npduHeader[3] = receiverStation;
        npduHeader[4] = (byte) (((receiverNetwork & 0x0F) << 4) + (receiverPort & 0x0F));
        return npduHeader;
    }

    private byte[] mergeBytesArrays(byte[] ... arrays){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        for (byte[] array : arrays) {
            try {
                stream.write(array);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return stream.toByteArray();
    }


    private byte[] createAPDU(RequestType requestType) {
        return new byte[]{requestType.type, (byte) 0x06, (byte) 0x68, (byte) 0x07};
    }

}
