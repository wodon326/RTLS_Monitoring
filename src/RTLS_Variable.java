
public interface RTLS_Variable {
	byte STX = (byte)0x02;
	byte ETX = (byte)0x03;
	byte CMD_RTDATA = (byte)0x00;
	byte CMD_ALLSTAT = (byte)0x01;
	byte CMD_MSG = (byte)0x02;
	byte CMD_PATH = (byte)0x03;
	byte CMD_LOGIN = (byte)0x10;
	byte CMD_Client_Danger = (byte) 0x11;
	byte CMD_SOS = (byte)0x33;
	byte CMD_RESCUE = (byte)0x34;
	byte CMD_LOCATION_ALERTS = (byte) 0x35;
	byte CMD_EXIT = (byte)0x44;
	byte normal = (byte)0x00;
	byte danger = (byte)0xFF;
}
