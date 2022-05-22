import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

import javax.swing.JLabel;
import javax.swing.JMenuItem;

public class Monitor_thread extends Thread implements RTLS_Variable {
    private InputStream is = null;
    private ObjectInputStream ois;
    private OutputStream os;
    private ObjectOutputStream oos;
    private RTLS_Monitoring frame;
    private byte[] buf = new byte[512];
    private HashMap<Integer, Integer> client_Path_linenum = new HashMap<Integer, Integer>();
    private HashMap<Integer, Queue<Pair>> client_Path_queue = new HashMap<Integer, Queue<Pair>>();
    private JMenuItem MenuItem_Client_path;


    public Monitor_thread(RTLS_Monitoring frame, Socket socket) {
        try {
            os = socket.getOutputStream();
            oos = new ObjectOutputStream(os);
            is = socket.getInputStream();
            ois = new ObjectInputStream(is);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        this.frame = frame;
        setName("monitor");
    }

    @Override
    public void run() {
        JLabel Client_Label;
        int client_num = 0;
        byte[] byte_recode = new byte[10];
        byte[] byte_int = new byte[4];
        int xArray[] = null;
        int yArray[] = null;
        int id;
        int x;
        int y;
        byte state = (byte) 0x00;
        while (true) {
            try {
                buf = (byte[]) ois.readObject();
                if (buf[0] == STX && buf[buf.length - 1] == ETX) {
                    switch (buf[1]) {
                        case CMD_LOGIN:
                            int ID = (int) buf[2];
                            if (!frame.containsKey(ID)) { // 기존에 모니터링되고있던 클라이언트가 아니면 JLabel을 만들어 hashmap에 넣고 클라이언트의 경로를 구하는
                                // 메뉴 추가
                                Client_Label = new JLabel(Integer.toString(ID));
                                Client_Label.setBounds(394, 225, 57, 15);
                                if (state == danger) {
                                    Client_Label.setForeground(Color.RED);
                                } else {
                                    Client_Label.setForeground(Color.BLACK);
                                }
                                frame.Add_Client(ID, Client_Label);
                                client_Path_queue.put(ID, new LinkedList<>());
                                client_Path_linenum.put(ID, 0);
                                // 클라이언트의 경로를 구하는 메뉴 추가
                                MenuItem_Client_path = new JMenuItem(Integer.toString(ID));
                                // 메뉴 클릭했을 때 데이터베이스에서 클라이언트의 위치를 가져오고 Client_path에 경로와 현재 위치와 현재 상태를 띄움
                                MenuItem_Client_path.addActionListener(new ActionListener() {
                                    public void actionPerformed(ActionEvent e) {
                                        try {
                                            byte[] buf_path = new byte[4];
                                            buf_path[0] = STX;
                                            buf_path[1] = CMD_PATH;
                                            buf_path[2] = (byte) ID;
                                            buf_path[3] = ETX;
                                            int path_id = Integer.parseInt(e.getActionCommand());
                                            client_Path_linenum.put(ID, 0);
                                            oos.writeObject(buf_path);

                                        } catch (IOException ex) {
                                            throw new RuntimeException(ex);
                                        }
                                    }
                                });
                                frame.Client_Path_Menuadd(MenuItem_Client_path);
                                frame.showMessage(ID);
                            }
                            break;
                        case CMD_PATH:
                            int id_db;
                            int x_db;
                            int y_db;
                            byte[] byte_recode_path = new byte[10];
                            System.arraycopy(buf, 2, byte_recode_path, 0, 10);
                            id_db = (int) byte_recode_path[0];
                            state = byte_recode_path[1];
                            System.arraycopy(byte_recode_path, 2, byte_int, 0, 4);
                            x_db = ByteBuffer.wrap(byte_int).getInt();
                            System.arraycopy(byte_recode_path, 6, byte_int, 0, 4);
                            y_db = ByteBuffer.wrap(byte_int).getInt();
                            client_Path_linenum.put(id_db, client_Path_linenum.get(id_db) + 1);
                            client_Path_queue.get(id_db).add(new Pair(x_db, y_db));
                            if (buf[12] == (byte) 1) {
                                Queue<Pair> queue = client_Path_queue.get(id_db);
                                // 데이터 개수만큼 배열을 생성하고 큐에 쌓인 데이터를 xArray, yArray에 집어넣음
                                xArray = new int[client_Path_linenum.get(id_db)];
                                yArray = new int[client_Path_linenum.get(id_db)];
                                int n = 0;
                                while (!queue.isEmpty()) {
                                    Pair pair = queue.poll();
                                    xArray[n] = pair.getX();
                                    yArray[n] = pair.getY();
                                    n++;
                                }
                                // 클라이언트의 현재상태를 Client_path에 복사하기 위해 새로운 JLabel에 복사 후 Client_path 띄움
                                JLabel Client_Path_Label = frame.Copy_Client(id_db);
                                Client_path client_path = new Client_path(xArray, yArray, client_Path_linenum.get(id_db),
                                        Client_Path_Label);
                                client_path.setVisible(true);
                            }
                            break;
                        case CMD_ALLSTAT:// 클라이언트들의 모든 상태와 위치가 담긴 패킷을 분석
                            client_num = (int) buf[2];
                            // 클라이언트 수만큼 상태와 위치 분석
                            for (int i = 0; i < client_num; i++) {
                                System.arraycopy(buf, 3 + i * 10, byte_recode, 0, 10);
                                id = (int) byte_recode[0];
                                state = byte_recode[1];
                                System.arraycopy(byte_recode, 2, byte_int, 0, 4);
                                x = ByteBuffer.wrap(byte_int).getInt();
                                System.arraycopy(byte_recode, 6, byte_int, 0, 4);
                                y = ByteBuffer.wrap(byte_int).getInt();

                                // 클라이언트의 상태와 위치가 담긴 JLabel을 hashmap에 넣어서 관리
                                if (frame.containsKey(id)) // 기존에 모니터링되고있던 클라이언트면 hashmap에서 가져와 상태와 위치 교체
                                {
                                    frame.Set_Client_Location(id, x, y);
                                    if (state == danger) {
                                        frame.Set_Client_Foreground(id, Color.RED);
                                    } else {
                                        frame.Set_Client_Foreground(id, Color.BLACK);
                                    }
                                }
                            }
                            break;
                        default:
                            break;
                    }
                }
            } catch (ClassNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}