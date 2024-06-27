package Tables;

import java.math.BigDecimal;
import java.util.Date;

public class PetInfo {
    public String pet_id;
    public String pet_type_id;
    public String pet_name;
    public String pet_gender;
    public String pet_birthday;
    public String pet_age;
    public String pet_character;
    public String pet_color;
    public String pet_state;
    public String pet_applys;



    public PetInfo(String pet_id,String pet_type_id,String pet_name,String pet_gender,
                        String pet_birthday, String pet_age, String pet_character, String pet_color,
                        String pet_state, String pet_applys){
        this.pet_id=pet_id;
        this.pet_type_id=pet_type_id;
        this.pet_name=pet_name;
        this.pet_gender=pet_gender;
        this.pet_birthday=pet_birthday;
        this.pet_age=pet_age;
        this.pet_character=pet_character;
        this.pet_color=pet_color;
        this.pet_state=pet_state;
        this.pet_applys=pet_applys;
    }
}
