import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;

public class Location_Alerts extends JFrame implements RTLS_Variable{
    private int x;
    private int y;
    private JLabel alerts;
    public Location_Alerts(ObjectOutputStream oos) {
        alerts = new JLabel("X");
        alerts.setForeground(Color.RED);
        JPanel contentPane;
        ImageIcon icon;
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); //Client_path 하나만 종료
        setBounds(100, 100, 500, 374);
        icon = new ImageIcon("RTLS map.png");
        setTitle("Specific location danger alerts");
        //배경을 띄우고 경로를 그림
        contentPane = new JPanel(){
            public void paintComponent(Graphics g) {
                Dimension d = getSize();
                g.drawImage(icon.getImage(), 0, 0,d.width,d.height-50, null);//배경 그리기
            }
        };
        contentPane.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                x = e.getX();
                y = e.getY();
                if(x<470&&y<270){
                    alerts.setLocation(x,y);
                    repaint();
                }
            }
        });

        JButton Send_Button = new JButton("전송");
        Send_Button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                byte[] data_buf;
                byte[] data_Location = new byte[8];
                byte[] int_byte = new byte[4];
                int_byte = intToBytes(x);
                System.arraycopy(int_byte, 0, data_Location, 0, 4);
                int_byte = intToBytes(y);
                System.arraycopy(int_byte, 0, data_Location, 4, 4);
                data_buf = makepacket(CMD_LOCATION_ALERTS, data_Location);
                dispose();
                try {
                    oos.writeObject(data_buf);
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }
        });
        Send_Button.setBounds(140, 300, 74, 23);
        contentPane.add(Send_Button);

        JButton Cancel_Button = new JButton("취소");
        Cancel_Button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        Cancel_Button.setBounds(300, 300, 74, 23);
        contentPane.add(Cancel_Button);
        alerts.setSize(100, 20);
        contentPane.add(alerts);
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);
    }
    // 패킷 만드는 함수
    public static byte[] makepacket(byte cmd, byte[] data) {
        byte[] pack = new byte[data.length + 3];
        pack[0] = STX;
        pack[1] = cmd;
        System.arraycopy(data, 0, pack, 2, data.length);
        pack[pack.length - 1] = ETX;
        return pack;
    }

    // int -> byte[] 함수
    public static byte[] intToBytes(final int i) {
        ByteBuffer bytebuffer = ByteBuffer.allocate(4);
        bytebuffer.putInt(i);
        return bytebuffer.array();
    }
}
