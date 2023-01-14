package java;
import com.fasterxml.jackson.databind.ObjectMapper; // version 2.11.1
import com.fasterxml.jackson.annotation.JsonProperty; // version 2.11.1
/*ObjectMapper om = new ObjectMapper();
Root root = om.readValue(myJsonString, Root.class);*/

public class Footer{
    @JsonProperty("Divers") 
    public Divers getDivers() { 
         return this.divers; } 
    public void setDivers(Divers divers) { 
         this.divers = divers; } 
    Divers divers;
}
