package data;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.FileInputStream;

public class XmlPersister {
    private static XmlPersister instance;
    private static final String DATA_FILE = "data.xml";
    public static XmlPersister instance() {
        if (instance == null) {
            instance = new XmlPersister();
        }
        return instance;
    }

    public void store(Data data){
        try {
            JAXBContext context = JAXBContext.newInstance(Data.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            File file = new File(DATA_FILE);
            marshaller.marshal(data, file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Data load() throws Exception{
        File file = new File(DATA_FILE);
        if (!file.exists()) {
            return new Data(); //cuando se corre el sistema por primera vez no hay un Xml
        }
        try {
            JAXBContext context = JAXBContext.newInstance(Data.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            FileInputStream in = new FileInputStream(DATA_FILE);
            Data data = (Data) unmarshaller.unmarshal(in);
            in.close();
            return data;
        }  catch (Exception e) {
            e.printStackTrace();
            return new Data();
        }
    }
}
