package model;

import javax.swing.JFileChooser;
import javax.swing.UIManager;

public class FChooserPL extends JFileChooser
{

	private static final long serialVersionUID = 5843851153627085159L;
	public JFileChooser chooser;

	public FChooserPL() {
		UIManager.put("FileChooser.saveDialogTitleText", "Zapisz");
		UIManager.put("FileChooser.openDialogTitleText", "Wczytaj");
		UIManager.put("FileChooser.lookInLabelText", "Szukaj w");
		UIManager.put("FileChooser.saveButtonText", "Zapisz");
		UIManager.put("FileChooser.openButtonText", "Otworz");
		UIManager.put("FileChooser.cancelButtonText", "Anuluj");
		UIManager.put("FileChooser.updateButtonText", "Modyfikuj");
		UIManager.put("FileChooser.helpButtonText", "Pomoc");
		UIManager.put("FileChooser.fileNameLabelText", "Nazwa pliku:");
		UIManager.put("FileChooser.filesOfTypeLabelText", "Rozszerzenie:");
		UIManager.put("FileChooser.homeFolderToolTipText", "Pulpit");
		UIManager.put("FileChooser.upFolderToolTipText",
				"do góry o jeden poziom");
		UIManager.put("FileChooser.newFolderToolTipText", "Utwórz nowy folder");
		UIManager.put("FileChooser.listViewButtonToolTipText", "Lista");
		UIManager.put("FileChooser.detailsViewButtonToolTipText", "Szczegóły");
		chooser = new JFileChooser(".");
	}
}
