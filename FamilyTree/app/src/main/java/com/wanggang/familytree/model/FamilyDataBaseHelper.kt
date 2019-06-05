package com.wanggang.familytree.model

import android.arch.persistence.room.Room
import android.content.Context

class FamilyDataBaseHelper constructor(context: Context) {

    private val appDataBase = Room.databaseBuilder(context, FamilyDataBase::class.java,
        "family.db").build()!!

    companion object {
        @Volatile
        var INSTANCE: FamilyDataBaseHelper? = null

        fun getInstance(context: Context): FamilyDataBaseHelper {
            if (INSTANCE == null) {
                synchronized(FamilyDataBaseHelper::class) {
                    if (INSTANCE == null) {
                        INSTANCE = FamilyDataBaseHelper(context.applicationContext)
                    }
                }
            }
            return INSTANCE!!
        }
    }

    /**
     * 根据id获取Member
     */
    fun getFamilyMember(id: Long): FamilyMemberEntity{
        return appDataBase.familyMemberDao().getMemberById(id)
    }

    /**
     * 根据id获取配偶Member
     */
    fun getSpouseMember(id: Long): FamilyMemberEntity{
        return appDataBase.familyMemberDao().getSpouseMemberById(id)
    }

    /**
     * 根据id获取子Member
     */
    fun getChildMembers(id: Long): List<FamilyMemberEntity> {
        return appDataBase.familyMemberDao().getChildMembers(id)
    }

    /**
     * 更新FamilyMemberEntity;必须在非主线程中进行
     */
    fun updateMember(member: FamilyMemberEntity) {
        appDataBase.familyMemberDao().updateItem(member)
    }

    /**
     * 插入FamilyMemberEntity;必须在非主线程中进行
     */
    fun insertMember(member: FamilyMemberEntity) {
        appDataBase.familyMemberDao().insertItem(member)
    }
}