package widok.podmiot;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import kontroler.PodmiotBaza;
import utils.Globals;
import utils.MojeUtils;

public class PodmiotOkno extends JFrame
{

	private static final long serialVersionUID = 3248301080100095973L;

	private final JPanel contentPane;

	private final PodmiotPanel panelPol;

	public PodmiotOkno() throws SQLException {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setIconImage(Globals.IkonaAplikacji.getImage());
		setSize(450, 300);
		setLocationRelativeTo(null);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));

		panelPol = new PodmiotPanel();
		contentPane.add(panelPol);

		JPanel panelPrzyciski = new JPanel();
		contentPane.add(panelPrzyciski, BorderLayout.SOUTH);

		JButton btnEdytuj = new JButton("Edytuj");
		panelPrzyciski.add(btnEdytuj);
		btnEdytuj.addActionListener(edytujListener);

		JButton btnAnuluj = new JButton("Anuluj");
		panelPrzyciski.add(btnAnuluj);
		btnAnuluj.addActionListener(anulujListener);
	}

	private final ActionListener edytujListener = new ActionListener()
	{

		@Override
		public void actionPerformed(ActionEvent arg0)
		{
			try
			{
				PodmiotBaza.edytuj(panelPol.pobierzZFormatki());
				dispose();
			} catch (Exception e)
			{
				MojeUtils.showError(e);
			}
		}
	};

	private final ActionListener anulujListener = new ActionListener()
	{

		@Override
		public void actionPerformed(ActionEvent arg0)
		{
			dispose();
		}
	};
}