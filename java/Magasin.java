package java;
import com.fasterxml.jackson.databind.ObjectMapper; // version 2.11.1
import com.fasterxml.jackson.annotation.JsonProperty; // version 2.11.1
/*ObjectMapper om = new ObjectMapper();
Root root = om.readValue(myJsonString, Root.class);*/

public class Magasin{
    @JsonProperty("Nom") 
    public String getNom() { 
         return this.nom; } 
    public void setNom(String nom) { 
         this.nom = nom; } 
    String nom;
    @JsonProperty("Addresse") 
    public String getAddresse() { 
         return this.addresse; } 
    public void setAddresse(String addresse) { 
         this.addresse = addresse; } 
    String addresse;
    @JsonProperty("Tel") 
    public String getTel() { 
         return this.tel; } 
    public void setTel(String tel) { 
         this.tel = tel; } 
    String tel;
}
