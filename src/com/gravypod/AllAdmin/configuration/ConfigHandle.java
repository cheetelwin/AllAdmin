/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.gravypod.AllAdmin.configuration;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.gravypod.AllAdmin.AllAdmin;

/**
 * 
 * Make sure all of our configs are in their respective places.
 * 
 */
public class ConfigHandle {
	
	private final File configFile;
	
	public ConfigHandle(final AllAdmin plugin) {
	
		if (!plugin.getDataFolder().exists()) {
			plugin.getDataFolder().mkdirs();
		}
		
		configFile = new File(plugin.getDataFolder(), "config.yml");
		
		if (!configFile.exists()) {
			try {
				ConfigHandle.copy(plugin.getResourceAsStream("config.yml"), configFile);
			} catch (final IOException ex) {
				ex.printStackTrace();
			}
		}
		
		new ConfigLoad(plugin, configFile);
		
	}
	
	/**
	 * Taken from groupmanager
	 * 
	 * @param src
	 * @param dst
	 * @throws IOException
	 */
	public static void copy(final InputStream src, final File dst) throws IOException {
	
		final InputStream in = src;
		final OutputStream out = new FileOutputStream(dst);
		
		// Transfer bytes from in to out
		final byte[] buf = new byte[1024];
		int len;
		while((len = in.read(buf)) > 0) {
			out.write(buf, 0, len);
		}
		out.close();
		try {
			in.close();
		} catch (final Exception e) {
		}
	}
	
}
