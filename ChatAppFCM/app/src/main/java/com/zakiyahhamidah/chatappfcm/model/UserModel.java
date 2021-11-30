package com.zakiyahhamidah.chatappfcm.model;

import static com.zakiyahhamidah.chatappfcm.activity.StartActivity.USERS_TABLE;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = USERS_TABLE)
public class UserModel {

    @NonNull
    @PrimaryKey
    public int user_id = 0;

    public String name = "";
    public String phone_number = "";
    public String reg_date = "";
    public String last_seen = "";
    public String profile_photo = "";
}