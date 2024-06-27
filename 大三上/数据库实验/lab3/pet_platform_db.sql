/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     2023/10/29 19:27:05                          */
/*==============================================================*/


/*==============================================================*/
/* Table: adoptInfo                                             */
/*==============================================================*/
create table adoptInfo
(
   adopt_id             char(16) not null,
   apply_id             char(16) not null,
   adopt_time           date not null,
   adopt_feedback       text,
   primary key (adopt_id)
);

/*==============================================================*/
/* Table: applyInfo                                             */
/*==============================================================*/
create table applyInfo
(
   apply_id             char(16) not null,
   staff_id             int not null,
   pet_id               char(16) not null,
   claimant_id          int not null,
   apply_time           date not null,
   apply_state          char(1) not null,
   primary key (apply_id)
);

/*==============================================================*/
/* Table: claimantInfo                                          */
/*==============================================================*/
create table claimantInfo
(
   claimant_id          int not null,
   user_id              char(16) not null,
   claimant_name        varchar(30) not null,
   claimant_gender      varchar(10) not null,
   claimant_age         int not null,
   primary key (claimant_id),
   check (claimant_age>14)
);

/*==============================================================*/
/* Table: contactInfo                                           */
/*==============================================================*/
create table contactInfo
(
   contact_id           char(16) not null,
   claimant_id          int not null,
   phone                decimal(11,0) not null,
   email                varchar(40),
   city                 varchar(30),
   street               varchar(30),
   house                varchar(10),
   primary key (contact_id)
);

/*==============================================================*/
/* Table: followInfo                                            */
/*==============================================================*/
create table followInfo
(
   follow_id            char(16) not null,
   adopt_id             char(16) not null,
   staff_id             int not null,
   follow_time          date not null,
   follow_feedback      text,
   primary key (follow_id)
);

/*==============================================================*/
/* Table: petInfo                                               */
/*==============================================================*/
create table petInfo
(
   pet_id               char(16) not null,
   pet_type_id          int not null,
   pet_name             varchar(15) not null,
   pet_gender           varchar(6) not null,
   pet_birthday         date not null,
   pet_age              int not null,
   pet_character        varchar(15),
   pet_color            varchar(10),
   pet_state            numeric(1,0) not null,
   pet_applys           int not null default 0,
   primary key (pet_id),
   check ((pet_age>-1) and (pet_gender in ('male','female')))
);

/*==============================================================*/
/* Table: petTypeInfo                                           */
/*==============================================================*/
create table petTypeInfo
(
   pet_type_id          int not null,
   pet_type_name        varchar(50),
   primary key (pet_type_id)
);

/*==============================================================*/
/* Table: staffInfo                                             */
/*==============================================================*/
create table staffInfo
(
   staff_id             int not null,
   user_id              char(16) not null,
   staff_name           varchar(30) not null,
   staff_gender         varchar(10) not null,
   staff_age            int not null,
   primary key (staff_id),
   check (staff_age>17)
);

/*==============================================================*/
/* Table: userInfo                                              */
/*==============================================================*/
create table userInfo
(
   user_id              char(16) not null,
   user_state           numeric(1,0) not null,
   user_password        varchar(20) not null default '123456',
   user_type            numeric(1,0) not null,
   primary key (user_id)
);

alter table adoptInfo add constraint FK_apply_adopt foreign key (apply_id)
      references applyInfo (apply_id) on delete restrict on update restrict;

alter table applyInfo add constraint FK_apply_pet foreign key (pet_id)
      references petInfo (pet_id) on delete restrict on update restrict;

alter table applyInfo add constraint FK_claimant_apply foreign key (claimant_id)
      references claimantInfo (claimant_id) on delete restrict on update restrict;

alter table applyInfo add constraint FK_staff_apply foreign key (staff_id)
      references staffInfo (staff_id) on delete restrict on update restrict;

alter table claimantInfo add constraint FK_user_claimant foreign key (user_id)
      references userInfo (user_id) on delete restrict on update restrict;

alter table contactInfo add constraint FK_claimant_contact foreign key (claimant_id)
      references claimantInfo (claimant_id) on delete restrict on update restrict;

alter table followInfo add constraint FK_adoopt_follow foreign key (adopt_id)
      references adoptInfo (adopt_id) on delete restrict on update restrict;

alter table followInfo add constraint FK_staff_follow foreign key (staff_id)
      references staffInfo (staff_id) on delete restrict on update restrict;

alter table petInfo add constraint FK_pet_type foreign key (pet_type_id)
      references petTypeInfo (pet_type_id) on delete restrict on update restrict;

alter table staffInfo add constraint FK_user_staff foreign key (user_id)
      references userInfo (user_id) on delete restrict on update restrict;

