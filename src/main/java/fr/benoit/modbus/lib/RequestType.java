package fr.benoit.modbus.lib;


public enum RequestType {
    READ_OBJECT((byte) 0x36),
    READ_OBJECT_OK((byte) 0x66),
    WRITE_OBJECT((byte) 0x37),
    ACK_OK((byte) 0xFE),
    ACK_NOK((byte) 0xFD);


    byte type;


    RequestType(byte type) {
        this.type = type;
    }


    public static RequestType getByByte(byte type){
        for (RequestType requestType : RequestType.values()) {
            if( requestType.type == type){
                return requestType;
            }
        }
        return null;
    }



}
