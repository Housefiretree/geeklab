package Main;

import java.math.BigDecimal;

/**
 * @author 86188
 */
public class StringManager {

    public static final BigDecimal USER_ROLE_CLAIMANT = new BigDecimal(0);
    public static final BigDecimal USER_ROLE_STAFF = new BigDecimal(1);

    public static final int LOGIN_SUCCESS = 0;
    public static final int ID_OR_ROLE_ERROR = 1;
    public static final int PSW_ERROR = 2;

    public static final BigDecimal USER_STATE_ACTIVE=new BigDecimal(1);
    public static final BigDecimal USER_STATE_FROZEN=new BigDecimal(0);

    public static final String APPLY_STATE_ACCEPT = "a";
    public static final String APPLY_STATE_WAIT = "w";
    public static final String APPLY_STATE_REFUSE = "r";

    public static final BigDecimal PET_STATE_NOT_ADOPTED = new BigDecimal(0);
    public static final BigDecimal PET_STATE_ADOPTED = new BigDecimal(1);

    public static final String START_PANEL = "StartPanel";
    public static final String REGISTER_PANEL = "RegisterPanel";
    public static final String LOGIN_PANEL = "LoginPanel";
    public static final String STAFF_PANEL = "StaffPanel";
    public static final String STAFF_PET_PANEL = "StaffPetPanel";
    public static final String UPDATE_PET_PANEL = "UpdatePetPanel";
    public static final String ADD_PET_PANEL = "AddPetPanel";
    public static final String REVIEW_APPLY_PANEL = "ReviewApplyPanel";
    public static final String PET_TYPE_PANEL = "PetTypePanel";
    public static final String CLAIMANT_PANEL = "ClaimantPanel";
    public static final String CLAIMANT_PET_PANEL = "ClaimantPetPanel";
    public static final String CLAIMANT_APPLY_PANEL = "ClaimantApplyPanel";
    public static final String CLAIMANT_ADOPT_PANEL = "ClaimantAdoptPanel";


    public static final int NAME_MAX_LENGTH = 30;
    public static final int CLAIMANT_MIN_AGE = 14;
    public static final int MAX_AGE = 150;

    public static final int PHONE_LENGTH = 11;
    public static final int EMAIL_MAX_LENGTH = 40;

    public static final int CITY_MAX_LENGTH = 30;
    public static final int STREET_MAX_LENGTH = 30;
    public static final int HOUSE_MAX_LENGTH = 10;



    public static final int PET_NAME_MAX_LENGTH = 15;
    public static final int CHARACTER_MAX_LENGTH = 15;
    public static final int COLOR_MAX_LENGTH = 10;


;
    public static final int STAFF_MIN_AGE = 17;


    public static final int TYPE_NAME_MAX_LENGTH = 50 ;


    public static final int PSW_MIN_LENGTH = 5;
    public static final int PSW_MAX_lENGTH = 20;





}
