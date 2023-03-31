
package com.example.tickettemplate;

import com.fasterxml.jackson.annotation.JsonProperty;
/*ObjectMapper om = new ObjectMapper();
Root root = om.readValue(myJsonString, Root.class);*/

public class Recapitulatif{
    @JsonProperty("Total_TTC") 
    public int getTotal_TTC() { 
         return this.total_TTC; } 
    public void setTotal_TTC(int total_TTC) { 
         this.total_TTC = total_TTC; } 
    int total_TTC;
    @JsonProperty("Total_Sans_Taxes") 
    public int getTotal_Sans_Taxes() { 
         return this.total_Sans_Taxes; } 
    public void setTotal_Sans_Taxes(int total_Sans_Taxes) { 
         this.total_Sans_Taxes = total_Sans_Taxes; } 
    int total_Sans_Taxes;
    @JsonProperty("TVA") 
    public int getTVA() { 
         return this.tVA; } 
    public void setTVA(int tVA) { 
         this.tVA = tVA; } 
    int tVA;
}
