package Tables;

public class ApplyInfo {
    public String apply_id;
    public String claimant_name;
    public String pet_name;
    public String pet_type;
    public String apply_count;
    public String apply_time;

    public ApplyInfo(String apply_id, String claimant_name, String pet_name, String pet_type,
                     String apply_count, String apply_time){
        this.apply_id = apply_id;
        this.claimant_name=claimant_name;
        this.pet_name=pet_name;
        this.pet_type=pet_type;
        this.apply_count=apply_count;
        this.apply_time=apply_time;
    }


}
