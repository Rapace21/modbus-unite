package fr.benoit.modbus.lib;


public enum RequestType {
    WRITE_INTERNAL_WORD((byte) 0x14),
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
