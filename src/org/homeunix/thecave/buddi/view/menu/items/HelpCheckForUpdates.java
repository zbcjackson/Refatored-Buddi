/*
 * Created on Aug 6, 2007 by wyatt
 */
package org.homeunix.thecave.buddi.view.menu.items;

import java.awt.event.ActionEvent;

import org.homeunix.thecave.buddi.Buddi;
import org.homeunix.thecave.buddi.i18n.keys.MenuKeys;
import org.homeunix.thecave.buddi.plugin.api.util.TextFormatter;
import org.homeunix.thecave.moss.swing.MossFrame;
import org.homeunix.thecave.moss.swing.MossMenuItem;

public class HelpCheckForUpdates extends MossMenuItem {
	public static final long serialVersionUID = 0;

	public HelpCheckForUpdates(MossFrame frame) {
		super(frame, TextFormatter.getTranslation(MenuKeys.MENU_HELP_CHECK_FOR_UPDATES));
	}

	public void actionPerformed(ActionEvent e) {
		Buddi.startUpdateCheck(getFrame(), true);
	}
}
