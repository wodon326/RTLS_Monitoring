
public interface RTLS_Variable {
	byte STX = (byte)0x02;
	byte ETX = (byte)0x03;
	byte CMD_RTDATA = (byte)0x00;
	byte CMD_ALLSTAT = (byte)0x01;
	byte CMD_MSG = (byte)0x02;
	byte CMD_PATH = (byte)0x02;
	byte CMD_LOGIN = (byte)0x10;
	byte normal = (byte)0x00;
	byte danger = (byte)0xFF;
}
