import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Queue;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JMenuBar;


public class RTLS_Monitoring extends JFrame implements RTLS_Variable  {
	//통신할 때 데이터를 받을 때 필요한 변수
	private JPanel contentPane;
	private HashMap<Integer,JLabel> client_location = new HashMap<Integer,JLabel>();
	private JMenu Client_path;

	public static void main(String[] args) {
		Socket socket = null;
		try {
			socket = new Socket("localhost",3001);
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		RTLS_Monitoring frame = new RTLS_Monitoring();
		frame.setVisible(true);

		Monitor_thread thread = new Monitor_thread(frame, socket);
		thread.start();
	}

	/**
	 * Create the frame.
	 */
	public RTLS_Monitoring() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//monitoring 모두 종료
		setBounds(100, 100, 500, 350);
		setTitle("monitoring");

		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		Client_path = new JMenu("Client_path");
		menuBar.add(Client_path);

		//배경 그리기
		ImageIcon icon;
		icon = new ImageIcon("RTLS map.png");
		contentPane = new JPanel(){
			public void paintComponent(Graphics g) {
				Dimension d = getSize();
				g.drawImage(icon.getImage(), 0, 0, d.width, d.height, null);
			}
		};

		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
	}

	public boolean containsKey(int id) {
		return client_location.containsKey(id);
	}
	public void Set_Client_Location(int id,int x,int y) {
		((JLabel)client_location.get(id)).setLocation(x, y);
	}

	public void Set_Client_Foreground(int id,Color color) {
		((JLabel)client_location.get(id)).setForeground(color);
	}
	public void Add_Client(int id, JLabel label) {
		contentPane.add(label);
		client_location.put(id, label);
	}
	public JLabel Copy_Client(int id) {
		JLabel Copy_Client = (JLabel)client_location.get(id);
		int x_copy=Copy_Client.getX();
		int y_copy=Copy_Client.getY();

		JLabel Client_Copy_Label = new JLabel(Integer.toString(id));
		Client_Copy_Label.setBounds(0, 0, 57, 15);
		Client_Copy_Label.setLocation(x_copy, y_copy);
		Client_Copy_Label.setForeground(Copy_Client.getForeground());
		return  Client_Copy_Label;
	}

	public void Client_Path_Menuadd (JMenuItem menu_item) {
		Client_path.add(menu_item);
	}

	public void showMessage(int id) {
		String message = "Client#"+id+"가 접속하였습니다.";
		JOptionPane.showMessageDialog(null, message);
	}

}

