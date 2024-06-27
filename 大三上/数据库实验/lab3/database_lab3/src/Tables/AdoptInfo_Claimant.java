package Tables;

/**
 * @author 86188
 */
public class AdoptInfo_Claimant {
    public String adopt_id;
    public String pet_name;
    public String adopt_time;
    public String adopt_feedback;

    public AdoptInfo_Claimant(String adopt_id,String pet_name,String adopt_time,String adopt_feedback) {
        this.adopt_id = adopt_id;
        this.pet_name = pet_name;
        this.adopt_time = adopt_time;
        this.adopt_feedback = adopt_feedback;
    }

}
