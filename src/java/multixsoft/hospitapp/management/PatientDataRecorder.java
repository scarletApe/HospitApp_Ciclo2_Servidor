/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package multixsoft.hospitapp.management;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import multixsoft.hospitapp.webservice.AdapterRest;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 * REST Web Service
 *
 * @author YONY
 */
@Path("patientdatarecorder")
public class PatientDataRecorder {

    @Context
    private UriInfo context;
    private AdapterRest adapter = new AdapterRest();

    /**
     * Creates a new instance of PatientDataRecorder
     */
    public PatientDataRecorder() {
    }

    /**
     * Retrieves representation of an instance of multixsoft.hospitapp.management.PatientDataRecorder
     * @return an instance of java.lang.String
     */
    @GET
    @Produces("application/json")
    public String getJson() {
        //TODO return proper representation object
        throw new UnsupportedOperationException();
    }

    /**
     * PUT method for updating or creating an instance of PatientDataRecorder
     * @param content representation for the resource
     * @return an HTTP response with content of the updated or created resource.
     */
    @PUT
    @Consumes("application/json")
    public void putJson(String content) {
    }
    /**
     * Este metodo se encarga de guardar un nuevo reporte
     * @param report conrresponde a la variable de tipo reporte que contiene al 
     * reporte
     * @return una variable de tipo booleana que indica si se pudo realizar la acción 
     */
    @GET
    @Path("/savereport")
    @Produces("text/plain")
    public boolean saveNewReport(@QueryParam("report") String report){
        try{
               JSONObject reportObject = (JSONObject)new JSONParser().parse(report);
               String path = "report";
               return adapter.post(path, reportObject.toJSONString());
    	}catch(org.json.simple.parser.ParseException io){
    		return false;
    	}
    }
    
    /**
     * Este metodo obtiene un reporte específico a partir de una cita
     * @param idAppointment corresponde al id de la cita de la cual se requiere
     * el reporte
     * @return un String que contiene al reporte asociado a la cita indicada
     */
    @GET
    @Path("/historyfromappointment")
    @Produces("application/json")
    public String obtainHistoryFromAppointment(@QueryParam("appointment") String idAppointment){
        String path = "report/findbyappointment?idAppointment" + idAppointment;
        JSONObject report = (JSONObject)adapter.get(path);
        return report.toJSONString();
    }
    
    
    /**
     * Este metodo se encarga de obtener el total de reportes guardados y sumarle 
     * uno. No requiere parámetros.
     * @return un String que contiene el numero de reportes más uno
     */
    @GET
    @Path("/idreportplusone")
    @Produces("text/plain")
    public String getIdReportPlusOne(){
        String path = "report/count";
        Long idReport = Long.parseLong(adapter.get(path).toString());
        idReport += 1L;
        return idReport.toString();
    }
}
