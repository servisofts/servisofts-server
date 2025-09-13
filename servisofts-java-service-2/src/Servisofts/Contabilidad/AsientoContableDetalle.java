package Servisofts.Contabilidad;

import org.json.JSONArray;
import org.json.JSONObject;

public class AsientoContableDetalle {

    public static AsientoContableDetalle fromJSON(JSONObject json) {
        AsientoContableDetalle detalle = new AsientoContableDetalle(json.optString("key_cuenta_contable"),
                json.optString("glosa"));

        detalle.glosa = (json.optString("glosa", ""));
        detalle.debe = (json.optDouble("debe", 0));
        detalle.haber = (json.optDouble("haber", 0));
        detalle.debe_me = json.optDouble("debe_me", 0);
        detalle.haber_me = json.optDouble("haber_me", 0);
        detalle.key_cuenta_contable = json.optString("key_cuenta_contable", "");
        if(json.has("tags")) detalle.tags = json.optJSONObject("tags");
        return detalle;
    }

    public String key_cuenta_contable;
    public String glosa;
    public String key_moneda;
    public double tipo_cambio = 1;
    public JSONObject tags = new JSONObject();
    public double debe;
    public double haber;
    public double debe_me;
    public double haber_me;

    public AsientoContableDetalle(String key_cuenta_contable, String glosa) {
        this.glosa = glosa;
        this.key_cuenta_contable = key_cuenta_contable;
    }


    public AsientoContableDetalle(String key_cuenta_contable, String glosa, String tipo, double monto, double monto_me, JSONObject tags) {
        this.glosa = glosa;
        this.key_cuenta_contable = key_cuenta_contable;
        this.tags = tags;
        if (tipo.equals("debe")) {
            this.debe = monto;
            this.debe_me = monto_me;
        } else {
            this.haber = monto;
            this.haber_me = monto_me;
        }
        
    }

    public AsientoContableDetalle setTags(JSONObject tags) {
        this.tags = tags;
        return this;
    }

    

    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        json.put("key_cuenta_contable", key_cuenta_contable);
        json.put("glosa", glosa);
        json.put("debe", debe);
        json.put("debe_me", debe_me);
        json.put("haber", haber);
        json.put("haber_me", haber_me);
        json.put("key_moneda", key_moneda);
        json.put("tags", tags);
        return json;
    }

}
