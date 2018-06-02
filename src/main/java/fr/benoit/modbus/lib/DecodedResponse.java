package fr.benoit.modbus.lib;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DecodedResponse {


    private byte transmitterStation;
    private byte transmitterNetwork;
    private byte transmitterPort;

    private byte receiverStation;
    private byte receiverNetwork;
    private byte receiverPort;

    private RequestType responseType;
    private int startAddr;
    private List<Integer> data;

    @Override
    public String toString() {
        return "DecodedResponse{" +
                "transmitterStation=" + transmitterStation +
                ", transmitterNetwork=" + transmitterNetwork +
                ", transmitterPort=" + transmitterPort +
                ", receiverStation=" + receiverStation +
                ", receiverNetwork=" + receiverNetwork +
                ", receiverPort=" + receiverPort +
                ", responseType=" + responseType +
                ", startAddr=" + startAddr +
                ", data=" + data +
                '}';
    }
}
