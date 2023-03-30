//JAVA version

package com.example.tickettemplate;

/*ObjectMapper om = new ObjectMapper();
Root root = om.readValue(myJsonString, Root.class);*/
import com.fasterxml.jackson.annotation.JsonProperty; // version 2.11.1

public class Article{
    @JsonProperty("Quantite") 
    public int getQuantite() {
         return this.quantite; } 
    public void setQuantite(int quantite) { 
         this.quantite = quantite; } 
    int quantite;
    @JsonProperty("Nom") 
    public String getNom() { 
         return this.nom; } 
    public void setNom(String nom) { 
         this.nom = nom; } 
    String nom;
    @JsonProperty("Prix")
    public double getPrix() { 
         return this.prix; } 
    public void setPrix(double prix) { 
         this.prix = prix; } 
    double prix;
}
