package java;
import com.fasterxml.jackson.databind.ObjectMapper; // version 2.11.1
import com.fasterxml.jackson.annotation.JsonProperty; // version 2.11.1
/*ObjectMapper om = new ObjectMapper();
Root root = om.readValue(myJsonString, Root.class);*/
import java.util.Date;

public class Header{
    @JsonProperty("Magasin") 
    public Magasin getMagasin() { 
         return this.magasin; } 
    public void setMagasin(Magasin magasin) { 
         this.magasin = magasin; } 
    Magasin magasin;
    @JsonProperty("ImageID") 
    public String getImageID() { 
         return this.imageID; } 
    public void setImageID(String imageID) { 
         this.imageID = imageID; } 
    String imageID;
    @JsonProperty("Date") 
    public Date getDate() { 
         return this.date; } 
    public void setDate(Date date) { 
         this.date = date; } 
    Date date;
    @JsonProperty("Divers") 
    public Divers getDivers() { 
         return this.divers; } 
    public void setDivers(Divers divers) { 
         this.divers = divers; } 
    Divers divers;
}
