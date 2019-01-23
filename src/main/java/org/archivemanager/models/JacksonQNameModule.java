package org.archivemanager.models;
import java.io.IOException;

import org.archivemanager.models.system.QName;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class JacksonQNameModule extends SimpleModule {
	private static final long serialVersionUID = 2474538804885591910L;

	
	public JacksonQNameModule() {
		addSerializer(QName.class, new QNameSerializer());
		addDeserializer(QName.class, new QNameDeserializer());
	}
	
	public static class QNameSerializer extends StdSerializer<QName> {
		private static final long serialVersionUID = -4107066628968332381L;
		
		public QNameSerializer() {
			this(null);
		}
		protected QNameSerializer(Class<QName> vc) {
			super(vc);
		}		
		@Override
		public void serialize(QName qname, JsonGenerator jgen, SerializerProvider provider) throws IOException {
			jgen.writeString(qname.toString());
		}
		
	}
	public static class QNameDeserializer extends StdDeserializer<QName> {
		private static final long serialVersionUID = 7142580498927076715L;
		
		public QNameDeserializer() {
			this(null);
		}
		protected QNameDeserializer(Class<?> vc) {
			super(vc);
			// TODO Auto-generated constructor stub
		}
		@Override
		public QName deserialize(JsonParser p, DeserializationContext ctxt)	throws IOException, JsonProcessingException {
			JsonNode node = p.getCodec().readTree(p);
			String q = node.asText();
			if(q != null) return new QName(q);
			return null;
		}		
	}
}
