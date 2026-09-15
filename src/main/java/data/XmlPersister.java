package data;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.FileInputStream;

public class XmlPersister {
    private static XmlPersister instance;
    private final String dataFile;

    private XmlPersister() {
        this.dataFile = "data.xml";
    }

    public XmlPersister(String dataFile) {
        this.dataFile = dataFile;
    }
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
            File file = new File(dataFile);
            marshaller.marshal(data, file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Data load() {
        File file = new File(dataFile);
        if (!file.exists()) {
            return new Data(); //cuando se corre el sistema por primera vez no hay un Xml
        }
        try {
            JAXBContext context = JAXBContext.newInstance(Data.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            FileInputStream in = new FileInputStream(dataFile);
            Data data = (Data) unmarshaller.unmarshal(in);
            in.close();
            return data;
        }  catch (Exception e) {
            e.printStackTrace();
            return new Data();
        }
    }
}
