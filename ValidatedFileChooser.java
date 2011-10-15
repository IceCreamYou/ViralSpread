import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;


public class ValidatedFileChooser extends JFileChooser {
	private static final long serialVersionUID = -8192863096623921736L;
	
	private static final String[] INVALID_NAMES = {
		"com1",
		"com2",
		"com3",
		"com4",
		"com5",
		"com6",
		"com7",
		"com8",
		"com9",
		"lpt1",
		"lpt2",
		"lpt3",
		"lpt4",
		"lpt5",
		"lpt6",
		"lpt7",
		"lpt8",
		"lpt9",
		"con",
		"nul",
		"prn",
		".",
		".."
	};
	
	private String extension;
	
	private String[] allowed_extensions;
	
	@Override
	public void approveSelection() {
		File file = getSelectedFile();
		String filename = file.getName();
		/**
		 * We do a series of checks to make sure we have a valid filename:
		 * - The parent directory exists (it might not if a user includes a backslash in the name)
		 * - The name can only use valid characters
		 * - The name can't be too long or short
		 * - The name can't end with a space or period (a Windows restriction)
		 * - The name can't start with a period (this would make it hidden)
		 * - The name can't be a system reserved name
		 * 
		 * Then, if specified, we add the required extension to the name if it
		 * is not already part of the filename. If a required extension was not
		 * specified, we check the extension against a list of valid
		 * extensions and complain if the filename uses an invalid extension or
		 * no extension at all.
		 * 
		 * Finally, we check whether a file with that name already exists, and
		 * ask for confirmation to overwrite it if it does.
		 */
		if (!(new File(file.getParent()).isDirectory()) && getDialogType() == SAVE_DIALOG) {
			complain("Invalid path - backslashes are not allowed in file names.");
			return;
		}
		if (!filename.matches("[\\w \\-_\\.]+") && getDialogType() == SAVE_DIALOG) {
			complain("\""+ filename + "\" is an invalid filename. You can only use alphanumeric characters, spaces, hyphens, underscores, and periods.");
			return;
		}
		if (filename.length() > 255 && getDialogType() == SAVE_DIALOG) {
			complain("The file name \""+ filename.substring(0, 64) + "...\" is too long. Please choose a shorter name.");
			return;
		}
		if (filename.length() < 1 && getDialogType() == SAVE_DIALOG) {
			complain("Please choose a file name.");
			return;
		}
		if (filename.matches(".*(\\.|\\s)+") && getDialogType() == SAVE_DIALOG) {
			complain("You cannot end a file name with a space or period.");
			return;
		}
		if (filename.matches("\\.+.*+") && getDialogType() == SAVE_DIALOG) {
			complain("You cannot start a file name with a period (hidden files are not allowed here).");
			return;
		}
		if (inArray(filename, INVALID_NAMES) && getDialogType() == SAVE_DIALOG) {
			complain("\""+ filename +"\" is an invalid filename.");
			return;
		}
		if (extension != null) {
			String ext = getExtension(file);
			if (ext == null || !ext.equals(extension)) {
				file = new File(file.getPath() +"."+ extension);
				setSelectedFile(file);
			}
		}
		else {
			String ext = getExtension(file);
			if (ext == null) {
				complain("You must specify a file extension.");
				return;
			}
			if (!inArray(ext, allowed_extensions)) {
				complain("The file extension "+ ext +" is not allowed.");
				return;
			}
		}
		if (file.exists() && getDialogType() == SAVE_DIALOG) {
			int confirm = JOptionPane.showConfirmDialog(
					this,
					file.getName() + " already exists! Would you like to overwrite it?",
					"File already exists",
					JOptionPane.YES_NO_OPTION,
					JOptionPane.WARNING_MESSAGE
			);
			if (confirm != JOptionPane.YES_OPTION)
				return;
		}
	    super.approveSelection();
	}
	
	private void complain(String complaint) {
		JOptionPane.showMessageDialog(
				this,
				complaint,
				"Invalid filename",
				JOptionPane.ERROR_MESSAGE
		);
	}
	
	public String getRequiredExtension() {
		return extension;
	}
	
	public void setRequiredExtension(final String extension) {
		this.extension = extension;
		setFileFilter(new FileFilter() {
			public boolean accept(File f) {
				if (f.isDirectory())
					return f.getName().charAt(0) != '.';
				String ext = ValidatedFileChooser.getExtension(f);
				return ext != null && ext.equals(extension);
			}
			public String getDescription() {
				return extension.toUpperCase();
			}
		});
	}

	public void setAllowedExtensions(String[] exts) {
		allowed_extensions = exts;
	}

	public String[] getAllowedExtensions() {
		return allowed_extensions;
	}

	// Based on code from http://download.oracle.com/javase/tutorial/uiswing/components/filechooser.html
    public static String getExtension(File f) {
        String ext = null, s = f.getName();
        int i = s.lastIndexOf('.');
        if (i > 0 &&  i < s.length() - 1) {
            ext = s.substring(i+1).toLowerCase();
        }
        return ext;
    }
    
    private static boolean inArray(String val, String[] array) {
    	for (String v : array) {
    		if (val.equalsIgnoreCase(v))
    			return true;
    	}
    	return false;
    }

}
