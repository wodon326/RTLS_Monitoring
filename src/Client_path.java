import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class Client_path extends JFrame {
    JPanel contentPane_path;
    ImageIcon icon;
    public Client_path(int[]X_Array,int[]Y_Array,int Line_Num,JLabel id) {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); //Client_path 하나만 종료
        setBounds(100, 100, 500, 324);
        icon = new ImageIcon("RTLS map.png");
        setTitle("ID : "+ id.getText()+" Path");
        //배경을 띄우고 경로를 그림
        contentPane_path = new JPanel(){
            public void paintComponent(Graphics g) {
                Dimension d = getSize();
                g.drawImage(icon.getImage(), 0, 0, d.width, d.height, null);//배경 그리기
                g.drawPolyline(X_Array, Y_Array, Line_Num);//Client의 경로를 그림
            }
        };
        contentPane_path.add(id);
        contentPane_path.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane_path);
        contentPane_path.setLayout(null);
    }
}
