//JAVA version
// import com.fasterxml.jackson.databind.ObjectMapper; // version 2.11.1
// import com.fasterxml.jackson.annotation.JsonProperty; // version 2.11.1
/* ObjectMapper om = new ObjectMapper();
Root root = om.readValue(myJsonString, Root.class); */
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
    public int getPrix() { 
         return this.prix; } 
    public void setPrix(int prix) { 
         this.prix = prix; } 
    int prix;
}

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

public class Divers{
}

public class Footer{
    @JsonProperty("Divers") 
    public Divers getDivers() { 
         return this.divers; } 
    public void setDivers(Divers divers) { 
         this.divers = divers; } 
    Divers divers;
}

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

public class Root{
    @JsonProperty("Header") 
    public Header getHeader() { 
         return this.header; } 
    public void setHeader(Header header) { 
         this.header = header; } 
    Header header;
    @JsonProperty("Body") 
    public Body getBody() { 
         return this.body; } 
    public void setBody(Body body) { 
         this.body = body; } 
    Body body;
    @JsonProperty("Footer") 
    public Footer getFooter() { 
         return this.footer; } 
    public void setFooter(Footer footer) { 
         this.footer = footer; } 
    Footer footer;
}
