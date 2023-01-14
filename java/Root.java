package java;
import com.fasterxml.jackson.databind.ObjectMapper; // version 2.11.1
import com.fasterxml.jackson.annotation.JsonProperty; // version 2.11.1
/*ObjectMapper om = new ObjectMapper();
Root root = om.readValue(myJsonString, Root.class);*/

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
