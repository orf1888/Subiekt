package widok;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import utils.Globals;

public class UpdateProgress extends JDialog
{
	private static final long serialVersionUID = -8399271453582221616L;
	private final JPanel contentPanel = new JPanel();
	private JLabel lblStatus;
	private JProgressBar progressBar;
	private static final int estimatedLines = 100;

	public void setProgress(int progress)
	{
		progressBar.setValue(Math.min(estimatedLines, progress));
		SwingUtilities.invokeLater(new Runnable()
		{

			@Override
			public void run()
			{
				progressBar.repaint();

			}
		});
	}

	public void setLblStatus(final String status)
	{
		SwingUtilities.invokeLater(new Runnable()
		{

			@Override
			public void run()
			{
				lblStatus.setText(status);
				lblStatus.repaint();
			}
		});
	}

	public UpdateProgress(WidokGlowny widokGlowny) {
		super(widokGlowny);
		setSize(new Dimension(300, 100));
		setTitle("Aktualizacja...");
		setResizable(false);
		setIconImage(Globals.IkonaAplikacji.getImage());
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		setLocationRelativeTo((JFrame) null);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		{
			lblStatus = new JLabel("");
			contentPanel.add(lblStatus, BorderLayout.CENTER);
		}
		{
			progressBar = new JProgressBar(0, estimatedLines);
			progressBar.setBackground(new Color(124, 252, 0));
			progressBar.setStringPainted(true);
			progressBar.setForeground(new Color(0, 0, 0));
			contentPanel.add(progressBar, BorderLayout.SOUTH);
		}
		setVisible(true);
	}
}
