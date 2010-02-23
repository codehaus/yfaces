package org.codehaus.yfaces.util.myfaces;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.io.OutputStream;

import org.apache.myfaces.shared_impl.util.ClassUtils;
import org.apache.myfaces.shared_impl.util.serial.SerialFactory;
import org.jboss.serial.io.JBossObjectInputStream;
import org.jboss.serial.io.JBossObjectOutputStream;

public class JBossSerialFactory implements SerialFactory {
	public static class MyFacesJBossObjectInputStream extends JBossObjectInputStream {
		public MyFacesJBossObjectInputStream(InputStream stream) throws IOException {
			super(stream);
		}

		@Override
		protected Class<?> resolveClass(ObjectStreamClass desc) throws ClassNotFoundException,
				IOException {
			try {
				return ClassUtils.classForName(desc.getName());
			} catch (ClassNotFoundException e) {
				return super.resolveClass(desc);
			}
		}

	}

	public ObjectOutputStream getObjectOutputStream(OutputStream stream) throws IOException {
		return new JBossObjectOutputStream(stream);
	}

	public ObjectInputStream getObjectInputStream(InputStream stream) throws IOException {
		return new MyFacesJBossObjectInputStream(stream);
	}

}