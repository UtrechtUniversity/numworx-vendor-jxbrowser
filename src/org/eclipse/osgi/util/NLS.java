package org.eclipse.osgi.util;

import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.ResourceBundle;

public class NLS {

	public static String bind(String msg, Object arg) {
		return MessageFormat.format(msg, arg);
	}

	public static void initializeMessages(String bundleName, Class<?> clazz) {
		ResourceBundle rb = ResourceBundle.getBundle(bundleName);
		Enumeration<String> e = rb.getKeys();
		while (e.hasMoreElements()) {
			String string = e.nextElement();
			try {
				clazz.getField(string).set(null, rb.getObject(string));
			} catch (IllegalArgumentException e1) {
			} catch (IllegalAccessException e1) {
			} catch (NoSuchFieldException e1) {
			} catch (SecurityException e1) {
			}
			
		}
	}

}
