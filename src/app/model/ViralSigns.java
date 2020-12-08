/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app.model;

/**
 *
 * @author Bright
 */
public class ViralSigns {

    private int id;
    private int clientId;
    private String clientName;
    private String email;
    private float weightLoss;
    private float height;
    private String rash;
    private String throat;
    private String gland;
    private String headache;
    private String stomach;
    private String pain;
    private String aches;
    private String diarrhoe;
    private String treatmentDate;
    private String rertuningDate;
    private String result;
    private String prescription;
    private String notify;

    public ViralSigns(int id, int clientId, String clientName, String email, float weightLoss, float height, String rash, String throat, String gland, String headache, String stomach, String pain, String aches, String diarrhoe, String treatmentDate, String rertuningDate, String result, String prescription, String notify) {
        this.id = id;
        this.clientId = clientId;
        this.clientName = clientName;
        this.email = email;
        this.weightLoss = weightLoss;
        this.height = height;
        this.rash = rash;
        this.throat = throat;
        this.gland = gland;
        this.headache = headache;
        this.stomach = stomach;
        this.pain = pain;
        this.aches = aches;
        this.diarrhoe = diarrhoe;
        this.treatmentDate = treatmentDate;
        this.rertuningDate = rertuningDate;
        this.result = result;
        this.prescription = prescription;
        this.notify = notify;
    }
    
     public ViralSigns(int id,float weightLoss, float height, String rash, String throat, String gland, String headache, String stomach, String pain, String aches, String diarrhoe, String result, String prescription) {
        this.id = id;
        this.weightLoss = weightLoss;
        this.height = height;
        this.rash = rash;
        this.throat = throat;
        this.gland = gland;
        this.headache = headache;
        this.stomach = stomach;
        this.pain = pain;
        this.aches = aches;
        this.diarrhoe = diarrhoe;
        this.result = result;
        this.prescription = prescription;
    }


    public ViralSigns(int clientId, String clientName, String email, float weightLoss, float height, String rash, String throat, String gland, String headache, String stomach, String pain, String aches, String diarrhoe, String treatmentDate, String rertuningDate, String result, String prescription, String notify) {
        this.clientId = clientId;
        this.clientName = clientName;
        this.email = email;
        this.weightLoss = weightLoss;
        this.height = height;
        this.rash = rash;
        this.throat = throat;
        this.gland = gland;
        this.headache = headache;
        this.stomach = stomach;
        this.pain = pain;
        this.aches = aches;
        this.diarrhoe = diarrhoe;
        this.treatmentDate = treatmentDate;
        this.rertuningDate = rertuningDate;
        this.result = result;
        this.prescription = prescription;
        this.notify = notify;
    }

    public int getId() {
        return id;
    }

    public int getClientId() {
        return clientId;
    }

    public String getClientName() {
        return clientName;
    }

    public String getEmail() {
        return email;
    }

    public float getWeightLoss() {
        return weightLoss;
    }

    public float getHeight() {
        return height;
    }

    public String getRash() {
        return rash;
    }

    public String getThroat() {
        return throat;
    }

    public String getGland() {
        return gland;
    }

    public String getHeadache() {
        return headache;
    }

    public String getStomach() {
        return stomach;
    }

    public String getPain() {
        return pain;
    }

    public String getAches() {
        return aches;
    }

    public String getDiarrhoe() {
        return diarrhoe;
    }

    public String getTreatmentDate() {
        return treatmentDate;
    }

    public String getRertuningDate() {
        return rertuningDate;
    }

    public String getResult() {
        return result;
    }

    public String getPrescription() {
        return prescription;
    }

    public String getNotify() {
        return notify;
    }

}
