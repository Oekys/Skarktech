
package com.example.tickettemplate;

import com.fasterxml.jackson.annotation.JsonProperty; // version 2.11.1

import java.util.ArrayList;

public class Body{
    @JsonProperty("Article") 
    public ArrayList<Article> getArticle() { 
         return this.article; } 
    public void setArticle(ArrayList<Article> article) { 
         this.article = article; } 
    ArrayList<Article> article;
    @JsonProperty("Recapitulatif") 
    public Recapitulatif getRecapitulatif() { 
         return this.recapitulatif; } 
    public void setRecapitulatif(Recapitulatif recapitulatif) { 
         this.recapitulatif = recapitulatif; } 
    Recapitulatif recapitulatif;
}
