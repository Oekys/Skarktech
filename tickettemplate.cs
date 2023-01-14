//C# version
    public class Article
    {
        [JsonProperty("Quantite")]
        [JsonPropertyName("Quantite")]
        public int? Quantite { get; set; }

        [JsonProperty("Nom")]
        [JsonPropertyName("Nom")]
        public string Nom { get; set; }

        [JsonProperty("Prix")]
        [JsonPropertyName("Prix")]
        public double Prix { get; set; }
    }

    public class Body
    {
        [JsonProperty("Article")]
        [JsonPropertyName("Article")]
        public List<Article> Article { get; set; }

        [JsonProperty("Recapitulatif")]
        [JsonPropertyName("Recapitulatif")]
        public Recapitulatif Recapitulatif { get; set; }
    }

    public class Divers
    {
    }

    public class Footer
    {
        [JsonProperty("Divers")]
        [JsonPropertyName("Divers")]
        public Divers Divers { get; set; }
    }

    public class Header
    {
        [JsonProperty("Magasin")]
        [JsonPropertyName("Magasin")]
        public Magasin Magasin { get; set; }

        [JsonProperty("ImageID")]
        [JsonPropertyName("ImageID")]
        public string ImageID { get; set; }

        [JsonProperty("Date")]
        [JsonPropertyName("Date")]
        public DateTime? Date { get; set; }

        [JsonProperty("Divers")]
        [JsonPropertyName("Divers")]
        public Divers Divers { get; set; }
    }

    public class Magasin
    {
        [JsonProperty("Nom")]
        [JsonPropertyName("Nom")]
        public string Nom { get; set; }

        [JsonProperty("Addresse")]
        [JsonPropertyName("Addresse")]
        public string Addresse { get; set; }

        [JsonProperty("Tel")]
        [JsonPropertyName("Tel")]
        public string Tel { get; set; }
    }

    public class Recapitulatif
    {
        [JsonProperty("Total_TTC")]
        [JsonPropertyName("Total_TTC")]
        public int? TotalTTC { get; set; }

        [JsonProperty("Total_Sans_Taxes")]
        [JsonPropertyName("Total_Sans_Taxes")]
        public int? TotalSansTaxes { get; set; }

        [JsonProperty("TVA")]
        [JsonPropertyName("TVA")]
        public int? TVA { get; set; }
    }

    public class Root
    {
        [JsonProperty("Header")]
        [JsonPropertyName("Header")]
        public Header Header { get; set; }

        [JsonProperty("Body")]
        [JsonPropertyName("Body")]
        public Body Body { get; set; }

        [JsonProperty("Footer")]
        [JsonPropertyName("Footer")]
        public Footer Footer { get; set; }
    }
