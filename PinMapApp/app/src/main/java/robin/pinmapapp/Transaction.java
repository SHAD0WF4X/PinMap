package robin.pinmapapp;

import java.io.Serializable;
import java.util.GregorianCalendar;

public class Transaction implements Serializable{
    public int id = -1;
    public Double lat = null, lon = null;
    public String name = "", description = "", dateTime = "", bedrag = "";
}
