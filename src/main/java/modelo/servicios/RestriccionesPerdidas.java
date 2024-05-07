package modelo.servicios;

import vista.Lenguaje;

import java.util.ArrayList;

@SuppressWarnings("serial")
public class RestriccionesPerdidas extends ArrayList<RestriccionPerdida> {

    @Override
    public String toString() {
        String total = "";
        String candidata = "";
        String tabla = "";
        for (RestriccionPerdida r : this) {
            switch (r.getTipo()) {
                case RestriccionPerdida.TOTAL:
                    total += r;
                    break;
                case RestriccionPerdida.CANDIDATA:
                    candidata += r;
                    break;
                case RestriccionPerdida.TABLA:
                    tabla += r;
                    break;
                default:
                    break;
            }
        }
        String res = "";

        res += (candidata != "") ? "<h3>" + Lenguaje.text(Lenguaje.CANDIDATE_KEYS) + "</h3>" + "<h4>" + Lenguaje.text(Lenguaje.CANDIDATE_KEYS2) + candidata : "";
        res += (total != "") ? "<h3>" + Lenguaje.text(Lenguaje.CARDINALITY) + "</h3>" + total : "";
        res += (tabla != "") ? "<h3>" + Lenguaje.text(Lenguaje.TABLE_CONSTR) + "</h3>" + tabla : "";
        return res;
    }

}
