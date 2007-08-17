/*
 * Created on Sep 14, 2006 by wyatt
 * 
 * The interface which can be extended to create import filters
 */
package org.homeunix.thecave.buddi.plugin.api;

import java.io.File;

import org.homeunix.thecave.buddi.plugin.api.model.mutable.MutableModel;
import org.homeunix.thecave.moss.plugin.MossActionPlugin;


public abstract class BuddiImportPlugin implements MossActionPlugin {
	
	/**
	 * Imports data as required.  The implementor chooses where to send
	 * the file (it is recommended to open a JFileChooser, but
	 * this is up to the implementor to decide).
	 */
	public abstract void importData(MutableModel model, File file);
}
