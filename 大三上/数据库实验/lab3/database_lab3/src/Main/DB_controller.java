package Main;
import Main.StringManager;
import Tables.*;

import java.math.BigDecimal;
import java.rmi.server.UID;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

import static Main.StringManager.*;


/**DB_Controller:用于处理mysql数据库
 * 而可视化则由swing页面处理。。。。。。
 *
 *  * 注意并没有完全写好，有的需要根据交互进行修改，或者新增函数来实现功能，，，
 *
 * */
public class DB_controller {
    String sql;
    Statement stmt;
    PreparedStatement statement;
    Connection conn;



    /**构造方法*/
    public DB_controller() throws SQLException {
        /**String URL = "jdbc:mysql://localhost:3306/?user=root";
        String USER = "root";
        String PASSWORD = "Fxz@2302802";
        conn = DriverManager.getConnection(URL,USER,PASSWORD);
        stmt = conn.createStatement();
        System.out.println("数据库连接成功");

        //使用数据库
        sql = "use pet_claim_platform_db;";
        stmt.execute(sql);*/

        //功能测试
        /**sql="select contact_id from contactinfo order by contact_id desc limit 1;";
        ResultSet res_contact_id =stmt.executeQuery(sql);
        res_contact_id.next();
        String res = res_contact_id.getString(1);
        System.out.println(res);

        BigDecimal phone = new BigDecimal("18821005890");
        BigDecimal role = new BigDecimal(1);
        add_user("Lily","female",25,phone,"12292@886.com",
                "Nanjing","Shanghai Street","099","123579",role);

        //Date birth = new Date("2023/6/16 11:40:00");
        //add_pet_info("1","aoao","female",birth,"lovely","green");

        //Date birth_ = new Date("2023/3/10 11:40:00");
        //update_pet_info("7","4","ace","female",birth_,"gentle","white");

        //delete_pet_info("7");

        browse_pet_info();

        add_apply("Coco",2);
        add_apply("Coco",3);

        review_apply("2",1);
        review_apply("4",3);

        adopt("2");
        adopt("3");

        BigDecimal phone_ = new BigDecimal("18821080890");
        BigDecimal role_ = new BigDecimal(0);
        add_user("Jucy","female",27,phone_,"122@686.com",
                "Shanghai","Nanjing Street","019","123179",role_);

        //BigDecimal phone_2 = new BigDecimal("17721005190");
        //BigDecimal role_2 = new BigDecimal(1);
        //add_user("David","male",29,phone_2,"12222@58.com",
                //"Wuhan","ra Street","029","123590",role_2);

        Date birth1 = new Date("2023/9/16 11:40:00");
        add_pet_info("5","ank","male",birth1,"lively","blue");

        Date birth_2 = new Date("2023/4/26 11:40:00");
        update_pet_info("8","4","anni","female",birth_2,"gentle","red");

        check_user("1","12456",true);

        //可能需要一个标志位来调用？
        //DB_close();


        browse_pet_info();

        browse_apply_info();

        //BigDecimal phone_2 = new BigDecimal("17721005190");
        //BigDecimal role_2 = new BigDecimal(1);
        //add_user("David","male",29,phone_2,"12222@58.com",
        //"Wuhan","ra Street","029","123590",role_2);

        add_apply("Coco",1);

        //DB_close();*/
    }

    public void DB_open() throws SQLException {
        String URL = "jdbc:mysql://localhost:3306/?user=root";
        String USER = "root";
        String PASSWORD = "Fxz@2302802";
        conn = DriverManager.getConnection(URL,USER,PASSWORD);
        stmt = conn.createStatement();
        System.out.println("数据库连接成功");

        //使用数据库
        sql = "use pet_claim_platform_db;";
        stmt.execute(sql);
    }


    private static String get16UUID(){
        UUID id= UUID.randomUUID();
        String[] idd=id.toString().split("-");
        return idd[0]+idd[1]+idd[2];
    }

    private String insert_contact(BigDecimal phone,int claimant_id, String email,
                               String city, String street, String house) throws SQLException {
        //
        String new_contact_id = get16UUID();
        //System.out.println("new contact_id:"+new_contact_id);

        //在联系方式表上插入信息
        sql="insert into contactinfo\n" +
                "values(?,?,?,?,?,?,?);";
        statement=conn.prepareStatement(sql);
        statement.setString(1, new_contact_id);
        statement.setInt(2, claimant_id);
        statement.setBigDecimal(3, phone);
        statement.setString(4, email);
        statement.setString(5, city);
        statement.setString(6, street);
        statement.setString(7, house);
        statement.executeUpdate();

        return new_contact_id;
    }
    private String insert_user(String psw, BigDecimal role) throws SQLException {
        //计算user_id
        String new_user_id = get16UUID();

        //在用户表上插入信息
        sql="insert into userinfo\n" +
                "values(?,?,?,?);";
        statement=conn.prepareStatement(sql);
        statement.setString(1, new_user_id);
        //statement.setString(2, new_contact_id);
        statement.setBigDecimal(2, USER_STATE_ACTIVE);
        statement.setString(3, psw);
        statement.setBigDecimal(4,role);
        statement.executeUpdate();

        return new_user_id;
    }
    private int insert_claimant(String name, String gender, int age,String new_user_id) throws SQLException {
        //计算认领人id
        sql="select claimant_id from claimantinfo order by claimant_id desc limit 1;";
        ResultSet res_claimant_id =stmt.executeQuery(sql);
        int new_claimant_id;
        if(res_claimant_id.next()){
            int res = res_claimant_id.getInt(1);
            new_claimant_id = res+1;
        }else{
            new_claimant_id = 1;
        }

        //在认领人表上插入信息
        sql="insert into claimantinfo\n" +
                "values(?,?,?,?,?);";
        statement=conn.prepareStatement(sql);
        statement.setInt(1, new_claimant_id);
        statement.setString(2, new_user_id);
        statement.setString(3, name);
        statement.setString(4, gender);
        statement.setInt(5,age);
        statement.executeUpdate();

        return new_claimant_id;
    }

    public int insert_staff(String name, String gender, int age,String new_user_id) throws SQLException {
        //计算管理员id
        sql="select staff_id from staffinfo order by staff_id desc limit 1;";
        ResultSet res_staff_id =stmt.executeQuery(sql);
        int new_staff_id;
        if(res_staff_id.next()){
            int res = res_staff_id.getInt(1);
            new_staff_id = res+1;
        }
        else{
            new_staff_id=1;
        }

        //在管理员表上插入信息
        sql="insert into staffinfo\n" +
                "values(?,?,?,?,?);";
        statement=conn.prepareStatement(sql);
        statement.setInt(1, new_staff_id);
        statement.setString(2, new_user_id);
        statement.setString(3, name);
        statement.setString(4, gender);
        statement.setInt(5,age);
        statement.executeUpdate();

        return new_staff_id;

    }

    public int add_claimant(String name,String gender,int age,String new_user_id,
                             BigDecimal phone,String email,String city,String street,String house) throws SQLException {
        int new_claimant_id = insert_claimant(name,gender,age,new_user_id);
        System.out.println("new_claimant_id:"+new_claimant_id);
        String new_contact_id = insert_contact(phone,new_claimant_id,email,city,street,house);
        System.out.println("new_contact_id:"+new_contact_id);

        return new_claimant_id;
    }

    public String add_user(String psw, BigDecimal role) throws SQLException {
        //注册用户的步骤
        String new_user_id=" ";
        new_user_id = insert_user(psw,role);
        System.out.println("new_user_id:"+new_user_id);
        return new_user_id;
    }

    //登录：检查账号和密码,特别小心引号
    public int check_user(String enter_id,String enter_psw,boolean staff_or_not) throws SQLException {
        String enter_type = "0";
        if(staff_or_not==true){
            enter_type = "1";
        }

        String right;
        try{
            sql =  "select * from userinfo\n" +
                    "where user_id = "+"\"" +enter_id+"\""+"\n"+
                    "and user_type = "+ enter_type+";";
            ResultSet res_check_user_id = stmt.executeQuery(sql);
            res_check_user_id.next();
            right = res_check_user_id.getString(1);
        }catch (Exception e1){
            //System.out.println("账号错误或者角色错误");
            System.out.println("账号错误或角色错误");
            return ID_OR_ROLE_ERROR;
        }

        sql =  "select user_password from userinfo\n" +
                "where user_id ="+"\""+enter_id +"\""+";";
        ResultSet res_check_user_psw = stmt.executeQuery(sql);
        res_check_user_psw.next();
        String right_psw = res_check_user_psw.getString(1);

        if(enter_psw.equals(right_psw)){
            System.out.println("密码正确");
            return LOGIN_SUCCESS;
        }else{
            System.out.println("密码错误");
            return PSW_ERROR;
        }
    }

    private static int calculateAge(Date birthDate, Date currentDate) {
        int age = 0;

        // 获取出生日期的年、月、日
        int yearOfBirth = birthDate.getYear();
        int monthOfBirth = birthDate.getMonth();
        int dayOfBirth = birthDate.getDate();

        // 获取当前日期的年、月、日
        int yearOfCurrentDate = currentDate.getYear();
        int monthOfCurrentDate = currentDate.getMonth();
        int dayOfCurrentDate = currentDate.getDate();

        // 计算年龄
        age = yearOfCurrentDate - yearOfBirth;
        // 如果当前月份小于出生月份，则年龄减1
        if (monthOfCurrentDate < monthOfBirth) {
            age--;
        }
        // 如果当前月份等于出生月份，并且当前日期小于出生日期，则年龄减1
        if (monthOfCurrentDate == monthOfBirth && dayOfCurrentDate < dayOfBirth) {
            age--;
        }
        return age;
    }


    public ArrayList<TypeInfo> browse_type_info() throws SQLException {
        ArrayList<TypeInfo> typeInfoList = new ArrayList<>();
        sql="select * from pettypeinfo;";
        ResultSet res_browse_type =stmt.executeQuery(sql);
        while(res_browse_type.next()){
            TypeInfo newType = new TypeInfo(
                    res_browse_type.getString(1),
                    res_browse_type.getString(2)
            );
            typeInfoList.add(newType);
        }
        return typeInfoList;
    }
    public void add_pet_type(String pet_type_name) throws SQLException {
        //计算品种id
        sql="select pet_type_id from pettypeinfo order by pet_type_id desc limit 1;";
        ResultSet res_type_id =stmt.executeQuery(sql);
        int new_type_id;
        if(res_type_id.next()){
            int res = res_type_id.getInt(1);
            new_type_id = res+1;
        }else{
            new_type_id = 1;
        }


        System.out.println("new_pet_type_id:"+new_type_id);
        System.out.println("new_type_name:"+pet_type_name);
        //在品种表上插入信息
        sql="insert into pettypeinfo\n" +
                "values(?,?);\n";
        statement=conn.prepareStatement(sql);
        statement.setInt(1, new_type_id);
        statement.setString(2, pet_type_name);
        statement.executeUpdate();
        //别忘了执行语句。。。。。。

    }

    public void update_pet_type(int pet_type_id,String new_name) throws SQLException {
        sql = "update pettypeinfo \n" +
                "set pet_type_name = \""+new_name+"\"\n" +
                "where pet_type_id =" +pet_type_id+";";
        stmt.executeUpdate(sql);
    }

    public boolean check_pet_type(int pet_type_id) throws SQLException {
        sql = "select count(*) from petinfo\n" +
                "where pet_type_id ="+pet_type_id+";";
        ResultSet res_check_type = stmt.executeQuery(sql);
        res_check_type.next();
        int num = res_check_type.getInt(1);
        if(num!=0){
            return true;
        }else{
            return false;
        }

    }

    public void delete_pet_type(int pet_type_id) throws SQLException {
        sql = "delete from pettypeinfo\n" +
                "where pet_type_id ="+pet_type_id+";";
        stmt.executeUpdate(sql);
    }


    public void add_pet_info(int pet_type_id,String pet_name,String pet_gender, Date pet_birthday,
                             String pet_character,String pet_color) throws SQLException {
        //id应该放在内部，交给系统处理，而不是管理员自己弄一个id
        //先把信息弄好，：pet.pet_id=...  pet.age=...
        String new_pet_id = get16UUID();

        java.sql.Date birthday = new java.sql.Date(pet_birthday.getTime());
        //System.out.println("birthday:"+birthday);

        //在宠物表上插入信息
        sql="insert into petinfo\n" +
                "values(?,?,?,?,?,?,?,?,?,?);";
        statement=conn.prepareStatement(sql);
        statement.setString(1, new_pet_id);
        statement.setInt(2,pet_type_id);
        statement.setString(3,pet_name);
        statement.setString(4,pet_gender);
        statement.setDate(5, birthday);
        Date nowDate = new Date();
        int pet_age = calculateAge(pet_birthday,nowDate);
        statement.setInt(6,pet_age);
        statement.setString(7,pet_character);
        statement.setString(8,pet_color);
        statement.setBigDecimal(9,PET_STATE_NOT_ADOPTED);
        statement.setInt(10,0);
        statement.executeUpdate();
        System.out.println("新增宠物信息");
    }

    public void update_pet_info(String pet_id,int pet_type_id,String pet_name,String pet_gender,
                                Date pet_birthday,String pet_character,String pet_color) throws SQLException {

        java.sql.Date birthday = new java.sql.Date(pet_birthday.getTime());
        System.out.println("birthday:"+birthday);
        int pet_age = calculateAge(pet_birthday,new Date());


        //pet_type_id是不是也要string
        sql =   "update petinfo\n" +
                "set pet_type_id = "+pet_type_id+"\n" +
                "where pet_id ="+"\""+pet_id+"\""+";\n";
        stmt.executeUpdate(sql);
        System.out.println("品种id已更新");
        sql =   "update petinfo\n" +
                "set pet_name ="+ "\"" +pet_name+ "\"" +"\n" +
                "where pet_id ="+"\""+pet_id+"\""+";\n";
        stmt.executeUpdate(sql);
        System.out.println("宠物昵称已更新");
        sql =   "update petinfo\n" +
                "set pet_gender = \""+pet_gender+"\""+"\n" +
                "where pet_id ="+"\""+pet_id+"\""+";\n";
        stmt.executeUpdate(sql);
        System.out.println("宠物性别已更新");
        sql =   "update petinfo\n" +
                "set pet_birthday ="+"\""+birthday+"\""+"\n" +
                "where pet_id ="+"\""+pet_id+"\""+";\n";
        stmt.executeUpdate(sql);
        System.out.println("宠物生日已更新");
        sql =   "update petinfo\n" +
                "set pet_age = "+pet_age+"\n" +
                "where pet_id ="+"\""+pet_id+"\""+";\n";
        stmt.executeUpdate(sql);
        System.out.println("宠物年龄已更新");
        sql =   "update petinfo\n" +
                "set pet_character = \""+pet_character+"\""+"\n" +
                "where pet_id ="+"\""+pet_id+"\""+";\n";
        stmt.executeUpdate(sql);
        System.out.println("宠物性格已更新");
        sql =   "update petinfo\n" +
                "set pet_color = \""+pet_color+"\""+"\n" +
                "where pet_id ="+"\""+pet_id+"\""+";\n";
        stmt.executeUpdate(sql);
        System.out.println("宠物颜色已更新");
        System.out.println("宠物信息更新完毕");
        //

    }


    public boolean check_pet_apply(String pet_id) throws SQLException {
        sql = "select pet_applys from petinfo\n" +
                "where pet_id = \""+pet_id+"\";";
        ResultSet res_check_pet_apply = stmt.executeQuery(sql);
        res_check_pet_apply.next();
        int num = res_check_pet_apply.getInt(1);
        if(num!=0){
            return true;
        }else{
            return false;
        }
    }
    public void delete_pet_info(String pet_id) throws SQLException {
        //根据pet_id来删除记录
        sql= "DELETE FROM petinfo where pet_id = \""+pet_id+"\";\n";

        int res_delete =stmt.executeUpdate(sql);
        System.out.println("删除宠物信息");
    }

    public ArrayList<PetInfo_Claimant> browse_pet_info_claimant() throws SQLException {
        //浏览没有被收养的宠物信息,利用了视图
        sql="select * from pet_info_claimant\n" +
                "where pet_state = "+PET_STATE_NOT_ADOPTED+" ;";
        ResultSet res_browse_pet =stmt.executeQuery(sql);
        ArrayList<PetInfo_Claimant> PetInfoList = new ArrayList<>();
        while(res_browse_pet.next()){
            PetInfo_Claimant newPetInfo = new PetInfo_Claimant(
                    res_browse_pet.getString(1),
                    res_browse_pet.getString(2),
                    res_browse_pet.getString(3),
                    res_browse_pet.getString(4),
                    res_browse_pet.getString(5),
                    res_browse_pet.getString(6)
            );
            PetInfoList.add(newPetInfo);
        }
        return PetInfoList;
    }

    public ArrayList<PetInfo> browse_pet_info_staff() throws SQLException {
        sql="select * from petinfo;";
        ResultSet res_browse_pet =stmt.executeQuery(sql);

        //ResultSetMetaData rsmd = res_browse_pet.getMetaData();
        //int columnCount = rsmd.getColumnCount();
        ArrayList<PetInfo> PetInfoList = new ArrayList<>();
        while(res_browse_pet.next()){
            PetInfo newPetInfo = new PetInfo(
                    res_browse_pet.getString(1),
                    res_browse_pet.getString(2),
                    res_browse_pet.getString(3),
                    res_browse_pet.getString(4),
                    res_browse_pet.getString(5),
                    res_browse_pet.getString(6),
                    res_browse_pet.getString(7),
                    res_browse_pet.getString(8),
                    res_browse_pet.getString(9),
                    res_browse_pet.getString(10)
            );
            PetInfoList.add(newPetInfo);
        }
        return PetInfoList;
    }


    public int getStaffID(String userID) throws SQLException {
        int staff_ID=0;
        sql = "select staff_id from staffinfo\n" +
                "where user_id = \""+userID+"\";";
        ResultSet res_staff_id =stmt.executeQuery(sql);
        while(res_staff_id.next()){
            staff_ID = res_staff_id.getInt(1);
        }
        return staff_ID;
    }
    public int getClaimantID(String userID) throws SQLException {
        int claimant_ID=0;
        sql = "select claimant_id from claimantinfo\n" +
                "where user_id = \""+userID+"\";";
        ResultSet res_claimant_id =stmt.executeQuery(sql);
        while(res_claimant_id.next()){
            claimant_ID = res_claimant_id.getInt(1);
        }
        return claimant_ID;
    }

    public boolean check_claimant_contact(String user_id) throws SQLException {
        sql = "select * from claimantinfo\n" +
                "where user_id = \""+user_id+"\";";
        ResultSet res_check=stmt.executeQuery(sql);
        if(res_check.next()){
            return true;
        }else{
            return false;
        }
    }

    public void add_apply(String pet_name,int claimant_id) throws SQLException {
        //其他的量应该在系统里面决定
        String new_apply_id = get16UUID();
        System.out.println("new_apply_id:"+new_apply_id);

        //随机选一个staff_id来接受这个apply
        sql = "SELECT staff_id FROM staffinfo\n" +
                "ORDER BY RAND()\n" +
                "LIMIT 1;";
        ResultSet res_rand =stmt.executeQuery(sql);
        res_rand.next();
        String staff_id=res_rand.getString(1);
        System.out.println("random staff id:"+staff_id);

        java.sql.Date date = new java.sql.Date(System.currentTimeMillis());

        //这里会用到索引,根据名字查id
        String pet_id=" ";
        try{
            sql = "select pet_id from petinfo\n" +
                    "where pet_name ="+"\""+pet_name+"\";";
            ResultSet res_search =stmt.executeQuery(sql);
            res_search.next();
            pet_id = res_search.getString(1);
        }catch (Exception e){
            System.out.println("没有找到");
        }



        //在申请 表上插入信息
        sql="insert into applyinfo\n" +
                "values(?,?,?,?,?,?);";
        statement=conn.prepareStatement(sql);
        statement.setString(1, new_apply_id);
        statement.setString(2, staff_id);
        statement.setString(3, pet_id);
        statement.setInt(4, claimant_id);
        statement.setDate(5, date);
        statement.setString(6, APPLY_STATE_WAIT);
        statement.executeUpdate();

        System.out.println("new apply,apply_id="+new_apply_id);
    }

    public ArrayList<ApplyInfo> browse_apply_info_staff(int staff_id) throws SQLException {
        //只能看等待审核的，通过的和驳回的不显示了，而且利用视图
        sql = "select * from apply_info\n" +
                "where staff_id = "+staff_id+" \n" +
                "and apply_state = \""+APPLY_STATE_WAIT+"\";";
        ResultSet res_browse_apply =stmt.executeQuery(sql);

        ArrayList<ApplyInfo> ApplyInfoList = new ArrayList<>();
        while(res_browse_apply.next()){
            ApplyInfo newApply = new ApplyInfo(
                    res_browse_apply.getString(1),
                    res_browse_apply.getString(3),
                    res_browse_apply.getString(4),
                    res_browse_apply.getString(5),
                    res_browse_apply.getString(6),
                    res_browse_apply.getString(7)
            );
            ApplyInfoList.add(newApply);
        }
        return ApplyInfoList;
    }


    public ArrayList<ApplyInfo_Claimant> browse_apply_info_claimant(int claimant_id) throws SQLException {
        sql = "select * from apply_info_claimant\n" +
                "where claimant_id ="+claimant_id+";";
        ResultSet res_browse_apply =stmt.executeQuery(sql);

        ArrayList<ApplyInfo_Claimant> ApplyInfoList = new ArrayList<>();
        while(res_browse_apply.next()){
            ApplyInfo_Claimant newApply = new ApplyInfo_Claimant(
                    res_browse_apply.getString(1),
                    res_browse_apply.getString(2),
                    res_browse_apply.getString(3),
                    res_browse_apply.getString(4),
                    res_browse_apply.getString(5),
                    res_browse_apply.getString(6)
            );
            ApplyInfoList.add(newApply);
        }
        return ApplyInfoList;
    }


    //要检查一下这个宠物有没有人收养了，如果已经收养，则应当自动进行驳回:管理员点通过的时候系统提示信息：自动驳回
    public boolean check_pet_apply_pass(String apply_id) throws SQLException {
        sql = "select pet_id from applyinfo\n" +
                "where apply_id = \""+apply_id+"\";";
        //记得执行
        ResultSet res_check_pet =stmt.executeQuery(sql);
        String pet_id="a";
        while(res_check_pet.next()){
            pet_id = res_check_pet.getString(1);
        }

        sql = "select * from applyinfo\n" +
                "where pet_id = \""+pet_id+"\"\n" +
                "and apply_state = \"a\";";
        ResultSet res_check_pass =stmt.executeQuery(sql);
        if(res_check_pass.next()){
            return true;
        }else {
            return false;
        }

    }

    public void review_apply(String apply_id,boolean decision) throws SQLException {
        //其实就是修改apply的状态为通过或者拒绝

        if(decision==true){
            //通过
            sql = "update applyinfo set apply_state =  \""+APPLY_STATE_ACCEPT+"\""+"\n" +
                    "where apply_id = "+"\""+apply_id+"\""+";";
            int res_accept =stmt.executeUpdate(sql);
            //
            System.out.println("apply_id: "+apply_id+" 通过");
        }else{
            //拒绝
            sql = "update applyinfo set apply_state = \""+APPLY_STATE_REFUSE+"\""+"\n" +
                    "where apply_id = "+"\""+apply_id+"\""+";";
            int res_refuse =stmt.executeUpdate(sql);
            //
            System.out.println("apply_id: "+apply_id+" 驳回");
        }



    }


    public boolean check_apply_adopt(String apply_id) throws SQLException {
        sql = "select * from adoptinfo\n" +
                "where apply_id = \""+apply_id+"\";";
        ResultSet res_check =stmt.executeQuery(sql);
        if(res_check.next()){
            return true;
        }else{
            return false;
        }
    }

    public int add_adopt(String apply_id) throws SQLException {
        //用户发起收养请求：可以做一个界面“我的收养”，弹出同一个用户的申请中和已收养的
        //申请思路：点击“查看信息”，查apply_id
        // 如果状态是驳回，就提示信息：不许收养；如果是等待，则提示仍在审核中；如果接受：新建一个adoptinfo数据
        sql = "select apply_state from applyinfo\n" +
                "where apply_id = "+"\""+apply_id+"\""+";";
        ResultSet res_adopt_check =stmt.executeQuery(sql);
        res_adopt_check.next();
        String state = res_adopt_check.getString(1);

        int flag = 0;

        if(state.equals("a")){
            System.out.println("该申请已通过！");
            flag=1;

            //新增一个adopt信息
            String new_adopt_id = get16UUID();

            java.sql.Date date = new java.sql.Date(System.currentTimeMillis());

            //在收养表上插入信息
            sql="insert into adoptinfo\n" +
                    "values(?,?,?,?);";
            statement=conn.prepareStatement(sql);
            statement.setString(1, new_adopt_id);
            statement.setString(2, apply_id);
            statement.setDate(3, date);
            statement.setString(4, "Start adopt.");
            statement.executeUpdate();


            //而且要根据收养的宠物，修改是否被收养的信息
            sql = "update petinfo set pet_state = 1 where pet_id in\n" +
                    "(select applyinfo.pet_id from applyinfo,adoptinfo\n" +
                    "where adoptinfo.apply_id = applyinfo.apply_id);";
            int res_adopt_pet =stmt.executeUpdate(sql);

            System.out.println("新增收养,adopt_id = "+new_adopt_id);
            //

        }else if(state.equals("w")){
            System.out.println("该申请正在审核中...");
            flag=2;
        }else{
            System.out.println("该申请已被驳回");
            flag=3;
        }
        return flag;
    }


    public ArrayList<AdoptInfo_Claimant> browse_adopt_info_claimant(int claimant_id) throws SQLException {
        sql = "select adopt_id,pet_name,adopt_time,adopt_feedback,claimant_id\n" +
                "from adoptinfo,applyinfo,petinfo\n" +
                "where adoptinfo.apply_id = applyinfo.apply_id\n" +
                "and applyinfo.pet_id = petinfo.pet_id\n" +
                "and claimant_id ="+claimant_id+";";
        ResultSet res_adopt =stmt.executeQuery(sql);
        //res_adopt.next();
        ArrayList<AdoptInfo_Claimant> AdoptInfoList = new ArrayList<>();
        while(res_adopt.next()){
            AdoptInfo_Claimant newAdopt = new AdoptInfo_Claimant(
                    res_adopt.getString(1),
                    res_adopt.getString(2),
                    res_adopt.getString(3),
                    res_adopt.getString(4)
            );
            AdoptInfoList.add(newAdopt);
        }
        return AdoptInfoList;
    }

    public void update_adopt_feedback(String adopt_id,String new_feedback) throws SQLException {
        sql = "update adoptinfo\n" +
                "set adopt_feedback = \n" +
                "\""+new_feedback+"\"\n" +
                "where adopt_id = \""+adopt_id+"\";";
        stmt.executeUpdate(sql);
        System.out.println("修改领养信息");
    }



    public void DB_close() throws SQLException {
        conn.close();
    }
    //还可以写的：领养跟踪表，冻结账号
    public void follow(){

    }

    public void froze_user(){

    }

}
