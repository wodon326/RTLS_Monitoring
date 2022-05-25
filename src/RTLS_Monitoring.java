import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
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
	private InputStream is = null;
	private ObjectInputStream ois;
	private OutputStream os;
	private ObjectOutputStream oos;
	private JPanel contentPane;
	private HashMap<Integer,JLabel> client_location = new HashMap<Integer,JLabel>();
	private JMenu Client_path;
	private JMenu Client_rescue;

	public static void main(String[] args) throws IOException {
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

		RTLS_Monitoring frame = new RTLS_Monitoring(socket);
		frame.setVisible(true);

		Monitor_thread thread = new Monitor_thread(frame);
		thread.start();
	}
	public RTLS_Monitoring(Socket socket) throws IOException {
		os = socket.getOutputStream();
		oos = new ObjectOutputStream(os);
		is = socket.getInputStream();
		ois = new ObjectInputStream(is);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//monitoring 모두 종료
		setBounds(100, 100, 500, 350);
		setTitle("monitoring");

		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		Client_path = new JMenu("Client_path");
		menuBar.add(Client_path);

		Client_rescue = new JMenu("Client_rescue");
		menuBar.add(Client_rescue);
		JMenuItem Specific_location_danger_alerts = new JMenuItem("Specific location danger alerts");
		Specific_location_danger_alerts.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Location_Alerts Location_Alerts = new Location_Alerts(oos);
				Location_Alerts.setVisible(true);
			}
		});
		menuBar.add(Specific_location_danger_alerts);

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

	public ObjectOutputStream getOos() {
		return oos;
	}

	public ObjectInputStream getOis() {
		return ois;
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
	public void remove_Client(int id){
		for (int i=0; i<Client_path.getItemCount(); ++i) {
			if(id==Integer.parseInt(Client_path.getItem(i).getText()))
				Client_path.remove(Client_path.getItem(i));
		}
		for (int i=0; i<Client_rescue.getItemCount(); ++i) {
			if(id==Integer.parseInt(Client_rescue.getItem(i).getText()))
				Client_rescue.remove(Client_rescue.getItem(i));
		}
		contentPane.remove(client_location.get(id));
		client_location.remove(id);
		contentPane.validate();
		contentPane.repaint();
		String message = "Client#"+id+"가 종료하였습니다.";
		JOptionPane.showMessageDialog(null, message);
	}
	public void ShowSOS(int ID,int State, int X, int Y){
		JLabel alerts = new JLabel("SOS");
		alerts.setBounds(X, Y, 57, 15);
		alerts.setForeground(Color.RED);
		contentPane.add(alerts);
		repaint();
		String State_str;
		if(State==danger)
			State_str = "Danger";
		else
			State_str = "Normal";
		String message = "Client#"+ID+"가 SOS 요청을 하였습니다.\n"+"X : "+X+" Y : "+Y+ " State : "+State_str;
		JOptionPane.showMessageDialog(null, message);
	}
	public void Client_Danger(int ID,int State, int X, int Y){
		String State_str;
		if(State==danger)
			State_str = "Danger";
		else
			State_str = "Normal";
		String message = "Client#"+ID+"가 위험지역에 오래 머물고있습니다.\n"+"X : "+X+" Y : "+Y+ " State : "+State_str;
		JOptionPane.showMessageDialog(null, message);
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
	public void Client_Rescue_Menuadd (JMenuItem menu_item) {
		Client_rescue.add(menu_item);
	}

	public void showMessage(int id) {
		String message = "Client#"+id+"가 접속하였습니다.";
		JOptionPane.showMessageDialog(null, message);
	}

}

