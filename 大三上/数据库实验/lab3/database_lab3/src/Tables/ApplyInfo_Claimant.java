package Tables;

public class ApplyInfo_Claimant {
    public String apply_id;

    public String pet_name;
    public String pet_type;
    public String apply_time;

    public String staff_name;

    public String apply_state;

    public ApplyInfo_Claimant(String apply_id,String pet_name, String pet_type,
                              String apply_time,String staff_name,String apply_state){
        this.apply_id = apply_id;
        this.pet_name=pet_name;
        this.pet_type=pet_type;
        this.apply_time=apply_time;
        this.staff_name = staff_name;
        this.apply_state = apply_state;
    }
}
