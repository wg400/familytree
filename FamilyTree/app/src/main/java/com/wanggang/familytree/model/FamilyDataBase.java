package com.wanggang.familytree.model;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

@Database(entities = {FamilyMemberEntity.class}, version = 1)
abstract public class FamilyDataBase extends RoomDatabase {

    public abstract FamilyMemberDao familyMemberDao();

}
